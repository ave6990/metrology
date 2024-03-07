(ns metrology.protocols.custom
  (:require
    [clojure.string :as string]
    [clojure.math :as math]
    [metrology.lib.metrology :as m]
    [metrology.lib.gen-html :refer :all]
    [metrology.utils.sequence :refer :all]))

(defn point
  [name content]
  (li
    {:class "appendix-section"}
    name
    content))

(defn appendix
  [& l]
  (string/join
    "\n"
    l))

(defn str->float
  [s]
  (Float/parseFloat (string/replace s "," ".")))

(defn get-vals-by-key
  [k m]
  (map (fn [item]
           (k item))
       m))

;; #fp12#фп12#protocol
(defn fp12
  []
  (string/join
    "\n" 
    (list (li
            {:class "appendix-section"}
            "Внешний осмотр: "
            (em "соответствует требованиям п. 7.1 МП.")
            (p (em "Внешние повреждения и загрязнения отсутствуют. Маркировка соответствует требованиям НД.")))
          (li
            {:class "appendix-section"}
            "Опробование: "
            (em "соответствует требованиям п. 7.2 МП."))
          (li
            {:class "appendix-section"}
            "Проверка порога чувствительности: "
            (em "соответствует требованиям п. 7.3 МП.")
            (p (em "При подаче ПГС № 5 срабатывает прерывистая звуковая и световая сигнализация, заполнение шкалы 3/4.")))
          (li
            {:class "appendix-section"}
            "Проверка времени срабатывания сигнализации: "
            (em "соответствует требованиям п. 7.4 МП."))
          (li
            {:class "appendix-section"}
            "Проверка порога срабатывания и абсолютной погрешности: "
            (em "соответствует требованиям п. 7.5 МП.")
            (p (em "При подаче ПГС № 7 срабатывает постоянная звуковая и световая сигнализация отображается символ «П»."))))))

(defn pr-68261-17
  "ГХ-М (АМ-5), АМ-0059"
  [m]
  (let [meas (vec (:measurements m))
        t (str->float (:temperature m))
        p (str->float (:pressure m))
        vals (vec (map (fn [i]
                           (:value i))
                       meas))
        germ (get vals 0)
        ac-val (/ (apply + vals) 3)
        a-val (m/round ac-val 1)
        n-val (m/round
                (/ (* a-val p 293.2) (* (+ t 273.2) 101.3))
                1)
        err (m/round (- 100 n-val) 1)]
    (appendix
      (point
        "Герметичность:"
        (em
          (str
            (:value germ)
            " см³ (не более 3 см³).")))
      (point
        "Определение метрологических характеристик: "
        (table
          (tr
            (th {:colspan 3}
                "Измеренный объем, см³")
            (th "Среднее значение, см³")
            (th "Значение объема приведенное к Н. У., см³")
            (th "Абсолютная погрешность, см³")
            (th "Допускаемая асолютная погрешность, см³"))
          (string/replace
            (tr
              (td (get vals 1))
              (td (get vals 2))
              (td (get vals 3))
              (td a-val)
              (td n-val)
              (td err)
              (td 5)) "." ","))))))

(defn pr-35823-07
  "Altairt H2S"
  [m]
  (let [meas (vec (:measurements m))
        vals (vec (map (fn [i]
                           (:measurement_text i))
                       meas))
        refs (vec (map (fn [i]
                           (:ref_value i))
                       meas))
        desc (vec (map (fn [i]
                           (:metrology_text i))
                       meas))]
    (appendix
      (point
        "Определение основной абсолютной погрешности сигнализатора:"
        (table
          (tr
            (th "Пороги срабатывания сигнализации, млн⁻¹")
            (th "Номер ПГС")
            (th "Номинальное значение концентрации определяемого компонента ПГС, млн⁻¹")
            (th "Состояние сигнализации при подаче ПГС по НД")
            (th "Состояние сигнализации соответствует требованиям НД (да/нет)"))
          (string/replace
            (string/join "\n"
              (list
                (tr
                  (td {:rowspan 2}
                      7)
                  (td 1)
                  (td (get refs 0))
                  (td (get desc 0))
                  (td (get vals 0)))
                (tr
                  (td 2)
                  (td (get refs 1))
                  (td (get desc 1))
                  (td (get vals 1)))
                (tr
                  (td {:rowspan 2}
                      14)
                  (td 3)
                  (td (get refs 2))
                  (td (get desc 2))
                  (td (get vals 2)))
                (tr
                  (td 4)
                  (td (get refs 3))
                  (td (get desc 3))
                  (td (get vals 3)))
                ))
            "."
            ","))))))

(defn get-channels-list
  [meas]
  (unique (get-vals-by-key
            :channel_name
            meas)))

(defn pr-18482-08
  "Кристалл-5000 NB_edit"
  [m]
  (let [meas (vec (:measurements m))
        ch-count (:channels m)
        noise-rows
          (string/join "\n"
            (map (fn [item]
                     ())
                 meas))]
    (appendix
      (point
        "Определение уровня флуктуационных шумов и дрейфа нулевого сигнала:"
        (table
          (tr
            (th {:rowspan 2}
                "Детектор")
            (th {:colspan 3}
                "Значение уровня шумов")
            (th {:colspan 3}
                "Значение дрейфа"))
          (string/join "\n"
            (repeat 2
                    (tr
                      (th "допускаемое")
                      (th "действительное")
                      (th "ед. изм."))))
          noise-rows)))))

(comment

  (require '[metrology.utils.sequence :refer :all] :reload)

  (clojure.math/round 0.2342)

  (rand-nth '(1 4 8))

  (get (vec '(1 2 3)) 2)

  (find-doc "slice")

  (doc peek)

)
