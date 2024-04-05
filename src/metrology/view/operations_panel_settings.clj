(ns metrology.view.operations-panel-settings)

(def column-settings
  [[:id 50 nil]
   [:v_id 50 nil]
   [:result 50 nil]
   [:section 50 nil]
   [:name 350 nil]
   [:unusability 350 nil]
   [:verification_type 50 nil]
   [:comment 150 nil]])

(def toolbar-fields-settings
  '(["id" " op.id "]
    ["v_id" " v_op.v_id "]
    ["МП" " op.methodology_id "]))
