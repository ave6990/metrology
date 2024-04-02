(ns metrology.view.verifications-panel-settings)

(def column-settings
  [[:id 50 nil]
   [:upload 25 nil]
   [:count 100 nil]
   ;[:ca nil nil]
   [:ca_short_name 350 nil]
   ;[:conditions nil nil]
   [:date 100 nil]
   [:v_type 30 nil]
   [:interval 30 nil]
   [:protocol_number 50 nil]
   [:registry_number 100 nil]
   [:mi_type 250 nil]
   ;[:meth_id nil nil]
   [:serial_number 150 nil]
   [:year 75 nil]
   [:channels 50 nil]
   [:components 350 nil]
   [:scope 350 nil]
   [:area 30 nil]
   [:sw_name 100 nil]
   [:sw_version 100 nil]
   [:sw_version_real 100 nil]
   [:sw_checksum 100 nil]
   [:sw_algorithm 100 nil]
   ;[:voltage nil nil]
   ;[:other nil nil]
   ;[:sign_mi nil nil]
   ;[:sign_pass nil nil]
   [:protocol 100 nil]
   [:copy_from 100 nil]
   [:comment 300 nil]])

(def toolbar-fields-settings
  '(["id" " id "]
   ["выгрузка" " upload "]
   ["год" " manufacture_year "]
   ["дата" " date "]
   ["зав. №" " serial_number "]
   ["количество" " channels "]
   ["контрагент" " short_name "]
   ["объем" " scope "]
   ["протокол" " protocol_number "]
   ["рег. №" " registry_number "]
   ["состав" " components "]
   ["счет" " count "]
   ["тип СИ" " mi_type "]))
