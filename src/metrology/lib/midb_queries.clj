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
    (v_id, op_id, result)
  select
      ?,
      op_id,
      1
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

(def find-mi
  "Query to select mi."
  "select
    v.id,
    v.protocol_number,
    v.count,
    c.date,
    v.verification_type,
    v.mi_type,
    met.registry_number,
    v.serial_number,
    v.manufacture_year,
    v.components,
    v.scope,
    v.channels,
    v.counteragent,
    ca.name
from
    verification as v
inner join
    methodology as met
    on met.id = v.methodology_id
inner join
    conditions as c
    on c.id = v.conditions
inner join
    counteragents as ca
    on ca.id = v.counteragent
where
    v.serial_number || ' '
        || met.registry_number || ' ' || v.mi_type like ?
order by
    c.date,
    v.id")

(def counteragents
  "Counteragents query."
  "select
      id, name, short_name, inn, type, address
  from
      counteragents
  where
      lower(id || ' ' || name || ' ' || short_name) like ?") 

(def all-refs
  ""
  "select
    *
from
    verification_refs
where
    v_id = ?")

(def metrology
  ""
  "select
      m.id,
      m.ref_value,
      met.value,
      met.r_from,
      met.r_to,
      met.type_id,
      ch.low_unit,
      ch.view_range_from,
      ch.view_range_to
  from
      measurements as m
  inner join
      metrology as met
      on met.id = m.metrology_id
  inner join
      channels as ch
      on ch.id = met.channel_id
  where v_id = ?
  ")

(def find-methodology
"select
  *
from
  methodology
where
  lower(id || ', ' || registry_number ||
      ', ' || mi_types) like ?")

(def report-verifications
"select
  v.id as id, v.count, v.verification_type, v.protocol_number, v.mi_type, v.serial_number,
  v.manufacture_year, v.channels, v.area, v.interval, v.components, v.scope,
  v.sw_name, v.sw_version, v.sw_checksum, v.sw_algorithm, v.sw_version_real, v.protocol,
  v.voltage as verification_voltage, v.sign_mi, v.sign_pass, v.upload, v.comment, v.copy_from,
  c.id as condition_id, c.date, c.temperature as real_temperature,
  c.humidity as real_humidity, c.pressure as real_pressure, c.voltage as real_voltage,
  c.frequency as real_frequence, c.other as real_other, c.location,
  m.id as methodology_id, m.registry_number, m.name as methodology_name,
  m.short_name as methodology_short_name, m.mi_name, m.date_from, m.date_to,
  m.temperature, m.humidity, m.pressure, m.voltage, m.frequency, m.other, m.limited,
  ca.id as counteragent_id, ca.name as counteragent_name,
  ca.short_name as counteragent_short_name, ca.address, ca.inn
from
  verification as v
inner join
  conditions as c
  on c.id = v.conditions
inner join
  methodology as m
  on m.id = v.methodology_id
inner join
  counteragents as ca
  on ca.id = v.counteragent
where
  v.id >= ? and v.id <= ?")

(def get-operations
"select
  op.id,
  op.methodology_id,
  op.section,
  op.name,
  op.verification_type,
  op.comment,
  op.info,
  v_op.v_id,
  v_op.result,
  v_op.unusability,
  v_op.comment as operation_comment
from
  verification_operations as op
left join v_operations as v_op
  on v_op.op_id = op.id
where
  v_op.v_id >= ?
  and v_op.v_id <= ?")
