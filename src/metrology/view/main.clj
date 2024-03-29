(ns metrology.view.main
  (:require 
    [seesaw.core :refer :all]
    [seesaw.mig :refer [mig-panel]]
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

(def toolbar-fields-panel
  (toolbar
    :items
    (vec
      (map (fn [[txt data]]
               (button :class :query-toolbar
                       :text txt
                       :user-data data))
           '(["id" " v.id "]
             ["выгрузка" " v.upload "]
             ["год" " v.manufacture_year "]
             ["дата" " c.date "]
             ["зав. №" " v.serial_number "]
             ["количество" " v.channels "]
             ["контрагент" " ca.short_name "]
             ["объем" " v.scope "]
             ["протокол" " v.protocol_number "]
             ["состав" " v.components "]
             ["счет" " v.count "]
             ["тип СИ" " v.mi_type "])))))

(def toolbar-operations-panel
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

(def navigation-panel
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

(def table-panel
  (border-panel
     :border 2
     :north  (mig-panel
               :items [[toolbar-fields-panel "width max!, wrap"]
                       [toolbar-operations-panel "grow, wrap"]
                       [navigation-panel "grow"]])
     :center tab
     :south (status-label "Готов!")))
