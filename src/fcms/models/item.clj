(ns fcms.models.item
  (:use [fcms.models.base :exclude [db-resource]])
  (:require [com.ashafa.clutch :as clutch]))
 
(defn create [params]
  (clutch/with-db (db)
    (clutch/put-document {:data params})))

(defn delete [item]
  (clutch/with-db (db)
    (clutch/delete-document item)))

(defn retrieve [id]
  (clutch/with-db (db)
    (clutch/get-document id)))