(ns metrology.lib.midb
  (:require 
    [clojure.java.jdbc :as jdbc]
    [clojure.string]
    [clojure.pprint :refer [pprint]]
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

(defn find-methodology
  "Возвращает список записей методик поверки соответствующих запросу."
  [s]
  (jdbc/query midb [q/find-methodology (str "%" s "%")]))

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
        (list delete-v-gso! delete-v-refs! delete-v-opt-refs! delete-v-operations!
         delete-measurements! delete-verification!)))

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

(defn get-v-operations
  ""
  [id]
  (jdbc/query
    midb
    ["select * from view_v_operations where v_id = ?" id]))

(defn get-protocol
  ""
  [id]
  (first (jdbc/query
           midb
           ["select * from protocol where id = ?" id])))

(defn get-record
  "Возвращает hash-map записи о поверке."
  [id]
  (conj (hash-map :verification (get-verification id))
              (hash-map :protocol (get-protocol id))
              (hash-map :operations
                        (get-v-operations id))
              (map (fn [table]
                     (hash-map
                       (keyword table)
                       (jdbc/query midb
                                   [(str "select * from "
                                         table
                                         " where v_id = ?;")
                                    id])))
                   (list "v_gso" "v_refs" "v_opt_refs"
                    "v_operations" "measurements"))))

(defn get-protocol-data
  "Возвращает hash-map данных о поверке."
  [id]
  (apply conj (hash-map :protocol (get-protocol id))
              (hash-map :operations
                        (get-v-operations id))))
 
(defn get-conditions-by-v-id 
  [id]
  (->>
    ["select *
      from conditions
      where id = ?"
          (->>
            ["select conditions
              from verification
              where id = ?" id]
            (jdbc/query midb)
            first
            :conditions
           )]
    (jdbc/query midb)
    first))

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

(defn set-v-gso!
  [v-id coll]
  (do (delete-v-gso! v-id)
      (jdbc/insert-multi!
        midb
        :v_gso
        (vec (map (fn [el] (hash-map :v_id v-id :gso_id el))
                  coll)))))

(defn set-v-refs!
  [v-id coll]
  (do (delete-v-refs! v-id)
      (jdbc/insert-multi!
        midb
        :v_refs
        (vec (map (fn [el] (hash-map :v_id v-id :ref_id el))
                  coll)))))

(defn set-v-opt-refs!
  [v-id coll]
  (do (delete-v-opt-refs! v-id)
      (jdbc/insert-multi!
        midb
        :v_opt_refs
        (vec (map (fn [el] (hash-map :v_id v-id :ref_id el))
                  coll)))))

(defn set-v-operations!
  [v-id coll]
  (do (delete-v-opt-refs! v-id)
      (jdbc/insert-multi!
        midb
        :v_operations
        (vec (map (fn [el] (hash-map :v_id v-id :op_id el :result 1))
                  coll)))))

(defn gen-vals!
  ""
  [id]
  (let [metr (jdbc/query midb [q/metrology id])]
    ))

(defn ins-channel!
  "Вставить запись канала измерения и метрологических характеристик."
  [ch-obj mc-list]
  (let [ch-id
          (->>
            (jdbc/insert!
              midb
              :channels
              ch-obj)
            first
            vals
            first)]
    (map (fn [m]
             (jdbc/insert!
               midb
               :metrology
               (assoc m :channel_id ch-id)))
         mc-list)))

(defn tolerance
  "Возвращает значение допускаемой основной погрешности выраженное
   в абсолютных единицах.
   :m (hash-map :value ; error nominal
                :type_id ; error type
                :ref_value ; references value
                :r_from ; start point of range
                :r_to ; end point of range"
   [m]
   (cond (= (:type_id m) 0)
           (:value m)
         (= (:type_id m) 1)
           (double (/ (* (:value m) (:ref_value)) 100))
         (= (:type_id m) 2)
           (double (/ (* (:value m) (- (:r_to m) (:r_from m))) 100))))

(comment

(doc if-let)

)
