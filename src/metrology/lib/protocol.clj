(ns metrology.lib.protocol
  (:require [clojure.math :as math]
            [clojure.string :as string]
            [metrology.lib.gen-html :refer :all]
            [metrology.lib.metrology :as metr]))

(defn field
  "Возвращает html поле для вывода данных в протокол."
  [name value]
  (div {:class "field"}
    (p
      (strong (str name ":"))
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
                           #"[а-я]+\s+(НД)"
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

(defn protocol-number
  [m]
  (str (:department m)
       "/" (:engineer m)
       "-" (:protocol_number m)
       "-" (:year m)))

(defn protocol-header
  ""
  [m]
  (header {:class "header1"}
          (p "ФБУ «ОРЕНБУРГСКИЙ ЦСМ»")
          (p "460021, Оренбург, ул.60 лет Октября, 2 «Б»")
          (p "тел/факс (3532) 33-37-05, факс (3532) 33-00-76")
          (p (br))
          (p {:class "capitalize"} "протокол "
                                   (:verification_type m)
                                  " поверки")
          (p (str "№ " (protocol-number m))
             "от"
             (time (date-iso->local (:date m)))
             "г.")))

(defn page-footer
  ""
  [n-page count-page]
    (footer 
      (p "Страница"
         n-page
         "из"
        (span {:contenteditable "true"}
              count-page))))

(defn page-1
  [m]
  "Первая страница протокола."
  (section {:class "page_1"}
    (protocol-header m)
    (main
      (field "Наименование, тип"
             (str (:name m)))
      (div {:class "two-column"}
        (p
          (strong "Заводской номер:")
          (str (:serial_number m) "."))
        (p
          (strong "Год изготовления:")
          (str (:manufacture_year m)
               " г.")))
      (field "Регистрационный номер"
             (:registry_number m))
      (field "В составе"
             (:components m))
      (field "Поверено в объеме"
             (:scope m))
      (field "Наименование, адрес владельца"
             (str (:counteragent m) "; "
                  (:address m)))
      (field "НД на поверку"
             (:methodology m))
      (field "Условия поверки"
             (str "температура воздуха: "
                  (:temperature m)
                  " " (:pr_temperature m) "; "
                  "относительная влажность: "
                  (:humidity m)
                  " " (:pr_humidity m) "; "
                  "атмосферное давление: "
                  (:pressure m)
                  " " (:pr_pressure m)
                  (if-let [other (:other m)]
                          other
                          "")))
      (field "Средства поверки"
             (str (:mi_references m)
                  (if-let [opt-ref (:optional_references m)]
                          opt-ref
                          "")))
      (div {:class "field"}
        (p {:class "capitalize"}
          (strong "Результаты поверки")))
      (div {:class "field"}
        (ol
          (:operations m)))
      (field "Заключение"
             (:conclusion m))
      (p {:class "sign"}
        "Подпись лица выполнявшего поверку"
        (span {:class "placeholder"} "____________________")
        (img {:class "sign_img"
              :src (str "signs/sign_"
                        (math/round (mod (* 100 (rand)) 72))
                        ".png")})
        (:engineer_name m))
      (p "Сведения о результатах поверки переданы в ФИФ ОЕИ."))
    (page-footer 1 2)))

(defn sw-version
  ""
  [m]
  (li {:class "appendix-section"}
      (p "Подтверждение соответствия программного обеспечения:")
      (table {:class "measurement-table"}
        (thead
          (tr
            (th "Идентификационное наименование ПО")
            (th "Идентификационный номер ПО")
            (if (:sw_version_real m)
                (th "Действительный идентификационный номер ПО"))
            (th "Цифровой идентификатор ПО")
            (th "Алгоритм вычисления цифрового идентификатора ПО"))
        (tbody
          (tr
            (string/join "\n"
                         (map (fn [s]
                                  (if s
                                      (th {:class "centered-cell"} s)))
                              (list (:sw_name m)
                                    (:sw_version m)
                                    (if (:sw_version_real m)
                                        (:sw_version_real m)
                                        nil)
                                    (if (:sw_checksum m)
                                        (:sw_checksum m)
                                        "-")
                                    (if (:sw_checksum m)
                                        (:sw_algorithm m)
                                        "-"))))))))))

(defn metrology-calc
  ""
  [m]
  (let [discrete-val
          (if (:low_unit m)
              (:low_unit m)
              0.1)
        val (if (:value m)
                (metr/discrete
                  (:value m)
                  discrete-val))
        err (if (:value m)
                (metr/error val
                            (metr/discrete (:ref_value m) discrete-val)
                            (:r_from m)
                            (:r_to m)))
        vari (if (:value_2 m)
                      (metr/round 
                        (metr/variation
                          (:value_2 m)
                          (:value m)
                          (:ref_value m)
                          (:error m)
                          (:error_type m)
                          (:r_from m)
                          (:r_to m))
                        2)
                      "-")]
    (if (:value m)
        (hash-map
          :value (string/replace val "." ",")
          :error
            (string/replace
              (case (:error_type m)
                    0 (metr/discrete (:abs err) discrete-val)
                    1 (metr/discrete (:rel err) 0.1)
                    2 (metr/discrete (:red err) 0.1))
              "." ",")
          :variation
            (string/replace
              vari
              "." ","))
        (hash-map :value "-" :error "-" :variation "-"))))

(defn measurements-table
  ""
  [coll]
  (if (not (zero? (count coll)))
    (li {:class "appendix-section"}
      (p "Определение метрологических характеристик:")
      (table {:class "measurement-table"}
        (thead
          (tr 
            (th "Канал измерений, диапазон")
            (th "Опорное значение")
            (th "Измеренное значение")
            (th "Действительное значение основной погрешности")
            (th "Предел допускаемого значение основной погрешности")
            (th "Вариация показаний")))
        (tbody
          (string/join
            (map (fn [m]
                     (tr
                       (td {:class "channel-cell"}
                           (str (:channel_name m)))
                       (if (< (:error_type m) 3)
                           (let [res (metrology-calc m)]
                             (str (td {:class "centered-cell"}
                                      (string/replace
                                        (metr/discrete
                                          (:ref_value m)
                                          (if (:low_unit m)
                                              (:low_unit m)
                                              0.1))
                                        "." ","))
                                  (td {:class "centered-cell"}
                                      (:value res))
                                  (td {:class "centered-cell"}
                                      (:error res))
                                  (td {:class "centered-cell"}
                                      (:error_string m))
                                  (td {:class "centered-cell"}
                                      (if (:variation res)
                                        (:variation res)
                                        "-"))))
                           (when (> (:error_type m) 5)
                             (td {:class "channel-cell" :colspan 5}
                                 (:chr_string m))))))
                 coll)))))))

(defn page-2
  "Приложение к протоколу поверки."
  [m]
  (section {:class "page_2"}
    (header {:class "header2"}
      (p
        (str "Приложение к протоколу " 
             (:verification_type m)
             " поверки ")
        (str "№ " (:department m)
        "/" (:engineer m)
        "-" (:protocol_number m)
        "-" (:year m) " от "
        (time (date-iso->local (:date m)))
        " г.")))
    (main
      (ol
        (when (:sw_version m)
              (sw-version m))
        (measurements-table (:measurements m))))
    (page-footer 2 2)))

(defn protocol
  ""
  [m]
    (article
      (page-1 m)
      (page-2 m)))

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
})
var blurred = false
  window.addEventListener('blur', (e) => {
    blurred = true
  })
  window.addEventListener('focus', (e) => {
    if (blurred) {
      location.reload()
    }
  })")

(defn protocols
  ""
  [verifications]
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
          (string/join "\n"
                       (map (fn [m] (protocol m))
                            verifications)))))))

