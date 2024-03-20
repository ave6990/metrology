(ns metrology.core
  (:gen-class)
  (:require 
    [seesaw.core :as w]
    [seesaw.table :as w.table]
    [metrology.lib.chemistry :as ch]
    [metrology.lib.metrology :as m]
    [metrology.lib.gs2000 :as gs]
    [metrology.lib.midb :as midb]))

(defn handler
  [event]
  (w/alert event
         (str "<html>Hello from <b>Clojure</b>. Button"
              (.getActionCommand event) "clicked.")))

(def verifications-model
  (let [data (midb/get-records)]
    (w.table/table-model
      :columns (->> data first keys vec)
      :rows data)))

(def tab
  (w/table
    :model verifications-model))

(def main-frame
  (w/frame :title "MIdb v.0.0.1"
           ;:on-close :exit
           :content tab))

(w/config!
  main-frame
  :content tab)

(-> 
    main-frame
    w/pack!
    w/show!)

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  ())

(comment

(w/frame :title "Hello Swing"
             :on-close :exit
             :content (w/button :text "Click Me"
                                :listen [:action handler]))

(require '[metrology.lib.midb :as midb] :reload)

(dir w)

)
