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

(def main-menu
  (v/make-main-menu
    [(menu :text "Главное"
           :items [m-menu/main-about-action m-menu/main-exit-action])
     (menu :text "Окна"
           :items [m-menu/frames-conditions-action
                   m-menu/frames-gso-action])]))

(defn make-frame
  [model c-menu]
  (frame
    :title "MIdb v.0.0.1"
    :menubar main-menu
    ;:on-close :exit
    :content (v/make-table-panel model c-menu)))

(defn set-status
  [s]
  (config! status-label :text s))

(defn add-behavior
  [root]
  (let [query (select root [:#query-text])
        v-table (select root [:#v-table])
        status (select root [:#status-label])
        page-text (select root [:#page-text])
        prev-page-button (select root [:#prev-page-button])
        next-page-button (select root [:#next-page-button])
        pages-label (select root [:#pages-label])]
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
      control/query-enter-pressed)
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
      control/query-enter-pressed)
    #_(b/bind
      upload
      (b/transform #(if %
                        "Фильтр включает выгруженные записи."
                        "Фильтр не включает выгруженные записи."))
      status))
  root)

((keyword "a") {:a 4 :b 5})

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (->>
    (make-frame
      (v/make-v-table-model
        (:data (midb/get-records "v.upload is null" 100 0)))
      control/table-c-menu)
    add-behavior
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

(dir string)

(find-doc "sorted-map")

(doc grid-panel)

(string/join (take 2 "hello"))
(cons (flatten (take 2 "hello")) (drop 2 "hello"))

(string/join (take 2 "hello") (drop 2 "hello"))

(string/join
  (map string/join
     (list (take 2 "hello")
     "+"
       (drop 2 "hello"))))

(show-options (label))

(show-events (text))

(find-doc "table-panel")

(require '[seesaw.widget-options :as w-opt])

(require '[metrology.controller.main-menu :as m-menu] :reload)

(require '[metrology.model.midb :as midb] :reload)

(require '[clojure.string :as string])

(require '[metrology.view.main :as v] :reload)

)
