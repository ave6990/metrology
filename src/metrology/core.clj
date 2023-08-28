(ns metrology.core
  (:gen-class)
  (:require 
    ;;[clojure.math :refer :all]
    ;;[clojure.core :refer :all]
    [clojure.repl :refer :all]
    [metrology.lib.chemistry :as ch]
    [metrology.lib.metrology :as m]
    [metrology.lib.calc :as c]
    [metrology.lib.gs2000 :as gs]
    [metrology.lib.tasks :as db]))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))

(comment

(m/delta 0.94 5 :%)

((m/range-converter 4 20 0 200) 13.964)

(m/round 12.1236 3)

(map (fn [x] (* (/ x 4.4) 100)) '(0.216 0.663 0.769 1.077))

(def lel (hash-map "ch4" 4.4 "c3h8" 1.7))

(some (fn [x] (= x "c3h4")) (keys lel))

(some (partial = "c3h4") (keys lel))

(re-seq #"[A-Z][a-z]?\d{0,2}" "NO2")

(clojure.string/upper-case "hello")

(.toString (java.time.LocalDateTime/now))

(macroexpand '(defn func [x] (x)))

;; ГС-2000

((gs/calculator (gs/passports 0)) :air 3015 50)

((gs/calculator (gs/passports 0)) "H2S" :air 3015 50)

(gs/re-calculate (gs/passports 0) :air 3015 2r1100100000)

(ch/ppm->mg "H2S" (gs/re-calculate (gs/passports 0) :air 3015 2r1100100000))

;; change namespace

(in-ns 'user)

(in-ns 'metrology.core)

;; documentation functions

(apropos "round")

(doc clojure.string/upper-case)

(doc min)

(doc gs/calculator)

(find-doc "index")

(source clojure.core/some)

(dir clojure.math.combinatorics)

)
