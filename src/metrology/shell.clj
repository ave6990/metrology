(def record (atom nil))
(def current (atom nil))

(pprint (find-mi "414"))

(pprint (find-counteragent "УТТ"))

;; Копировать существующую запись
(copy-record! 2013)
(reset! current (get-last-id "verification"))
(pprint (reset! record (get-record @current)))

(reset! record (get-record 1960))

;; Создать однотипные записи по массиву зав. №.
(map (fn [s] (copy-record! 934))
     (range 1))

(let [nums (map (fn [n] (str "" n))
                (list "ER414191287"))
      start-id 2191
      start-protocol-number 2183]
  (map (fn [n i]
         (jdbc/update!
           midb
           :verification
           (hash-map
             :protocol nil
             :protolang nil
             :count "9/002845"
             :counteragent 50
             :conditions 1008
             :serial_number n
             :manufacture_year 2019
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
(delete-record! 2091)

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
(set-gso! @current (check-gso '("11101-23" "00808-23" "007465-22"
                                "02463-22" "06869-23" "00810-23")
                          "pass_number"))

;Проверить ГСО в записи.
(pprint (check-gso (map (fn [x] (:gso_id x))
                (:v_gso (get-record 2186)))
           "id"))

(pprint (check-gso (list "11101-23" "00808-23" "007465-22"
                     "02463-22" "06869-23" "00810-23")
                   "pass_number"))

(for [f (list copy-v-refs! copy-v-operations! copy-measurements! copy-v-opt-refs!)
      n (range 22)]
      (f 2068 (+ 2069 n)))

(pprint @record)

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

(pprint (all-refs 2186))

(map (fn [m] (:serial_number m)) (all-refs @current))

(get-record (get-last-id "verification"))

(defn get-last
  []
  (let [m (:verification (get-record (get-last-id "verification")))]
    (apply hash-map (flatten (map (fn [k]
             (list k (k m)))
         (list :id :protocol_number))))))

(get-last)

;; documentations

(require '[clojure.repl :refer :all])

(find-doc "assoc")

(doc repeat)

(dir clojure.core)

