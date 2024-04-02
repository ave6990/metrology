(ns metrology.db.queries
  (:require
    [clojure.string :as string]))

(def get-verification-records-count
"select
  count(*) as count
from
    verification as v
inner join
    conditions as c
    on c.id = v.conditions
inner join
    counteragents as ca
    on ca.id = v.counteragent
{where}
{group-by}")

(def get-verifications
"select *
from
(select
  v.id, v.engineer, v.count, v.counteragent, v.conditions,
  v.verification_type as v_type,
  v.protocol_number, v.mi_type, v.methodology_id, v.serial_number,
  v.manufacture_year as year, v.channels, v.area, v.interval, v.components,
  v.scope, v.sw_name, v.sw_version, v.sw_checksum, v.sw_algorithm, v.protocol,
  v.protolang, v.voltage as v_voltage, v.other_conditions as v_other_conditions
  , v.upload, v.comment, v.copy_from,
  v.hash_refs, c.date, c.temperature, c.humidity, c.pressure, c.voltage, c.frequency,
  c.other, c.location, c.comment as c_comment, ca.id as ca_id,
  ca.name as ca_name, ca.short_name as ca_short_name, ca.address, ca.inn,
  ca.type as ca_type, met.registry_number, met.name as met_name,
  met.short_name as met_short_name, met.approved, met.date_from as met_date_from,
  met.date_to as met_date_to, met.temperature as met_temperature,
  met.humidity as met_humidity, met.pressure as met_pressure,
  met.voltage as met_voltage, met.frequency as met_frequency,
  met.other as met_other, met.limited
from
    verification as v
left join
    conditions as c
    on c.id = v.conditions
inner join
    counteragents as ca
    on ca.id = v.counteragent
inner join
    methodology as met
    on met.id = v.methodology_id
order by
  c.date desc,
  v.id desc)
{where}
{group-by}
limit {limit} offset {offset}")

(def get-condition-records-count
"select
  count(*) as count
from
  conditions
{where}")

(def get-conditions
"select
  *
from
  conditions
{where}
order by
  id desc
limit {limit} offset {offset}")

(def get-gso-records-count
"select
  count(*) as count
from
  gso
{where}")

(def get-gso
"select
  *
from
  gso
{where}
order by
  id desc
limit {limit} offset {offset}")

(def get-counteragent-records-count
"select
  count(*) as count
from
  counteragents
{where}")

(def get-counteragents
"select
  *
from
  counteragents
{where}
limit {limit} offset {offset}")

(def get-reference-records-count
"select
  count(*) as count
from
  view_refs_use_count
{where}")

(def get-references
"select
  *
from
  view_refs_use_count
{where}
limit {limit} offset {offset}")
