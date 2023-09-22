(def record (atom nil))
(def current (atom nil))
(def protocol (atom nil))

(load-icu)

(pprint (find-mi "СГОЭС-2"))

(pprint (find-methodology "58111"))

(pprint (find-counteragent "ОГПЗ"))

(reset! record (get-record 2220))

(pprint (reset! protocol (get-protocol-data 2220)))

;; Создать однотипные записи по массиву зав. №.
(map (fn [s] (copy-record! 1861))
     (range 2))

(let [nums (map (fn [n] (str "040" n))
                (list "091" "106" "117" "118"))
      start-id 2327
      start-protocol-number 2319]
  (map (fn [n i]
         (jdbc/update!
           midb
           :verification
           (hash-map
             :protocol nil
             :protolang nil
             :count "9/0029899"
             :counteragent 166
             :conditions 1015
             :serial_number n
             :manufacture_year 2020
             :protocol_number (+ start-protocol-number i)
             ;:comment "Леонтьев"
             ;:upload 1
             ;:channels 3
             ;:components "БД №№: 22878, 22335"
             ;:scope
             ;:sw_name 8320039
             ;:sw_version "не ниже V6.9"
             ;:sw_checksum "F8B9"
             ;:sw_algorithm "CRC-16"
             ;:sw_version_real "v7044"
             )
           ["id = ?" (+ start-id i)]))
       nums
       (range (count nums))))

(map (fn [id] (copy-v-gso! 2308 id))
     (range 2301 2327))

(copy-v-gso! 2123 2144)

;; Удалить записи с id >=
(map (fn [i]
         (delete-record! (+ 2308 i)))
     (range 19))

;; Удалить запись
(delete-record! 2305)

(pprint (get-conditions "2023-09-18"))

(insert-conditions! {:date "2023-09-14"
                     :temperature 20.3
                     :humidity 60.2
                     :pressure 100.25
                     :voltage 223.3
                     ;:other "расход ГС (0,1 - 0,3) л/мин."
                     :location "УЭСП"
                     ;:comment ""
                     })


(pprint (:verification @record))

;Установить ГСО по номерам паспортов ГСО.
(set-v-gso! 2243
            (map (fn [m]
                     (:id m))
                 (check-gso (list "08198-23")
                            "pass_number")))

(set-v-gso! 2300 
            (list 347))

(set-v-refs! 2276
             (list 2846 2820))

(delete-v-refs! 2260)

(set-v-opt-refs! 2243
                 (list 2831 2643 2670 2762 2756))

(set-v-operations! 2216
                   (list 60 608 1063 1672 1673))

;Проверить ГСО в записи.
(pprint (check-gso (map (fn [x] (:gso_id x))
                        (:v_gso (get-record 2215)))
                   "id"))

(pprint (check-gso (list "11101-23" "00808-23" "007465-22"
                     "02463-22" "06869-23" "00810-23")
                   "pass_number"))

;; Копировать ГСО, эталоны, операции и результаты измерений в несколько записей.
(for [f (list copy-v-refs! copy-v-operations! copy-measurements! copy-v-opt-refs!)
      n (range 10)]
      (f 2276 (+ 2277 n)))

(copy-v-operations! 4 8)

(map (fn [v] (copy-measurements! 2329 v))
     (range 2330 2228))

(copy-measurements! 2329 2330)

(pprint @record)

;; Создать запись о поверке.
(jdbc/insert!
  midb
  :verification
  (hash-map
     :engineer 3514
     :count "9/0029829"
     :counteragent 12
     :conditions 1012
     :verification_type 1
     :protocol_number 2238
     :mi_type "SGW CO0 NX"
     :methodology_id 280
     :serial_number 226556
     :manufacture_year 2014
     :channels 1
     :area "05"
     :interval 12
     ;:components
     ;:scope
     ;:sw_name 8320039
     ;:sw_version "не ниже V6.9"
     ;:sw_checksum "F8B9"
     ;:sw_algorithm "CRC-16"
     ;:sw_version_real "V3.04"
     ;:voltage nil
     ;:upload
     ;:comment "Леонтьев"
     ))

;;Добавить ГСО
(jdbc/insert!
  midb
  :gso
  {:type "ГСО"
    :available 1
    :document "паспорт"
    :number "10563-2015"
    :components "NH3+N2"
    :concentration 22.5
    :units "ppm"
    :uncertainity 0.3
    :pass_number "11874-23"
    :number_1c 1850 
    :manufacture_date "2023-08-11"
    :expiration_period 12
    :expiration_date "2024-08-10"
    :level 0
    :date "2023-09-05"
    :cylinder_number "6414"
    :volume 4
    :pressure 8
    ;:comment
  })

;; Просроченные эталоны
(pprint (filter (fn [m] (not= "" (:expiration m)))
        (all-refs 345)))

(pprint (all-refs 1861))

(map (fn [m]
         ({:number_1c (:number_1c m)
           :components (:components m)
           :expiration_date (:expiration_date m)}))
     (all-refs 2327))

(pprint (map (fn [m]
             (hash-map
              :number_1c (:number_1c m)
              :components (:components m)
              :expiration_date (:expiration_date m)))
         (all-refs 2327)))

(get-record (get-last-id "verification"))

