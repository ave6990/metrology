;; #gs2000
(require '[metrology.lib.gs2000 :as gs])
(require '[clojure.java.shell :refer [sh]])

(pprint (gs2000 1
                "NH3"
                3017
                (list 7 35 67)
                #_(map #(ch/ppm->mg "H2S" %1)
                     (list 35 60))))

((gs/calculator
  (gs/passports 1))
  "CH4"
  :N2
  7090
  150)

;; #chemistry
(ch/coefficient "C6H14")

(ch/ppm->mg "NH3" 10)

(* 7 0.05)

(ch/mg->ppm "C6H14" 3500)

(map #(/ (* %1 32.07) 62.14)
     '(4.9 7.8 40 70))

(map #(/ %1 1.7 0.01)
     '(0.857 1.556))

;; #report#methodology
(methodology (list 313))
(sh "vivaldi" (str midb-path "methodology.html"))

;; #find#methodology
(methodology
  (map (fn [m]
           (:id m))
       (find-methodology "millen")))
(sh "vivaldi" (str midb-path "methodology.html"))

;; #report#find#mi
(gen-report
  (find-verification
    "lower(v.mi_type) like '%millen%'
     --and lower(v.mi_type) not like '%elgas%'
     --and channels = 2
     --and components like '%nh3%'
     --and components like '%7000%'
     --and v.comment not like 'Леонтьев'
     --and met.registry_number like '%-88%'
     --v.protocol_number = 1293 and v.protocol_number = 1295
     --and v.serial_number like '%418-1231797%'
     --v.serial_number like '%042800%'
     --count like '%000119%'
     --v.id = 1322 or v.id = 1320"))
(sh "vivaldi" (str midb-path "report.html"))

;; #report
(gen-report (list 3822))
(sh "vivaldi" (str midb-path "report.html"))

;; #report#protocols
(gen-protocols "id >= 3847")
(sh "vivaldi" (str midb-path "protocol.html"))

;; #gen#measurements#values
(gen-values! "id >= 3847")

;; #find#counteragents
(counteragents "УЭСП")
(sh "vivaldi" (str midb-path "counteragents.html"))

;; #copy#record
(copy-record! 3865 1)

(let [nums (map (fn [n] (str "" n))
                (list "0090698"))
      years (repeat (count nums) 2019)
            #_(list 2019 2016 2019 2016 2020)
      start-id (next-id)
      start-protocol-number (next-protocol-number)]
      ;start-protocol-number 176]
  (map (fn [n i y]
         (jdbc/update!
           midb
           :verification
           (hash-map
             ;:methodology_id 375
             ;:mi_type "Колион-1В-03"
             ;:components "БД №№: 6025, 1369"
             ;:channels 2
             :count "9/0000140"
             :counteragent 2
             :conditions 1154
             :manufacture_year y
             :comment "Леонтьев"
             ;:comment 11
             ;:comment "ГИС блок 2"
             ;:upload 1
             ;:verification_type 2
             ;:scope
             ;:engineer 3514
             ;:sw_name "СЕАН-П"
             ;:sw_version "не ниже v.6015" 
             ;:sw_checksum "8BFD"
             ;:sw_algorithm "CRC 16"
             ;:sw_version_real "3.10"
             :serial_number n
             :protocol_number (+ start-protocol-number i)
             :protocol nil
             :protolang nil
             )
           ["id = ?" (+ start-id i)]))
       nums
       (range (count nums))
       years))

;; #edit#verification
(jdbc/update!
  midb
  :verification
  (hash-map
    :components "CH₄ (метан); O₂ (кислород); CO (оксид углерода); H₂S (сероводород)")
  ["id = ?" 3649]
  "returning mi_types, components")

;; #delete#record
(delete-record! 3853)

;; Удалить записи с id >=
;; #delete#record
(map (fn [i]
         (delete-record! (+ 3861 i)))
     (range 4))

(map (fn [id] (copy-v-gso! 2337 id))
     (range 2301 2327))

;; #report#gso
(gso "lower(components) like '%%'
      --pass_number like '%18817%'
      and expiration_date > date('now')")
(sh "vivaldi" (str midb-path "gso.html"))

;; #report#refs
(references "mi_type like '%test%'")
(sh "vivaldi" (str midb-path "references.html"))

;; #set#gso
(set-v-gso!
  #_3668
  (last-id "verification")
  (list 383)
  #_(map (fn [m]
           (:id m))
       (check-gso (list "16871-23" "08197-23" "14638-23" "14636-23" "14630-23" "14628-23")
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
(copy-v-gso! 3753 3754)

;; #delete#gso
(delete-v-gso! 2343)

(set-v-gso! 2453
            (remove #{258 334} (get-v-gso 2386)))

(/ (- 94.3 95.1) 95.1)

;; #conditions
(conditions "2024-02-20")
(sh "vivaldi" (str midb-path "conditions.html"))

;; #add#conditions
(insert-conditions! {:date "2024-02-27"
                     :temperature 23.7
                     :humidity 51.3
                     :pressure 103.15
                     :voltage 220.5
                     :frequency 50
                     ;:other "0,4 (0,4 ± 0,1) дм³/мин"
                     ;:location "УЭСП"
                     ;:comment ""
                     })

;; #edit#conditions
(jdbc/update!
  midb
  :conditions
  {:voltage 223.5
  }
  ["id = ?" 1147])

;; #set#refs
(set-v-refs! ;3668
             (last-id "verification")
             (list 2663 2820))

;; #copy#refs
(copy-v-refs! 3753 3754)

;; #delete#refs
(delete-v-refs! 2260)

(jdbc/insert!
  midb
  :v_opt_refs
  {:v_id 2343 :ref_id 2762})

;; #set#opt-refs
(set-v-opt-refs! ;3668
                 (last-id "verification")
                 (list 2643 2827 2762 2756))

;; #copy#opt-refs
(copy-v-opt-refs! 3355 3646)

;; #add#operations
(jdbc/insert!
  midb
  :verification_operations
  {:methodology_id 389
   :section "6.5"
   :name "Определение абсолютной погрешности измерений коэффициента поглощения светового потока"
   :verification_type 1
   ;:comment "См. в приложении к протоколу"
   ;:info "для Микрохром-1121-3"
   })

;; #set#operations
(set-v-operations! ;3668
                   (last-id "verification")
                   (list 239 513 787 1161 1881))

;; #copy#operations
(copy-v-operations! 3187 3210)

;; #unusability#update#operations
(unusability
  3762
  505
  "не заряжается")

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
     :count "9/0000140"
     :counteragent 2
     :conditions 1154
     :verification_type 1
     :protocol_number (next-protocol-number) 
     ;:protocol_number 54
     :mi_type "Millennium II, мод. M22-ARD-S-EM"
     :methodology_id 313
     :serial_number "0061289"
     :manufacture_year 2019
     :channels 1
     :area "05"
     :interval 12
     :components "ST320A-100-ASSY-EM зав. № 0061548"
     ;:components "CO (0 - 0,5) % об.; CH (0 - 0,2) % об.; NO (0 - 0,5) % об.; CO₂ (0 - 15) % об.; O₂ (0 - 21) % об."
     ;:scope
     :sw_name "FRM-0117"
     :sw_version "2.2"
     :sw_checksum "07442363d196c21fb357aa7fa278495d"
     :sw_algorithm "MD5"
     :sw_version_real "2.2"
     ;:voltage 24
     ;:upload 
     :comment "Леонтьев"
     ;:comment 11
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
    :number "10563-2015"
    :components "CO+N2"
    :concentration 9.81
    :units "%"
    :uncertainity 0.07
    :pass_number "01275-24"
    :number_1c 2062 
    :manufacture_date "2024-02-08"
    :expiration_period 12
    :expiration_date "2025-02-07"
    :level 0
    :date "2024-02-14"
    :cylinder_number "21387"
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
  {:registry_number "56918-14"
   :mi_name "Дымомеры"
   :mi_types "СМОГ-2"
   :name "МП-640-0020-2-14 «Инструкция. Дымомеры СМОГ-2. Методика поверки»"
   :short_name "МП-640-0020-2-14"
   :date_from nil
   :date_to "2029-03-06"
   :temperature "22 ± 5"
   :humidity "30 ÷ 80"
   :pressure "84,0 ÷ 106,7"
   :voltage "220 ± 22"
   :frequency "50 ± 1"
   ;:other "расход ГСО-ПГС: не менее 1,0 л/ч"
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
  ;3775
  (last-id "verification")
  (list [1695 0 7] [1696 17]
        [1695 7 0] [1696 17]
        [1698 nil]
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
(delete-measurements!
  ;3775
  (last-id "verification"))

(jdbc/delete!
  midb
  :measurements
  ["v_id >= ? and v_id <= ?" 2670 2689])


;; #add#metrology#channel
(ins-channel!
  {:methodology_id 313
   :channel "ST320x-100-ASSY-EM"
   :component "H2S"
   :range_from 0
   :range_to 20
   :units "млн⁻¹"
   :low_unit 1
   :view_range_from 0
   :view_range_to 20
   ;:comment "диапазон показаний условно! ГИАМ-29М-4."
   }
  (list {:r_from 0
         :r_to 10
         :value 2
         :fraction nil
         :type_id 0
         :units nil
         :operation_id 1161
         :comment nil}
        {:r_from 10
         :r_to 20
         :value 20
         :fraction nil
         :type_id 1
         :units nil
         :operation_id 1161
         ;:comment "(15 - 30) % об."
         }
        {:value 0.5
         :type_id 5
         :units ""
         :operation_id 1880}
        {;:r_from 0
         ;:r_to 10
         :value 36
         :type_id 6
         :units "с"
         :operation_id 1881}
        #_{
         :r_from 0
         :r_to 71 
         :value 60
         :type_id 6
         :units "с"
         :operation_id nil}
        #_{:value 10
         :type_id 12
         :units nil
         :operation_id 878
         ;:comment "порог 11 % НКПР"
        }
        #_{:value 10
         :type_id 7
         :units "с"
         :operation_id 923}
        #_{:value 30
         :type_id 16
         :units "с"
         :operation_id 1299
         :comment "время срабатывания защиты "}))

;; #add#counteragents
(jdbc/insert!
  midb
  :counteragents
  {:name "Общество с ограниченной ответственностью «ПЛАСТИНВЕСТ»"
   :short_name "ООО «ПЛАСТИНВЕСТ»"
   :address "460014, Оренбургская область, г. Оренбург, ул. Чичерина, д. 14, помещ. 1, офис 2"
   :inn 5610149071})

;; #edit#counteragents#update
(jdbc/update!
  midb
  :counteragents
  {
   ;:name "ООО АТЦ «Жигули-Оренбург»"
   ;:short_name "ООО АТЦ «Жигули-Оренбург»"
   :address "123298, Москва г, Берзарина ул, дом 3, корпус 1, квартира 17"
   }
  ["id = ?" 12996])

;; #cars
;; #insert#order#auto
(do
  (jdbc/insert!
    auto
    :travel_order
    {:auto_id 4
     :count "9/0000279"
     :date_departure "2024-02-27T09:00"
     :date_arrive "2024-02-27T13:30"
     :odometr_departure 145039
     :fuel_departure 23.65
     :odometr_arrive 145078
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
