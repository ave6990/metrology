(ns metrology.view.measurements-panel-settings)

(def column-settings
  [[:id 50 nil]
   [:v_id 50 nil]
   [:serial_number 100 nil]
   [:channel_name 150 nil]
   [:channel 150 nil]
   [:value 75 nil]
   [:value_2 75 nil]
   [:ref_value 75 nil]
   [:text 100 nil]
   [:error_value 75 nil]
   [:error_fraction 75 nil]
   [:error_units 75 nil]
   [:error_type 50 nil]
   [:comment 300 nil]])

(def toolbar-fields-settings
  '(["id" " meas.id "]
    ["v_id" " v.id "]
    ["зав. №" " v.serial_number "]))
