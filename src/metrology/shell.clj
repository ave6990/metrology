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
(methodology (list 68))
(sh "vivaldi" (str midb-path "methodology.html"))

;; #find#methodology
(methodology
  (map (fn [m]
           (:id m))
       (find-methodology "Орт")))
(sh "vivaldi" (str midb-path "methodology.html"))

;; #find#record
(gen-report
  (find-verification
    "v.id = 3546
     --count like '%3007981%'"))
(sh "vivaldi" (str midb-path "report.html"))

;; #report#find#mi
(gen-report
  (find-verification
    "lower(v.mi_type) like '%64М%'
     --and channels = 3
     --and components like '%CH4%'
     --and components like '%O2%'
     --and v.comment not like 'Леонтьев'
     --and met.registry_number like '%-13%'
     --v.protocol_number = 2124
     --and v.serial_number like '%219-025786%'
     --and count like '%3007952%'"))
(sh "vivaldi" (str midb-path "report.html"))

;; #report
(gen-report (list 2515 2516))
(sh "vivaldi" (str midb-path "report.html"))

;; #report#protocols
(gen-protocols "id >= 3553")
(sh "vivaldi" (str midb-path "protocol.html"))

;; #gen#measurements#values
(gen-values! "id >= 3553")

;; #find#counteragents
(counteragents "ИНФОРМПЛА")
(sh "vivaldi" (str midb-path "counteragents.html"))

;; #copy#record
(map (fn [s] (copy-record! 3553))
     (range 6))

(let [nums (map (fn [n] (str "" n))
                (list 200015 200135 200132 200019 200139 200048)
                #_(range 18))
      start-id (next-id)
      start-protocol-number (next-protocol-number)]
      ;start-protocol-number 3129]
  (map (fn [n i]
         (jdbc/update!
           midb
           :verification
           (hash-map
             ;:methodology_id 375
             ;:mi_type ""
             ;:components "CH4 (метан); H2S (сероводород)"
             ;:channels 3
             :count "0922/0030"
             :counteragent 42321
             :conditions 1119
             :manufacture_year 2020
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
             ;:sw_version_real "2.27.0"
             :serial_number n
             :protocol_number (+ start-protocol-number i)
             :protocol nil
             :protolang nil
             )
           ["id = ?" (+ start-id i)]))
       nums
       (range (count nums))))

;; #delete#record
(delete-record! 3530)

;; Удалить записи с id >=
;; #delete#record
(map (fn [i]
         (delete-record! (+ 3530 i)))
     (range 10))

(map (fn [id] (copy-v-gso! 2337 id))
     (range 2301 2327))

;; #report#gso
(gso "lower(components) like '%%'
      and expiration_date > date('now')")
(sh "vivaldi" (str midb-path "gso.html"))

;; #set#gso
(set-v-gso!
  3553
  (list 388 286 385 382 379 381)
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
(copy-v-gso! 3255 3256)

;; #delete#gso
(delete-v-gso! 2343)

(set-v-gso! 2453
            (remove #{258 334} (get-v-gso 2386)))

(/ (- 94.3 95.1) 95.1)

;; #conditions
(conditions "2023-12-26")
(sh "vivaldi" (str midb-path "conditions.html"))

;; #add#conditions
(insert-conditions! {:date "2023-12-28"
                     :temperature 21.1
                     :humidity 52.4
                     :pressure 98.30
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
(copy-v-refs! 3187 3197)

;; #delete#refs
(delete-v-refs! 2260)

(jdbc/insert!
  midb
  :v_opt_refs
  {:v_id 2343 :ref_id 2762})

;; #set#opt-refs
(set-v-opt-refs! 3476
                 (list 2643 2831 2762 2756))

;; #copy#opt-refs
(copy-v-opt-refs! 2337 2339)

;; #add#operations
(jdbc/insert!
  midb
  :verification_operations
  {:methodology_id 380
   :section "7.6"
   :name "Контроль правильности выполнения измерений"
   :verification_type 1
   :comment "См. в приложении к протоколу"
   ;:info "только для газовых моделей"
   })

;; #set#operations
(set-v-operations! 3476
                   (list 1764 1765 1766))

;; #copy#operations
(copy-v-operations! 3187 3210)

;; #unusability#update#operations
(unusability
  3549
  589
  "превышение предела допускаемой основной погрешности")

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
     :count "0922/0030"
     :counteragent 42321
     :conditions 1119
     :verification_type 1
     :protocol_number (next-protocol-number) 
     ;:protocol_number 3182
     :mi_type "Орт-02"
     :methodology_id 377
     :serial_number "3776"
     :manufacture_year nil
     :channels 5
     :area "05"
     :interval 12
     :components "ПИ.005 зав.№ 3856"
     ;:scope
     :sw_name "Pr.Gas3.hex"
     :sw_version "ПО 1.0"
     ;:sw_checksum "AFD3"
     ;:sw_algorithm "CRC-16"
     :sw_version_real "1.00"
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
  {:registry_number "8436-81"
   :mi_name "Хроматографы промышленные"
   :mi_types "«НЕФТЕХИМ-СКЭП»"
   :name "«Хроматограф промышленный «НЕФТЕХИМ-СКЭП». Методические указания по поверке» ЗП2.840.000 Д"
   :short_name "ЗП2.840.000 Д"
   :date_from nil
   :date_to nil
   :temperature "10 ÷ 35"
   ;:humidity "30 ÷ 80"
   :pressure "95,7 ÷ 106,4"
   :voltage "220 [+22 / -33]"
   :frequency "50 ± 1"
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
  3549
  (list [1136 0 5 13.38 29.19 13.38 5 0 29.19]
        [1137 nil]
        [638 0 0.15] [639 0.25 0.45 0.25] [638 0.15 0]
        [639 0.45] 
        [641 nil]
        [1115 0 15]
        [1116 50 90 50]
        [1115 15 0]
        [1116 90]
        [1118 nil]
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
(delete-measurements! 3553)

(jdbc/delete!
  midb
  :measurements
  ["v_id >= ? and v_id <= ?" 2670 2689])


;; #add#metrology#channel
(ins-channel!
  {:methodology_id 377
   :channel nil 
   :component "CH4"
   :range_from 0
   :range_to 60
   :units "% НКПР"
   :low_unit 0.1
   :view_range_from 0
   :view_range_to 100
   ;:comment "диапазон показаний условно!"
   }
  (list {:r_from 0
         :r_to 60
         :value 5
         :fraction nil
         :type_id 0
         :units nil
         :operation_id 1766
         :comment nil}
        #_{:r_from 100
         :r_to 3500
         :value 15
         :fraction nil
         :type_id 1
         :units nil
         :operation_id 1763
         ;:comment "(15 - 30) % об."
         }
        {:value 0.5
         :type_id 5
         :units ""
         :operation_id nil}
        #_{;:r_from 0
         ;:r_to 10
         :value 60
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
         :operation_id 878
         ;:comment "порог 11 % НКПР"
        }
        {:value 15
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
  {:name "ОБЩЕСТВО С ОГРАНИЧЕННОЙ ОТВЕТСТВЕННОСТЬЮ «ИССЛЕДОВАТЕЛЬСКИЙ ЦЕНТР ГАЗИНФОРМПЛАСТ»"
   :short_name "ООО «ИЦ ГАЗИНФОРМПЛАСТ»"
   :address "634003, Томская область, г Томск, пер Телевизионный, дом 3, строение 1"
   :inn "7017253595"})

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
    {:auto_id 1
     :count "9/3008021"
     :date_departure "2023-12-25T09:00"
     :date_arrive "2023-12-25T14:30"
     :odometr_departure 238148
     :fuel_departure 14.05
     :odometr_arrive 238250
     :fuel_add 30})
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
