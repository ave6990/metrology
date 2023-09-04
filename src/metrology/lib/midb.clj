(ns metrology.lib.midb
  (:require 
    [clojure.java.jdbc :as jdbc]
    [clojure.string]
    [metrology.lib.database :as db]
    [metrology.lib.midb-queries :as q]))

(db/defdb midb)

(defn get-last-id
  "Получить id последней записи заданной таблицы."
  [s]
  (:id (first (jdbc/query 
                midb
                (clojure.string/replace q/last-id "?" s)))))

(defn insert-conditions!
  "Вставка данных условий поверки в БД."
  ([date temp moist press volt freq other location c]
    (jdbc/execute! (str "insert into conditions values (null, "
         (apply str (db/prepare-vals date temp moist
                     press volt freq other location c))
         ");")))
  ([date temp moist press volt]
    (insert-conditions! date temp moist press volt 50 nil "ОЦСМ" nil)))

(defn get-conditions
  "Возвращает запись БД с условиями поверки на заданную дату."
  [date]
  (jdbc/query midb
              ["select * from conditions where date = ?" date]))

(defn copy-verification!
  "Копировать строку таблицы verification."
  [id]
  (jdbc/execute! midb [q/copy-verification id]))

(defn delete-verification!
  "Удалить строку таблицы verification."
  [id]
  (jdbc/delete! midb :verification ["id = ?;" id]))

(defmacro defn-delete
  [s]
  (let [id (gensym "id")]
    `(defn ~(symbol (str "delete-" s "!"))
      ~(str "Удалить строки таблицы "
            (clojure.string/replace s "-" "_")
            " соответствующие заданному v_id.")
      [~id]
      (jdbc/delete! midb 
                    ~(keyword (clojure.string/replace s "-" "_"))
                    ["v_id = ?" ~id]))))

(defn-delete v-gso)
(defn-delete v-refs)
(defn-delete v-opt-refs)
(defn-delete v-operations)
(defn-delete measurements)

(defmacro defn-copy
  [s]
  `(defn ~(symbol (str "copy-" s "!"))
    ~(str "Копировать строки таблицы "
          s
          " соответствующие заданному v_id.")
    [~(symbol "id-from") ~(symbol "id-to")]
    (map (fn [~(symbol "f") ~(symbol "args")] (~(symbol "f") ~(symbol "args")))
         [~(symbol (str "metrology.lib.midb/delete-" s "!"))
           (partial jdbc/execute! midb)]
         [~(symbol "id-to")
           [~(symbol (str "q/copy-" s))
           ~(symbol "id-to")
           ~(symbol "id-from")]])))

(defn-copy v-gso)
(defn-copy v-refs)
(defn-copy v-opt-refs)
(defn-copy v-operations)
(defn-copy measurements)

(defn delete-record!
  "Удалить запись о поверке с заданным id, вместе с данными о
   эталонах, операциях и измерениях."
   [id]
   (map (fn [f] (f id))
        [delete-v-gso! delete-v-refs! delete-v-opt-refs! delete-v-operations!
         delete-measurements! delete-verification!]))

(defn copy-record!
  "Копировать запиь о поверке с данными о применяемых эталонах, операциях
   поверки и результатах измерений.
   args:
     id - целочисленный идентификатор записи в БД."
  [id-from]
  (let [id-to (inc (get-last-id "verification"))]
    (conj (copy-verification! id-from)
          (map (fn [f] (f id-from id-to))
               [copy-v-gso!
                copy-v-refs!
                copy-v-opt-refs!
                copy-v-operations!
                copy-measurements!]))))

(defn get-verification
  "Возвращает hash-map записи verification."
  [id]
  (first (jdbc/query midb
              ["select * from verification where id = ?" id]))) 

(defn get-record
  "Возвращает hash-map записи о поверке."
  [id]
  (apply conj (hash-map :verification (get-verification id))
        (map (fn [table]
               (hash-map
                 (keyword table)
                 (first (jdbc/query midb
                             [(str "select * from " table " where v_id = ?;")
                              id]))))
             (list "v_gso" "v_refs" "v_opt_refs"
              "v_operations" "measurements"))))

(comment

(conj (hash-map :verification (get-verification 2060))
  (apply hash-map (list {:key1 23423} {:key2 3223})))
      
(apply conj {:key0 0} '({:key1 1} {:key2 2} {:key3 3}))

(reset! record (get-record 2060))

(get-in @record [:verification :serial_number])

(->> @record
     :verification
     :serial_number)

(reverse (list 1 2 3 4))

(pop (list 1 2 3 4))

(get-last-id "verification")

(copy-record! 2060)

(delete-record! 2061)

(insert-conditions! "2023-08-29" 22.7 52.9 98.57 223.2)

(get-conditions "2023-08-30")

(def record (atom nil))

(reduce (fn [a b] (assoc a b nil)) @record '(:channels :sw_name :protocol))

(clear-record @record '(:channels :sw_name))

(:verification @record)

(some (fn [x] (= x :sw_name)) (keys @record))

(reset! record (new-record midb "conditions"))

(reset! record (first (jdbc/query midb "Select * from verification limit 1;")))

(swap! record clear-record)

(clear-record @record)

(.toString (java.time.LocalDate/now))

(assoc {:key1 1.2 :key2 32 :key3 200} :key1 5.4)

(conj {} {:key1 1 :key2 2 :key3 3} {:key4 4})

;; documentations

(require '[clojure.repl :refer :all])

(find-doc "hash-map")

(doc get-in)

(dir clojure.core)

)
