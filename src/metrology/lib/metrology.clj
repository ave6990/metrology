(ns metrology.lib.metrology
  (:require
    [clojure.math :as math]))

(defn delta
  "Возвращает границы диапазона значений 
  (value - tolerance, value + tolerance)
  , если передан третий аргумент, то значение tolerance расчитывается как
  процент от value."
  ([value tolerance percent]
    (let [dif (double (/ (* value tolerance) 100))]
      (list (- value dif) (+ value dif))))
  ([value tolerance]
    (list (- value tolerance) (+ value tolerance))))

(defn range-converter
  "Возвращает функцию-конвертер диапазонов."
  [s-min s-max d-min d-max]
  (fn
    [val]
    (+ d-min (/ (* (- val s-min) (- d-max d-min)) (- s-max s-min)))))

(defn round
  "Возвращает значение округленное до заданного знака после запятой или
  до целых, если передан один аргумент."
  ([val prec]
    (/ (math/round (* val (math/pow 10 prec)))
        (math/pow 10 prec)))
  ([val]
    (round val 0)))

(defn average
  "Возращает среднее арифметическое переданных числовых значений."
  [& vals]
  (let [sum (reduce + vals)]
    (double (/ sum (count vals)))))
