(ns metrology.core
  (:gen-class)
  (:require 
    [lib.chemistry :as ch]
    [clojure.math :refer :all]
    [clojure.core :refer :all]
    [clojure.repl :refer :all]
    [clojure.java.jdbc :as jdbc]))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))


(defn delta%
  [value tolerance]
  (let [dif (double (/ (* value tolerance) 100))]
    (list (- value dif) (+ value dif))))

(defn delta
  [value tolerance]
  (list (- value tolerance) (+ value tolerance)))

(defn lel
  [value component]
  ())

(comment

(delta% 0.94 5)

(map (fn [x] (* (/ x 4.4) 100)) '(0.216 0.663 0.769 1.077))

(def lel (hash-map "ch4" 4.4 "c3h8" 1.7))

(some (fn [x] (= x "c3h4")) (keys lel))

(some (partial = "c3h4") (keys lel))

(in-ns 'user)

(in-ns 'metrology.metr)

(re-seq #"[A-Z][a-z]?\d{0,2}" "NO2")

(reduce + '(1 2 3 4))

(clojure.string/upper-case "hello")

;; documentation functions

(apropos "upper")

(doc clojure.string/upper-case)

(find-doc "reduce")

(source clojure.core/some)

(dir clojure.core)

)
