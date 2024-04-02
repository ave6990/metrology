(ns metrology.view.main
  (:require 
    [seesaw.core :refer :all]
    [seesaw.mig :refer [mig-panel]]
    [metrology.lib.chemistry :as ch]
    [metrology.lib.metrology :as m]
    [metrology.lib.gs2000 :as gs]
    [metrology.view.verifications-panel-settings :as v-panel-settings]
    [metrology.view.conditions-panel-settings :as c-panel-settings]
    [metrology.view.counteragents-panel-settings :as ca-panel-settings]
    [metrology.view.references-panel-settings :as refs-panel-settings]
    [metrology.view.gso-panel-settings :as gso-panel-settings]))

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

(defn make-buttons-vec
  [items]
  (vec
    (map (fn [[txt data]]
             (button :class :query-toolbar
                     :text txt
                     :user-data data))
         items)))

(defn make-toolbar-fields  
  [items]
  (toolbar
    :items
    (make-buttons-vec
      items)))

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

(defn toolbar-symbols-panel
  []
  (toolbar
    :items
      (->>
        '(["млн⁻¹" " млн⁻¹ "]
          ["мг/м³" " мг/м³ "]
          ["«" "«"]
          ["»" "»"])
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
               :items [[toolbar-fields "width max!, span 3, wrap"]
                       [(toolbar-operations-panel)]
                       [(toolbar-symbols-panel) "span 2, grow, wrap"]
                       [(navigation-panel) "span 3, grow, wrap"]])
     :center (vertical-panel
                :items
                  [(make-table id)
                   (mig-panel
                     :id :edit-panel
                     :visible? false)]) 
     :south (label :id :status-label
                   :text "Готов!")))

(def verifications-column-settings
  (make-column-settings
    v-panel-settings/column-settings))

(def verifications-toolbar-fields
  (->>
    (make-buttons-vec
      v-panel-settings/toolbar-fields-settings)
    (cons
      (checkbox :class :group-by
                :text "группировка по КСП"
                :user-data "hash_refs"))
    vec
    (toolbar :items)))

(def verifications-table-panel
  (make-table-panel
    :v-table
    verifications-toolbar-fields))

(def verifications-edit-panel
  (vector
    (label :text "good")))

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

(def gso-column-settings
  (make-column-settings
    gso-panel-settings/column-settings))

(def gso-toolbar-fields
  (make-toolbar-fields
    gso-panel-settings/toolbar-fields-settings))

(def gso-table-panel
  (make-table-panel
    :gso-table
    gso-toolbar-fields))

(def counteragents-column-settings
  (make-column-settings
    ca-panel-settings/column-settings))

(def counteragents-toolbar-fields
  (make-toolbar-fields
    ca-panel-settings/toolbar-fields-settings))

(def counteragents-table-panel
  (make-table-panel
    :ca-table
    counteragents-toolbar-fields))

(def references-column-settings
  (make-column-settings
    refs-panel-settings/column-settings))

(def references-toolbar-fields
  (make-toolbar-fields
    refs-panel-settings/toolbar-fields-settings))

(def references-table-panel
  (make-table-panel
    :refs-table
    references-toolbar-fields))

(comment
  
(require
  '[metrology.view.counteragents-panel-settings :as ca-panel-settings]
  :reload)
(require
  '[metrology.view.references-panel-settings :as refs-panel-settings]
  :reload)
(require
  '[metrology.view.gso-panel-settings :as gso-panel-settings]
  :reload)
(require
  '[metrology.view.verifications-panel-settings :as v-panel-settings]
  :reload)

)
