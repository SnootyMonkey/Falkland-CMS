(ns fcms.resources.common
  (:require [clojure.string :refer (lower-case trim split join)]
            [com.ashafa.clutch :as clutch]
            [fcms.config :refer (db-resource)]))

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

(defn- internal-whitespace [slug]
  (join "-" (split slug #"[\p{Space}\p{P}]+")))

;; TODO implement
;; Slugify
;; Rules:
;; replace non-ASCII with normalized
;; replace A-Z with a-z
;; remove prefixed and trailing whitespace
;; replace internal white space with -
;; replace non-alpha-numeric with -
;; replace -- with -
;; replace - at the beginning and end with nothing
;; limit to 256 characters
;; call make-unique function
(defn slugify [resource-name make-unique]
  (-> resource-name 
    lower-case
    trim
    internal-whitespace
    make-unique))

(defn valid-slug? [provided-slug]
  ;; if the slug is the same one we'd provide for a resource with that name, then it's valid 
  (= provided-slug (slugify provided-slug identity)))

;; TODO set timestamps
;; TODO base version
;; TODO provide slugify a uniqueness function for the type
(defn create-with-db
  [{resource-name :name provided-slug :slug :as props} provided-type]
  (let [slug (or provided-slug (slugify resource-name identity))]
    (clutch/put-document {:data (merge props {:slug slug :type (name provided-type)})})))

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