(ns metrology.controller.controller
  (:require
    [clojure.string :as string]
    [seesaw.core :refer :all]
    [seesaw.value :refer :all]
    [seesaw.table :refer [table-model]]
    [metrology.view.main :as v]
    [metrology.model.midb :as midb]))

(defn make-table-model
  "external deps:
    metrology.core.clj"
  [data column-settings]
  (table-model
    :columns (->> column-settings
                  (map :key)
                  vec)
    :rows data))

(def table-c-menu
  [(action
    :handler (fn [e] 
                 (println "Refresh!"))
    :name "Refresh"
    :key "menu R")])

(defn insert-string
  [ss s pos]
  (string/replace
    (->>
      (list 
        (take pos ss)
        s
        (drop pos ss))
      (map string/join)
      string/join)
    #"/s+"
    " "))

(defn query-toolbar-button-handler
  [btn]
  (fn [e]
      (let [root (to-frame e)
            query-text (select root [:#query-text])]
        (value!
          query-text
          (insert-string
            (value query-text)
            (user-data btn)
            (config
              query-text
              :caret-position))
          #_(str (value query-text) (user-data btn))))))

(defn prev-page-button-clicked
  [e]
  (let [root (to-frame e)
        page-text (select root [:#page-text])
        v (read-string (value page-text))]
    (text!
      page-text
      (if (> v 1)
          (dec v)
          1)))
  (query-enter-pressed e))

(defn next-page-button-clicked
  [e]
  (let [root (to-frame e)
        page-text (select root [:#page-text])
        pages-label (select root [:#pages-label])
        pages-count (read-string (first (string/split (value pages-label) #" ")))
        current-page (read-string (value page-text))]
    (text!
      page-text
      (if (< current-page pages-count)
          (inc current-page)
          pages-count)))
  (query-enter-pressed e))

(defn calc-offset
  [page limit]
  (* (dec page) limit))

(defn calc-pages
  [records-count limit]
  (int (Math/ceil (/ records-count limit))))

(defn query-enter-pressed
  [e]
  (let [root (to-frame e)
        limit 100
        query (select root [:#query-text])
        v-table (select root [:#v-table])
        pages-label (select root [:#pages-label])
        page-text (select root [:#page-text])
        records (midb/get-records
                  (value query)
                  limit
                  (calc-offset (read-string (value page-text)) limit))
        data (:data records)
        records-count (:count records)]
    (config!
      v-table
      :model (make-table-model
               data v/column-settings)
      :column-widths (->>
                       column-settings
                       (map :width)
                       vec))
    (text!
      pages-label
      (str
        (calc-pages records-count limit)
        " (" records-count ")"))))
