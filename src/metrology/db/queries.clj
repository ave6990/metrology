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
{where}")

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
limit {limit} offset {offset}")

(def get-verifications
"select
    v.id
    , v.upload
    , v.count
    , v.counteragent as ca
    , ca.short_name
    , v.conditions
    , c.date
    , v.verification_type as v_type
    , v.interval
    , v.protocol_number
    , v.mi_type
    , v.methodology_id as meth_id
    , v.serial_number
    , v.manufacture_year as year
    , v.channels
    , v.components
    , v.scope
    , v.area
    , v.sw_name
    , v.sw_version
    , v.sw_version_real
    , v.sw_checksum
    , v.sw_algorithm
    , v.voltage
    , v.other_conditions as other
    , v.sign_mi
    , v.sign_pass
    , v.protocol
    , v.copy_from
    , v.comment
from
    verification as v
inner join
    conditions as c
    on c.id = v.conditions
inner join
    counteragents as ca
    on ca.id = v.counteragent
{where}
order by
  v.id desc
limit {limit} offset {offset}")
