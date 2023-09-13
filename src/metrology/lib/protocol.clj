(ns metrology.lib.protocol
  (:require [metrology.lib.gen-html :refer all]
            [metrology.lib.midb :refer all]
            [clojure.math :as math]))

(defn field
  "Возвращает html поле для вывода данных в протокол."
  [name value]
  (div {:class "field"}
    (p
      (strong (str name ": "))
      (str value "."))))

(defn date-iso->local
  "Преобразует дату из формата ISO в локальный формат."
  [s]
  (->> (string/split s #"\-")
       reverse
       (string/join ".")))

(defn find-nbsp-place
  "Соблдение требований к размещению неразрывных пробелов."
  [s]
  (->>
    (map (fn [re]
                         (map (fn [el]
                                  (first el))
                              (re-seq re s)))
                     (list #"\d+\s+(\-|÷|±)\s+\d+"
                           #"\d+\)?\s+(см|кПа|Па|млн|с|м|кг|г|%|°C|\()"
                           #"(\.|орт)\s+№"
                           #"(№|СО)\s+\d"
                           #"(р(\-|ай)он\.?|ул\.?|г\.?|д(ом)?\.?)\s+[№а-яА-Я]+"
                           #"[а-яА-Я]+\s+(р(\-|ай)|обл\.?|ул\.?|г\.?|д\.?)"))
    (apply concat)
    set))

(defn bsp->nbsp
  ""
  [s]
  (reduce (fn [a b]
              (string/replace a
                              b
                              (string/replace b " " " ")))
          s
          (find-nbsp-place s)))

 (defn operations
  ""
  [id]
  (div {:class "field"}
    (ol
      (string/join
        "\n"
        (map (fn [m]
                 (li 
                   (p
                     (str (:name m) ":")
                     (em (:result m)))
                   (p {:class "comment"} (:comment m))))
             (get-operations id))))))
     
(defn page_1
  [m]
  "Первая страница протокола."
  (section {:class "page_1"}
    (header {:class "header1"}
      (p "ФБУ «ОРЕНБУРГСКИЙ ЦСМ»")
      (p "460021, Оренбург, ул.60 лет Октября, 2 «Б»")
      (p "тел/факс (3532) 33-37-05, факс (3532) 33-00-76")
      (p (br))
      (p {:class "capitalize"} "протокол "
                               (get-in m [:protocol :verification_type])
                              " поверки")
      (p (str "№ " (get-in m [:protocol :department])
         "/" (get-in m [:protocol :engineer])
         "-" (get-in m [:protocol :protocol_number])
         "-" (get-in m [:protocol :year]) " от "
         (time (date-iso->local (get-in m [:protocol :date])))
         " г.")))
    (main
      (field "Наименование, тип"
             (str (get-in m [:protocol :name])))
      (div {:class "two-column"}
        (p
          (strong "Заводской номер: ")
          (get-in m [:protocol :serial_number]))
        (p
          (strong "Год изготовления: ")
          (get-in m [:protocol :manufacture_year])
          " г."))
      (field "Регистрационный номер"
             (get-in m [:protocol :registry_number]))
      (field "В составе"
             (get-in m [:protocol :components]))
      (field "Поверено в объеме"
             (get-in m [:protocol :scope]))
      (field "Наименование, адрес владельца"
             (str (get-in m [:protocol :counteragent]) "; "
                  (get-in m [:protocol :address])))
      (field "НД на поверку"
             (get-in m [:protocol :methodology]))
      (field "Условия поверки"
             (str "температура воздуха: "
                  (get-in m [:protocol :temperature])
                  " " (get-in m [:protocol :pr_temperature]) "; "
                  "относительная влажность: "
                  (get-in m [:protocol :humidity])
                  " " (get-in m [:protocol :pr_humidity]) "; "
                  "атмосферное давление: "
                  (get-in m [:protocol :pressure])
                  " " (get-in m [:protocol :pr_pressure])
                  (if-let [other (get-in m [:protocol :other])]
                          other
                          "")))
      (field "Средства поверки"
             (str (get-in m [:protocol :mi_references])
                  (if-let [opt-ref (get-in m [:protocol :optional_references])]
                          opt-ref
                          "")))
      (div {:class "field"}
        (p {:class "capitalize"}
          (strong "Результаты поверки")))
      (operations (get-in m [:protocol :id]))
      (field "Заключение"
             (get-in m [:protocol :conclusion]))
      (p {:class "sign"}
        "Подпись лица выполнявшего поверку"
        (span {:class "placeholder"} "____________________")
        (img {:class "sign_img"
              :src (str "signs/sign_"
                        (math/round (mod (* 100 (rand)) 72))
                        ".png")})
        (get-in m [:protocol :engineer_name]))
      (p "Сведения о результатах поверки переданы в ФИФ ОЕИ."))
    (footer 
      (p "Страница 1 из "
        (span {:contenteditable "true"} 2)))))

(defn page_2
  "Приложение к протоколу поверки."
  [m]
  (section {:class "page_2"}
    (header {:class "header2"}
      (p
        "Приложение к протоколу первичной поверки "
        (str "№ " (get-in m [:protocol :department])
        "/" (get-in m [:protocol :engineer])
        "-" (get-in m [:protocol :protocol_number])
        "-" (get-in m [:protocol :year]) " от "
        (time (date-iso->local (get-in m [:protocol :date])))
        " г.")))
    (main)
    (footer
      (p
        "Страница 2 из "
        (span {:contenteditable "true"} "2")))))

(defn protocol
  ""
  [m]
    (article
      (page_1 m)
      (page_2 m)))

(defn protocols
  ""
  []
  (bsp->nbsp
    (doctype
      (html
        (head
          (meta {:charset "utf-8"})
          (meta {:name "author" :content "Aleksandr Ermolaev"})
          (meta {:name "e-mail" :content "ave6990@ya.ru"})
          (meta {:name "version" :content "2023-04-19"})
          (title "protocols"))
          (style {:type "text/css"} styles)
          (script {:type "text/javascript"} scripts)
        (body
          (protocol (get-protocol-data 2220)))))))

(spit "/media/sf_YandexDisk/Ermolaev/midb/protocol.html"
      (protocols))

(meta {:charset "utf-8"})

(def styles
"html {
  font-family: Times New Roman;
  font-size: 12pt; }
section {
  position: relative;
  height: 100%;
  page-break-after: always;
}
.appendix-section {
  margin: 0 0 6pt 0;  
}
.capitalize {
  text-transform: uppercase;
}
p {
  margin: 0;
  text-align: justify;
}
header {
  margin: 0 0 10pt 0;
}
.header1 > p {
  text-align: center;
}
.field {
  margin: 0 0 6pt 0;
}
table {
  width: 100%;
  border-collapse: collapse;
  border: 1px solid;
  font-size: 10pt;
  line-height: 0.9;
}
td, th {
  border: 1px solid;
}
.measurement-table th {
  max-width: 25%;
}
.centered-cell {
  text-align: center;
}
.channel-cell {
  white-space: nowrap;
  min-width: 20%;
}
.sign {
  margin: 10pt 0;
  position: relative;
}
ol {
  margin: 0;
  padding: 0 0.35cm;
}
.comment {
  font-size: 9pt;
}
footer > p {
  position: absolute;
  top: 26.5cm;
  right: 0;
}
@media print {
  @page {
    margin: 1cm;
  }
}
.two-column {
  margin: 0 0 6pt 0;
  display: flex;
  flex-direction: row;
}
.two-column > p {
  flex: 1;
}
.sign > img {
  position: absolute;
  top: -3.5cm;
  left: 3.5cm;
    transform: scale(0.28);
}")

(def scripts
"document.addEventListener(\"dblclick\", (event) => {
  console.log(document.getElementsByClassName(\"sign_img\"))
  const signs = document.getElementsByClassName(\"sign_img\")
  for (const el of signs) {
    el.style.visibility = el.style.visibility == \"visible\" ? \"hidden\" : \"visible\"
  }
})")

(comment

(require '[clojure.java.jdbc :as jdbc])

(require '[clojure.string :as string])

(require '[clojure.math :as math])

)
