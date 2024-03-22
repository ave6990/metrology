(ns metrology.gui.verification-table
  (:require 
    [seesaw.core :refer :all]
    [seesaw.dev :refer :all]  ;;NB TOFIX delete before release!
    [clojure.pprint :refer [pprint]]  ;;NB TOFIX delete before release!
    [metrology.lib.chemistry :as ch]
    [metrology.lib.metrology :as m]
    [metrology.lib.gs2000 :as gs]
    [metrology.lib.midb :as midb]))

(defstruct column-attr
  :key
  :width
  :text)

(def ^:private columns
  (vec
    (map (fn [[k w t]]
             (struct column-attr k w t))
         [[:id 50 nil]
          [:upload 25 nil]
          [:count 100 nil]
          ;[:ca nil nil]
          [:short_name 350 nil]
          ;[:conditions nil nil]
          [:date 100 nil]
          [:v_type 30 nil]
          [:interval 30 nil]
          [:protocol_number 50 nil]
          [:mi_type 350 nil]
          ;[:meth_id nil nil]
          [:serial_number 150 nil]
          [:year 75 nil]
          [:channels 50 nil]
          [:components 350 nil]
          [:scope 30 nil]
          [:area 30 nil]
          [:sw_name 100 nil]
          [:sw_version 100 nil]
          [:sw_version_real 100 nil]
          [:sw_checksum 100 nil]
          [:sw_algorithm 100 nil]
          ;[:voltage nil nil]
          ;[:other nil nil]
          ;[:sign_mi nil nil]
          ;[:sign_pass nil nil]
          [:protocol 100 nil]
          [:copy_from 100 nil]
          [:comment 300 nil]])))

(def ^:private verifications-model
  (let [data (midb/get-records)]
    (seesaw.table/table-model
      :columns (->> columns
                    (map :key)
                    vec)
      :rows data)))

(def tab
  (table
    :id :v-table
    :model verifications-model
    :auto-resize :off
    :selection-mode :multi-interval
    :column-widths (->> columns
                        (map :width)
                        vec)
    :popup (fn [e] [(action
                      :handler (fn [e] 
                                   (println "Refresh!"))
                      :name "Refresh"
                      :key "menu R")])))

(defn make-v-table
  []
  (scrollable
    tab
    :hscroll :as-needed
    :vscroll :as-needed))
