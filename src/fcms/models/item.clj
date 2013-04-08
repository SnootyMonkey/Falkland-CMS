(ns fcms.models.item
  (:require [fcms.models.base :as base]))
 
(defn create-item [params]
  (base/create (assoc params :type :item)))
