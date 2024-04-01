(ns metrology.view.conditions-panel)

(def column-settings
  [[:id 50 nil]
   [:date 100 nil]
   [:location 350 nil]
   [:temperature 75 nil]
   [:humidity 75 nil]
   [:pressure 75 nil]
   [:voltage 75 nil]
   [:frequency 75 nil]
   [:other 350 nil]
   [:comment 350 nil]])

(def toolbar-fields-settings
  '(["id" " id "]
    ["дата" " date "]
    ["локация" " location "]
    ["комментарий" " comment "]))
