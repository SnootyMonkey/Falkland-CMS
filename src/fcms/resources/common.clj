(ns fcms.resources.common
  "Namespace for FCMS resources. FCMS resources are things stored in FCMS, so either collections, taxonomies or items."
  (:require [clojure.string :as s]
            [clj-time.format :refer (parse formatters unparse)]
            [clj-time.core :refer (now)]
            [com.ashafa.clutch :as clutch]
            [fcms.lib.slugify :refer (slugify)]
            [fcms.config :refer (db-resource)]))

;; ----- Properties common to all FCMS resources -----

(def
  ^{:no-doc true}
  reserved-properties
  "Properties that can't be specified during a create and are ignored during an update."
  #{:id :created-at :updated-at :type :version :links})
(def
  ^{:no-doc true}
  retained-properties
  "Properties that are retained during an update even if they aren't in the updated property set."
  #{:name :slug :created-at :type})

;; ----- ISO 8601 timestamp -----

(def
  ^{:no-doc true}
  timestamp-format (formatters :date-time-no-ms))

(defn- current-timestamp []
  (unparse timestamp-format (now)))

;; ----- Validation functions -----

(defn valid-name?
  "Return true if the provided name is a valid name, false if not"
  [n]
  ;; any non-blank string is a valid name
  (and (string? n) (not (s/blank? n))))

;; ----- CouchDB base functions -----

(defn
  ^{:no-doc true}
  db [] (clutch/get-database db-resource))

(defn
  ^{:no-doc true}
  from-view
  "Use the specified function of the specified view to return view contents (what was emitted by the view).
   Optionally include a key-value used to filter the documents.
   This works when you AREN'T already in a clutch/with-db macro."
  ([view function] (clutch/get-view (db) (name view) function {:include_docs false}))
  ([view function key-value] (clutch/get-view (db) (name view) function {:key key-value :include_docs false})))

(defn
  ^{:no-doc true}
  from-view-with-db
  "Use the specified function of the specified view to return view contents (what was emitted by the view).
   Optionally include a key-value used to filter the documents.
   This works when you ARE already in a clutch/with-db macro."
  ([view function] (clutch/get-view (name view) function {:include_docs false}))
  ([view function key-value] (clutch/get-view (name view) function {:key key-value :include_docs false})))

(defn
  ^{:no-doc true}
  doc-from-view
  "Use the specified function of the specified view to return CouchDB documents.
   Optionally include a key-value used to filter the documents.
   This works when you AREN'T already in a clutch/with-db macro."
  ([view function] (clutch/get-view (db) (name view) function {:include_docs true}))
  ([view function key-value] (clutch/get-view (db) (name view) function {:key key-value :include_docs true})))

(defn
  ^{:no-doc true}
  doc-from-view-with-db
  "Use the specified function of the specified view to return CouchDB documents.
   Optionally include a key-value used to filter the documents.
   This works when you ARE already in a clutch/with-db macro."
  ([view function] (clutch/get-view (name view) function {:include_docs true}))
  ([view function key-value] (clutch/get-view (name view) function {:key key-value :include_docs true})))

;; ----- Slug functions -----

(defn valid-slug?
  "Return true if the provided-slug is a valid slug and false if not"
  [provided-slug]
  ;; if the slug is the same one we'd provide for a resource with that name, then it's valid
  (and (string? provided-slug) (= provided-slug (slugify provided-slug)) (not (s/blank? provided-slug))))

(defn slug-in-collection?
  "Return true if an item or a taxonomy with the specified slug exists in the collection with the specified id, false if not."
  [coll-id slug]
  (if (first (doc-from-view-with-db :resource :all-ids-by-coll-id-and-slug [coll-id slug]))
    true
    false))

(defn
  ^{:no-doc true}
  next-slug
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
    (if-not (or (s/blank? slug) (slug-in-collection? coll-id slug))
      slug
      ;; recur with the next possible slug
      (recur coll-id (next-slug slug counter) (inc counter)))))

;; ----- Raw resource functions -----

(defn
  ^{:no-doc true}
  resource-doc
  "Get the CouchDB map representation of an item given the ID of its collection, the resource's slug and its type."
  [coll-id slug type]
  (:doc (first (doc-from-view-with-db type :all-ids-by-coll-id-and-slug [coll-id slug]))))

(defn
  ^{:no-doc true}
  map-from-db
  "Turn a resource from its CouchDB map representation into its FCMS map representation."
  [db-map]
  (if-let [data (:data db-map)]
    (-> 
      (assoc (dissoc data :type) :id (:_id db-map))
      (update-in [:created-at] parse)
      (update-in [:updated-at] parse))))

(defn
  ^{:no-doc true}
  resource-from-db
  "Turn a collection resource from its CouchDB map representation into its FCMS map representation."
  [coll-slug resource]
  (map-from-db (assoc-in resource [:data :collection] coll-slug)))

;; ----- Resource CRUD funcitons -----

(defn
  ^{:no-doc true}
  create-resource-with-db
  "Create an FCMS resource in the DB, returning the property map for the resource.
   This works when you ARE already in a clutch/with-db macro."
  [props provided-type]
  (let [timestamp (current-timestamp)]
    (clutch/put-document {:data (merge props {
      :version 1
      :created-at timestamp
      :updated-at timestamp
      :type (name provided-type)})})))

(defn
  ^{:no-doc true}
  create-resource
  "Create an FCMS resource in the DB, returning the property map for the resource.
   This works when you AREN'T already in a clutch/with-db macro."
  [props provided-type]
  (clutch/with-db (db)
    (create-resource-with-db props provided-type)))

(defn
  ^{:no-doc true}
  update-resource-with-db
  "Update the CouchDB document provided with a new version number, updated-at timestamp,
   and any new or updated properties from the provided map of props.
   This works when you ARE already in a clutch/with-db macro."
  [document props]
  (clutch/update-document document {:data (merge props {
    :version (inc (:version props))
    :updated-at (current-timestamp)})}))

(defn
  ^{:no-doc true}
  update-resource
  "Update the CouchDB document provided with a new version number, updated-at timestamp,
   and any new or updated properties from the provided map of props.
   This works when you AREN'T already in a clutch/with-db macro."
  [document props]
  (clutch/with-db (db)
    (update-resource-with-db document props)))

(defn
  ^{:no-doc true}
  delete-map
  "Given an array of the id and rev of a document, return the map needed to delete the document in a bulk update"
  [id-rev]
  {:_id (first id-rev) :_rev (last id-rev) :_deleted true})

(defn
  ^{:no-doc true}
  delete-resource
  "Delete the provided CouchDB document.
   This works when you AREN'T already in a clutch/with-db macro."
  [resource]
  (clutch/with-db (db)
    (clutch/delete-document resource)))