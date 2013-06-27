(ns fcms.models.common
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

(defn all []
  (clutch/with-db (db)
    (clutch/all-documents {:include_docs true})))

(defn map-from-db [db-map]
  "Turn the CouchDB map into the FCMS map"
  (if-let [data (:data db-map)]
    (assoc (dissoc data :type) :id (:_id db-map))))

;; Slugify
;; Rules:
;; replace A-Z with a-z
;; replace non-alpha-numeric with -
;; replace -- with -
;; replace - at the end with nothing
;; if not unique, add -1 to the end and increment 1 until it is unique for that type
(defn slugify [doc-name type-name]
  (str/lower-case doc-name))

;; TODO set timestamps
;; TODO base version
(defn create
  "Create a resource in the DB, returning the property map for the resource."
  [{doc-name :name provided-slug :slug :as props} provided-type]
  (clutch/with-db (db)
    (let [slug (or provided-slug (slugify doc-name (name provided-type)))]
      (clutch/put-document {:data (merge props {:slug slug :type (name provided-type)})}))))

(defn retrieve [id]
  (clutch/with-db (db)
    (clutch/get-document id)))

(defn delete [resource]
  (clutch/with-db (db)
    (clutch/delete-document resource)))

(defn delete-by-id [id]
  (let [resource (retrieve id)]
    (if-not (nil? resource)
      (delete resource))))