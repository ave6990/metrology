(ns metrology.lib.midb-queries
  (:require [clojure.string]))

(def copy-verification
  "Query to copy the record with id from the verification table"
  "with temp as (
      select
        ? as v_id
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

(defmacro defquery-delete
  [s]
  `(def ~(symbol (str "delete-" s))
    ~(str "Query to delete " s ".")
    ~(str "delete from v_"
          (clojure.string/replace s "-" "_")
          " where v_id = ?;")))

(defquery-delete gso)

(defquery-delete refs)

(defquery-delete opt-refs)

(defquery-delete operations)

(def copy-gso
  "Query to copy gso."
  "insert into v_gso
  select
      ?,
      gso_id
  from
      v_gso
  where
      v_id = ?;")

(def copy-refs
  "Query to copy refs."
  "insert into v_refs
  select
      ?,
      ref_id
  from
      v_refs
  where
      v_id = ?;")

(def copy-opt-refs
  "Query to copy refs."
  "insert into v_opt_refs
  select
      ?,
      ref_id
  from
      v_opt_refs
  where
      v_id = ?;")

(def copy-operations
  "Query to copy operations."
  "insert into v_operations
  select
      ?,
      op_id
  from
      v_operations
  where
      v_id = ?;")