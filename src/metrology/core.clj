(ns metrology.core
  (:gen-class)
  (:require 
    [seesaw.core :refer :all]
    [seesaw.dev :refer :all]  ;;NB TOFIX delete before release!
    [metrology.lib.chemistry :as ch]
    [metrology.lib.metrology :as m]
    [metrology.lib.gs2000 :as gs]
    [metrology.lib.midb :as midb]
    [metrology.gui.main-menu :refer [make-menu]]
    [metrology.gui.verification-table :refer [make-v-table update-data!]]))

(defn handler
  [event]
  (alert event
           (str "<html>Hello from <b>Clojure</b>. Button"
                (.getActionCommand event) "clicked.")))

(defn make-frame
  []
  (frame
    :title "MIdb v.0.0.1"
    :menubar (make-menu)
    ;:on-close :exit
    :content (border-panel
               :border 5
               :north (make-v-table)
               :south (label :id :status
                             :text "Ready"))))

(-main)

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (-> 
    (make-frame)
    pack!
    show!))

(comment

(frame :title "Hello Swing"
             :on-close :exit
             :content (w/button :text "Click Me"
                                :listen [:action handler]))

(config!
  main-frame
  :content tab)

(require '[metrology.lib.midb :as midb] :reload)

(dir w)

(find-doc "sorted-map")

(doc defstruct)

(show-options (w/vertical-panel))

(show-options (w/scrollable tab))

(show-options (w.table/table-model))

(show-events (w/table))

(doc w/config!)

(doc select)

(find-doc "border-panel")

(require '[metrology.gui.main-menu :refer [make-menu]] :reload)

(require '[metrology.gui.verification-table :refer [make-v-table update-data!]] :reload)


)