(defn mc-ppm->mg
  [m]
  (if ( = "млн⁻¹" (:units m))
      (hash-map
        :units "мг/м³"
        :range_from)
      m))

(defn get-last
  []
  (let [m (:verification (get-record (get-last-id "verification")))]
    (apply hash-map (flatten (map (fn [k]
                               (list k (k m)))
                             (list :id :protocol_number))))))

(get-last)

;; Методика поверки
(jdbc/query midb [q/get-methodology "%19437%"])

;; Методику добавить
(jdbc/insert!
  midb
  :methodology
  {:registry_number "19437-11"
   :mi_name "Газоанализаторы кислорода электрохимические выдыхаемых газовых
             смесей к аппаратам ИН и ИВЛ"
   :mi_types "ГКМ-01-ИНСОВТ, ГКМ-02-ИНСОВТ"
   :name "Приложение 1 ИЮЕМ 941329.506 РЭ"
   :short_name "Приложение 1 ИЮЕМ 941329.506 РЭ"
   :date_from nil
   :date_to "2026-04-28"
   :temperature "20 ± 5"
   :humidity "65 ± 15"
   :pressure "101,3 ± 4"
   :voltage nil
   :frequency nil
   :other nil
   :limited nil})

;; Методика поверки - изменить запись
(jdbc/update!
  midb
  :methodology
  {:temperature "20 ± 5"
   :humidity "30 ÷ 80"
   :pressure "101.3 ± 4.0"}
  ["id = ?" 280])

;; Операции методики поверки
(jdbc/insert!
  midb
  :verification_operations
  (hash-map
    :methodology_id 280
    :section "6.3"
    :name "Подтверждение соответствия программного обеспечения"
    :verification_type 1
    :comment "См. в приложении к протоколу."
    ))

;; Измерения
(map (fn [ref]
         (jdbc/insert!
           midb
           :measurements
           (hash-map
             :v_id 2327
             :metrology_id 1133
             :operation_id 1165
             :ref_value ref
             )))
    (list 50))

(ch/ppm->mg "NH3" 95)

;; Каналы и МХ
(ins-channel!
  {:methodology_id 322
   :channel nil
   :component "NH3"
   :range_from 0
   :range_to 70.8 
   :units "мг/м³"
   :low_unit 0.1
   :view_range_from 0
   :view_range_to 150
   :comment "диапазон показаний условно!"}
  (list {:r_from 0
         :r_to 7.1
         :value 1.4
         :type_id 0
         :units nil
         :comment nil}
        {:r_from 7.1
         :r_to 70.8
         :value 20
         :type_id 1
         :units nil
         :comment nil}
        #_{:value 0.5
         :type_id 5
         :units ""}
        #_{:value 80
         :type_id 6
         :units "с"}))

;; Контрагенты изменение записи
(jdbc/update!
  midb
  :counteragents
  {:address "460028, Оренбургская область, город Оренбург, улица Заводская, 30"
   :name "ОАО «НЕФТЕМАСЛОЗАВОД»"
   :short_name "ОАО «НЕФТЕМАСЛОЗАВОД»"}
  ["id = ?" 4274])

;; Генерация протоколов поверки
(gen-protocols "id >= 2327 and id <= 2330")

;; Генерация результатов измерений
(gen-values! "id >= 2277 and id <= 2286")

(gen-values! "id >= 2327")

(pr/gen-value (get (vec (:measurements (first (get-protocols-data "id = 2220")))) 7))

;; Cars
;; Insert record
(do
  (jdbc/insert!
    auto
    :travel_order
    {:auto_id 1
     :count "9/0029905"
     :date_departure "2023-09-22T08:40"
     :date_arrive "2023-09-22T14:30"
     :odometr_departure 232818
     :fuel_departure 5.77
     :odometr_arrive 233097
     :fuel_add 40})
  (pprint
    (jdbc/query
      auto
      "select * from view_travel_order order by id desc limit 1;")))

(jdbc/query
  auto
  "select * from view_travel_order order by id desc limit 1;")

(get-report-data "id >= 2327")

(jdbc/query
  midb
  (str q/report-verifications "id >= 2327"))

(jdbc/query
  midb
  ["select * from view_v_measurements where id >= ? and id <= ?" 2327 2330])

(jdbc/query
  midb
  (str q/get-operations "v_op.v_id >= 2327"))

(jdbc/query
  midb
  (str "select * from verification_refs where " where))

;; documentations
(require '[clojure.repl :refer :all])

(require '[clojure.java.shell :refer [sh]])

;; Дата изменения скана протокола = дата поверки
(let [scan-path "/media/sf_Y_DRIVE/СКАНЫ РЕЗЕРВНОЕ КОПИРОВАНИЕ/2023/Ермолаев/"
      data (jdbc/query
             midb
             "select protocol_number, date
              from verification
              inner join conditions
                on conditions.id = verification.conditions
              where protocol_number >= 2319
                and protocol_number  <= 2322")]
  (map (fn [m]
           (sh
             "touch"
             (str scan-path
                  (str "9-61-" (:protocol_number m) "-2023.pdf"))
             "-mad"
             (str (:date m) "T17:30")))
       data))

(find-doc "assoc

(doc get-in)

(dir clojure.core)

