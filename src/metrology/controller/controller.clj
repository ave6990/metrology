(ns metrology.controller.controller
  (:require
    [seesaw.core :refer :all]))

(def table-c-menu
  [(action
    :handler (fn [e] 
                 (println "Refresh!"))
    :name "Refresh"
    :key "menu R")])
