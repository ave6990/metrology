(ns metrology.view.main
  (:require 
    [clojure.string :as string]
    [seesaw.core :refer :all]
    [seesaw.mig :refer [mig-panel]]
    [metrology.lib.chemistry :as ch]
    [metrology.lib.metrology :as m]
    [metrology.lib.gs2000 :as gs]
    [metrology.view.verifications-panel-settings :as v-panel-settings]
    [metrology.view.conditions-panel-settings :as c-panel-settings]
    [metrology.view.counteragents-panel-settings :as ca-panel-settings]
    [metrology.view.references-panel-settings :as refs-panel-settings]
    [metrology.view.operations-panel-settings :as ops-panel-settings]
    [metrology.view.measurements-panel-settings :as meas-panel-settings]
    [metrology.view.set-verification-tools-panel-settings :as svt-panel-settings]
    [metrology.view.gso-panel-settings :as gso-panel-settings]))

(defn make-main-menu
  [items]
  (menubar
     :id :main-menu
     :items items))

(defn make-delete-dialog
   [ids del-fn]
   (dialog
     :modal? true
     :content
       (label
         :text (str "Удалить записи: "
                    (string/join ", " ids)
                    "?"))
     :option-type :ok-cancel
     :type :warning
     :success-fn (del-fn ids)))

(defn make-insert-dialog
   [tab-id data f]
   (dialog
     :modal? true
     :content
       (label
         :text (str "Создать/обновить запись "
                    (:id data)
                    "?"))
     :option-type :ok-cancel
     :type :warning
     :success-fn (f tab-id data)))

(defn make-copy-dialog
   [ids copy-fn]
   (let [txt (text
               :id :copy-count
               :size [75 :by 20]
               :text "1")]
     (dialog
       :modal? true
       :content
         (mig-panel
           :items
            [[(label
             :text (str "Копировать запись с id: "
                        (string/join ", " ids)
                        ".")) "width 100%, wrap"]
             [(label
                  :text "Укажите количество копий: ")]
             [txt "wrap"]])
       :option-type :ok-cancel
       :type :warning
       :success-fn (copy-fn (first ids)
                            txt))))

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

