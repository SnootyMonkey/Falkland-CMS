(ns fcms.resources.common
  (:require [clojure.string :as s]
            [clj-time.format :refer (formatters unparse)]
            [clj-time.core :refer (now)]
            [com.ashafa.clutch :as clutch]
            [fcms.lib.slugify :refer (slugify)]
            [fcms.config :refer (db-resource)]))

(def reserved-properties
  "Properties that can't be specified during a create or update."
  #{:id :created-at :updated-at :type :version :links}) 
(def retained-properties
  "Properties that are retained during an update even if they aren't in the updated property set."
  #{:name :slug :created-at :type})

(def timestamp-format (formatters :date-time-no-ms))

;; CouchDB functions

(defn db [] (clutch/get-database db-resource))

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

;; Slug functions

(defn valid-slug? [provided-slug]
  ;; if the slug is the same one we'd provide for a resource with that name, then it's valid 
  (and (= provided-slug (slugify provided-slug) (not (s/blank? provided-slug)))))

(defn slug-in-collection? [coll-id slug]
  (if (first (doc-from-view-with-db :fcms :all-ids-by-coll-id-and-slug [coll-id slug]))
    true
    false))

(defn next-slug 
  "Generate the next possible slug to attempt by removing the old counter suffix and adding the new counter suffix."
  [slug counter]
    ;; if the slug is blank then it's just the counter
    (if (= slug "")
      (str (inc counter))
      (str (s/replace slug (java.util.regex.Pattern/compile (str "-" counter "$")) "") "-" (inc counter))))

(defn unique-slug
  "Make a given slug unique in the collection if needed by incrementing a slug counter
  numeral and appending it with a dash until you have a unique slug. If the slug is already
  unique it is just returned."
  ([coll-id slug] (unique-slug coll-id slug 0))
  ([coll-id slug counter] 
    (if-not (or (slug-in-collection? coll-id slug) (= slug ""))
      slug
      ;; recur with the next possible slug
      (recur coll-id (next-slug slug counter) (inc counter)))))

;; Raw resource functions

(defn resource-doc [coll-id slug type]
  "Get the CouchDB map representation of an item given the ID of its collection, the resource's slug and its type."
  (:doc (first (doc-from-view-with-db type :all-ids-by-coll-id-and-slug [coll-id slug]))))

(defn map-from-db
  "Turn the CouchDB map into the FCMS map"
  [db-map]
  (if-let [data (:data db-map)]
    (assoc (dissoc data :type) :id (:_id db-map))))

(defn resource-from-db 
  "Turn an item from its CouchDB map representation into its FCMS map representation."
  [coll-slug resource]
  (map-from-db (assoc-in resource [:data :collection] coll-slug)))

;; ISO 8601 timestamp

(defn- current-timestamp [] 
  (unparse timestamp-format (now)))

;; CRUD funcitons

(defn all-meta []
  (clutch/with-db (db)
    (clutch/all-documents)))

(defn all []
  (clutch/with-db (db)
    (clutch/all-documents {:include_docs true})))

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

(defn delete-map
  "Given an array of the id and rev of a document, return the map needed to delete the document in a bulk update"
  [id-rev]
  {:_id (first id-rev) :_rev (last id-rev) :_deleted true})

(defn delete [resource]
  (clutch/with-db (db)
    (clutch/delete-document resource)))

(defn delete-by-id [id]
  (let [resource (retrieve id)]
    (if-not (nil? resource)
      (delete resource))))