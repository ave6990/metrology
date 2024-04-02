(ns metrology.view.gso-panel-settings)

(def column-settings
  [[:id 50 nil]
   [:number_1c 75 nil]
   [:type 75 nil]
   [:number 150 nil]
   [:level 50 nil]
   [:available 50 nil]
   [:components 150 nil]
   [:concentration 75 nil]
   [:uncertainity 75 nil]
   [:units 75 nil]
   [:pass_number 100 nil]
   [:date 100 nil]
   [:expiration_date 100 nil]
   [:metrology 300 nil]])

(def toolbar-fields-settings
  '(["id" " id "]
   ["дата" " date "]
   ["№ паспорта" " pass_number "]
   ["номер 1С" " number_1c "]
   ["концентрация %" " conc "]
   ["рег. №" " number "]
   ["срок годности" " expiration_date "]))
