(ns metrology.view.main
  (:require 
    [seesaw.core :refer :all]
    [metrology.lib.chemistry :as ch]
    [metrology.lib.metrology :as m]
    [metrology.lib.gs2000 :as gs]))

(defn make-main-menu
  [items]
  (menubar
     :id :main-menu
     :items items))

(defstruct column-attr
  :key
  :width
  :text)

(def column-settings
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
          [:scope 350 nil]
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

(defn make-v-table-model
  [data]
  (seesaw.table/table-model
    :columns (->> column-settings
                  (map :key)
                  vec)
    :rows data))

(def column-widths
  (->>
    column-settings
    (map :width)
    vec))

(defn tab
  [model c-menu]
  (table
    :id :v-table
    :model model
    :auto-resize :off
    :selection-mode :multi-interval
    :column-widths  column-widths
    :popup (fn [e] c-menu)))

(defn make-v-table
  [model c-menu]
  (scrollable
    (tab model c-menu)
    :hscroll :as-needed
    :vscroll :as-needed))

(defn status-label
  [s]
  (label :id :status
         :text s))

(defn filter-panel
  []
  (vertical-panel
    :border 5
    :items
      [(text :id :query)]))

(defn make-table-panel
  [model c-menu]
  (border-panel
     :border 5
     :north (filter-panel)
     :center (make-v-table model c-menu)
     :south (status-label "Готов!")))
