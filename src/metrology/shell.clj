(def record (atom nil))
(def current (atom nil))

(pprint (find-mi "ГХ-М"))

(pprint (find-counteragent "ЦМСЧ"))

;; Копировать существующую запись
(copy-record! 2013)
(reset! current (get-last-id "verification"))
(pprint (reset! record (get-record @current)))

(reset! record (get-record 1960))

;; Создать однотипные записи по массиву зав. №.
(map (fn [s] (copy-record! 2116))
     (range 23))

(let [nums (map (fn [n] (str "02" n))
                (list 254 261 311 249 252 01731 266 269 246 265 310 258
                      267 253 247 251 272 264 302 260 255 256 273))
      start-id 2192
      start-protocol-number 2169]
  (map (fn [n i]
         (jdbc/update!
           midb
           :verification
           (hash-map
             :protocol nil
             :protolang nil
             :count "9/002928"
             :counteragent 79
             :conditions 1008
             :serial_number n
             :manufacture_year 2021
             :protocol_number (+ start-protocol-number i)
             ;:comment "Леонтьев"
             ;:channels
             ;:components
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

(insert-conditions! {:date "2023-09-07"
                     :temperature 22.9
                     :humidity 52.7
                     :pressure 100.02
                     :voltage 221.4
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

(set-v-refs! 3000
             (list ))

(set-v-opt-refs! 2215
             (list 2758 2756 2670 2643))

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

(pprint @record)

;; Создать запись о поверке.
(jdbc/insert!
  midb
  :verification
  (hash-map
     :engineer 3514
     :count "9/002944"
     :counteragent 577
     :conditions 1008
     :verification_type 1
     :protocol_number 2162
     :mi_type "ГКМП-02-ИНСОВТ"
     :methodology_id 369
     :serial_number 1548
     :manufacture_year 2018
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

;; Обновить запись verification
(update-record!
  :verification
  @record
  (hash-map
    :protocol nil
    :protolang nil
    :count "9/0029711"
    :counteragent 10646
    :conditions 1004
    :serial_number "KA417-1095585"
    :manufacture_year 2017
    :protocol_number nil
    ;:comment "Леонтьев"
    ;:channels
    ;:components
    ;:scope
    ;:sw_name 8320039
    ;:sw_version "не ниже V6.9"
    ;:sw_checksum "F8B9"
    ;:sw_algorithm "CRC-16"
    ;:sw_version_real "V3.04"
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

(jdbc/query midb [q/get-methodology "%19437%"])

;; documentations

(require '[clojure.repl :refer :all])

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
    :methodology_id 369
    :section "6.5"
    :name "Проверка программного обеспечения"
    :verification_type 1
    ;:comment "См. в приложении к протоколу."
    ))

;; Каналы и МХ
(ins-channel!
  {:methodology_id 369
   :channel nil
   :component "O₂"
   :range_from 0
   :range_to 100
   :units "% об."
   :low_unit 0.1
   :view_range_from nil
   :view_range_to nil
   :comment nil}
  (list {:r_from 0
         :r_to 100
         :value 2
         :type_id 0
         :units nil
         :comment nil}))

;; Генерация протоколов поверки
(defn protocols
  "Возвращает протоколы поверки в html формате.
  :args
    :where - условие выборки записей о поверке для включения в файл."
  [where]
  ())

(find-doc "assoc")

(doc repeat)

(dir clojure.core)

