;; ГС-2000
(require '[metrology.lib.gs2000 :as gs])

(pprint (gs2000 1 "NO2" 24.3 (list 1.5 10 18)))

(ch/ppm->mg "NO2" 24.3)

(/ 1.077 4.4)

(def record (atom nil))
(def current (atom nil))
(def protocol (atom nil))

(gso "lower(components) like 'h2s%'
      and expiration_date > date('now')")

(methodology (list 68))

;; Найти запись о поверке
(gen-report
  (find-verification
    "v.id >= 2359 and v.id <= 2360"))

;; Найти СИ
(gen-report
  (find-verification
    "lower(v.mi_type) like '%М 02%'"))

;; Генерация отчета о поверке
(gen-report (list 2359))

;; Генерация протоколов поверки
(gen-protocols "id >= 2359 and id <= 2360")

;; Генерация результатов измерений
(gen-values! "id >= 2359")

(pprint (find-methodology "СГОЭС-М"))

(pprint (find-counteragent "огистический"))

(reset! record (get-record 2220))

;; Создать однотипные записи по массиву зав. №.
(map (fn [s] (copy-record! 2190))
     (range 1))

(let [nums (map (fn [n] (str "" n))
                (list "1910489"))
      start-id 2360
      start-protocol-number 2357]
  (map (fn [n i]
         (jdbc/update!
           midb
           :verification
           (hash-map
             :protocol nil
             :protolang nil
             :count "9/0029623"
             :counteragent 50
             :conditions 1013
             ;:mi_type "ОКА-Т-NH₃-NO₂-CO₂-И11(6)"
             :serial_number n
             :manufacture_year 2020
             :protocol_number (+ start-protocol-number i)
             ;:comment "Леонтьев"
             ;:comment 11
             ;:upload 1
             ;:channels 1
             ;:components "датчик Хоббит-ТВ зав. № 672"
             ;:scope
             ;:sw_name "Mag6sc.txt"
             ;:sw_version "не ниже 1.00"
             ;:sw_checksum "f62bb67c59102cee9bbe35e996178c37d53a7aa96f248694a2ff91fe542afb44"
             ;:sw_algorithm "ГОСТ Р 34.11-94"
             ;:sw_version_real "v1.64"
             )
           ["id = ?" (+ start-id i)]))
       nums
       (range (count nums))))

(map (fn [id] (copy-v-gso! 2337 id))
     (range 2301 2327))

(copy-v-gso! 2123 2144)

(delete-v-gso! 2343)

(set-v-gso! 2360 (list 237 278 284 332 347))

;; Удалить записи с id >=
(map (fn [i]
         (delete-record! (+ 2308 i)))
     (range 19))

;; Удалить запись
(delete-record! 2333)

(pprint (get-conditions "2023-09-14"))

(insert-conditions! {:date "2023-09-25"
                     :temperature 23.3
                     :humidity 50.6
                     :pressure 101.85
                     :voltage 222.8
                     ;:other "расход ГС (0,1 - 0,3) л/мин."
                     ;:location "УЭСП"
                     ;:comment ""
                     })


(pprint (:verification @record))

;Установить ГСО по номерам паспортов ГСО.
(set-v-gso! 2333
            (map (fn [m]
                     (:id m))
                 (check-gso (list "11636-23" "11637-23")
                            "pass_number")))

(set-v-gso! 2331 
            (list 278 285 349 332 334 258))

(set-v-refs! 2332
             (list 923))

(delete-v-refs! 2260)

(jdbc/insert!
  midb
  :v_opt_refs
  {:v_id 2343 :ref_id 2762})

(set-v-opt-refs! 2332
                 (list 2756 2762))

(set-v-operations! 2216
                   (list 60 608 1063 1672 1673))

(jdbc/update!
  midb
  :v_operations
  {:result -1
   :unusability "ошибка Е3 по каналу измерения O₂"}
  ["v_id = ? and op_id = ?" 2360 813])

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

(copy-v-refs! 2337 2339)

(copy-v-opt-refs! 2337 2339)

(copy-v-operations! 4 8)

(map (fn [v] (copy-measurements! 2329 v))
     (range 2330 2228))

(copy-measurements! 2337 2339)

(delete-measurements! 2359)

(pprint @record)

;; Создать запись о поверке.
(jdbc/insert!
  midb
  :verification
  (hash-map
     :engineer 3514
     :count "9/0029904"
     :counteragent 273 
     :conditions 1019
     :verification_type 1
     :protocol_number 2339
     :mi_type "СГОЭС-М11 метан"
     :methodology_id 310
     :serial_number 3727
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
     :voltage 24
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

(get-last-id "verification")

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

(get-last-id "verification")

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
  {:temperature "15 ÷ 30"
   ;:humidity "30 ÷ 80"
   ;:pressure "101.3 ± 4.0"
   }
  ["id = ?" 339])

;; Операции методики поверки
(jdbc/insert!
  midb
  :verification_operations
  (hash-map
    :methodology_id 339
    :section "4.4"
    :name "Проверка предела допускаемой основной абсолютной погрешности"
    :verification_type 1
    :comment "См. в приложении к протоколу."
    ))

;; Операции поверки
(map (fn [n]
         (jdbc/insert!
           midb
           :v_operations
           (hash-map
             :v_id 2339
             :op_id n
             :result 1)))
     (list 236 510 1541))

;; Измерения
(map (fn [ref]
         (jdbc/insert!
           midb
           :measurements
           (hash-map
             :v_id 2359
             :metrology_id (ref 0)
             :ref_value (ref 1)
             )))
    (list [1143 0] [1143 15.17] [1144 49.41] [1144 89.88]
          [1144 49.41] [1143 15.17] [1143 0] [1144 89.88]
          [1146 nil]
          [1152 0] [1152 1.52] [1153 10.01] [1153 18]
          [1153 10.01] [1152 1.52] [1152 0] [1153 18]
          [1155 nil]
          [1156 0] [1156 0.35] [1157 2.5] [1157 4.75]
          [1157 2.5] [1156 0.35] [1156 0] [1157 4.75]
          [1159 nil]))

;; Изменить измерения
(map (fn [id m]
         (jdbc/update!
           midb
           :measurements
           m
           ["id = ?" id]))
     (list 20916 20922 20928 20934)
     (list {:value_2 0.04}
           {:value_2 0.485}
           {:value_2 0.046}
           {:value_2 0.503}))

(ch/ppm->mg "NH3" 95)

(ch/mg->ppm "CO2" 27000)

;; Каналы и МХ
(ins-channel!
  {:methodology_id 68
   :channel nil
   :component "CO2"
   :range_from 0
   :range_to 5
   :units "% об."
   :low_unit 0.01
   :view_range_from 0
   :view_range_to 7
   :comment "диапазон показаний условно!"}
  (list {:r_from 0
         :r_to 2
         :value 25 
         :type_id 2
         :units nil
         :operation_id 589
         :comment nil}
        {:r_from 2
         :r_to 5
         :value 25
         :type_id 1
         :units nil
         :operation_id 589
         :comment nil}
        {:value 0.5
         :type_id 5
         :units ""
         :operation_id 850}
        {:value 120
         :type_id 6
         :units "с"
         :operation_id 1048}))

;; Контрагенты
(jdbc/insert!
  midb
  :counteragents
  {:name "ФЕДЕРАЛЬНОЕ ГОСУДАРСТВЕННОЕ КАЗЕННОЕ УЧРЕЖДЕНИЕ «ЛОГИСТИЧЕСКИЙ КОМПЛЕКС № 29»"
   :short_name "ФГКУ «ЛОГИСТИЧЕСКИЙ КОМПЛЕКС № 29»"
   :address "461504, Оренбургская область, р-н Соль-Илецкий, г. Соль-Илецк, ул. Вокзальная, дом 125"
   :inn "5646007850"})

(jdbc/update!
  midb
  :counteragents
  {:address "460028, Оренбургская область, город Оренбург, улица Заводская, 30"
   :name "ОАО «НЕФТЕМАСЛОЗАВОД»"
   :short_name "ОАО «НЕФТЕМАСЛОЗАВОД»"}
  ["id = ?" 4274])

;; Cars
;; Insert record
(do
  (jdbc/insert!
    auto
    :travel_order
    {:auto_id 1
     :count "9/0030005"
     :date_departure "2023-09-27T09:00"
     :date_arrive "2023-09-27T13:30"
     :odometr_departure 233148
     :fuel_departure 15.41
     :odometr_arrive 233248
     :fuel_add 15})
  (pprint
    (jdbc/query
      auto
      "select * from view_travel_order order by id desc limit 1;")))

(jdbc/query
  auto
  "select * from view_travel_order order by id desc limit 1;")

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

(require '[clojure.repl :refer :all])

(find-doc "assoc")

(doc get-in)

(doc assoc)

(dir clojure.core)

