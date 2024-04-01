(ns metrology.view.gso-panel-settings)

(def column-settings
  [[:id 50 nil]
   [:number_1c 75 nil]
   [:type 75 nil]
   [:number 150 nil]
   [:available 50 nil]
   [:components 150 nil]
   [:concentration 100 nil]
   [:uncertainity 100 nil]
   [:units 100 nil]
   [:pass_number 100 nil]
   [:date 100 nil]
   [:expiration_date 100 nil]])

(def toolbar-fields-settings
  '(["id" " id "]
   ["дата поступления" " date "]
   ["дата_изготовления" " manufacture_date "]
   ["состав" " components "]
   ["срок годности" " expiration_date "]
   ["номер 1С" " number_1c "]
   ["номер ГСО" " number "]
))
