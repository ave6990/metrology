(ns metrology.lib.calc
  (:require
    [clojure.math :as math]))

(sheet-count
  ([l width]
    (hash-map
      :count (math/ceil (double (/ l width)))
      :width (- l (* width (math/ceil (double (/ l width)))))))
  ([l]
    (sheet-count l 1.1)))


