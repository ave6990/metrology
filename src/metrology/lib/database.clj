(ns metrology.lib.database
  (:require 
    [clojure.java.jdbc :as jdbc]
    [clojure.string]))

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

(defn prepare-val
  "Оборачивает строки в кавычки, nil заменяет на null."
  [el]
  (cond
    (= el nil)
      "null"
    (= (class el) java.lang.String)
      (str \" el \")
    :else
      el))

(defn prepare-vals
  [& xs]
  (clojure.string/join 
    ", "
    (map prepare-val xs)))

(defn insert-conditions!
  "Вставка данных условий поверки в БД."
  ([date temp moist press volt freq other location c]
    (jdbc/execute! (str "insert into conditions values (null, "
         (apply str (prepare-vals date temp moist
                     press volt freq other location c))
         ");")))
  ([date temp moist press volt]
    (add-condition date temp moist press volt 50 nil "ОЦСМ" nil)))

(defn get-conditions
  "Возвращает запись БД с условиями поверки на заданную дату."
  [s]
  (jdbc/query midb
              (str "select * from conditions where date = "
                   (prepare-val s)
                   ";")))

(defn get-record
  "Возвращает пустую запись выбранной таблицы БД."
  [db table]
  (->>
    (str "select * from " table " limit 1;")
    (jdbc/query db)
    first
    clear-record))

(defn clear-record
  "Все значения хэша меняются на nil."
  [m]
  (apply hash-map (flatten (map (fn [k] (list k nil)) (keys m)))))

(comment

(insert-conditions! "2023-08-29" 22.7 52.9 98.57 223.2)

(get-conditions "2023-08-30")

(def record (->> "Select * from verification limit 1;"
                 (jdbc/query midb)
                 first
                 atom))

@record

(reset! record (get-record midb "conditions"))

(reset! record (first (jdbc/qurey midb "Select * from verification limit 1;")))

(swap! record clear-record)

(clear-record @record)

(.toString (java.time.LocalDate/now))

(assoc {:key1 1.2 :key2 32 :key3 200} :key1 5.4)

(conj {} {:key1 1 :key2 2 :key3 3} {:key4 4})

(assoc-in {:key1 1.2 :key2 32 :key3 200} [:key1] (5.4 5.4))

(active tasks)

;; documentations

(require '[clojure.repl :refer :all])

(find-doc "hash-map")

(doc jdbc/execute!)

(dir jdbc)

(macroexpand '(defdb midb))

)
