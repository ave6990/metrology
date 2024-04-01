(ns metrology.core
  (:gen-class)
  (:require 
    [clojure.string :as string]
    [seesaw.core :refer :all]
    [seesaw.bind :as b]
    [seesaw.value :refer :all]
    [seesaw.keymap :refer :all]
    [seesaw.dev :refer :all]  ;;NB TOFIX delete before release!
    [clojure.pprint :refer [pprint]]  ;;NB TOFIX delete before release!
    [metrology.lib.chemistry :as ch]
    [metrology.lib.metrology :as m]
    [metrology.lib.gs2000 :as gs]
    [metrology.model.midb :as midb]
    [metrology.view.main :as v]
    [metrology.controller.controller :as control]
    [metrology.controller.main-menu :as m-menu]))

;;TO_FIX move to controller
(defn add-behavior
  [fn-get-records column-settings root]
  (let [query (select root [:#query-text])
        v-table (select root [:#v-table])
        status (select root [:#status-label])
        page-text (select root [:#page-text])
        prev-page-button (select root [:#prev-page-button])
        next-page-button (select root [:#next-page-button])
        pages-label (select root [:#pages-label])
        query-enter-pressed (control/query-enter-pressed
                              fn-get-records
                              column-settings)]
    (doall
      (map (fn [btn]
               (listen
                 btn
                 :mouse-clicked
                 (control/query-toolbar-button-handler btn)))
           (select root [:.query-toolbar])))
    (listen
      page-text
      :action-performed
      query-enter-pressed)
    (listen
      prev-page-button
      :mouse-clicked
      control/prev-page-button-clicked)
    (listen
      next-page-button
      :mouse-clicked
      control/next-page-button-clicked)
    (b/bind
      query
      status)
    (map-key query "ENTER"
      query-enter-pressed)
    (listen
      v-table
      :mouse-clicked
      (control/table-mouse-clicked [:#v-table]))
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

(def main-menu
  (v/make-main-menu
    [(menu :text "Главное"
           :items [m-menu/main-about-action m-menu/main-exit-action])
     (menu :text "Окна"
           :items [#_m-menu/frames-conditions-action
                   (action
                    :handler (fn [e]
                                 (->>
                                   (make-frame
                                     :conditions
                                     "Условия поверки"
                                     nil
                                     v/conditions-table-panel)
                                   (add-behavior
                                     midb/get-conditions
                                     v/conditions-column-settings)
                                   pack!
                                   show!))
                    :name "Условия поверки")
                  (action
                    :handler (fn [e]
                                 (->>
                                   (make-frame
                                     :gso
                                     "ГСО"
                                     nil
                                     v/gso-table-panel)
                                   (add-behavior
                                     midb/get-gso
                                     v/gso-column-settings)
                                   pack!
                                   show!))
                    :name "ГСО")])]))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (->>
    (make-frame
      :verifications
      "MIdb v.0.0.1"
      main-menu
      v/verifications-table-panel)
    (add-behavior
      midb/get-verifications
      v/verifications-column-settings)
    pack!
    show!))

(require '[metrology.controller.controller :as control] :reload)
(require '[metrology.model.midb :as midb] :reload)
(require '[metrology.view.main :as v] :reload)

(-main)

(comment

(frame :title "Hello Swing"
       :on-close :exit
       :content (w/button :text "Click Me"
                          :listen [:action handler]))

(:text (text :text "hello"))

(find-doc "table-panel")

(dir string)

(doc clojure.core/juxt)

(show-options (table))

(show-events (table))

(require '[seesaw.widget-options :as w-opt])

(require '[metrology.controller.main-menu :as m-menu] :reload)

(require '[metrology.model.midb :as midb] :reload)

(require '[clojure.string :as string])

(require '[metrology.view.main :as v] :reload)

)
