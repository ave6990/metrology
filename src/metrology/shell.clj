;; #gs2000
(require '[metrology.lib.gs2000 :as gs])
(require '[clojure.java.shell :refer [sh]])

(pprint (gs2000 2
                "H2S"
                496 
                #_(list ) 
                (map #(ch/ppm->mg "H2S" %1)
                     (list 5 9 11 15 18))))

((gs/calculator
  (gs/passports 1))
  "CH4"
  :N2
  7090
  150)

;; #chemistry
(ch/coefficient "NH3")

(map (partial ch/ppm->mg
              "H2S")
     '(25 47.5))

(* 0.94 0.05)

(ch/mg->ppm "NH3" 1600)

(map #(/ (* %1 32.07) 62.14)
     '(4.9 7.8 40 70))

(map #(/ %1 4.4 0.01)
     '(2.236 4.2))

(map #(* % 30)
     '(0.05 0.5 0.95))

;; #report#methodology
(methodology (list 393))
(sh "vivaldi" (str midb-path "methodology.html"))

;; #find#methodology
(methodology
  (map (fn [m]
           (:id m))
       (find-methodology "ЦВЕТ")))
(sh "vivaldi" (str midb-path "methodology.html"))

;; #report#find#mi
(gen-report
  (find-records
    "lower(mi_type) like '%ЭРИС-210%'
     --and lower(mi_type) not like '%elgas%'
     --and channels = 3
     --and methodology_id = 305
     --and components like '%SO2%'
     --and components like '%7000%'
     --and date > '2024-02-01'
     --and comment not like 'Леонтьев'
     --and registry_number like '%-17%'
     --protocol_number = 1293 and protocol_number = 1295
     --and serial_number like '%1752554%'
     --serial_number like '%20191%'
     --count like '%000374%'
     --id = 1322 or id = 1320"))
(sh "vivaldi" (str midb-path "report.html"))

;; #report#find#verifications
(gen-report
  (list 4183)
  #_(range 4167 4171))
(sh "vivaldi" (str midb-path "report.html"))

;; #report#find#verification
(gen-report
  (find-verifications
    "--lower(v.mi_type) like '%СГГ-20%'
     v.upload is null
     --and lower(v.mi_type) not like '%elgas%'
     --and v.channels = 1
     --and v.methodology_id = 305
     --and v.components like '%H2S%'
     --and v.components like '%7000%'
     --and c.date > '2024-01-01'
     --and v.comment not like 'Леонтьев'
     --and met.registry_number like '%-17%'
     --v.protocol_number = 1293 and v.protocol_number = 1295
     --and v.serial_number like '%1752554%'
     --and v.serial_number like '%850%'
     --v.count like '%000388%'
     --v.id = 1322 or v.id = 1320"))
(sh "vivaldi" (str midb-path "report.html"))

;; #report#protocols
(let [where "id >= 4435"]
  (gen-protocols where))
(sh "vivaldi" (str midb-path "protocol.html"))

(pprint (get-protocols-data "id = 3893"))

;; #gen#measurements#values
(let [where "id >= 3982 and id <= 3987"]
  (gen-values! where))

;;#gen#custom#protocols
(let [where "id = 4014"]
  (gen-custom-protocols (get-protocols-data where)))

;; #find#counteragents
(counteragents "ТЕПЛОИЗ")
(sh "vivaldi" (str midb-path "counteragents.html"))

;; #copy#record
(copy-record! 2833 1)

(let [nums (map (fn [n] (str "" n))
                (list 191)
                #_(range 13))
      years (repeat (count nums) 2017)
            #_(list 2014 )
      start-id (next-id)
      start-protocol-number (next-protocol-number)]
      ;start-protocol-number 701]
  (map (fn [n i y]
           (jdbc/update!
            midb
            :verification
            (hash-map
             ;:methodology_id 193
             ;:mi_type "СЕАН-П, мод. СЕАН-П2"
             :components "БД зав. №№: 2226, 2011, 2261, 2265, 2232, 2251"
             ;:channels 6
             :count "9/0000633"
             :counteragent 2
             :conditions 1197
             :manufacture_year y
             ;:comment "Леонтьев"
             ;:comment 11
             ;:comment "ГИС блок 2"
             ;:upload 1
             ;:verification_type 2
             ;:scope
             ;:engineer 3514
             ;:sw_name "SGO"
             ;:sw_version "не ниже V 7.47" 
             ;:sw_checksum "387535e5"
             ;:sw_algorithm "CRC 32"
             ;:sw_version_real "V 7.135"
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
(delete-record! 4286)

;; Удалить записи с id >=
;; #delete#record
(map (fn [i]
         (delete-record! (+ 4167 i)))
     (range 10))

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
  #_4329
  (last-id "verification")
  (list 404)
  #_(map (fn [m]
           (:id m))
       (check-gso (list "")
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
(copy-v-gso! 4251 '(4252))

;; #delete#gso
(delete-v-gso! 2343)

(set-v-gso! 2453
            (remove #{258 334} (get-v-gso 2386)))

(/ (- 94.3 95.1) 95.1)

;; #conditions
(conditions "2024-03-27")
(sh "vivaldi" (str midb-path "conditions.html"))

;; #add#conditions
(insert-conditions! {:date "2024-04-12"
                     :temperature 22.5
                     :humidity 52.4
                     :pressure 100.7
                     :voltage 222.1
                     :frequency 50
                     ;:other "0,4 (0,4 ± 0,1) дм³/мин"
                     ;:location "ОГЗ"
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
(set-v-refs! ;4329
             (last-id "verification")
             (list 3151 2768)
             #_(list 2663 2820)
             #_(list 2765 2768))

;; #copy#refs
(copy-v-refs! 4075 '(4076))

;; #copy#refs#set
(copy-refs-set! 4170 '(4175))

;; #delete#refs
(delete-v-refs! 
  4153
  #_(last-id "verification"))

(jdbc/insert!
  midb
  :v_opt_refs
  {:v_id 2343 :ref_id 2762})

;; #set#opt-refs
(set-v-opt-refs! ;3668
                 (last-id "verification")
                 (list 2643 2827 2762 2756 2670 2715))

;; #copy#opt-refs
(copy-v-opt-refs! 4075 '(4076))

;; #add#operations
(jdbc/insert!
  midb
  :verification_operations
  {:methodology_id 395
   :section "11.2"
   :name "Определение вариации выходного сигнала"
   :verification_type 2
   :comment "См. в приложении к протоколу"
   ;:info "для Микрохром-1121-3"
   })

;; #set#operations
(set-v-operations! ;4193
                   (last-id "verification")
                   (list 1910 1911 1912 1914))

;; #copy#operations
(copy-v-operations! 3187 '(3210))

;; #unusability#update#operations
(unusability
  4431
  589
  "превышение предела допускаемой основной погрешности по каналу измерения O₂")

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
    mi_type like '%ГС-2000%'")

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
     :count "9/0000736"
     :counteragent 3
     :conditions 1197
     :verification_type 1
     ;:protocol_number (next-protocol-number) 
     :protocol_number 837
     :mi_type "ОПТИМУС"
     :methodology_id 395
     :serial_number 1260
     :manufacture_year 2023
     :channels 1
     :area "05"
     :interval 12
     :components "ПГЭ-H₂S (0 - 141,3) мг/м³"
     ;:components "CO (0 - 0,5) % об.; CH (0 - 0,2) % об.; NO (0 - 0,5) % об.; CO₂ (0 - 15) % об.; O₂ (0 - 21) % об."
     ;:scope
     :sw_name "Optimus_T_FW_3.16"
     :sw_version "не ниже 3.16"
     :sw_checksum "A9F46EAB"
     :sw_algorithm "CRC32"
     :sw_version_real "3.17"
     ;:voltage 24
     ;:upload 
     ;:comment "Леонтьев"
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
  {:registry_number "16904-03"
   :mi_name "Хроматографы газовые аналитические"
   :mi_types "ЦВЕТ-800"
   :name "5Е1.550.205 МП «Хроматограф газовый аналитический «ЦВЕТ-800». Методика поверки»"
   :short_name "5Е1.550.205 МП"
   :date_from nil 
   ;:order nil
   :date_to "2008-02-01"
   :temperature "20 ± 5"
   :humidity "30 ÷ 80"
   :pressure "84 ÷106" 
   :voltage "220 ± 5"
   :frequency "50 ± 1"
   :other "изменение атмосферного давления в процессе поверки не более чем на ± 5 кПа"
   ;:other_limit "± 5 °C"
   :limited 0
   :comment nil
  })

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
  (last-id "verification")
  (list [nil [[1786 0 35.432 67.314]] nil]
        ))

(insert-measurements
  ;3867
  (last-id "verification")
  nil
  (list [871 nil] [1713 nil] [872 nil]
  )
  nil)

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
(copy-measurements! 4170 (range 4171 4175))

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
  {:methodology_id 306
   :channel "ПГТ-903У-метан"
   :component "CH4"
   :range_from 0
   :range_to 2.2
   :units "% об."
   :low_unit 0.01
   :view_range_from 0
   :view_range_to 4.4
   ;:comment "диапазон показаний условно!"
   }
  (list {:r_from 0
         :r_to 2.2
         :value 0.22
         :fraction nil
         :type_id 0
         :units nil
         :operation_id 1013
         ;:text "отсутствует"
         :comment nil}
        #_{:r_from 14.2
         :r_to 28.4 
         :value 10
         :fraction nil
         :type_id 1
         :units nil
         :operation_id 1112
         ;:comment "(15 - 30) % об."
         }
        {:value 0.5
         :type_id 5
         :units ""
         :operation_id 1015}
        {;:r_from 0
         ;:r_to 10
         :value 30
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
        #_{:value 10
         :type_id 12
         :units nil
         :operation_id 686
         ;:comment "порог 11 % НКПР"
        }
        #_{:value 10
         :type_id 7
         :units "с"
         :operation_id 1511}
        #_{:value 30
         :type_id 16
         :units "с"
         :operation_id 1299
         :comment "время срабатывания защиты "}))

;; #add#counteragents
(jdbc/insert!
  midb
  :counteragents
  {:name "Общество с ограниченной ответственностью «ТЕПЛОИЗОЛЯЦИЯ-1»"
   :short_name "ООО «ТЕПЛОИЗОЛЯЦИЯ-1»"
   :address "453104, Республика Башкортостан, г. Стерлитамак, ул. Профсоюзная, дом 16"
   :inn "0268046915"})

;; #edit#counteragents#update
(jdbc/update!
  midb
  :counteragents
  {
   ;:name "Общество с ограниченной ответственностью «НАУЧНО-ИННОВАЦИОННОЕ ПРЕДПРИЯТИЕ «ТЕХНОЛОГИЯ»"
   ;:short_name "ООО АТЦ «Жигули-Оренбург»"
   ;:inn 2130160440
   :address "460003, Оренбургская обл, Оренбург г, Станочный пер, дом № 7"
   }
  ["id = ?" 2262])

;; #cars
;; #insert#order#auto
(do
  (jdbc/insert!
    auto
    :travel_order
    {:auto_id 3
     :count "9/0000633"
     :date_departure "2024-04-16T09:00"
     :date_arrive "2024-04-16T12:00"
     :odometr_departure 148867
     :fuel_departure 29.36
     :odometr_arrive 148901
     :fuel_add 0})
  (pprint
    (jdbc/query
      auto
      "select * from view_travel_order order by id desc limit 1;")))

;; #backup
(sh "backup_midb")

;; #hash#references
(calc-references-hash "v_id >= 4025")

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

(defn split-pdf
  [scan-path f pages start-number]
  (let [scan-path "/media/sf_SCAN/"
        scan-pages (->
                     (sh "identify" (str scan-path f))
                     :out
                     (string/split #"\n")
                     count)
        pages (read-string pages)
        start-number (read-string start-number)]
    (dotimes [i (/ scan-pages pages)]
      (sh "pdftk"
          (str scan-path f)
          "cat"
          (str (inc (* i pages)) "-" (+ (* i pages) pages))
          "output"
          (str scan-path "9-61-" (+ i start-number) "-2024.pdf"))
      #_(sh "mv"
          (str scan-path f)
          (str scan-path "trash/")))))

;; #split#rename#scan#protocol
(let [scan-path "/media/sf_SCAN/"
      get-files-list
        (fn []
            (doall
              (filter (fn [s]
                          (re-matches #".*\.pdf" s))
                      (->
                        (sh "ls" scan-path)
                        :out
                        (string/split #"\n")))))]
  ;; приводим имена сканов к общему виду {start_protocol}.{pages_per_protocol}.pdf
  (dorun
    (map (fn [f] 
             (sh "mv" (str scan-path f)
                      (->
                        (str scan-path f)
                        (string/replace #"Protokol" "")
                        (string/replace #"\d{6}\.pdf" "pdf"))))
         (get-files-list)))
  ;; делим общий скан на протоколы и переименовываем их.
  (dorun
    (map (fn [f]
             (let [[start-number pages _] (string/split f #"\.")]
               (split-pdf scan-path f pages start-number)
               ;; реализация с использованием bash-скрипта split-pdf.sh
               #_(sh "bash"
                   (str scan-path "split_pdf.sh")
                   (str scan-path f)
                   pages
                   start-number)
               (println (str scan-path "split_pdf.sh") f pages start-number)))
         (doall
           (filter (fn [f]
                       (re-matches #"\d+\.\d\.pdf" f))
                   (get-files-list))))))

(split-pdf "/media/sf_SCAN" "824.1.pdf" "1" "824")

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
(metr/air-vnc->v 100 22.1 99.97)

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

(require '[clojure.repl :refer :all])

(find-doc "in")

(doc some?)

(dir metrology.lib.metrology)

(doc metr/air-v->vnc)

(find-doc "nil?")

(nil? "")

(metr/air-v->vnc 100 23.4 103.87)

(dir clojure.core)

(require '[metrology.view.report :as report] :reload)

(require '[metrology.lib.gs2000 :as gs] :reload)

(require '[metrology.lib.midb-queries :as q] :reload)

(require '[metrology.lib.gen-html :refer :all] :reload)

(require '[metrology.lib.protocol :as pr] :reload)

(list* 1 2 3 [4 5])
