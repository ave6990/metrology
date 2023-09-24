(ns metrology.view.report
  (:require [metrology.lib.gen-html :refer :all]
            [clojure.string :as string]))

(defn report-head
  []
  (head
    (meta {:charset "utf-8"})
    (meta {:name "author" :content "Aleksandr Ermolaev"})
    (meta {:name "e-mail" :content "ave6990@ya.ru"})
    (meta {:name "version" :content "2023-09-21"})
    (title "report")
    #_(style {:type "text/css"} styles)
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
        (th "вид поверки")))
    (tbody
      (tr
        (td (:id m))
        (td (:protocol_number m))
        (td (:count m))
        (td (:date m))
        (td (if (= 1 (:verification_type m))
                "периодическая"
                "первичная")))
      (tr
        (td (:engineer m))
        (td {:colspan 3}
            (string/join
              " " 
              (list (:last_name m)
                    (:first_name m)
                    (:second_name m))))
        (td (:comment m))))))

(defn counteragent
  [m]
  (table
    (tr
      (th "id")
      (td (:counteragent_id m))
      (th "инн")
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
  ())

(defn methodology
  [m]
  ())

(defn refs
  [m]
  ())

(defn operations
  [m]
  ())

(defn measurements
  [m]
  ())

(defn record
  [m]
  (section
    {:class record}
    (hr)
    (common-data m)
    (counteragent m)
    (mi-data m)
    (methodology m)
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
      (report-head)
      (body
        (header
          #_(h1 "Условия выборки:"
             where))
        (main
          (records
            coll))))))

(comment

(require '[clojure.string :as string])

(require '[metrology.lib.gen-html :refer :all])

)
