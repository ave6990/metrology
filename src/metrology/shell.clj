;; #gs2000
(require '[metrology.lib.gs2000 :as gs])
(require '[clojure.java.shell :refer [sh]])

(pprint (gs2000 1
                "CH4"
                18170
                (list 150 285 650 3500 6650)
                #_(map #(ch/ppm->mg "H2S" %1)
                     (list 5 9.5 14 25 47))))

;; #chemistry
(* (ch/coefficient "C6H14") 900)

(ch/ppm->mg "H2S" 95)

(ch/mg->ppm "CO" 500)

(map #(/ (* %1 32.07) 62.14)
     '(4.9 7.8 40 70))

;; #report#methodology
(methodology (list 67))
(sh "vivaldi" (str midb-path "methodology.html"))

;; #find#methodology
(methodology
  (map (fn [m]
           (:id m))
       (find-methodology "23436")))
(sh "vivaldi" (str midb-path "methodology.html"))

;; #find#record
(gen-report
  (find-verification
    "v.id >= 3156"))
(sh "vivaldi" (str midb-path "report.html"))

;; #report#find#mi
(gen-report
  (find-verification
    "lower(v.mi_type) like '%КОМПАКТ%2%'
     --and components like '%ПФД%'
     --and channels = 3
     --and components like '%3000%'
     --and met.registry_number like '%-07%'
     --v.protocol_number = 2124
     --v.serial_number like '%412-1014930%'
     --count like '%29758%'"))
(sh "vivaldi" (str midb-path "report.html"))

;; #report
(gen-report (list 2515 2516))
(sh "vivaldi" (str midb-path "report.html"))

;; #report#protocols
(gen-protocols "id >= 3117")
(sh "vivaldi" (str midb-path "protocol.html"))

;; #gen#measurements#values
(gen-values! "id >= 3102")

;; #find#counteragents
(counteragents "ВЕКТОР")
(sh "vivaldi" (str midb-path "counteragents.html"))

;; #copy#record
(map (fn [s] (copy-record! 2306))
     (range 1))

(let [nums (map (fn [n] (str "" n))
                (list "SK0821027")
                #_(range 18))
      start-id (next-id)
      start-protocol-number (next-protocol-number)]
      ;start-protocol-number 3129]
  (map (fn [n i]
         (jdbc/update!
           midb
           :verification
           (hash-map
             ;:methodology_id 67 
             ;:mi_type "ОКА-92"
             ;:components "O₂ (кислород)"
             ;:channels 1
             :count "9/029934"
             :counteragent 10646
             :conditions 1072
             :manufacture_year 2021
             :comment "Леонтьев"
             ;:comment 11
             ;:comment "ГИС блок 2"
             :upload 1
             ;:verification_type 2
             ;:scope
             ;:engineer 3514
             ;:sw_name "Лидер 041-Main"
             ;:sw_version "не ниже V1.01"
             ;:sw_checksum "8BFD"
             ;:sw_algorithm "CRC 16"
             ;:sw_version_real "v2.29"
             :serial_number n
             :protocol_number (+ start-protocol-number i)
             :protocol nil
             :protolang nil
             )
           ["id = ?" (+ start-id i)]))
       nums
       (range (count nums))))

;; #delete#record
(delete-record! 3157)

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
(set-v-gso! 3156 (list ))

;; #set#gso#passport
(set-v-gso!
  3156
  (map (fn [m]
           (:id m))
       (check-gso (list "08197-23" "14633-23" "02464-22")
                  "pass_number")))

;; #copy#gso
(copy-v-gso! 2574 2579)

;; #delete#gso
(delete-v-gso! 2343)

(set-v-gso! 2453
            (remove #{258 334} (get-v-gso 2386)))

;; #conditions
(pprint (get-conditions "2023-11-23"))

;; #add#conditions
(insert-conditions! {:date "2023-11-24"
                     :temperature 22.7
                     :humidity 52.2
                     :pressure 99.24
                     :voltage 220.0
                     :frequency 50
                     ;:other "расход ГС (0,1 - 0,3) л/мин."
                     ;:location "ОГЗ"
                     ;:comment ""
                     })

;; #set#refs
(set-v-refs! 3149
             (list 2663 2820))

;; #copy#refs
(copy-v-refs! 2396 2399)

;; #delete#refs
(delete-v-refs! 2260)

(jdbc/insert!
  midb
  :v_opt_refs
  {:v_id 2343 :ref_id 2762})

;; #set#opt-refs
(set-v-opt-refs! 3149
                 (list 2643 2831 2670 2756))

;; #copy#opt-refs
(copy-v-opt-refs! 2337 2339)

;; #add#operations
(jdbc/insert!
  midb
  :verification_operations
  {:methodology_id 370
   :section "6.3.3"
   :name "Определение абсолютной погрешности измерения температуры масла"
   :verification_type 1 
   :comment "См. в приложении к протоколу"
   ;:info nil
   })

;; #set#operations
(set-v-operations! 3149
                   (list 264 538 822 878))

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
     :count "9/029705"
     :counteragent 255
     :conditions 1069
     :verification_type 1
     :protocol_number (next-protocol-number) 
     :mi_type "ЩИТ-2"
     :methodology_id 340
     :serial_number 1753
     :manufacture_year 1990
     :channels 1
     :area "05"
     :interval 12
     :components "блок датчика № 147"
     ;:scope
     ;:sw_name "Microsense 1.bin"
     ;:sw_version "1.1"
     ;:sw_checksum "8D36DF56"
     ;:sw_algorithm "CRC32"
     ;:sw_version_real "2.64"
     ;:voltage 24
     ;:upload
     :comment "Леонтьев"
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
  {:registry_number "17438-08"
   :mi_name "Газоанализаторы ИНФРАКАР"
   :mi_types "ИНФРАКАР-08, ИНФРАКАР-10, ИНФРАКАР-12, ИНФРАКАР-12Т"
   :name "МП 242-0770-2008 «Газоанализаторы ИНФРАКАР. Методика поверки"
   :short_name "МП 242-0770-2008"
   :date_from nil
   :date_to "2013-12-01"
   :temperature "20 ± 5"
   :humidity "65 ± 15"
   :pressure "101,3 ± 1,5"
   :voltage "220 (+ 10 / - 15)"
   :frequency nil
   :other "расход анализируемой газовой смеси (50 - 60) л/ч"
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

;; #add#measurements
(add-measurements
  3153
  (list [1451 0] [1451 10] [1451 18]
        [1451 10] [1451 0] [1451 18]
        [1452 20] [1452 150] [1452 260]
        [1452 150] [1452 20] [1452 260]
        ))

;; #update#measurements
(map (fn [id m]
         (jdbc/update!
           midb
           :measurements
           m
           ["id = ?" id]))
     (list 30123 30124 30125)
     (list {:ref_value 0}
           {:ref_value 1}
           {:ref_value 1.9}))

;; #copy#measurements
(map (fn [v] (copy-measurements! 2670 v))
     (range 2671 2689))

;; #delete#measurements
(delete-measurements! 3153)

(jdbc/delete!
  midb
  :measurements
  ["v_id >= ? and v_id <= ?" 2670 2689])


;; #add#metrology#channel
(ins-channel!
  {:methodology_id 67
   :channel nil
   :component "CH4"
   :range_from 0
   :range_to 0.5
   :units "% об."
   :low_unit 0.01
   :view_range_from 0
   :view_range_to 0.5
   :comment "диапазон показаний условно!"
   }
  (list {:r_from 0
         :r_to 0.2
         :value 25
         :fraction nil
         :type_id 2
         :units nil
         :operation_id 849
         :comment nil}
        {:r_from 0.2
         :r_to 0.5
         :value 25
         :fraction nil
         :type_id 1
         :units nil
         :operation_id 849
         :comment nil}
        {:value 0.5
         :type_id 5
         :units ""
         :operation_id 1047}
        {;:r_from 0
         ;:r_to 10
         :value 15
         :type_id 6
         :units "с"
         :operation_id 1721}
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
         :operation_id 878
         ;:comment "порог 11 % НКПР"
        }
        #_{:value 15
         :type_id 7
         :units "с"
         :operation_id 1719}
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
   :name "ООО АТЦ «Жигули-Оренбург»"
   :short_name "ООО АТЦ «Жигули-Оренбург»"
   :address "460507, Оренбургская обл., Оренбургский р-н, Пригородный п., шоссе 12 км автодороги Оренбург-Орск"
   }
  ["id = ?" 9135])

;; #cars
;; #insert#order#auto
(do
  (jdbc/insert!
    auto
    :travel_order
    {:auto_id 2
     :count "9/"
     :date_departure "2023-11-23T09:00"
     :date_arrive "2023-11-23T17:00"
     :odometr_departure 136410
     :fuel_departure 21.48
     :odometr_arrive 136709
     :fuel_add 15})
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

(require '[metrology.lib.gs2000 :as gs] :reload)

(require '[metrology.lib.midb-queries :as q] :reload)
