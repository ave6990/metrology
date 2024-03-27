(ns metrology.controller.main-menu
  (:require 
    [seesaw.core :refer :all]))

(def about-action
  (action
    :handler (fn [e] (alert "MIdb version 0.0.1"))
    :name "About"
    :key "menu O"))

(def exit-action
  (action
    :handler (fn [e] (.dispose (to-frame e)))
    :name "Exit"
    :key "menu X"))

(def copy-action
  (action
    :handler (fn [e] (alert "Nothing yet"))
    :name "Copy"))

(def paste-action
  (action
    :handler (fn [e] (alert "Nothing yet"))
    :name "Edit"))
