(ns metrology.core
  (:gen-class)
  (:require 
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

(def limit (atom 100))
(def page (atom 0))

(def main-menu
  (v/make-main-menu
    [(menu :text "File"
           :items [m-menu/about-action m-menu/exit-action])
     (menu :text "Edit"
           :items [m-menu/copy-action m-menu/paste-action])]))

(defn handler
  [e]
  (config!
    (select (to-frame e)
            [:#status])
    :text "Button pressed"))

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
  (let [query (select root [:#query])
        v-table (select root [:#v-table])
        status (select root [:#status])]
    (b/bind
      query
      status)
    (map-key query "ENTER"
      (fn [e]
          (config!
            v-table
            :model (v/make-v-table-model
                     (midb/get-records (value query) @limit @page))
            :column-widths v/column-widths)))
    #_(b/bind
      upload
      (b/transform #(if %
                        "Фильтр включает выгруженные записи."
                        "Фильтр не включает выгруженные записи."))
      status))
  root)

(doc b/bind)

(doc config!)

(dir seesaw.value)

(doc map-key)

(find-doc "value-at")

(show-options (table))

(show-events (text))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (->>
    (make-frame
      (v/make-v-table-model
        (midb/get-records "v.upload is null" @limit @page))
      control/table-c-menu)
    add-behavior
    pack!
    show!))

(-main)

(comment

(frame :title "Hello Swing"
       :on-close :exit
       :content (w/button :text "Click Me"
                          :listen [:action handler]))

(config!
  main-frame
  :content tab)

(dir w)

(find-doc "sorted-map")

(doc defstruct)

(show-options (vertical-panel))

(show-options (checkbox))

(show-options (seesaw.table/table-model))

(show-events (toggle))

(doc config!)

(doc b/tee)

(doc swap!)

(dir seesaw.core)

(find-doc "table-panel")

(require '[metrology.model.midb :as midb] :reload)

(require '[metrology.view.main :as v] :reload)

)
