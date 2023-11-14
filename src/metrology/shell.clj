;; #gs2000
(require '[metrology.lib.gs2000 :as gs])
(require '[clojure.java.shell :refer [sh]])

(pprint (gs2000 1
                "H2S"
                496
                (list 50 85)
                #_(map #(ch/ppm->mg "C6H14" %1)
                     (list 500 900))))

;; #chemistry
(* (ch/coefficient "C6H14") 900)

(ch/ppm->mg "H2S" 95)

(ch/mg->ppm "CO" 500)

(map #(/ (* %1 32.07) 62.14)
     '(4.9 7.8 40 70))

;; #report#methodology
(methodology (list 332))
(sh "vivaldi" (str midb-path "methodology.html"))

;; #find#methodology
(pprint (find-methodology "Лидер"))

;; #find#record
(gen-report
  (find-verification
    "v.id >= 3004"))
(sh "vivaldi" (str midb-path "report.html"))

(/ 2.12 4.4)

;; #find#mi
(gen-report
  (find-verification
    "lower(v.mi_type) like '%414%'
     and components like '%CO2%'
     --and channels = 5
     --and components like '%ch4%7000%'
     --and met.registry_number like '%-16%'
     --v.protocol_number = 2124
     --v.serial_number like '%412-1014930%'
     --count like '%29871%'"))
(sh "vivaldi" (str midb-path "report.html"))

;; #report
(gen-report (list 2515 2516))
(sh "vivaldi" (str midb-path "report.html"))

;; #report#protocols
(gen-protocols "id >= 3002")
(sh "vivaldi" (str midb-path "protocol.html"))

;; #gen#measurements#values
(gen-values! "id >= 3002")

;; #find#counteragents
(counteragents "ОРЕНБУРГНЕФ")
(sh "vivaldi" (str midb-path "counteragents.html"))

;; #copy#record
(map (fn [s] (copy-record! 3005))
     (range 26))

(let [nums (map (fn [n] (str "ER414214" n))
                (list 280 306 278 335)
                #_(range 18))
      start-id (next-id)
      start-protocol-number (next-protocol-number)]
      ;start-protocol-number 2995]
  (map (fn [n i]
         (jdbc/update!
           midb
           :verification
           (hash-map
             ;:methodology_id 305
             ;:mi_type "Лидер, мод. Лидер 041"
             ;:components "БД зав. №№: 349, 350, 351, 352, 330"
             ;:channels 3
             :count "9/029720"
             :counteragent 212
             :conditions 1061
             :manufacture_year 2021
             ;:comment "Леонтьев"
             ;:comment 11
             ;:comment "ГИС блок 2"
             ;:upload 1
             ;:scope
             ;:sw_name "Лидер 041-Main"
             ;:sw_version "не ниже V1.01"
             ;:sw_checksum "8BFD"
             ;:sw_algorithm "CRC 16"
             ;:sw_version_real "V1.04"
             :serial_number n
             :protocol_number (+ start-protocol-number i)
             :protocol nil
             :protolang nil
             )
           ["id = ?" (+ start-id i)]))
       nums
       (range (count nums))))

;; #delete#record
(delete-record! 3002)

;; Удалить записи с id >=
;; #delete#record
(map (fn [i]
         (delete-record! (+ 2308 i)))
     (range 19))

(map (fn [id] (copy-v-gso! 2337 id))
     (range 2301 2327))

;; #report#gso
(gso "lower(components) like '%%'
      and expiration_date > date('now')")
(sh "vivaldi" (str midb-path "gso.html"))

;; #set#gso
(set-v-gso! 3002 (list 349 387 285 379 381 382))

;; #set#gso#passport
(set-v-gso!
  3005
  (map (fn [m]
           (:id m))
       (check-gso (list "08199-23" "00810-23" "11100-23"
                        "12210-22" "12197-22" "02462-22"
                        "02464-22" "08197-23")
                  "pass_number")))

;; #copy#gso
(copy-v-gso! 2574 2579)

;; #delete#gso
(delete-v-gso! 2343)

(set-v-gso! 2453
            (remove #{258 334} (get-v-gso 2386)))

;; #conditions
(pprint (get-conditions "2023-11-10"))

;; #add#condigions
(insert-conditions! {:date "2023-11-13"
                     :temperature 21.6
                     :humidity 52.6
                     :pressure 101.56
                     :voltage 222.2
                     :frequency 50
                     ;:other "расход ГС (0,1 - 0,3) л/мин."
                     ;:location "Алексеевское ЛПУ"
                     ;:comment ""
                     })

;; #set#refs
(set-v-refs! 3003
             (list 2663 2846 2820))

;; #copy#refs
(copy-v-refs! 2396 2399)

;; #delete#refs
(delete-v-refs! 2260)

(jdbc/insert!
  midb
  :v_opt_refs
  {:v_id 2343 :ref_id 2762})

;; #set#opt-refs
(set-v-opt-refs! 3000
                 (list 2643 2762 2831 2717 2670))

;; #copy#opt-refs
(copy-v-opt-refs! 2337 2339)

;; #set#operations
(set-v-operations! 3000
                   (list 209 483 757 1703))

;; #copy#operations
(copy-v-operations! 4 8)

;; #update#operations
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

;; Копировать ГСО, эталоны, операции и результаты измерений в несколько записей.
(for [f (list copy-v-refs! copy-v-operations! copy-measurements! copy-v-opt-refs!)
      n (range 10)]
      (f 2276 (+ 2277 n)))

;; #add#verification
(jdbc/insert!
  midb
  :verification
  (hash-map
     :engineer 3514
     :count "9/029981"
     :counteragent 57
     :conditions 1060
     :verification_type 1
     :protocol_number (next-protocol-number) 
     :mi_type "ССС-903, мод. ССС-903МЕ"
     :methodology_id 278
     :serial_number "9305"
     :manufacture_year 2015
     :channels 1
     :area "05"
     :interval 12
     :components "ПГЭ-903-сероводород-10"
     ;:scope
     ;:sw_name "Microsense 1.bin"
     ;:sw_version "1.1"
     ;:sw_checksum "8D36DF56"
     ;:sw_algorithm "CRC32"
     ;:sw_version_real "2.64"
     :voltage 24
     ;:upload
     ;:comment "КЦ"
     ))

;; #add#gso
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

;; #find#last#id
(last-id "verification")

(let [data (get-verification (get-last-id "verification"))]
  (map (fn [k]
         (k data))
     (list :id :protocol_number)))

;; #add#methodology
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

;; #update#methodology
(jdbc/update!
  midb
  :methodology
  {:temperature "15 ÷ 30"
   ;:humidity "30 ÷ 80"
   ;:pressure "101.3 ± 4.0"
   }
  ["id = ?" 339])

;; #add#verification-operations
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

;; #add#v-operations
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

;; #add#measurements
(add-measurements
  3000
  (list [1389 0] [1389 2.04] [1390 7.94]
        ))

;; #update#measurements
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

;; #copy#measurements
(map (fn [v] (copy-measurements! 2670 v))
     (range 2671 2689))

;; #delete#measurements
(delete-measurements! 2983)

(jdbc/delete!
  midb
  :measurements
  ["v_id >= ? and v_id <= ?" 2670 2689])


;; #add#metrology#channel
(ins-channel!
  {:methodology_id 332
   :channel nil
   :component "ЛОС (по C6H14)"
   :range_from 0
   :range_to 4000
   :units "мг/м³"
   :low_unit 1
   :view_range_from 0
   :view_range_to 4000
   ;:comment "диапазон показаний условно!"
   }
  (list {:r_from 0
         :r_to 300
         :value 15
         :fraction nil
         :type_id 2
         :units nil
         :operation_id 1171
         :comment nil}
        {:r_from 300
         :r_to 4000
         :value 15
         :fraction nil
         :type_id 1
         :units nil
         :operation_id 1171
         :comment nil}
        #_{:value 0.5
         :type_id 5
         :units ""
         :operation_id nil}
        {;:r_from 0
         ;:r_to 10
         :value 15
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

;; #add#counteragents
(jdbc/insert!
  midb
  :counteragents
  {:name "ОБЩЕСТВО С ОГРАНИЧЕННОЙ ОТВЕТСТВЕННОСТЬЮ «ЭНКИСТРОЙМОНТАЖ»"
   :short_name "ООО «ЭСМ»"
   :address "460021, Оренбургская область, город Оренбург, Красногорская ул., д. 72/1"
   :inn "5610227756"})

;; #update#counteragents
(jdbc/update!
  midb
  :counteragents
  {
   :name "ООО «ГАЗТЕХНОЛОГИЯ»"
   ;:short_name "ФИЛИАЛ ООО «ГАЗПРОМ ПХГ» «СОВХОЗНОЕ УПХГ»"
   :address "460000, ГОРОД ОРЕНБУРГ, УЛИЦА ПУШКИНСКАЯ, 35"
   }
  ["id = ?" 8665])

;; #cars
;; #insert#order#auto
(do
  (jdbc/insert!
    auto
    :travel_order
    {:auto_id 1
     :count "9/029981"
     :date_departure "2023-11-10T09:30"
     :date_arrive "2023-11-10T12:00"
     :odometr_departure 235844
     :fuel_departure 14.20
     :odometr_arrive 235878
     :fuel_add 0})
  (pprint
    (jdbc/query
      auto
      "select * from view_travel_order order by id desc limit 1;")))

;; #documentations
(require '[clojure.repl :refer :all])

(require '[clojure.java.shell :refer [sh]])

;; Дата изменения скана протокола = дата поверки
;; #set#date#scans
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

;; #air#volume
(metr/air-vnc->v 100 23.1 101.14)

(metr/air-v->vnc 106 23.1 101.14)

(require '[clojure.repl :refer :all])

(find-doc "assoc")

(doc get-in)

(dir clojure.math)

(doc string/split)

(dir clojure.core)

(require '[metrology.view.report :as report] :reload)

(require '[metrology.lib.midb-queries :as q] :reload)
