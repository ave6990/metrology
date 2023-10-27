;; ГС-2000
(require '[metrology.lib.gs2000 :as gs])

(pprint (gs2000 1
                "CO"
                8950
                (list 500 862)
                #_(map #(ch/ppm->mg "CO" %1)
                     (list 430 740))))

(map #(/ (* %1 32.07) 62.14)
     '(4.9 7.8 40 70))

(for [x (range 3)
      y (range 3)]
  (println (str x "x" y)))

(ch/ppm->mg "CH4" 9660)

(ch/mg->ppm "CH4" 6650)

(def record (atom nil))
(def current (atom nil))
(def protocol (atom nil))

(gso "lower(components) like '%%'
      and expiration_date > date('now')")

(methodology (list 247))

(pprint (find-methodology "ДАТ"))

;; Найти запись о поверке
(gen-report
  (find-verification
    "v.id >= 2713 and v.id <= 2800"))

;; Найти СИ
(gen-report
  (find-verification
    "lower(v.mi_type) like '%АМ-5Е%'
     --and components like '%H2S%'
     --and channels = 3
     --and components like '%ch4%7000%'
     --and met.registry_number like '%24051-02%'
     --v.protocol_number = 2124
     --v.serial_number like '%412-1014930%'
     --count like '%29871%'"))

;; Генерация отчета о поверке
(gen-report (list 2515 2516))

;; Генерация протоколов поверки
(gen-protocols "id >= 2749")

;; Генерация результатов измерений
(gen-values! "id >= 2749 and id <= 2800")

(pprint (find-counteragent "ОРМЕТ"))

(reset! record (get-verification (get-last-id "verification")))

(get-v-operations 2380)

;; Создать однотипные записи по массиву зав. №.
(map (fn [s] (copy-record! 2709))
     (range 4))

(let [nums (map (fn [n] (str "" n))
                (list 783 782 784))
      start-id 2753
      start-protocol-number 2745]
  (map (fn [n i]
         (jdbc/update!
           midb
           :verification
           (hash-map
             :protocol nil
             :protolang nil
             :count "9/029719"
             :counteragent 161
             :conditions 1046
             ;:methodology_id 305
             ;:mi_type "М 02, исп. М 02-01"
             :serial_number n
             :manufacture_year 2020
             :protocol_number (+ start-protocol-number i)
             ;:comment "Леонтьев"
             ;:comment 11
             ;:upload 1
             ;:channels 1
             ;:components "H₂S (сероводород)"
             ;:scope
             ;:sw_name "Лидер 04-Main"
             ;:sw_version "не ниже V3.00"
             ;:sw_checksum "8BFD"
             ;:sw_algorithm "CRC 16"
             ;:sw_version_real "V3.04"
             )
           ["id = ?" (+ start-id i)]))
       nums
       (range (count nums))))

(map (fn [id] (copy-v-gso! 2337 id))
     (range 2301 2327))

(copy-v-gso! 2574 2579)

(delete-v-gso! 2343)

(set-v-gso! 2749 (list 387 285 379 381 322 382))

;; Удалить записи с id >=
(map (fn [i]
         (delete-record! (+ 2308 i)))
     (range 19))

;; Удалить запись
(delete-record! 2697)

(pprint (get-conditions "2023-10-25"))

(insert-conditions! {:date "2023-10-25"
                     :temperature 22.1
                     :humidity 51.4
                     :pressure 99.00
                     :voltage 223.3
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

(set-v-refs! 2749
             (list 2846 2820))

(delete-v-refs! 2260)

(jdbc/insert!
  midb
  :v_opt_refs
  {:v_id 2343 :ref_id 2762})

(set-v-opt-refs! 2710
                 (list 2643 2831 2762 2670 2756 2717))

(set-v-operations! 2710
                   (list 80 354 628 880 1072))

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

(map (fn [v] (copy-measurements! 2670 v))
     (range 2671 2689))

(copy-measurements! 2396 2399)

(delete-measurements! 2748)

(jdbc/delete!
  midb
  :measurements
  ["v_id >= ? and v_id <= ?" 2670 2689])

(pprint @record)

;; Создать запись о поверке.
(jdbc/insert!
  midb
  :verification
  (hash-map
     :engineer 3514
     :count "9/0029949"
     :counteragent 225
     :conditions 1028
     :verification_type 1
     :protocol_number 2702
     :mi_type "ДАТ"
     :methodology_id 116
     :serial_number "1487"
     :manufacture_year 2006
     :channels 1
     :area "05"
     :interval 12
     ;:components "O₂ (кислород); CH₄ (метан); H₂S (сероводород); CO (оксид углерода)"
     ;:scope
     ;:sw_name "Microsense 1.bin"
     ;:sw_version "1.1"
     ;:sw_checksum "8D36DF56"
     ;:sw_algorithm "CRC32"
     ;:sw_version_real "2.64"
     :voltage 13
     ;:upload
     :comment "Леонтьев"
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
  2748
  (list [1253 0] [1253 7.47] [1254 44.93] [1254 83.04]
        [1254 44.93] [1253 7.47] [1253 0] [1254 83.04]
        ))

(/ 4.22 4.4)

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
  {:methodology_id 322
   :channel nil
   :component "C2H5SH"
   :range_from 0
   :range_to 36.2
   :units "млн⁻¹"
   :low_unit 0.1
   :view_range_from 0
   :view_range_to 36.2
   :comment "диапазон показаний условно!"
   }
  (list {:r_from 0
         :r_to 36.2
         :value 1.03
         :fraction nil
         :type_id 0
         :units nil
         :operation_id 1165
         :comment nil}
        #_{:r_from 15
         :r_to 200
         :value 20
         :fraction nil
         :type_id 1
         :units nil
         :operation_id 768
         :comment nil}
        {:value 0.5
         :type_id 5
         :units ""
         :operation_id nil}
        {;:r_from 0
         ;:r_to 10
         :value 90
         :type_id 6
         :units "с"
         :operation_id nil}
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
         :operation_id 1072
         :comment "порог 11 % НКПР"
        }
        #_{:value 15
         :type_id 7
         :units "с"
         :operation_id nil}
        #_{:value 30
         :type_id 16
         :units "с"
         :operation_id 1299
         :comment "время срабатывания защиты "}))

;; Контрагенты
(jdbc/insert!
  midb
  :counteragents
  {:name "ОБЩЕСТВО С ОГРАНИЧЕННОЙ ОТВЕТСТВЕННОСТЬЮ «ЭНКИСТРОЙМОНТАЖ»"
   :short_name "ООО «ЭСМ»"
   :address "460021, Оренбургская область, город Оренбург, Красногорская ул., д. 72/1"
   :inn "5610227756"})

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
     :count "9/029838"
     :date_departure "2023-10-25T12:30"
     :date_arrive "2023-10-25T14:30"
     :odometr_departure 234545
     :fuel_departure 15.01
     :odometr_arrive 234549
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
  "Расчитывает значения опорных точек 5, 50 и 95 %
   (или заданных вектором `points) диапазона
   измерений заданного вектором `rng"
  ([rng points]
   (let [s-rng (rng 0)
         e-rng (rng 1)]
    (map (fn [p]
             (+ s-rng (* (- e-rng s-rng) (double (/ p 100)))))
         points)))
  ([rng]
   (control-points rng [5 50 95])))

(control-points [10 100])

(metr/air-vnc->v 100 23.1 101.14)

(metr/air-v->vnc 106 23.1 101.14)

(require '[clojure.repl :refer :all])

(find-doc "assoc")

(doc get-in)

(dir clojure.math)

(doc string/split)

(dir clojure.core)

