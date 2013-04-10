(ns fcms.models.base
  (:require [clojure.string :as str]
            [com.ashafa.clutch :as clutch])
  (:import (java.net URI)))

(def db-resource (assoc (cemerick.url/url "http://localhost:5984/" "falklandcms")
                    :username nil
                    :password nil)) 

(defn db []
  (clutch/get-database db-resource))

(defn all-meta []
  (clutch/with-db (db)
    (clutch/all-documents)))
 
(defn create [params]
  (clutch/with-db (db)
    (clutch/put-document {:data params})))

(defn all []
  (clutch/with-db (db)
    (clutch/all-documents {:include_docs true})))

(defn retrieve [id]
  (clutch/with-db (db)
    (clutch/get-document id)))

(defn delete [item]
  (clutch/with-db (db)
    (clutch/delete-document item)))

(defn delete-by-id [id]
  (let [item (retrieve id)]
    (if-not (nil? item)
      (delete item))))