(defn make-checkboxes-vec
  [items]
  (vec
    (map (fn [[txt data cls]]
             (checkbox :class cls
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
        '(["AND" " AND "]
          ["OR" " OR "]
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

(defn status-label
  [s]
  (label :id :status-label
         :text s))

(defn navigation-panel
  []
  (mig-panel
    :items
      [[(text :id :page-text
             ;:border 5
             :halign :center
             :size [50 :by 20]
             :text "1")
        "width 100px"]
       [(button :id :prev-page-button
               ;:border 5
               ;:size [30 :by 20]
               :text "<")
        "width 20px"]
       [(button :id :next-page-button
               ;:border 5
               ;:size [20 :by 20]
               :text ">")
        "width 20px"]
       [(scrollable
         (text :id :query-text
               #_(:multi-line? true
               :wrap-lines? true))
         :hscroll :always)
        "width 100%, span 1 2, wrap"]
       [(label :id :pages-label
              ;:border 5
              ;:size [100 :by 40]
              :text "1")
        "align center, span 3"]
       ;;TOFIX use the cursor position to paste a text at the position
       #_(label :id :cursor-position-label
              :text "0")]))

(defn make-table-panel
  ([id toolbars edit-panel]
   (mig-panel
     :items
       [[toolbars "grow, wrap"]
        [(mig-panel
           :items
             [[(toolbar-operations-panel)]
              [(toolbar-symbols-panel) "grow, wrap"]
              [(navigation-panel) "span 2, grow, wrap"]
              [(make-table id) "height 100%, span 2, grow, wrap"]]) "grow, wrap"] 
              [edit-panel "grow, wrap"]]))
  ([id toolbars]
   (make-table-panel id toolbars (mig-panel
                                   :id :edit-panel
                                   :visible? false))))

(defn make-edit-fields
  [settings]
  (vec
    (reduce (fn [v [txt id stl]]
                (conj v
                      [(label :text txt) ""]
                      [(text :id id
                        :class :record-editor) "x 150, width 90%, wrap"]))
         []
         settings)))

(defn make-edit-panel
  [id settings]
  (scrollable
    (mig-panel
      :id :edit-panel
      :items
        (conj
          (make-edit-fields settings)
          [(button :id :save-button
                   :text "Сохранить"
                   :user-data id) "newline"]
          [(button :id :clear-button
                   :text "Очистить") "wrap"]))))

(def verifications-column-settings
  (make-column-settings
    v-panel-settings/column-settings))

(def verifications-toolbar-fields
  (->>
    (make-buttons-vec
      v-panel-settings/toolbar-fields-settings)
    vec
    (toolbar :items)))

(def verifications-toolbar-group-by
  (->>
    (make-checkboxes-vec
      v-panel-settings/checkboxes-settings)
    vec
    (toolbar :items)))

(def verifications-table-panel
  (make-table-panel
    :v-table
    (mig-panel
      :border 0
      :items
        [[verifications-toolbar-fields "width 100%, wrap"]
         [verifications-toolbar-group-by]])
    #_(mig-panel
      :border 0
      :items
        [])))

(def verifications-edit-panel
  (vector
    (label :text "good")))

(defn make-frame
  [id title main-menu content]
  (frame
    :id id
    :title title
    :menubar main-menu
    ;:on-close :exit
    :content content))

(def conditions-column-settings
  (make-column-settings
    c-panel-settings/column-settings))

(def conditions-frame
  (make-frame
    :conditions
    "Условия поверки"
    nil
    (make-table-panel
      :c-table
      (make-toolbar-fields
        c-panel-settings/toolbar-fields-settings)
      (make-edit-panel :conditions c-panel-settings/edit-panel-settings))))

(def svt-column-settings
  (make-column-settings
    svt-panel-settings/column-settings))

(def svt-frame
  (make-frame
    :set-verification-tools
    "КСП"
    nil
    (make-table-panel
      :svt-table
      (make-toolbar-fields
        svt-panel-settings/toolbar-fields-settings))))

(def gso-column-settings
  (make-column-settings
    gso-panel-settings/column-settings))

(def gso-frame
  (make-frame
    :gso
    "ГСО"
    nil
    (make-table-panel
      :gso-table
      (make-toolbar-fields
        gso-panel-settings/toolbar-fields-settings))))

(def counteragents-column-settings
  (make-column-settings
    ca-panel-settings/column-settings))

(def counteragents-frame
  (make-frame
    :counteragents
    "Контрагенты"
    nil
    (make-table-panel
      :ca-table
      (make-toolbar-fields
        ca-panel-settings/toolbar-fields-settings))))

(def references-column-settings
  (make-column-settings
    refs-panel-settings/column-settings))

(def references-frame
  (make-frame
    :references
    "Эталоны"
    nil
    (make-table-panel
      :refs-table
      (make-toolbar-fields
        refs-panel-settings/toolbar-fields-settings))))

(def operations-column-settings
  (make-column-settings
    ops-panel-settings/column-settings))

(def operations-frame
  (make-frame
    :operations
    "Операции поверки"
    nil
    (make-table-panel
      :ops-table
      (make-toolbar-fields
        ops-panel-settings/toolbar-fields-settings))))

(def measurements-column-settings
  (make-column-settings
    meas-panel-settings/column-settings))

(def measurements-frame
  (make-frame
    :measurements
    "Результаты измерений"
    nil
    (make-table-panel
      :meas-table
      (make-toolbar-fields
        meas-panel-settings/toolbar-fields-settings))))

(comment
  
(require
  '[metrology.view.conditions-panel-settings :as c-panel-settings]
  :reload)
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
(require
  '[metrology.view.operations-panel-settings :as ops-panel-settings]
  :reload)
(require
  '[metrology.view.measurements-panel-settings :as meas-panel-settings]
  :reload)
(require
  '[metrology.view.set-verification-tools-panel-settings :as svt-panel-settings]
  :reload)

)
