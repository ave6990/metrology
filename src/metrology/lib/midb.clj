(ns metrology.lib.midb
  (:require 
    [clojure.java.jdbc :as jdbc]
    [clojure.string]
    [metrology.lib.database :as db]
    [metrology.lib.midb-queries :as q]))

(db/defdb midb)

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

(defmacro defn-copy
  [s]
  `(defn ~(symbol (str "copy-" s "!"))
    ~(str "Копировать строки таблицы v_"
          s
          " соответствующие заданному v_id.")
    [~(symbol "id-from") ~(symbol "id-to")]
    (map (partial jdbc/execute! midb)
         [[~(symbol (str "q/delete-" s)) ~(symbol "id-to")]
          [~(symbol (str "q/copy-" s))
           ~(symbol "id-to")
           ~(symbol "id-from")]])))

(macroexpand-1 '(defn-copy gso))

(defn-copy gso)

(defn copy-gso!
  "Копировать строки таблицы v_gso соответствующие заданному v_id."
  [id-from id-to]
  (map (partial jdbc/execute! midb)
       [[q/delete-gso id-to] [q/copy-gso id-to id-from]])) 

(defn delete-gso!
  "Удалить строки таблицы v_gso соответствующие заданному v_id."
  [id]
  (jdbc/execute! midb [q/delete-gso id]))

(defn copy-record!
  "Копировать запиь о поверке с данными о применяемых эталонах, операциях
   поверки и результатах измерений.
   args:
     id - целочисленный идентификатор записи в БД."
  [id]
  (map (partial jdbc/execute! midb)
    [[q/copy-verification id]
     q/copy-gso 
      "--to copy refs
      with temp as (
          select
              id as v_id,
              copy_from as v_id_from
          from
              verification
          order by id desc
          limit 1
          )
      insert into v_refs
      select
          (select v_id from temp),
          ref_id
      from
          v_refs
      where
          v_id = (select v_id_from from temp);"
      "--to copy opt_refs
      with temp as (
          select
              id as v_id,
              copy_from as v_id_from
          from
              verification
          order by id desc
          limit 1
          )
      insert into v_opt_refs
      select
          (select v_id from temp),
          ref_id
      from
          v_opt_refs
      where
          v_id = (select v_id_from from temp);"
      "--to copy v_operations
      with temp as (
          select
              id as v_id,
              copy_from as v_id_from
          from
              verification
          order by id desc
          limit 1
          )
      insert into v_operations
          (v_id, op_id, comment)
      select
          (select v_id from temp),
          op_id,
          comment
      from
          v_operations
      where
          v_id = (select v_id_from from temp);"
      "--to copy measurements
      with temp as (
          select
              id as v_id,
              copy_from as v_id_from
          from
              verification
          order by id desc
          limit 1
          )
      insert into measurements
          (v_id, metrology_id, operation_id, ref_value)
      select
          (select v_id from temp),
          metrology_id,
          operation_id,
          ref_value
      from
          measurements
      where
          v_id = (select v_id_from from temp);"]))

(comment

((fn [x]
  (do (inc x)
      (dec x))) 3)

(map (fn [f] (f)) (list (inc 2) (inc 4)))

(jdbc/query midb "select id from verification limit 1;")

(map (partial * 2) '(2 3 4))

(copy-verification! 2060)

(copy-gso! 2060 3000)

(delete-gso! 3000)

(insert-conditions! "2023-08-29" 22.7 52.9 98.57 223.2)

(get-conditions "2023-08-30")

(def record (atom nil))

(reduce (fn [a b] (assoc a b nil)) @record '(:channels :sw_name :protocol))

(clear-record @record '(:channels :sw_name))

@record

(some (fn [x] (= x :sw_name)) (keys @record))

(reset! record (new-record midb "conditions"))

(reset! record (first (jdbc/query midb "Select * from verification limit 1;")))

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

(dir clojure.string)

)
