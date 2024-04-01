(ns metrology.view.main
  (:require 
    [seesaw.core :refer :all]
    [seesaw.mig :refer [mig-panel]]
    [metrology.lib.chemistry :as ch]
    [metrology.lib.metrology :as m]
    [metrology.lib.gs2000 :as gs]
    [metrology.view.verifications-panel :as v-panel-settings]
    [metrology.view.conditions-panel :as c-panel-settings]))

(defn make-main-menu
  [items]
  (menubar
     :id :main-menu
     :items items))

(defstruct column-attr
  :key
  :width
  :text)

(defn make-column-settings
  [items]
  (vec
    (map (fn [[k w t]]
             (struct column-attr k w t))
         items)))

(defn make-toolbar-fields  
  [items]
  (toolbar
    :items
    (vec
      (map (fn [[txt data]]
               (button :class :query-toolbar
                       :text txt
                       :user-data data))
           items))))

(defn toolbar-operations-panel
  []
  (toolbar
    :items
      (->>
        '(["AND" " AND ()"]
          ["OR" " OR ()"]
          ["LIKE" " LIKE '%%'"]
          ["IS" " IS "]
          ["NOT" " NOT "]
          ["NULL" " NULL "])
        (map (fn [[txt data]]
                 (button :class :query-toolbar
                         :text txt
                         :user-data data)))
        vec)))

(defn make-table
  [id]
  (scrollable
    (table
      :id :v-table
      :auto-resize :off
      :selection-mode :multi-interval)
    :hscroll :as-needed
    :vscroll :as-needed))

(def tab
  (scrollable
    (table
      :id :v-table
      :auto-resize :off
      :selection-mode :multi-interval)
    :hscroll :as-needed
    :vscroll :as-needed))

(defn status-label
  [s]
  (label :id :status-label
         :text s))

(defn navigation-panel
  []
  (toolbar
    :items
      [(text :id :page-text
             :border 5
             :halign :center
             :text "1"
             :size [50 :by 20])
       (button :id :prev-page-button
               :border 5
               :size [20 :by 20]
               :text "<")
       (button :id :next-page-button
               :border 5
               :size [20 :by 20]
               :text ">")
       (label :id :pages-label
              :border 5
              :size [100 :by 20]
              :text "1")
       (text :id :query-text)
       (label :id :cursor-position-label
              :text "0")]))

(defn make-table-panel
  [id toolbar-fields]
  (border-panel
     :border 2
     :north  (mig-panel
               :items [[toolbar-fields "width max!, wrap"]
                       [(toolbar-operations-panel) "grow, wrap"]
                       [(navigation-panel) "grow, wrap"]])
     :center (make-table id) 
     :south (label :id :status-label
                   :text "Готов!")))

(def verifications-column-settings
  (make-column-settings
    v-panel-settings/column-settings))

(def verifications-toolbar-fields
  (make-toolbar-fields
    v-panel-settings/toolbar-fields-settings))

(def verifications-table-panel
  (make-table-panel
    :v-table
    verifications-toolbar-fields))

(def conditions-column-settings
  (make-column-settings
    c-panel-settings/column-settings))

(def conditions-toolbar-fields
  (make-toolbar-fields
    c-panel-settings/toolbar-fields-settings))

(def conditions-table-panel
  (make-table-panel
    :c-table
    conditions-toolbar-fields))
