(ns metrology.controller.controller
  (:require
    [clojure.string :as string]
    [seesaw.core :refer :all]
    [seesaw.bind :as b]
    [seesaw.keymap :refer :all]
    [seesaw.value :refer :all]
    [seesaw.table :refer [table-model value-at update-at!]]
    [metrology.model.midb :as midb]
    [metrology.view.main :as v]
    [metrology.controller.main-menu :as m-menu]))

(defn make-table-model
  "external deps:
    metrology.core.clj"
  [data column-settings]
  (table-model
    :columns (->> column-settings
                  (map :key)
                  vec)
    :rows data))

(defn table-mouse-clicked
  [column-settings]
  (fn [e]
    (let [root (to-frame e)
          tab (select root [:#v-table])]
      (println (value-at
                  tab
                  (selection tab {:multi? true}))))))

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
              :caret-position))))))

(defn prev-page-button-clicked
  [query-event-handler]
  (fn [e]
    (let [root (to-frame e)
          page-text (select root [:#page-text])
          v (read-string (value page-text))]
      (text!
        page-text
        (if (> v 1)
            (dec v)
            1)))
    (query-event-handler e)))

(defn calc-offset
  [page limit]
  (* (dec page) limit))

(defn calc-pages
  [records-count limit]
  (int (Math/ceil (/ records-count limit))))

(defn query-enter-pressed
  [e]
  (let [root (to-frame e)
        page-text (select root [:#page-text])]
    (value! page-text 1)))

(defn make-query-handler
  [fn-get-records column-settings]
  (fn [e]
      (let [root (to-frame e)
            limit 100
            query (->>
                    (select root [:#query-text])
                    value)
            v-table (select root [:#v-table])
            pages-label (select root [:#pages-label])
            page-text (select root [:#page-text])
            group-by (->>
                       (select root [:.group-by])
                       (filter #(value %))
                       (map (fn [el]
                                (user-data el)))
                       (string/join ", "))
            #_order-by #_(->>
                       (select root [:#order-by])
                       value)
            records (fn-get-records 
                      query
                      limit
                      (calc-offset (read-string (value page-text)) limit)
                      group-by
                      #_order-by)
            data (:data records)
            records-count (:count records)]
        (config!
          v-table
          :model (make-table-model
                   data column-settings)
          :column-widths (->>
                           column-settings
                           (map :width)
                           vec))
        (text!
          pages-label
          (str
            (calc-pages records-count limit)
            " (" records-count ")")))))

(defn next-page-button-clicked
  [query-event-handler]
  (fn [e]
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
      (query-event-handler e)))

(defn add-behavior
  [fn-get-records column-settings root]
  (let [query (select root [:#query-text])
        v-table (select root [:#v-table])
        status (select root [:#status-label])
        page-text (select root [:#page-text])
        prev-page-button (select root [:#prev-page-button])
        next-page-button (select root [:#next-page-button])
        pages-label (select root [:#pages-label])
        query-handler (make-query-handler
                              fn-get-records
                              column-settings)]
    (doall
      (map (fn [btn]
               (listen
                 btn
                 :mouse-clicked
                 (query-toolbar-button-handler btn)))
           (select root [:.query-toolbar])))
    (listen
      page-text
      :action-performed
      query-handler)
    (listen
      prev-page-button
      :mouse-clicked
      (prev-page-button-clicked
        query-handler))
    (listen
      next-page-button
      :mouse-clicked
      (next-page-button-clicked
        query-handler))
    #_(b/bind
      query
      status)
    (map-key query "ENTER"
      (fn [e]
        (query-enter-pressed e)
        (query-handler e)))
    (listen
      v-table
      :mouse-clicked
      (table-mouse-clicked column-settings))
    #_(b/bind
      upload
      (b/transform #(if %
                        "Фильтр включает выгруженные записи."
                        "Фильтр не включает выгруженные записи."))
      status))
  root)

(defn make-frame
  [id title main-menu content]
  (frame
    :id id
    :title title
    :menubar main-menu
    ;:on-close :exit
    :content content))

(def conditions-frame
  (->>
    (make-frame
       :conditions
      "Условия поверки"
      nil
      v/conditions-table-panel)
    (add-behavior
      midb/get-conditions
      v/conditions-column-settings)))

(def gso-frame
  (->>
    (make-frame
      :gso
      "ГСО"
      nil
      v/gso-table-panel)
    (add-behavior
      midb/get-gso
      v/gso-column-settings)))

(def counteragents-frame
  (->>
    (make-frame
      :counteragents
      "Контрагенты"
      nil
      v/counteragents-table-panel)
    (add-behavior
      midb/get-counteragents
      v/counteragents-column-settings)))

(def references-frame
  (->>
    (make-frame
      :references
      "Эталоны"
      nil
      v/references-table-panel)
    (add-behavior
      midb/get-references
      v/references-column-settings)))

(def operations-frame
  (->>
    (make-frame
      :operations
      "Операции поверки"
      nil
      v/operations-table-panel)
    (add-behavior
      midb/get-operations
      v/operations-column-settings)))

(def main-menu
  (v/make-main-menu
    [(menu :text "Главное"
           :items [m-menu/main-about-action m-menu/main-exit-action])
     (menu :text "Окна"
           :items 
             (vec
               (map (fn [nm fr]
                        (action
                          :handler (fn [e]
                                       (->>
                                         fr
                                         pack!
                                         show!))
                          :name nm))
                    ["ГСО" "Контрагенты" "Условия поверки" "Эталоны"]
                    [gso-frame counteragents-frame
                     conditions-frame references-frame]))
              #_[(action
                     :handler (fn [e]
                                  (->>
                                    conditions-frame
                                    pack!
                                    show!))
                     :name "Условия поверки")
                   (action
                     :handler (fn [e]
                                  (->>
                                    gso-frame
                                    pack!
                                    show!))
                     :name "ГСО")
                   (action
                     :handler (fn [e]
                                  (->>
                                    counteragents-frame
                                    pack!
                                    show!))
                     :name "Контрагенты")])]))
