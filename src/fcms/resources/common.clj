(ns fcms.resources.common
  (:require [clojure.string :as s]
            [clj-time.format :refer (formatters unparse)]
            [clj-time.core :refer (now)]
            [com.ashafa.clutch :as clutch]
            [fcms.lib.slugify :refer (slugify)]
            [fcms.config :refer (db-resource)]))

(def properties [:name :slug :type :version :created-at :updated-at])

(def timestamp-format (formatters :date-time-no-ms))

(defn valid-slug? [provided-slug]
  ;; if the slug is the same one we'd provide for a resource with that name, then it's valid 
  (= provided-slug (slugify provided-slug)))

(defn next-slug 
  "Generate the next possible slug to attempt by removing the old counter suffix and adding the new counter suffix."
  [slug counter]
    ;; if the slug is blank then it's just the counter
    (if (= slug "")
      (str (inc counter))
      (str (s/replace slug (java.util.regex.Pattern/compile (str "-" counter "$")) "") "-" (inc counter))))

(defn delete-map
  "Given an array of the id and rev of a document, return the map needed to delete the document in a bulk update"
  [id-rev]
  {:_id (first id-rev) :_rev (last id-rev) :_deleted true})

(defn db []
  (clutch/get-database db-resource))

(defn from-view
  ([view function] (clutch/get-view (db) (name view) function {:include_docs false}))
  ([view function key-value] (clutch/get-view (db) (name view) function {:key key-value :include_docs false})))

(defn from-view-with-db
  ([view function] (clutch/get-view (name view) function {:include_docs false}))
  ([view function key-value] (clutch/get-view (name view) function {:key key-value :include_docs false})))

(defn doc-from-view
  ([view function] (clutch/get-view (db) (name view) function {:include_docs true}))
  ([view function key-value] (clutch/get-view (db) (name view) function {:key key-value :include_docs true})))

(defn doc-from-view-with-db
  ([view function] (clutch/get-view (name view) function {:include_docs true}))
  ([view function key-value] (clutch/get-view (name view) function {:key key-value :include_docs true})))

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

;; ISO 8601 timestamp
(defn- current-timestamp [] 
  (unparse timestamp-format (now)))

(defn create-with-db [props provided-type]
  (let [timestamp (current-timestamp)]
    (clutch/put-document {:data (merge props {
      :version 1
      :created-at timestamp
      :updated-at timestamp
      :type (name provided-type)})})))

(defn create
  "Create a resource in the DB, returning the property map for the resource."
  [props provided-type]
  (clutch/with-db (db)
    (create-with-db props provided-type)))

(defn update-with-db [document props]
  (clutch/update-document document {:data (merge props {
    :version (inc (:version props))
    :updated-at (current-timestamp)})}))

(defn update [document props]
  (clutch/with-db (db)
    (update-with-db document props)))

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