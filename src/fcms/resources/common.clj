(ns fcms.resources.common
  (:require [clojure.string :as s]
            [com.ashafa.clutch :as clutch]
            [fcms.lib.slugify :refer (slugify)]
            [fcms.config :refer (db-resource)]))

(defn valid-slug? [provided-slug]
  ;; if the slug is the same one we'd provide for a resource with that name, then it's valid 
  (= provided-slug (slugify provided-slug)))

(defn db []
  (clutch/get-database db-resource))

(defn all-meta []
  (clutch/with-db (db)
    (clutch/all-documents)))

(defn all []
  (clutch/with-db (db)
    (clutch/all-documents {:include_docs true})))

(defn map-from-db
  "Turn the CouchDB map into the FCMS map"
  [db-map]
  (if-let [data (:data db-map)]
    (assoc (dissoc data :type) :id (:_id db-map))))

;; TODO set timestamps
(defn create-with-db [props provided-type]
  (clutch/put-document {:data (merge props {
    :version 1
    :type (name provided-type)})}))

;; TODO update version
(defn update-with-db [document props]
  (clutch/update-document document {:data props}))

(defn create
  "Create a resource in the DB, returning the property map for the resource."
  [props provided-type]
  (clutch/with-db (db)
    (create-with-db props provided-type)))

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