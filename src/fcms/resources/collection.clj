(ns fcms.resources.collection
  (:require [com.ashafa.clutch :as clutch]
            [fcms.resources.common :as common]))

(def collection-media-type "application/vnd.fcms.collection+json;version=1")

(defn create-collection
  "Create a new collection using the specified name and optional map of properties.
  If :slug is included in the properties it will be used as the collection's slug,
  otherwise one will be created from the name."
  ([name] (create-collection name {}))
  ([name props] (when-let [collection (common/create (merge props {:name name}) :collection)]
    (common/map-from-db collection))))

(defn get-collection
  "Given the slug of the collection, return the collection as a map, or nil if there's no collection with that slug"
  [slug]
  (clutch/with-db (common/db)
    (if-let [coll (:doc (first (clutch/get-view "collection" :all {:key slug :include_docs true})))]
      (common/map-from-db coll))))

;; TODO delete the items as well
(defn delete-collection
  "Given the slug of the collection, delete it and all its contents and return :ok,
  or return :bad-collection if the collection slug is not good"
  [slug]
  (if-let [id (clutch/with-db (common/db)
    (:id (first (clutch/get-view "collection" :all {:key slug :include_docs false}))))]
      (do (common/delete-by-id id) :ok)
      :bad-collection))

(defmacro with-collection
  "Given the slug of the collection, execute some code with the retrieved collection
  lexically scoped as collection, or return :bad-collection if the collection slug is no good"
  [coll-slug & body]
  `(if-let [~'collection (get-collection ~coll-slug)]
    (clutch/with-db (common/db)
      ~@body)
    :bad-collection))

(defn item-count
  "Given the slug of the collection, return the number of items it contains,
  or return :bad-collection if the collection slug is no good"
  [coll-slug]
  (with-collection coll-slug
    (if-let [result (first (clutch/get-view "item" :count-by-collection {:key (:id collection) :include_docs false}))]
      (:value result)
      0)))

(defn all-items [coll-slug])

(defn all-collections [])