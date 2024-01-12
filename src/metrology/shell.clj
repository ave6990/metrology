;; #gs2000
(require '[metrology.lib.gs2000 :as gs])
(require '[clojure.java.shell :refer [sh]])

(pprint (gs2000 1
                "H2S"
                496
                (list 20 34)
                #_(map #(ch/ppm->mg "H2S" %1)
                     (list 7 30))))

((gs/calculator
  (gs/passports 1))
  "CH4"
  :N2
  7090
  150)

;; #chemistry
(* (ch/coefficient "SO2") 900)

(ch/ppm->mg "H2S" 90)

(ch/mg->ppm "C6H14" 3500)

(map #(/ (* %1 32.07) 62.14)
     '(4.9 7.8 40 70))

(map #(/ %1 4.4 0.01)
     '(2.18 4.22))

;; #report#methodology
(methodology (list 247))
(sh "vivaldi" (str midb-path "methodology.html"))

;; #find#methodology
(methodology
  (map (fn [m]
           (:id m))
       (find-methodology "Pac")))
(sh "vivaldi" (str midb-path "methodology.html"))

;; #find#record
(gen-report
  (find-verification
    "v.id = 1827
     --count like '%3007981%'"))
(sh "vivaldi" (str midb-path "report.html"))

;; #report#find#mi
(gen-report
  (find-verification
    "lower(v.mi_type) like '%ПГА%'
     --and channels = 4
     --and components like '%H2S%'
     --and components like '%7000%'
     --and v.comment not like 'Леонтьев'
     --met.registry_number like '%32633%'
     --v.protocol_number = 2124
     and v.serial_number like '%1835%'
     --and count like '%3007952%'"))
(sh "vivaldi" (str midb-path "report.html"))

;; #report
(gen-report (list 2515 2516))
(sh "vivaldi" (str midb-path "report.html"))

;; #report#protocols
(gen-protocols "id >= 3580")
(sh "vivaldi" (str midb-path "protocol.html"))

;; #gen#measurements#values
(gen-values! "id >= 3580")

;; #find#counteragents
(counteragents "лицо")
(sh "vivaldi" (str midb-path "counteragents.html"))

;; #copy#record
(map (fn [s] (copy-record! 1827))
     (range 1))

(let [nums (map (fn [n] (str "" n))
                (list "SH0424023")
                #_(range 18))
      start-id (next-id)
      start-protocol-number (next-protocol-number)]
      ;start-protocol-number 1]
  (map (fn [n i]
         (jdbc/update!
           midb
           :verification
           (hash-map
             ;:methodology_id 375
             ;:mi_type ""
             ;:components "CH4 (метан); H2S (сероводород)"
             ;:channels 3
             :count "9/0000017"
             :counteragent 42303
             :conditions 1123
             :manufacture_year nil
             ;:comment "Леонтьев"
             ;:comment 11
             ;:comment "ГИС блок 2"
             ;:upload 1
             ;:verification_type 2
             ;:scope
             ;:engineer 3514
             ;:sw_name "MXTF_11_000"
             ;:sw_version "не ниже v01.00" 
             ;:sw_checksum "8BFD"
             ;:sw_algorithm "CRC 16"
             ;:sw_version_real "2.29"
             :serial_number n
             :protocol_number (+ start-protocol-number i)
             :protocol nil
             :protolang nil
             )
           ["id = ?" (+ start-id i)]))
       nums
       (range (count nums))))

;; #delete#record
(delete-record! 3580)

;; Удалить записи с id >=
;; #delete#record
(map (fn [i]
         (delete-record! (+ 3560 i)))
     (range 20))

(map (fn [id] (copy-v-gso! 2337 id))
     (range 2301 2327))

;; #report#gso
(gso "lower(components) like '%%'
      and expiration_date > date('now')")
(sh "vivaldi" (str midb-path "gso.html"))

;; #report#refs
(references "mi_type like '%test%'")
(sh "vivaldi" (str midb-path "references.html"))

;; #set#gso
(set-v-gso!
  3588
  (list 379 381 388 249 322 352)
  #_(map (fn [m]
           (:id m))
       (check-gso (list "14632-23" "14633-23" "02464-23" "00810-23" "08197-23")
                  "pass_number")))

;; #update#gso
(jdbc/update!
  midb
  :v_gso
  {
   :gso_id 377
  }
  ["v_id = ? 
    and gso_id = ?" 3349 332])

;; #copy#gso
(copy-v-gso! 3495 3587)

;; #delete#gso
(delete-v-gso! 2343)

(set-v-gso! 2453
            (remove #{258 334} (get-v-gso 2386)))

(/ (- 94.3 95.1) 95.1)

;; #conditions
(conditions "2023-12-29")
(sh "vivaldi" (str midb-path "conditions.html"))

;; #add#conditions
(insert-conditions! {:date "2024-01-11"
                     :temperature 21.8
                     :humidity 52.0
                     :pressure 100.35
                     :voltage 220.0
                     :frequency 50
                     ;:other "0,4 (0,4 ± 0,1) дм³/мин"
                     ;:location "с. Ивановка"
                     ;:comment ""
                     })

;; #set#refs
(set-v-refs! 3553
             (list 2846 2820))

;; #copy#refs
(copy-v-refs! 3495 3587)

;; #delete#refs
(delete-v-refs! 2260)

(jdbc/insert!
  midb
  :v_opt_refs
  {:v_id 2343 :ref_id 2762})

;; #set#opt-refs
(set-v-opt-refs! 3560
                 (list 2827 2762 2756))

;; #copy#opt-refs
(copy-v-opt-refs! 3495 3587)

;; #add#operations
(jdbc/insert!
  midb
  :verification_operations
  {:methodology_id 382
   :section "6.1"
   :name ""
   :verification_type 1
   ;:comment "См. в приложении к протоколу"
   :info "для Микрохром 1121-3"
   })

;; #set#operations
(set-v-operations! 3587
                   (list 1804 1806 1807 1809 1810))

;; #copy#operations
(copy-v-operations! 3187 3210)

;; #unusability#update#operations
(unusability
  3577
  466
  "не включается (неисправен аккумулятор)")

;Проверить ГСО в записи.
(pprint (check-gso (map (fn [x] (:gso_id x))
                        (:v_gso (get-record 2215)))
                   "id"))

;; #find#references
(jdbc/query
  midb
  "select *
   from refs
   where
    mi_type like '%esto%'")

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
     :count "9/0000017"
     :counteragent 42303
     :conditions 1123
     :verification_type 1
     :protocol_number (next-protocol-number) 
     ;:protocol_number 3182
     :mi_type "Drager Pac, мод. Pac 5500"
     :methodology_id 381
     :serial_number "ERAK-1757"
     :manufacture_year 2009
     :channels 1
     :area "05"
     :interval 12
     :components "H₂S (сероводород)"
     ;:scope
     ;:sw_name "Pr.Gas3.hex"
     ;:sw_version "ПО 1.0"
     ;:sw_checksum "AFD3"
     ;:sw_algorithm "CRC-16"
     ;:sw_version_real "1.00"
     ;:voltage 24
     ;:upload 
     ;:comment "Леонтьев"
     ))

(jdbc/update!
  midb
  :verification
  {
    :components "Σ C₂-C₁₀ (0 - 100) % НКПР"
  }
  ["id = ?" 3174])

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
  {:registry_number "32633-09"
   :mi_name "Газоанализаторы портативные"
   :mi_types "Drager Pac, мод. Pac 3500, Pac 5500, Pac 7000"
   :name "МП-242-0356-2009 «Газоанализаторы Drager Pac. Модификации Pac 3500, Pac 5500, Pac 7000. Методика поверки»"
   :short_name "МП-242-0356-2009"
   :date_from nil
   :date_to "2014-07-01"
   :temperature "20 ± 5"
   :humidity "30 ÷ 80"
   :pressure "90,6 ÷ 104,8"
   ;:voltage "220 ± 4,4"
   ;:frequency "50 ± 1"
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
  3588
  (list [503 0 13.38 29.19 13.38 0 29.19]
        [508 nil]
        [1578 0 24.43 48.18 24.43 0 48.18]
        [1579 nil]
        [504 0 47.5] [505 250 475 250] [504 47.5] [505 475]
        [509 nil]
        [53 0 19] [54 50 95 50] [53 0] [54 95]
        [536 nil] 
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
(map (fn [v] (copy-measurements! 3495 v))
     (list 3587))

;; #delete#measurements
(delete-measurements! 3553)

(jdbc/delete!
  midb
  :measurements
  ["v_id >= ? and v_id <= ?" 2670 2689])


;; #add#metrology#channel
(ins-channel!
  {:methodology_id 247
   :channel nil 
   :component "CH4"
   :range_from 0
   :range_to 50
   :units "% НКПР"
   :low_unit 1
   :view_range_from 0
   :view_range_to 100
   :comment "диапазон показаний условно!"
   }
  (list {:r_from 0
         :r_to 50
         :value 5
         :fraction nil
         :type_id 0
         :units nil
         :operation_id 1128
         :comment nil}
        #_{:r_from 10
         :r_to 100
         :value 20
         :fraction nil
         :type_id 1
         :units nil
         :operation_id 1826
         ;:comment "(15 - 30) % об."
         }
        #_{:value 0.5
         :type_id 5
         :units ""
         :operation_id 1827}
        {;:r_from 0
         ;:r_to 10
         :value 25
         :type_id 6
         :units "с"
         :operation_id 1344}
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
         :operation_id 1767}
        #_{:value 30
         :type_id 16
         :units "с"
         :operation_id 1299
         :comment "время срабатывания защиты "}))

;; #add#counteragents
(jdbc/insert!
  midb
  :counteragents
  {:name "Зленко А. П."
   :short_name "Физ. лицо"
   :address nil
   :inn nil})

;; #update#counteragents
(jdbc/update!
  midb
  :counteragents
  {
   ;:name "ООО АТЦ «Жигули-Оренбург»"
   ;:short_name "ООО АТЦ «Жигули-Оренбург»"
   :address "461048, Оренбургская обл, Бузулук г, Заречная ул, дом № 6"
   }
  ["id = ?" 2305])

;; #cars
;; #insert#order#auto
(do
  (jdbc/insert!
    auto
    :travel_order
    {:auto_id 2
     :count "9/0000017"
     :date_departure "2024-01-11T11:00"
     :date_arrive "2024-01-11T13:30"
     :odometr_departure 138251
     :fuel_departure 13.68
     :odometr_arrive 138255
     :fuel_add 0})
  (pprint
    (jdbc/query
      auto
      "select * from view_travel_order order by id desc limit 1;")))

;; #backup
(sh "backup_midb")

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

(jdbc/insert!
  midb
  :v_html
  {:id 3210
   :html (string/join
          "\n"
          (list
            (li {:class "appendix-section"}
              "Место расположения комплекса: "
              (em "с. Ивановка."))
            (li {:class "appendix-section"}
              "Состав комплекса:"
              (br)
              (table
                (thead
                  (tr
                    (th "Наименование СИ: ")
                    (th "Тип СИ")
                    (th "Заводской номер")
                    (th "Дата поверки")))
                (tbody
                  (tr
                    (td "Газоанализатор")
                    (td "450i")
                    (td "1027044181")
                    (td "19.09.2023"))
                  #_(tr
                    (td "Газоанализатор")
                    (td "42i")
                    (td 1114748166)
                    (td "31.03.2023"))
                  #_(tr
                    (td "Газоанализатор")
                    (td "200E")
                    (td 1829)
                    (td "17.04.2023"))
                  #_(tr
                    (td "Газоанализатор")
                    (td "101E")
                    (td 1322)
                    (td "13.03.2023"))
                  (tr
                    (td "Газоанализатор")
                    (td "ГАММА-ЕТ")
                    (td 78)
                    (td "26.09.2023"))
                  #_(tr
                    (td "Газоанализатор")
                    (td "К-100")
                    (td "220-2-07")
                    (td "04.04.2023"))
                  (tr
                    (td "Станция автоматическая метеорологическая")
                    (td "Vantage Pro2")
                    (td "A110913P033")
                    (td "23.08.2023"))
                  (tr
                    (td "Блок детектирования")
                    (td "БДМГ-200")
                    (td 114)
                    (td "28.03.2023")))))
            (li
              "Определение метрологических характеристик: "
              (em "проведено по методикам поверки, в соответствии с описанием типа каждого СИ, в лабораторных условиях поэлементно."))))})

(custom-protocol
  3454
  (fp12))

(pprint
  (get-protocols-data
    "id = 3187"))

(require '[clojure.repl :refer :all])

(find-doc "air")

(doc get-in)

(dir metrology.lib.metrology)

(doc metr/air-v->vnc)

(metr/air-v->vnc 100 23.4 103.87)

(dir clojure.core)

(require '[metrology.view.report :as report] :reload)

(require '[metrology.lib.gs2000 :as gs] :reload)

(require '[metrology.lib.midb-queries :as q] :reload)

(require '[metrology.lib.gen-html :refer :all] :reload)

(require '[metrology.lib.protocol :as pr] :reload)
