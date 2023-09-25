(ns metrology.view.report
  (:require [metrology.lib.gen-html :refer :all]
            [metrology.lib.protocol :as pr]
            [metrology.lib.metrology :as metr]
            [clojure.string :as string]))

(def styles
"html {
  color: #393939;
  font: 10pt sans-serif;
}
table {
  border: 1px solid lightgray;
  margin: 4pt 0;
}
td {
  padding: 0 3pt;
  border: 1px solid lightgray;
}")

(defn report-head
  [page]
  (head
    (meta {:charset "utf-8"})
    (meta {:name "author" :content "Aleksandr Ermolaev"})
    (meta {:name "e-mail" :content "ave6990@ya.ru"})
    (meta {:name "version" :content "2023-09-21"})
    (title page)
    (style {:type "text/css"} styles)
    #_(script {:type "text/javascript"} scripts)))

(defn common-data
  [m]
  (table
    (thead
      (tr
        (th "id")
        (th "№ протокола")
        (th "счет")
        (th "дата")
        (th "вид поверки")
        (th "скопировано")))
    (tbody
      (tr
        (td (:id m))
        (td (:protocol_number m))
        (td (:count m))
        (td (:date m))
        (td (if (= 1 (:verification_type m))
                "периодическая"
                "первичная"))
        (td (:copy_from m)))
      (tr
        (td (:engineer m))
        (td {:colspan 3}
            (string/join
              " " 
              (list (:last_name m)
                    (:first_name m)
                    (:second_name m))))
        (td {:colspan 2} (:comment m))))))

(defn counteragent
  [m]
  (table
    (tr
      (th "id")
      (td (:counteragent_id m))
      (th "ИНН")
      (td (:inn m)))
    (tr
      (th {:colspan 2} "наименование")
      (td {:colspan 2} (:counteragent_name m)))
    (tr
      (th {:colspan 2} "сокращенное")
      (td {:colspan 2} (:counteragent_short_name m)))
    (tr
      (th {:colspan 2} "адрес")
      (td {:colspan 2} (:address m)))))

(defn mi-data
  [m]
  (table
    (tr
      (th "тип СИ")
      (td {:colspan 4} (:mi_type m)))
    (tr (th "рег. №")
      (th "год изг.")
      (th "зав. №")
      (th "сфера")
      (th "МПИ"))
    (tr
      (td (:registry_number m))
      (td (:manufacture_year m))
      (td (:serial_number m))
      (td (:area m))
      (td (:interval m)))
    (tr
      (th "состав")
      (td {:colspan 4} (:components m)))
    (tr
      (th "объем")
      (td {:colspan 4} (:scope m)))
    (tr
      (th "ПО")
      (th "версия")
      (th "версия СИ")
      (th "контр. сумма")
      (th "алгоритм"))
    (tr
      (td (:sw_name m))
      (td (:sw_version m))
      (td (:sw_version_real m))
      (td (:sw_checksum m))
      (td (:sw_algorithm m)))))

(defn methodology
  [m]
  (table
    (tr
      (th "id")
      (td (:methodology_id m))
      (th "наименование")
      (td (:methodology_short_name m)))
    (tr
      (th {:colspan 2} "полное наименование")
      (td {:colspan 2} (:methodology_name m)))))

(defn conditions
  [m]
  (table
    (tr
      (th)
      (th "температура")
      (th "влажность")
      (th "давление")
      (th "напряжение")
      (th "частота")
      (th "прочие"))
    (tr
      (th "НД")
      (td (:temperature m))
      (td (:humidity m))
      (td (:pressure m))
      (td (:voltage m))
      (td (:frequency m))
      (td (:other m)))
    (tr
      (th "изм.")
      (td (:real_temperature m))
      (td (:real_humidity m))
      (td (:real_pressure m))
      (td (:real_voltage m))
      (td (:real_frequency m))
      (td (:real_other m)))))

(defn refs
  [m]
  (table
    (tr
      (th)
      (th "тип")
      (th "id")
      (th "тип СИ")
      (th "компонент")
      (th "значение")
      (th "зав. №")
      (th "номер 1С")
      (th "разряд")
      (th "дата")
      (th "срок годности")
      (th "доступно"))
    (string/join
      "\n"
      (map (fn [m]
               (tr
                (td (:expiration m))
                (td (:type m))
                (td (:ref_id m))
                (td (:mi_type m))
                (td (:components m))
                (td (:value m))
                (td (:serial_number m))
                (td (:number_1c m))
                (td (:level m))
                (td (:date m))
                (td (:expiration_date m))
                (td (:available m))))
           (:refs m)))))

(defn operations
  [m]
  (table
    (tr
      (th "id")
      (th "пункт НД")
      (th "наименование")
      (th "результат")
      (th "причина непригодности")
      (th "комментарий"))
    (string/join
      "\n"
      (map (fn [m]
               (tr
                 (td (:id m))
                 (td (:section m))
                 (td (:name m))
                 (td (case (:result m)
                       0 "-"
                       1 "V"
                       -1 "X"))
                 (td (:unusability m))
                 (td (:comment m))))
           (:operations m)))))

(defn measurements
  ""
  [m]
  (table
    (tr 
      (th "канал")
      (th "опорное")
      (th "измеренное")
      (th "погрешность")
      (th "предел погрешности")
      (th "вариация"))
    (string/join
      "\n"
      (map (fn [m]
               (tr
                 (td (:channel_name m))
                 (if (< (:error_type m) 3)
                     (let [res (pr/metrology-calc m)]
                       (str (td
                              (string/replace
                                (metr/discrete
                                  (:ref_value m)
                                  (if (:low_unit m)
                                      (:low_unit m)
                                      0.1))
                                "." ","))
                            (td (:value res))
                            (td (:error res))
                            (td (:error_string m))
                            (td
                              (if (:variation res)
                                (:variation res)
                                "-"))))
                     (when (> (:error_type m) 5)
                       (td {:class "channel-cell" :colspan 5}
                           (:chr_string m))))))
           (:measurements m)))))

(defn record
  [m]
  (section
    {:class record}
    (hr)
    (common-data m)
    (counteragent m)
    (mi-data m)
    (methodology m)
    (conditions m)
    (refs m)
    (operations m)
    (measurements m)))

(defn records
  [coll]
  (string/join
    "\n"
    (map (fn [m] (record m))
       coll)))

(defn report
  "Создает содержимое файла report.html."
  [coll]
  (doctype
    (html
      (report-head "report")
      (body
        (header
          #_(h1 "Условия выборки:"
             where))
        (main
          (records
            coll))))))

(defn gso
  ""
  [coll]
  (doctype
    (html
      (report-head "gso")
      (body
        (header
          (h1 "ГСО"))
        (main
          (table
            (thead
              (tr
                (th "id")
                (th)
                (th "№ 1С")
                (th "тип")
                (th "наличие")
                (th "состав")
                (th "значение")
                (th "погрешность")
                (th "ед. изм.")
                (th "№ паспорта")))
            (tbody
              (string/join
                "\n"
                (map (fn [m]
                         (tr
                           (td (:id m))
                           (td (:expiration m))
                           (td (:number_1c m))
                           (td (str (:type m) " " (:number m)))
                           (td (:available m))
                           (td (:components m))
                           (td (:concentration m))
                           (td (:uncertainity m))
                           (td (:units m))
                           (td (:pass_number m))))
                     coll)))))))))

(defn metrology
  ""
  [coll]
  ())

(comment

(require '[clojure.string :as string])

(require '[metrology.lib.gen-html :refer :all])

(require '[metrology.lib.metrology :as metr])

)
