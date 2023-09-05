(ns metrology.lib.midb
  (:require 
    [clojure.java.jdbc :as jdbc]
    [clojure.string]
    [metrology.lib.database :as db]
    [metrology.lib.midb-queries :as q]))

(db/defdb midb)

(defn get-last-id
  "Получить id последней записи заданной таблицы."
  [s]
  (:id (first (jdbc/query 
                midb
                (clojure.string/replace q/last-id "?" s)))))

(defn insert-conditions!
  "Вставка данных условий поверки в БД."
  ([m]
    (jdbc/insert! midb :conditions m))
  ([date temp moist press volt]
    (insert-conditions! (hash-map :date date
                                  :temperature temp
                                  :humidity moist
                                  :pressure press
                                  :voltage 50 
                                  :location "ОЦСМ"))))

(defn get-conditions
  "Возвращает запись БД с условиями поверки на заданную дату."
  [date]
  (jdbc/query midb
              ["select * from conditions where date = ?" date]))

(defn find-mi
  "Возвращает список записей поверок соответсвующих запросу.
   Запрос: заводской номер или номер реестра или наименование типа СИ."
  [s]
  (jdbc/query midb [q/find-mi (str "%" s "%")]))

(defn find-counteragent
  "Возвращает список контрагентов соответствующих запросу."
  [s]
  (jdbc/query midb [q/counteragents (str "%" s "%")]))


(defn copy-verification!
  "Копировать строку таблицы verification."
  [id]
  (jdbc/execute! midb [q/copy-verification id]))

(defn delete-verification!
  "Удалить строку таблицы verification."
  [id]
  (jdbc/delete! midb :verification ["id = ?;" id]))

(defmacro defn-delete
  [s]
  (let [id (gensym "id")]
    `(defn ~(symbol (str "delete-" s "!"))
      ~(str "Удалить строки таблицы "
            (clojure.string/replace s "-" "_")
            " соответствующие заданному v_id.")
      [~id]
      (jdbc/delete! midb 
                    ~(keyword (clojure.string/replace s "-" "_"))
                    ["v_id = ?" ~id]))))

(defn-delete v-gso)
(defn-delete v-refs)
(defn-delete v-opt-refs)
(defn-delete v-operations)
(defn-delete measurements)

(defmacro defn-copy
  [s]
  `(defn ~(symbol (str "copy-" s "!"))
    ~(str "Копировать строки таблицы "
          s
          " соответствующие заданному v_id.")
    [~(symbol "id-from") ~(symbol "id-to")]
    (map (fn [~(symbol "f") ~(symbol "args")] (~(symbol "f") ~(symbol "args")))
         [~(symbol (str "metrology.lib.midb/delete-" s "!"))
           (partial jdbc/execute! midb)]
         [~(symbol "id-to")
           [~(symbol (str "q/copy-" s))
           ~(symbol "id-to")
           ~(symbol "id-from")]])))

(defn-copy v-gso)
(defn-copy v-refs)
(defn-copy v-opt-refs)
(defn-copy v-operations)
(defn-copy measurements)

(defn delete-record!
  "Удалить запись о поверке с заданным id, вместе с данными о
   эталонах, операциях и измерениях."
   [id]
   (map (fn [f] (f id))
        [delete-v-gso! delete-v-refs! delete-v-opt-refs! delete-v-operations!
         delete-measurements! delete-verification!]))

(defn copy-record!
  "Копировать запиь о поверке с данными о применяемых эталонах, операциях
   поверки и результатах измерений.
   args:
     id - целочисленный идентификатор записи в БД."
  [id-from]
  (let [id-to (inc (get-last-id "verification"))]
    (conj (copy-verification! id-from)
          (map (fn [f] (f id-from id-to))
               (list copy-v-gso!
                     copy-v-refs!
                     copy-v-opt-refs!
                     copy-v-operations!
                     copy-measurements!)))))

(defn get-verification
  "Возвращает hash-map записи verification."
  [id]
  (first (jdbc/query midb
              ["select * from verification where id = ?" id]))) 

(defn get-record
  "Возвращает hash-map записи о поверке."
  [id]
  (apply conj (hash-map :verification (get-verification id))
        (map (fn [table]
               (hash-map
                 (keyword table)
                 (jdbc/query midb
                             [(str "select * from " table " where v_id = ?;")
                              id])))
             (list "v_gso" "v_refs" "v_opt_refs"
              "v_operations" "measurements"))))

(defn assoc-multi
  [m nm]
  (reduce (fn [a b]
            (let [[k v] b]
              (assoc a k v)))
          m
          nm))

(defn update-record!
  [table record changes]
  (jdbc/update! midb
                table
                (assoc-multi (table record) changes)
                ["id = ?" ((comp :id table) record)]))

(defn all-refs
  [id]
  (jdbc/query midb [q/all-refs id]))

(defn check-gso
  [coll column]
  (map (fn [num]
         (first (jdbc/query midb
                     [(str "select id, number_1c, pass_number, expiration_date
                            from gso
                            where " column " = ?") num])))
       coll))

(defn set-gso!
  [v_id coll]
  (do (delete-v-gso! v_id)
      (jdbc/insert-multi! midb
                      :v_gso
                      (vec (map (fn [m] (hash-map :v_id v_id :gso_id (:id m)))
                                coll)))))

(comment

(def record (atom nil))
(def changes (atom nil))
(def current (atom nil))

(find-mi "ГХ-М")

(find-counteragent "ГОК")

(:verification @record)

;Установить ГСО по номерам паспортов ГСО.
(set-gso! @current (check-gso '("11101-23" "00808-23" "007465-22"
                                "02463-22" "06869-23" "00810-23")
                          "pass_number"))

;Проверить ГСО в записи.
(check-gso (map (fn [x] (:gso_id x))
                (:v_gso @record))
           "id")

(check-gso '("11101-23" "00808-23" "007465-22" "02463-22" "06869-23" "00810-23")
                          "pass_number")

(for [f (list copy-v-refs! copy-v-operations! copy-measurements! copy-v-opt-refs!)
      n (range 22)]
      (f 2068 (+ 2069 n)))

(reset! changes (hash-map
                     :protocol nil
                     :protolang nil
                     :count "9/002928"
                     :counteragent 79
                     :conditions 1005
                     :serial_number "02468"
                     :manufacture_year 2021
                     :protocol_number 2061
                     :comment "Леонтьев"
                     ;:channels
                     ;:components
                     ;:scope
                     ;:sw_name 8320039
                     ;:sw_version "не ниже V6.9"
                     ;:sw_checksum "F8B9"
                     ;:sw_algorithm "CRC-16"
                     ;:sw_version_real "V3.04"
                     ))

(:verification @record)

@changes

;; Обновить запись
(update-record! :verification @record @changes)

;; Просроченные эталоны
(filter (fn [m] (not= "" (:expiration m)))
        (all-refs @current))

(map (fn [m] (:serial_number m)) (all-refs @current))

(get-last-id "verification")

(copy-record! 1307)

;; Копировать существующую запись
(copy-record! 1307)
(reset! current (get-last-id "verification"))
(reset! record (get-record @current))

;; Удалить запись
(delete-record! 2091)

(get-conditions "2023-09-04")

(insert-conditions! {:date "2023-09-04"
                     :temperature 23.1
                     :humidity 50.3
                     :pressusre 100.91
                     :voltage 222.4
                     ;:other "расход ГС (0,1 - 0,3) л/мин."
                     ;:location "ОГЗ"
                     ;:comment ""
                     })

;; documentations

(require '[clojure.repl :refer :all])

(find-doc "assoc")

(doc when)

(dir clojure.core)

)
