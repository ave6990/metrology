;; #gs2000
(require '[metrology.lib.gs2000 :as gs])
(require '[clojure.java.shell :refer [sh]])

(pprint (gs2000 1
                "H2S"
                496
                #_(list 40 300 550)
                (map #(ch/ppm->mg "H2S" %1)
                     (list 7 30))))

(* 6.25 (- 16.92 4))

(- 50.3 38.3)

((gs/calculator
  (gs/passports 1))
  "CH4"
  :N2
  7090
  150)

;; #chemistry
(* (ch/coefficient "C6H14") 900)

(ch/ppm->mg "H2S" 90)

(ch/mg->ppm "C6H14" 3500)

(map #(/ (* %1 32.07) 62.14)
     '(4.9 7.8 40 70))

(map #(/ %1 4.4 0.01)
     '(2.18 4.22))

;; #report#methodology
(methodology (list 376))
(sh "vivaldi" (str midb-path "methodology.html"))

;; #find#methodology
(methodology
  (map (fn [m]
           (:id m))
       (find-methodology "АНКАТ-64")))
(sh "vivaldi" (str midb-path "methodology.html"))

;; #find#record
(gen-report
  (find-verification
    "v.id >= 3403
     --count like '%3007981%'"))
(sh "vivaldi" (str midb-path "report.html"))

;; #report#find#mi
(gen-report
  (find-verification
    "lower(v.mi_type) like '%Solaris%'
     --and channels = 1
     --and components like '%CH4%'
     --and components like '%O2%'
     --and v.comment not like 'Леонтьев'
     --and met.registry_number like '%-86%'
     --v.protocol_number = 2124
     --and v.serial_number like '%219-025786%'
     --and count like '%3007952%'"))
(sh "vivaldi" (str midb-path "report.html"))

;; #report
(gen-report (list 2515 2516))
(sh "vivaldi" (str midb-path "report.html"))

;; #report#protocols
(gen-protocols "id >= 3470")
(sh "vivaldi" (str midb-path "protocol.html"))

;; #gen#measurements#values
(gen-values! "id >= 3447")

;; #find#counteragents
(counteragents "В/Ч")
(sh "vivaldi" (str midb-path "counteragents.html"))

;; #copy#record
(map (fn [s] (copy-record! 3349))
     (range 3))

(let [nums (map (fn [n] (str "A5-" n))
                (list 58402 57818 58431)
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
             ;:mi_type "ПГА, исп. ПГА-48"
             ;:components "CO₂ (диоксид углерода); H₂ (водород)"
             ;:channels 2
             :count "9/3008029"
             :counteragent 16
             :conditions 1114
             :manufacture_year 2007
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
             ;:sw_version_real "5.15"
             :serial_number n
             :protocol_number (+ start-protocol-number i)
             :protocol nil
             :protolang nil
             )
           ["id = ?" (+ start-id i)]))
       nums
       (range (count nums))))

;; #delete#record
(delete-record! 3457)

;; Удалить записи с id >=
;; #delete#record
(map (fn [i]
         (delete-record! (+ 3422 i)))
     (range 4))

(map (fn [id] (copy-v-gso! 2337 id))
     (range 2301 2327))

;; #report#gso
(gso "lower(components) like '%%'
      and expiration_date > date('now')")
(sh "vivaldi" (str midb-path "gso.html"))

;; #set#gso
(set-v-gso!
  3457
  #_(list 382 387 286)
  (map (fn [m]
           (:id m))
       (check-gso (list "14632-23" "14633-23" "02464-23" "00810-23" "08198-23" "00804-23")
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
(conditions "2023-12-20")
(sh "vivaldi" (str midb-path "conditions.html"))

;; #add#conditions
(insert-conditions! {:date "2023-12-21"
                     :temperature 21.1
                     :humidity 53.9
                     :pressure 100.86
                     :voltage 220.0
                     :frequency 50
                     :other "0,4 (0,4 ± 0,1) дм³/мин"
                     ;:location "с. Ивановка"
                     ;:comment ""
                     })

;; #set#refs
(set-v-refs! 3457
             (list 2663 2820))

;; #copy#refs
(copy-v-refs! 3187 3197)

;; #delete#refs
(delete-v-refs! 2260)

(jdbc/insert!
  midb
  :v_opt_refs
  {:v_id 2343 :ref_id 2762})

;; #set#opt-refs
(set-v-opt-refs! 3457
                 (list 2643 2831 2762 2756))

;; #copy#opt-refs
(copy-v-opt-refs! 2337 2339)

;; #add#operations
(jdbc/insert!
  midb
  :verification_operations
  {:methodology_id 376
   :section "10.1"
   :name "Определение основной погрешности газоанализатора"
   :verification_type 1
   :comment "См. в приложении к протоколу"
   ;:info nil
   })

;; #set#operations
(set-v-operations! 3457
                   (list 1759 1760 1761 1763))

;; #copy#operations
(copy-v-operations! 3187 3210)

;; #unusability#update#operations
(unusability
  3472
  646
  "не включается")

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
     :count "9/3007986"
     :counteragent 2
     :conditions 1114
     :verification_type 1
     :protocol_number (next-protocol-number) 
     ;:protocol_number 3182
     :mi_type "АНКАТ-64М3.2, мод. АНКАТ-64М3.2-32"
     :methodology_id 376
     :serial_number "220732"
     :manufacture_year 2022
     :channels 5
     :area "05"
     :interval 12
     :components "ТХ(М-100); ЭХ(O2-30); ЭХ(CO-500); ЭХ(H2S-100);ФИ(Нефть)"
     ;:scope
     :sw_name "ANKAT-64M3.2"
     :sw_version "3.00"
     :sw_checksum "AFD3"
     :sw_algorithm "CRC-16"
     :sw_version_real "3.00"
     ;:voltage 24
     ;:upload 
     :comment "Леонтьев"
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
  {:registry_number "86024-22"
   :mi_name "Газоанализаторы"
   :mi_types "АНКАТ-64М3.2"
   :name "МП-230/11-2020 «ГСИ. Газоанализаторы АНКАТ-64М3.2. Методика поверки»"
   :short_name "МП-230/11-2020"
   :date_from nil
   :date_to "2027-07-07"
   :temperature "20 ± 5"
   :humidity "60 ± 15"
   :pressure "101,3 ± 4,0"
   ;:voltage "220 ± 22"
   ;:frequency "(50 ± 1)"
   :other nil
   :limited 1})

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
  3457
  (list [1553 0 48] [1554 90] [1553 48]
        [1557 0 13.38 29.19 13.38 0 29.19]
        [1560 0] [1561 250 475 250] [1560 0] [1561 475]
        [1564 0] [1565 50 85 50] [1564 0] [1565 85]
        [1568 0 285] [1569 1750 3325]
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
(delete-measurements! 3457)

(jdbc/delete!
  midb
  :measurements
  ["v_id >= ? and v_id <= ?" 2670 2689])


;; #add#metrology#channel
(ins-channel!
  {:methodology_id 376
   :channel "ФИ(Нефть)"
   :component "C6H14"
   :range_from 0
   :range_to 3500
   :units "мг/м³"
   :low_unit 1
   :view_range_from 0
   :view_range_to 4000
   ;:comment "диапазон показаний условно!"
   }
  (list {:r_from 0
         :r_to 100
         :value 45
         :fraction nil
         :type_id 0
         :units nil
         :operation_id 1763
         :comment nil}
        {:r_from 100
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
        {;:r_from 0
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
        #_{:value 10
         :type_id 7
         :units "с"
         :operation_id 1757}
        #_{:value 30
         :type_id 16
         :units "с"
         :operation_id 1299
         :comment "время срабатывания защиты "}))

;; #add#counteragents
(jdbc/insert!
  midb
  :counteragents
  {:name "ФИЛИАЛ АО «ГАЗПРОМ ГАЗОРАСПРЕДЕЛЕНИЕ ОРЕНБУРГ» В Г. МЕДНОГОРСКЕ (МЕДНОГОРСКМЕЖРАЙГАЗ)"
   :short_name "МЕДНОГОРСКМЕЖРАЙГАЗ"
   :address "462250, Оренбургская область, город Медногорск, ул. Кирова, д. 10"
   :inn "5610010369"})

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
     :count "9/3007981"
     :date_departure "2023-12-08T10:00"
     :date_arrive "2023-12-08T13:30"
     :odometr_departure 137964
     :fuel_departure 12.97
     :odometr_arrive 137984
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
