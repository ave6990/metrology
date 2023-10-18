;; ГС-2000
(require '[metrology.lib.gs2000 :as gs])

(pprint (gs2000 1
                #_"H2S"
                8950
                (list 30 250 475)
                #_(map #(ch/ppm->mg "H2S" %1)
                     (list 5.8 29))))

(ch/ppm->mg "CH4" 2200)

(def record (atom nil))
(def current (atom nil))
(def protocol (atom nil))

(gso "lower(components) like '%%'
      and expiration_date > date('now')")

(/ 4.22 4.4)

(methodology (list 320))

(pprint (find-methodology "Микросенс"))

(jdbc/update!
  midb
  :methodology
  {:short_name "ИБЯЛ.413412.005МП"
   :date_to "2015-08-01"}
  ["id = ?" 218])

;; Найти запись о поверке
(gen-report
  (find-verification
    "v.id >= 2554 and v.id <= 2600"))

;; Найти СИ
(gen-report
  (find-verification
    "lower(v.mi_type) like '%Хоббит%'
     --and components like '%co %'
     --and channels = 3
     --and components like '%co %'
     --and met.registry_number like '%68988%'
     --v.protocol_number = 2385
     --count like '%0922/0004%'"))

;; Генерация отчета о поверке
(gen-report (list 2515 2516))

;; Генерация протоколов поверки
(gen-protocols "id >= 2566")

(* 10.4 1.42)

;; Генерация результатов измерений
(gen-values! "id >= 2566")

(pprint (find-counteragent "СТМ-С"))

(reset! record (get-verification (get-last-id "verification")))

(get-v-operations 2380)

;; Создать однотипные записи по массиву зав. №.
(map (fn [s] (copy-record! 2232))
     (range 1))

(let [nums (map (fn [n] (str "" n))
                (list "2009464"))
      start-id 2575
      start-protocol-number 2567]
  (map (fn [n i]
         (jdbc/update!
           midb
           :verification
           (hash-map
             :protocol nil
             :protolang nil
             :count "9/0029985"
             :counteragent 358
             :conditions 1041
             ;:methodology_id 305
             :mi_type "ХОББИТ-Т-C₃H₈-И11(20)"
             :serial_number n
             :manufacture_year 2020
             :protocol_number (+ start-protocol-number i)
             ;:comment "Леонтьев"
             ;:comment 11
             ;:upload 1
             :channels 1
             ;:components "H₂S (сероводород)"
             ;:scope
             :sw_name "Plow_max_04"
             :sw_version "40 16 00 02"
             :sw_checksum "8BFD"
             :sw_algorithm "CRC 16"
             :sw_version_real "40 16 00 02"
             )
           ["id = ?" (+ start-id i)]))
       nums
       (range (count nums))))

(map (fn [id] (copy-v-gso! 2337 id))
     (range 2301 2327))

(copy-v-gso! 2396 2399)

(delete-v-gso! 2343)

(set-v-gso! 2569 (list 347))

;; Удалить записи с id >=
(map (fn [i]
         (delete-record! (+ 2308 i)))
     (range 19))

;; Удалить запись
(delete-record! 2554)

(pprint (get-conditions "2023-10-04"))

(insert-conditions! {:date "2023-10-18"
                     :temperature 23.3
                     :humidity 51.7
                     :pressure 100.44
                     :voltage 222.4
                     :frequency 50
                     ;:other "расход ГС (0,1 - 0,3) л/мин."
                     ;:location "УЭСП"
                     ;:comment ""
                     })

(pprint (:verification @record))

;Установить ГСО по номерам паспортов ГСО.
(set-v-gso! 2433
            (map (fn [m]
                     (:id m))
                 (check-gso (list "02031-23" "00810-23" "11100-23"
                                  "12210-22" "12197-22" "02462-22"
                                  "02464-22" "08197-23")
                            "pass_number")))

(set-v-gso! 2480 
            (list 359))

(set-v-gso! 2453
            (remove #{258 334} (get-v-gso 2386)))

(set-v-refs! 2569
             (list 2846 2820))

(delete-v-refs! 2260)

(jdbc/insert!
  midb
  :v_opt_refs
  {:v_id 2343 :ref_id 2762})

(set-v-opt-refs! 2565
                 (list 2643 2831 2762 2670 2756))

(set-v-operations! 2565
                   (list 229 203 777 1158 1697))

(jdbc/update!
  midb
  :v_operations
  {:result -1
   :unusability "ошибка канала измерения O₂"}
  ["v_id = ? and op_id = ?" 2574 525])

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

(copy-v-refs! 2396 2399)

(copy-v-opt-refs! 2337 2339)

(copy-v-operations! 4 8)

(map (fn [v] (copy-measurements! 2329 v))
     (range 2330 2228))

(copy-measurements! 2396 2399)

(delete-measurements! 2565)

(jdbc/delete!
  midb
  :measurements
  ["id >= ?" 21927])

(pprint @record)

;; Создать запись о поверке.
(jdbc/insert!
  midb
  :verification
  (hash-map
     :engineer 3514
     :count "9/0029873"
     :counteragent 174
     :conditions 1029
     :verification_type 1
     :protocol_number 2557
     :mi_type "Микросенс М2"
     :methodology_id 328
     :serial_number "017834"
     :manufacture_year 2018
     :channels 4
     :area "05"
     :interval 12
     :components "O₂ (кислород); CH₄ (метан); H₂S (сероводород); CO (оксид углерода)"
     ;:scope
     :sw_name "Microsense 1.bin"
     :sw_version "1.1"
     :sw_checksum "8D36DF56"
     :sw_algorithm "CRC32"
     :sw_version_real "2.64"
     ;:voltage 14
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
    :number "10506-2014"
    :components "O2+N2"
    :concentration "13.38"
    :units "%"
    :uncertainity "0.11" 
    :pass_number "14633-23"
    :number_1c 1919 
    :manufacture_date "2023-09-13"
    :expiration_period 12
    :expiration_date "2025-09-12"
    :level 1
    :date "2023-10-10"
    :cylinder_number "01259"
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
    :methodology_id 218
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
  2569
  (list [377 0] [377 2.1] [378 49.98] [378 94.97]
        [378 49.98] [377 2.1] [377 0] [378 94.97]
        ))

(map #(/ %1 4.4)
     (list 0 1.1 2.1 2.3 3.3 4.22))

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
  {:methodology_id 328
   :channel nil
   :component "CH₄"
   :range_from 0
   :range_to 100
   :units "% НКПР"
   :low_unit 0.1
   :view_range_from 0
   :view_range_to 100
   ;:comment "диапазон показаний условно!"
   }
  (list {:r_from 0
         :r_to 60
         :value 3
         :fraction nil 
         :type_id 0
         :units nil
         :operation_id 1169
         :comment nil}
        {:r_from 60
         :r_to 100
         :value 5
         :fraction nil
         :type_id 1
         :units nil
         :operation_id 1169
         :comment nil}
        {:value 0.5
         :type_id 5
         :units ""
         :operation_id 1285}
        {;:r_from 0
         ;:r_to 10
         :value 60
         :type_id 6
         :units "с"
         :operation_id 1696}
        #_{
         :r_from 0
         :r_to 71 
         :value 60
         :type_id 6
         :units "с"
         :operation_id nil}
        #_{:value 1 
         :type_id 12
         :units nil
         :operation_id 1694
         ;:comment "порог 0.81 % об."
        }
        #_{:value 15
         :type_id 7
         :units "с"
         :operation_id 1695}
        #_{:value 30
         :type_id 16
         :units "с"
         :operation_id 1299
         :comment "время срабатывания защиты "}))

;; Контрагенты
(jdbc/insert!
  midb
  :counteragents
  {:name "Общество с ограниченной ответственностью «АВТОКОНТИНЕНТ»"
   :short_name "ООО «АВТОКОНТИНЕНТ»"
   :address "460048, Оренбургская обл., г. Оренбург, Автоматики проезд, д. 30, лит Е5, оф 12"
   :inn "5609193774"})

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
    {:auto_id 4
     :count "9/029619"
     :date_departure "2023-10-11T09:00"
     :date_arrive "2023-10-04T14:00"
     :odometr_departure 136358
     :fuel_departure 34.53
     :odometr_arrive 136458
     :fuel_add 0})
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

(defn control-points
  ([rng points]
   (let [s-rng (rng 0)
         e-rng (rng 1)]
    (map (fn [p]
             (+ s-rng (* (- e-rng s-rng) (double (/ p 100)))))
         points)))
  ([v-range]
   (control-points v-range [5 50 95])))

(control-points [10 100])

(require '[clojure.repl :refer :all])

(find-doc "assoc")

(doc get-in)

(dir clojure.math)

(doc string/split)

(dir clojure.core)

