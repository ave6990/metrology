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
  ([x y]
    (/ (math/round (* x (math/pow 10 y)))
       (math/pow 10 y)))
  ([x]
    (round x 0)))

(defn average
  "Возращает среднее арифметическое переданных числовых значений."
  [& xs]
  (double (/
            (reduce + xs)
            (count xs))))

(defn discrete
  "Округляет значение с учетом заданной дискретности."
  [val discrete-val]
  (let [exp
    (let [t-val (math/log10 discrete-val)]
      (if (pos? t-val)
          (* -1 (inc (math/ceil t-val)))
          (* -1 (inc (math/floor t-val)))))]
    (+ (round val exp)
       (* discrete-val
          (round (double (/ 
                           (- val (round val exp))
                           discrete-val)))))))

(defn sko
  "Функция вычисления среднего квадратического отклонения."
  [& xs]
  (math/sqrt
    (/
      (reduce +
              (map
                (fn [x] (math/pow (- x (apply average xs)) 2))
                xs))
      (dec (count xs)))))
