(ns metrology.controller.table-context-menu
  (:require
    [clojure.string :as string]
    [seesaw.core :refer :all]
    [seesaw.table :refer [table-model value-at update-at!]]
    [metrology.model.midb :as midb]
    [metrology.view.main :as v]))

(defn get-selection-field-values
  [field tab]
  (->>
    (selection tab {:multi? true})
    (value-at tab)
    (map field)))

(defn make-filter-string
  [field where root fr]
  (let [query-text (select fr [:#query-text])
        tab (select root [:#v-table])
        id (get-selection-field-values
             field tab)]
    (text!
      query-text
      (str
        where
        (string/join (str " or " where) id))))
  fr)

(defn make-delete-fn
  [ids]
  (fn [e]
      (dorun
        (map (fn [id]
                 (midb/delete-record! id))
             ids))
      (println (str "Удалены записи " (string/join ", " ids)))))

(defn make-copy-fn
  [id txt]
  (fn [e]
      (midb/copy-record! id (read-string (value txt)))
      (println (str "Запись " id " скопирована " (value txt) " раз!"))))

(defn make-copy-del-records-menu
  [tab]
  [(action
       :name "Копировать запись"
       :handler
         (fn [e]
             (->>
               (v/make-copy-dialog
                 (get-selection-field-values :id tab)
                 make-copy-fn)
               show!)))
     (action
       :name "Удалить запись"
       :handler
         (fn [e]
             (->>
               (v/make-delete-dialog 
                 (get-selection-field-values :id tab)
                 make-delete-fn)
               show!)))])

(defn verifications-table-menu
  [e]
  (let [tab (select (to-frame e) [:#v-table])]
    (conj
      (make-copy-del-records-menu tab)
      (separator)
      (action
        :name "КСП"
        :handler
          (fn [e]
              (->>
                v/svt-frame
                (make-filter-string :id " v_id = " (to-frame e))
                pack!
                show!)))
      (action
        :name "Операции поверки"
        :handler
          (fn [e]
              (->>
                v/operations-frame
                (make-filter-string :id "v_op.v_id = " (to-frame e))
                pack!
                show!)))
      (action
        :name "Результаты измерений"
        :handler
          (fn [e]
              (->>
                v/measurements-frame
               (make-filter-string :id "v.id = " (to-frame e))
               pack!
               show!))))))

;;TOFIX delete records from verification table
#_(defn conditions-table-menu
  [e]
  (let [tab (select (to-root e) [:#v-table])]
    (make-copy-del-records-menu tab)))

(comment

(ns metrology.controller.table-context-menu)
(require '[metrology.model.midb :as midb] :reload)

)
