(ns metrology.view.verification
  (:require [metrology.lib.html-gen]
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
        (th "date")
        (th "protocol")))))

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
      (report-head 
      (body
        (header
          #_(h1 "Условия выборки:"
             where))
        (main
          (records
            coll)))))))

(comment

(require '[clojure.string :as string])

(require '[metrology.lib.gen-html :refer :all])

)
