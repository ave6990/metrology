(ns metrology.lib.metrology
  (:require
    [clojure.math :as math]))

(defn error
  "Порешность измерения."
  ([x y]
    {:abs (- x y)
     :rel (if (not (zero? y))
              (* (double (/ (- x y) y)) 100)
              nil)
     :red nil})
  ([x y a]
    (let [res (error x y)]
      {:abs (:abs res)
       :rel (:rel res)
       :red (* (double (/ (- x y) a)) 100)}))
  ([x y a b]
    (error x y (- b a))))

(defn variation
  "Возвращает значение вариации в долях от основной погрешности."
  ([vg vl v-ref err err-type r-from r-to]
   (case err-type
     0 (double (/ (- vg vl) err))
     1 (* 100 (double (/ (- vg vl) v-ref err)))
     2 (* 100 (double (/ (- vg vl) (- r-to r-from) err)))))
  ([vg vl err]
   (variation vg vl 0 err 1 0 0))
  ([vg vl v-ref err r-from r-to]
   (variation vg vl 0 err 2 r-from r-to)))

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
    (round (+ (round val exp)
     (* discrete-val
        (round (double (/ 
                         (- val (round val exp))
                         discrete-val))))) (inc exp))))

(defn exponent
  [val]
  (let [t-val (if (zero? val)
                  0
                  (math/log10 val))]
    (if (pos? t-val)
        (math/ceil t-val))
        (math/floor t-val)))

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

(comment

(error 43 45 10 50)

)
