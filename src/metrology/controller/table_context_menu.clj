(ns metrology.controller.table-context-menu
  (:require
    [clojure.string :as string]
    [seesaw.core :refer :all]
    [seesaw.table :refer [table-model value-at update-at!]]
    [metrology.view.main :as v]))

(defn make-filter-string
  [field where root fr]
  (let [query-text (select fr [:#query-text])
        tab (select root [:#v-table])
        id (->>
             (selection tab {:multi? true})
             (value-at
                 tab)
             (map field))]
    (text!
      query-text
      (str
        where
        (string/join (str " or " where) id))))
  fr)

(defn verifications-table-menu
  [e]
  [(action
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
            show!)))])
