(ns metrology.lib.midb
  (:require 
    [clojure.java.jdbc :as jdbc]
    [clojure.string]
    [metrology.lib.database :as db]))

(db/defdb midb)

(defn insert-conditions!
  "Вставка данных условий поверки в БД."
  ([date temp moist press volt freq other location c]
    (jdbc/execute! (str "insert into conditions values (null, "
         (apply str (db/prepare-vals date temp moist
                     press volt freq other location c))
         ");")))
  ([date temp moist press volt]
    (add-condition date temp moist press volt 50 nil "ОЦСМ" nil)))

(defn get-conditions
  "Возвращает запись БД с условиями поверки на заданную дату."
  [date]
  (jdbc/query midb
              ["select * from conditions where date = ?" date]))

(defn copy-verification!
  ""
  [id]
  (map (partial jdbc/execute! midb)
    [(str "--to copy the record with id from the verification table
      with temp as (
          select
            " id " as v_id
          )
      insert into
          verification (
              engineer,
              counteragent,
              mi_type,
              methodology_id,
              serial_number,
              manufacture_year,
              components,
              scope,
              channels,
              area,
              interval,
              verification_type,
              sw_name,
              sw_version,
              sw_version_real,
              sw_checksum,
              sw_algorithm,
              voltage,
              protocol,
              protolang,
              copy_from
          )
      select
          engineer,
          counteragent,
          mi_type,
          methodology_id,
          serial_number,
          manufacture_year,
          components,
          scope,
          channels,
          area,
          interval,
          verification_type,
          sw_name,
          sw_version,
          sw_version_real,
          sw_checksum,
          sw_algorithm,
          voltage,
          protocol,
          protolang,
          (select v_id from temp)
      from
          verification
      where
          id = (select v_id from temp);")
      "--to copy gso
      with temp as (
          select
              id as v_id,
              copy_from as v_id_from
          from
              verification
          order by id desc
          limit 1
          )
      insert into v_gso
      select
          (select v_id from temp),
          gso_id
      from
          v_gso
      where
          v_id = (select v_id_from from temp);"]))

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
          v_id = (select v_id_from from temp);")
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
          v_id = (select v_id_from from temp);")
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

(jdbc/query midb ["select * from conditions limit 1;"
                  "seledt * form verification limit 1;"])

(map (partial * 2) '(2 3 4))

(copy-verification! 1973)

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
