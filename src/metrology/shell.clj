(def record (atom nil))
(def current (atom nil))
(def protocol (atom nil))

(pprint (find-mi "Ока"))

(pprint (find-methodology "Ока"))

(pprint (find-counteragent "ОРЕНБУ%МИНЕР"))

(reset! record (get-record 2220))

(pprint (reset! protocol (get-protocol-data 2220)))

;; Создать однотипные записи по массиву зав. №.
(map (fn [s] (copy-record! 2224))
     (range 3))

(let [nums (map (fn [n] (str "21031" n))
                (list 93 94 96))
      start-id 2225
      start-protocol-number 2222]
  (map (fn [n i]
         (jdbc/update!
           midb
           :verification
           (hash-map
             :protocol nil
             :protolang nil
             :count "9/002803"
             :counteragent 78
             :conditions 1010
             :serial_number n
             :manufacture_year 2021
             :protocol_number (+ start-protocol-number i)
             ;:comment "Леонтьев"
             ;:channels
             ;:components "БД горючих газов №№: "
             ;:scope
             ;:sw_name 8320039
             ;:sw_version "не ниже V6.9"
             ;:sw_checksum "F8B9"
             ;:sw_algorithm "CRC-16"
             ;:sw_version_real "V3.04"
             )
           ["id = ?" (+ start-id i)]))
       nums
       (range (count nums))))

(copy-v-gso! 2123 2144)

;; Удалить записи с id >=
(map (fn [i]
         (delete-record! (+ 2117 i)))
     (range 13))

;; Удалить запись
(delete-record! 2216)

(pprint (get-conditions "2023-09-07"))

(insert-conditions! {:date "2023-09-12"
                     :temperature 22.7
                     :humidity 51.3
                     :pressure 100.23
                     :voltage 224.6
                     ;:other "расход ГС (0,1 - 0,3) л/мин."
                     ;:location "ОГЗ"
                     ;:comment ""
                     })


(pprint (:verification @record))

;Установить ГСО по номерам паспортов ГСО.
(set-v-gso! 2215
            (map (fn [m]
                     (:id m))
                 (check-gso (list "13305-22" "12211-22")
                            "pass_number")))

(set-v-gso! 2224 
            (list 315))

(set-v-refs! 2219
             (list 2765 2820))

(set-v-opt-refs! 2216
                 (list 2643 2758 2831 2756 2670))

(set-v-operations! 2216
                   (list 60 608 1063 1672 1673))

;Проверить ГСО в записи.
(pprint (check-gso (map (fn [x] (:gso_id x))
                        (:v_gso (get-record 2215)))
                   "id"))

(pprint (check-gso (list "11101-23" "00808-23" "007465-22"
                     "02463-22" "06869-23" "00810-23")
                   "pass_number"))

;; Копировать ГСО, эталоны, операции и результаты измерений в несколько записей.
(for [f (list copy-v-refs! copy-v-operations! copy-measurements! copy-v-opt-refs!)
      n (range 22)]
      (f 2068 (+ 2069 n)))

(copy-v-operations! 4 8)

(copy-measurements! 4 8)

(pprint @record)

;; Создать запись о поверке.
(jdbc/insert!
  midb
  :verification
  (hash-map
     :engineer 3514
     :count "9/0029591"
     :counteragent 198
     :conditions 1010
     :verification_type 1
     :protocol_number 2211 :mi_type "СТГ1-1Д10(в)"
     :methodology_id 90
     :serial_number 1198
     :manufacture_year 2005
     :channels 2
     :area "05"
     :interval 12
     ;:components
     ;:scope
     ;:sw_name 8320039
     ;:sw_version "не ниже V6.9"
     ;:sw_checksum "F8B9"
     ;:sw_algorithm "CRC-16"
     ;:sw_version_real "V3.04"
     ;:voltage nil
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
    :number "10563-2015"
    :components "NH3+N2"
    :concentration 22.5
    :units "ppm"
    :uncertainity 0.3
    :pass_number "11874-23"
    :number_1c 1850 
    :manufacture_date "2023-08-11"
    :expiration_period 12
    :expiration_date "2024-08-10"
    :level 0
    :date "2023-09-05"
    :cylinder_number "6414"
    :volume 4
    :pressure 8
    ;:comment
  })

;; Просроченные эталоны
(pprint (filter (fn [m] (not= "" (:expiration m)))
        (all-refs 345)))

(pprint (all-refs 2215))

(map (fn [m] (:serial_number m)) (all-refs @current))

(get-record (get-last-id "verification"))

(defn get-last
  []
  (let [m (:verification (get-record (get-last-id "verification")))]
    (apply hash-map (flatten (map (fn [k]
                               (list k (k m)))
                             (list :id :protocol_number))))))

(get-last)

;; Методика поверки
(jdbc/query midb [q/get-methodology "%19437%"])

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

;; Операции методики поверки
(jdbc/insert!
  midb
  :verification_operations
  (hash-map
    :methodology_id 326
    :section "6.5"
    :name "Проверка программного обеспечения"
    :verification_type 1
    ;:comment "См. в приложении к протоколу."
    ))

(/ 2.14 4.4)

;; Измерения
(jdbc/insert!
  midb
  :measurements
  (hash-map
    :v_id 4
    :metrology_id 1107
    :operation_id 1167
    :ref_value 48.64 
    ))

;; Каналы и МХ
(ins-channel!
  {:methodology_id 326
   :channel nil
   :component "H₂S"
   :range_from 0
   :range_to 40
   :units "мг/м³"
   :low_unit 0.1
   :view_range_from 0
   :view_range_to 40
   :comment nil}
  (list {:r_from 0
         :r_to 10
         :value 2
         :type_id 0
         :units nil
         :comment nil}
        {:r_from 10
         :r_to 40
         :value 20
         :type_id 1
         :units nil
         :comment nil}
        {:value 30
         :type_id 6
         :units "с"}))

;; Генерация протоколов поверки
(gen-protocols "id >= 4 and id <= 8")

;; Генерация результатов измерений
(gen-values! "id >= 4 and id <= 8")

;; documentations
(require '[clojure.repl :refer :all])

(find-doc "assoc")

(doc get-in)

(dir clojure.core)

