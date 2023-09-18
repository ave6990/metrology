(def record (atom nil))
(def current (atom nil))
(def protocol (atom nil))

(load-icu)

(pprint (find-mi "58111"))

(pprint (find-methodology "58111"))

(pprint (find-counteragent "СТРЕЛА"))

(reset! record (get-record 2220))

(pprint (reset! protocol (get-protocol-data 2220)))

;; Создать однотипные записи по массиву зав. №.
(map (fn [s] (copy-record! 1078))
     (range 2))

(let [nums (map (fn [n] (str "" n))
                (list 6303))
      start-id 2242
      start-protocol-number 2237]
  (map (fn [n i]
         (jdbc/update!
           midb
           :verification
           (hash-map
             :protocol nil
             :protolang nil
             :count "9/0029824"
             :counteragent 6862
             :conditions 1011
             :serial_number n
             :manufacture_year 2021
             :protocol_number (+ start-protocol-number i)
             ;:comment "Леонтьев"
             ;:upload 1
             ;:channels
             ;:components "O₂ (кислород); CH₄ (метан); CO₂ (диоксид углерода)"
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
(delete-record! 2230)

(pprint (get-conditions "2023-09-13"))

(insert-conditions! {:date "2023-09-14"
                     :temperature 22.8
                     :humidity 53.7
                     :pressure 100.25
                     :voltage 220.9
                     ;:other "расход ГС (0,1 - 0,3) л/мин."
                     ;:location "ОГЗ"
                     ;:comment ""
                     })


(pprint (:verification @record))

;Установить ГСО по номерам паспортов ГСО.
(set-v-gso! 2231
            (map (fn [m]
                     (:id m))
                 (check-gso (list "08198-23")
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

(map (fn [v] (copy-measurements! 2224 v))
     (range 2225 2228))

(pprint @record)

;; Создать запись о поверке.
(jdbc/insert!
  midb
  :verification
  (hash-map
     :engineer 3514
     :count "9/0029829"
     :counteragent 12
     :conditions 1012
     :verification_type 1
     :protocol_number 2238
     :mi_type "SGW CO0 NX"
     :methodology_id 280
     :serial_number 226556
     :manufacture_year 2014
     :channels 1
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

(pprint (all-refs 1078))

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

;; Методика поверки - изменить запись
(jdbc/update!
  midb
  :methodology
  {:temperature "20 ± 5"
   :humidity "30 ÷ 80"
   :pressure "101.3 ± 4.0"}
  ["id = ?" 280])

;; Операции методики поверки
(jdbc/insert!
  midb
  :verification_operations
  (hash-map
    :methodology_id 280
    :section "6.3"
    :name "Подтверждение соответствия программного обеспечения (для сигнализаторов
    "
    :verification_type 1
    :comment "См. в приложении к протоколу."
    ))

(/ 4.23 4.4)

;; Измерения
(map (fn [ref]
         (jdbc/insert!
           midb
           :measurements
           (hash-map
             :v_id 2224
             :metrology_id 1118
             :operation_id 850
             :ref_value ref
             )))
    (list nil))

;; Каналы и МХ
(ins-channel!
  {:methodology_id 68
   :channel nil
   :component "CH₄"
   :range_from 0
   :range_to 2.2
   :units "% об."
   :low_unit 0.01
   :view_range_from 0
   :view_range_to 5
   :comment nil}
  (list {:r_from 0
         :r_to 0.88
         :value 25
         :type_id 2
         :units nil
         :comment nil}
        {:r_from 0.88
         :r_to 2.2
         :value 25
         :type_id 1
         :units nil
         :comment nil}
        {:value 0.5
         :type_id 5
         :units ""}
        {:value 15
         :type_id 6
         :units "с"}))

;; Контрагенты изменение записи
(jdbc/update!
  midb
  :counteragents
  {:address "460028, Оренбургская область, город Оренбург, улица Заводская, 30"
   :name "ОАО «НЕФТЕМАСЛОЗАВОД»"
   :short_name "ОАО «НЕФТЕМАСЛОЗАВОД»"}
  ["id = ?" 4274])

;; Генерация протоколов поверки
(gen-protocols "id >= 2241 and id <= 2242")

;; Генерация результатов измерений
(gen-values! "id >= 2236 and id <= 2240")

(gen-values! "id = 2220")

(pr/gen-value (get (vec (:measurements (first (get-protocols-data "id = 2220")))) 7))

;; documentations
(require '[clojure.repl :refer :all])

(find-doc "assoc

(doc get-in)

(dir clojure.core)

