(ns fcms.models.collection
  (:require [com.ashafa.clutch :as clutch]
            [fcms.models.base :as base]))

(def collection-media-type "application/vnd.fcms.collection+json")

(defn create-collection
  "Create a new collection using the specified name and optional map of properties.
  If :slug is included in the properties it will be used as the collection's slug,
  otherwise one will be created from the name."
  ([name] (create-collection name {}))
  ([name props] (base/create (merge props {:name name}) :collection)))

(defn all-collections [])

;; TODO get a nice map of the collection, not CouchDB's
(defn get-collection
  "Gives a slug return the collection as a map, or nil if there's no collection with that slug"
  [slug]
  (clutch/with-db (base/db)
    (:doc (first (clutch/get-view "collection" :all {:key slug :include_docs true})))))