(ns metrology.lib.metrology)

(defn delta
  ([value tolerance percent]
    (let [dif (double (/ (* value tolerance) 100))]
      (list (- value dif) (+ value dif))))
  ([value tolerance]
    (list (- value tolerance) (+ value tolerance))))


