(ns metrology.lib.midb
  (:require 
    [clojure.java.jdbc :as jdbc]
    [clojure.string :as string]
    [clojure.pprint :refer [pprint]]
    [metrology.lib.database :as db]
    [metrology.lib.midb-queries :as q]
    [metrology.lib.chemistry :as ch]
    [metrology.lib.protocol :as pr]
    [metrology.view.report :as report]))

(def midb-path
  ;"/mnt/d/UserData/YandexDisk/Ermolaev/midb/"
  "/media/sf_YandexDisk/Ermolaev/midb/")

(db/defdb midb)
(db/defdb auto)

(defn load-icu
  ;; {TOFIX} не работает.
  []
  (try
    (jdbc/query
      midb
      "select load_extension('/media/sf_YandexDisk/Ermolaev/midb/icu.so');")
    (catch Exception e (print e))))

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
  (map (fn [m]
           (:id m))
       (jdbc/query midb [q/find-mi (str "%" s "%")])))

(defn find-verification
  ""
  ([s]
    (map (fn [m]
             (:id m))
         (jdbc/query
          midb
          (string/replace
            q/find-verification
            "{where}"
            s)))))

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
        (list delete-v-gso! delete-v-refs! delete-v-opt-refs!
         delete-v-operations!
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

(defn get-record
  "Возвращает hash-map записи о поверке."
  [id]
  (conj (hash-map :verification (get-verification id))
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

(defn get-protocols-data
  ""
  [where]
  (let [data (jdbc/query
                midb
                (str "select * from protocol where " where))
        measurements (jdbc/query
                        midb
                        (str "select * from view_v_measurements where " where))]
    (map (fn [m]
             (assoc m
                    :measurements
                    (doall (filter (fn [r]
                                (= (:id r) (:id m)))
                            measurements))))
         data)))

(defn assoc-multi
  [m nm]
  (reduce (fn [a b]
            (let [[k v] b]
              (assoc a k v)))
          m
          nm))

(defn get-report-data
  ""
  [coll]
  (let [v-data (jdbc/query
                midb
                (str
                  q/report-verifications
                  "("
                  (string/join ", " coll)
                  ")"))
        measurements (jdbc/query
                      midb
                      (str "select * from view_v_measurements
                          where id in ("
                          (string/join ", " coll)
                          ")"))
        operations (jdbc/query
                    midb
                    (str
                      q/get-operations
                      "("
                      (string/join ", " coll)
                      ")"))
        refs (jdbc/query
              midb
              (str "select * from verification_refs
                      where v_id in ("
                      (string/join ", " coll)
                      ")"))]
    (map (fn [m]
             (assoc-multi m
                          {:measurements
                           (doall (filter (fn [r]
                                              (= (:id r) (:id m)))
                                          measurements))
                           :refs
                           (doall (filter (fn [r]
                                              (= (:v_id r) (:id m)))
                                          refs))
                           :operations
                           (doall (filter (fn [r]
                                              (= (:v_id r) (:id m)))
                                          operations))}))
         v-data)))

(defn get-methodology-data
  [coll]
  (let [met-data
         (jdbc/query
           midb
           (str "select * from methodology
                 where id in ("
                 (string/join
                   ", "
                   coll)
                 ")"))
        metrology-data
          (jdbc/query
            midb
            (str "select * from view_metrology
                  where id in ("
                  (string/join
                    ", "
                    coll)
                  ")"))
        operations-data
          (jdbc/query
            midb
            (str "select * from verification_operations
                  where methodology_id in ("
                  (string/join
                    ", "
                    coll)
                  ")"))]
    (map (fn [m]
             (assoc-multi m
                          {:metrology
                           (doall (filter (fn [r]
                                              (= (:id r) (:id m)))
                                          metrology-data))
                           :operations
                           (doall (filter (fn [r]
                                              (= (:methodology_id r) (:id m)))
                                          operations-data))}))
         met-data)))

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

(defn get-operations
  "Возвращает коллекцию операций поверки по заданному v_id."
  [id]
  (jdbc/query
    midb
    ["select * from view_operations where v_id = ?" id]))

(defn gen-protocols
  "Генерирует протоколы поверки в файл protocol.html."
  [where]
  (spit
    (str midb-path
         "protocol.html")
         (pr/protocols (get-protocols-data where))))

(defn gen-report
  "генерирует отчет о записях в файл report.html."
  ([coll]
   (spit
     (str midb-path
          "report.html")
         (report/verification-report (get-report-data coll))))
  ([from to]
   (gen-report (range from (inc to)))))

(defn gso
  ([where]
  (spit
    (str midb-path "gso.html")
    (report/gso-report
      (jdbc/query
        midb
        (str "select * from gso"
             (if (= "" where)
                 ""
                 (str " where " where))
             " order by available desc, components, conc")))))
  ([]
   (gso "")))

(defn methodology
  [coll]
  (spit
    (str midb-path
         "methodology.html")
    (report/methodology-report (get-methodology-data coll))))

(defn gen-values!
  "Записывает в БД случайные значения результатов измерений в пределах
   основной погрешности."
  [where]
  (map (fn [prot] 
           (map (fn [m]
                    (jdbc/update!
                      midb
                      :measurements
                      {:value (if (not= (:error_type m) 5)
                                  (pr/gen-value m))}
                      ["id = ?" (:measurement_id m)]))
                (:measurements prot)))
       (get-protocols-data where)))

(defn gs2000
  ([gen-n gas s-conc t-conc-coll]
    (map (fn [c]
             ((gs/calculator (gs/passports gen-n))
                             gas
                             :air
                             s-conc
                             c))
         t-conc-coll))
  ([gen-n s-conc t-conc-coll]
    (map (fn [c]
             ((gs/calculator (gs/passports gen-n))
                             :air
                             s-conc
                             c))
         t-conc-coll)))

(comment

(doc if-let)

)
