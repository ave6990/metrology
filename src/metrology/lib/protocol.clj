(ns metrology.lib.protocol
  (:require [metrology.lib.gen-html :as html]))

(defn protocols
  ""
  []
  (doctype
    (html
      (head
        (meta "charset=\"utf-8\"")
        (meta {:author "Aleksandr Ermolaev"})
        (meta {:e-mail "ave6990@ya.ru"})
        (meta {:version "2023-04-19"})
        (title "protocols"))
        (style {:type "text/css"} styles)
        (script {:type "text/javascript"} scripts)
      (body
        (protocol)))))

(defn field
  "Возвращает html поле для вывода данных в протокол."
  [name value]
  (div {:class "field"}
    (p
      (strong (str name ": "))
      (str value "."))))

(defn protocol
  ""
  []
  (section {:class "page_1"}
    (header {:class "header1"}
      (p "ФБУ «ОРЕНБУРГСКИЙ ЦСМ»")
      (p "460021, Оренбург, ул.60 лет Октября, 2 «Б»")
      (p "тел/факс (3532) 33-37-05, факс (3532) 33-00-76")
      (p br)
      (p {:class "capitalize"} "протокол первичной поверки")
      (p "№ 9/61-2191-2023 от "
         (time "07.09.2023")
         " г."))
    (main
      (field "Наименование, тип"
             "Газоопределители химические, ГХ-М")
      (div {:class "two-column"}
        (p
          (strong "Заводской номер: ")
          "02273")
        (p
          (strong "Год изготовления: ")
          "2020 г."))
      (field "Регистрационный номер"
             "68261-17")
      (field "В составе"
             "Аспиратор АМ-5, (100±5) см³")
      (field "Поверено в объеме"
             "в диапазоне (100±5) см³")
      (field "Наименование, адрес владельца"
             "ПАО «ГАЙСКИЙ ГОК», 462631, Оренбургская обл, Гай г, Промышленная ул, дом № 1")
      (field "НД на поверку"
             "МП 242-2129-2017 с Изменением № 1 «Газоопределители химичесчкие ГХ-М. Методика поверки»")
      (field "Условия поверки"
             "температура воздуха: 22,9 (20 ± 5) °C; относительная влажность: 52,7 (≤ 80) %; атмосферное давление: 100,02 (101,3 ± 3,3) кПа")
      (field "Средства поверки"
             "Измеритель объема, ИО-1М, мод. ИО-1М(100) , зав. № 0101; ; Барометр-анероид контрольный, М-67, зав. № 1465; Секундомер электронный, Интеграл С-01, зав. № 414235; Прибор комбинированный, Testo 608-Н1, зав. № 41391837")
      (div {:class "field"}
        (p {:class "capitalize"}
          (strong "Результаты поверки")))
      (div {:class "field"}
        ;; операции поверки с заключением
        ))))

(spit "/media/sf_YandexDisk/Ermolaev/midb/protocol.html"
      (protocols))

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
