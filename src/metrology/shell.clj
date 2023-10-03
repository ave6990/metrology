;; ГС-2000
(require '[metrology.lib.gs2000 :as gs])

(pprint (gs2000 1 "H2S" 2020 (list 70.84 134.6)))

(ch/ppm->mg "NO2" 24.3)

(/ 1.077 4.4)

(def record (atom nil))
(def current (atom nil))
(def protocol (atom nil))

(gso "lower(components) like '%'
      and expiration_date > date('now')")

(methodology (list 331))

;; Найти запись о поверке
(gen-report
  (find-verification
    "v.id >= 2380 and v.id <= 2385"))

;; Найти СИ
(gen-report
  (find-verification
    "lower(v.mi_type) like '%ГХ-М%'"))

;; Генерация отчета о поверке
(gen-report (list 2381))

;; Генерация протоколов поверки
(gen-protocols "id >= 2380")

;; Генерация результатов измерений
(gen-values! "id >= 2381 and id <= 2381")

(pprint (find-methodology "СГГ-20М"))

(pprint (find-counteragent "СОВХОЗНОЕ"))

(reset! record (get-verification (get-last-id "verification")))

(get-v-operations 2380)

;; Создать однотипные записи по массиву зав. №.
(map (fn [s] (copy-record! 2384))
     (range 1))

(let [nums (map (fn [n] (str "19" n))
                (list 5097 4996))
      start-id 2386
      start-protocol-number 2339]
  (map (fn [n i]
         (jdbc/update!
           midb
           :verification
           (hash-map
             :protocol nil
             :protolang nil
             :count "9/0029683"
             :counteragent 171
             :conditions 1025
             ;:mi_type "АНКАТ-64М3-01"
             :serial_number n
             :manufacture_year 2019
             :protocol_number (+ start-protocol-number i)
             :comment "Леонтьев"
             ;:comment 11
             ;:upload 1
             ;:channels 4
             ;:components "O₂ (кислород); CH₄ (метан); CO (оксид углерода); H₂S (сероводород)"
             ;:scope
             ;:sw_name "Mag6sc.txt"
             ;:sw_version "не ниже 1.00"
             ;:sw_checksum "f62bb67c59102cee9bbe35e996178c37d53a7aa96f248694a2ff91fe542afb44"
             ;:sw_algorithm "ГОСТ Р 34.11-94"
             ;:sw_version_real "v4.21"
             )
           ["id = ?" (+ start-id i)]))
       nums
       (range (count nums))))

(map (fn [id] (copy-v-gso! 2337 id))
     (range 2301 2327))

(copy-v-gso! 2123 2144)

(delete-v-gso! 2343)

(set-v-gso! 2384 (list 320 365))

;; Удалить записи с id >=
(map (fn [i]
         (delete-record! (+ 2308 i)))
     (range 19))

;; Удалить запись
(delete-record! 2333)

(pprint (get-conditions "2023-09-28"))

(insert-conditions! {:date "2023-10-02"
                     :temperature 23.1
                     :humidity 51.9
                     :pressure 100.86
                     :voltage 224.4
                     :frequency 50
                     ;:other "расход ГС (0,1 - 0,3) л/мин."
                     ;:location "УЭСП"
                     ;:comment ""
                     })


(pprint (:verification @record))

;Установить ГСО по номерам паспортов ГСО.
(set-v-gso! 2381
            (map (fn [m]
                     (:id m))
                 (check-gso (list "11101-23" "00808-23"
                                  "14307-21" "02464-22"
                                  "00810-23" "08198-23")
                            "pass_number")))

(set-v-gso! 2331 
            (list 278 285 349 332 334 258))

(set-v-refs! 2381
             (list 2663 2820))

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

(delete-measurements! 2370)

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
    :number "10510-2014"
    :components "C3H8+N2+He"
    :concentration "0.302+0.206+"
    :units "ppm"
    :uncertainity "0.005+0.003" 
    :pass_number "02029-23"
    :number_1c 1687 
    :manufacture_date "2023-03-10"
    :expiration_period 24
    :expiration_date "2025-03-09"
    :level 1
    :date "2023-03-27"
    :cylinder_number "02990"
    :volume 4
    :pressure 7.5
    ;:comment
  })

(pprint (all-refs 1861))

(get-last-id "verification")

(let [data (get-verification (get-last-id "verification"))]
  (map (fn [k]
         (k data))
     (list :id :protocol_number)))

(defn mc-ppm->mg
  [m]
  (if ( = "млн⁻¹" (:units m))
      (hash-map
        :units "мг/м³"
        :range_from)
      m))

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

(defn add-measurements
  [id coll]
  (map (fn [ref]
         (jdbc/insert!
           midb
           :measurements
           (hash-map
             :v_id id
             :metrology_id (ref 0)
             :ref_value (ref 1)
             )))
       coll))

;; Измерения
(add-measurements
  2381
  (list [1162 0] [1162 24.48] [1162 48.18]
        [1162 24.48] [1162 0] [1162 48.18]
        [1176 0] [1176 13.42] [1176 29.19]
        [1176 13.42] [1176 0] [1176 29.19]
        [1169 1.2] [1170 100] [1170 190]
        [1170 100] [1170 1.2] [1170 190]
        [1172 0] [1173 20] [1173 34]
        [1173 20] [1172 0] [1173 34]))

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

(ch/ppm->mg "H2S" 95)

(ch/mg->ppm "CO" 500)

;; Каналы и МХ
(ins-channel!
  {:methodology_id 331
   :channel nil
   :component "O2"
   :range_from 0
   :range_to 30
   :units "% об."
   :low_unit 0.1
   :view_range_from 0
   :view_range_to 45
   ;:comment "диапазон показаний условно!"
   }
  (list {:r_from 0
         :r_to 30
         :value 0.9
         :fraction nil
         :type_id 0
         :units nil
         :operation_id 1485
         :comment nil}
        #_{:r_from 10
         :r_to 40
         :value nil
         :fraction 0.25
         :type_id 0
         :units nil
         :operation_id 1485
         :comment nil}
        {:value 0.5
         :type_id 5
         :units ""
         :operation_id nil}
        {:value 15
         :type_id 6
         :units "с"
         :operation_id nil}))

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
  {
   :name "ФИЛИАЛ ООО «ГАЗПРОМ ПХГ» «СОВХОЗНОЕ УПХГ»"
   :short_name "ФИЛИАЛ ООО «ГАЗПРОМ ПХГ» «СОВХОЗНОЕ УПХГ»"
   ;:address "460028, Оренбургская область, город Оренбург, улица Заводская, 30"
   }
  ["id = ?" 171])

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

(doc get)

(dir clojure.core)