(defn tolerance
  "Возвращает значение допускаемой основной погрешности выраженное
   в абсолютных единицах.
   :m (hash-map :value ; error nominal
                :error_type ; error type
                :ref_value ; references value
                :r_from ; start point of range
                :r_to ; end point of range"
   [m]
   (cond (= (:error_type m) 0)
           (if (:addition m)
               (+ (* (:error m) (:ref_value m)) (:addition m))
               (:error m))
         (= (:error_type m) 1)
           (double (/ (* (:error m) (:ref_value m)) 100))
         (= (:error_type m) 2)
           (double (/ (* (:error m) (- (:r_to m) (:r_from m))) 100))
         (= (:error_type m) 6)
           (* (:error m) 0.15)))

(defn gen-value
  "Возвращает случайное число в пределах основной погрешности."
  [m]
  (let [ref (if (= (:error_type m) 6)
                (* (:error m) 0.8)
                (:ref_value m))
        diff (* 0.75 (tolerance m))
        low-unit (if (= (:error_type m) 6)
                     1
                     (if (:low_unit m)
                         (:low_unit m)
                         0.1))
        res (metr/discrete (- (+ ref (* (rand) 2 diff)) diff)
                           low-unit)]
    (if (< (:error_type m) 3)
        (cond (and (:view_range_from m) (< res (:view_range_from m)))
                (:view_range_from m)
              (and (:view_range_to m) (> res (:view_range_to m)))
                (:view_range_to m)
              :else
                res)
        res)))

(comment

(require '[clojure.java.jdbc :as jdbc])

(require '[clojure.string :as string])

(require '[clojure.math :as math])

(require '[metrology.lib.metrology :as metr])

)
