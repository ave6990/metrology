(ns metrology.lib.midb-queries
  (:require [clojure.string]))

(def last-id
  "SQL-запрос возвращающий id послдней записи таблицы."
  "select id from ? order by id desc limit 1;")

(def copy-verification
  "Query to copy the record with id from the verification table"
  "with temp as (
      select
        ? as v_id
      )
  insert into
      verification (
          engineer, counteragent, mi_type, methodology_id,
          serial_number, manufacture_year, components,
          scope, channels, area, interval, verification_type,
          sw_name, sw_version, sw_version_real, sw_checksum,
          sw_algorithm, voltage, protocol, protolang, copy_from
      )
  select
      engineer, counteragent, mi_type, methodology_id,
      serial_number, manufacture_year, components, scope,
      channels, area, interval, verification_type, sw_name,
      sw_version, sw_version_real, sw_checksum, sw_algorithm,
      voltage, protocol, protolang,
      (select v_id from temp)
  from
      verification
  where
      id = (select v_id from temp);")

(def copy-v-gso
  "Query to copy gso."
  "insert into v_gso
  select
      ?,
      gso_id
  from
      v_gso
  where
      v_id = ?;")

(def copy-v-refs
  "Query to copy refs."
  "insert into v_refs
  select
      ?,
      ref_id
  from
      v_refs
  where
      v_id = ?;")

(def copy-v-opt-refs
  "Query to copy refs."
  "insert into v_opt_refs
  select
      ?,
      ref_id
  from
      v_opt_refs
  where
      v_id = ?;")

(def copy-v-operations
  "Query to copy operations."
  "insert into v_operations
    (v_id, op_id)
  select
      ?,
      op_id
  from
      v_operations
  where
      v_id = ?;")

(def copy-measurements
  "Query to copy measurements."
  "insert into measurements
      (v_id, metrology_id, operation_id, ref_value)
  select
      ?,
      metrology_id,
      operation_id,
      ref_value
  from
      measurements
  where
      v_id = ?;")
