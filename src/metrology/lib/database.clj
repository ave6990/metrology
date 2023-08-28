(ns metrology.lib.tasks
  (:require 
    [clojure.java.jdbc :as jdbc]))

(defmacro defdb
  [s]
  `(def ~s
    {:classname "org.sqlite.jdbc"
     :subprotocol "sqlite"
     :subname ~(str "data/" s ".db")}))

(defdb personal)

(defdb tasks)

(defdb midb)

(defn active
  "Активные задачи."
  [db]
  (jdbc/query
    db
    ["select * from tasks
      where 
        (status >= 0)
        and (status < 100);"]))

(comment

(active tasks)

(macroexpand '(defdb midb))

)
