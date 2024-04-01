(ns metrology.controller.main-menu
  (:require 
    [seesaw.core :refer :all]))

(def main-about-action
  (action
    :handler (fn [e] (alert "MIdb version 0.0.1"))
    :name "О программе"
    :key "menu O"))

(def main-exit-action
  (action
    :handler (fn [e] (.dispose (to-frame e)))
    :name "Выход"
    :key "menu X"))

(def frame-gso-action 
  (action
    :handler (fn [e] (alert "Nothing yet"))
    :name "ГСО"))

(def frame-conditions-action
  (action
    :handler (fn [e] (alert "Nothing yet"))
    :name "Условия поверки"))

(def frame-counteragents-action
  (action
    :handler (fn [e] ())
    :name "Контрагенты"))
