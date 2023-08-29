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
        and (status < 100)
      order by priority;"]))

(defn miq
  "SQL запросы к базе midb."
  [& sql]
  (jdbc/query
    midb
    (vec sql)))

(comment

(miq "Select * from verification limit 1;")

(def ^:dynamic *x* 4)

(list *x*)

(binding [*x* 2])

(assoc-in {:key1 1.2 :key2 32 :key3 200} [:key2] 4)

(assoc {:key1 1.2 :key2 32 :key3 200} :key1 5.4)

(active tasks)

(require '[clojure.repl :refer :all])

(find-doc "hash-map")

(macroexpand '(defdb midb))

)
