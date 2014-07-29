(ns fcms.resources.collection
  "Namespace for FCMS collections. A collection is a container for all the items that you'd like to be organized
  and searched together."
  (:require [clojure.set :refer (intersection)]
            [clojure.string :refer (blank?)]
            [clojure.walk :refer (keywordize-keys)]
            [com.ashafa.clutch :as clutch]
            [fcms.lib.slugify :refer (slugify)]
            [fcms.resources.common :as common]))

(def
  ^{:no-doc true}
  collection-media-type "application/vnd.fcms.collection+json;version=1")
(def
  ^{:no-doc true}
  collection-collection-media-type "application/vnd.collection+vnd.fcms.collection+json;version=1")

(defn get-collection
  "Given the slug of the collection, return the collection as a map, or nil if there's no collection with that slug"
  [slug]
  (if-let [coll (:doc (first (common/doc-from-view :collection :all-ids-by-slug slug)))]
    (common/map-from-db coll)))

(defn- unique-slug
  "Look for a conflicting collection slug and increment an appended slug counter
  numeral until you have a unique collection slug."
  ([slug] (unique-slug slug 0))
  ([slug counter]
    (if-not (or (blank? slug) (get-collection slug))
      slug
      ;; recur with the next possible slug
      (recur (common/next-slug slug counter) (inc counter)))))

(defn valid-new-collection
  "Given the name of a new collection, and a map of the new collection's properties,
  check if the everything is in order to create the new collection.
  Ensure the name of the item is specified or return :no-name.
  Ensure the slug is valid and doesn't already exist if it's specified,
  or return :invalid-slug or :slug-conflict respectively.
  :property-conflict is returned if a property is included in the map of properties that is in the common/reserved-properties
  set."
  ([coll-name] (valid-new-collection coll-name {}))
  ([coll-name {provided-slug :slug :as props}]
  (cond
    (or (not (string? coll-name)) (blank? coll-name)) :no-name
    (not-empty (intersection (set (keys (keywordize-keys props))) common/reserved-properties)) :property-conflict
    (not provided-slug) true
    (not (common/valid-slug? provided-slug)) :invalid-slug
    :else (if (nil? (get-collection provided-slug)) true :slug-conflict))))

(defn create-collection
  "Create a new collection using the specified name and optional map of properties.
  If :slug is included in the properties it will be used as the collection's slug,
  otherwise one will be created from the name.
  :slug-conflict is returned if a :slug is included in the properties and a collection already exists
  with that slug.
  :invalid-slug is returned if a :slug is included in the properties and it's not valid.
  :property-conflict is returned if a property is included in the map of properties that is in
  the reserved-properties set."
  ([coll-name] (create-collection coll-name {}))
  ([coll-name properties]
    (let [props (keywordize-keys properties)
          validity (valid-new-collection coll-name props)]
      (if (true? validity)
        (let [slug (unique-slug (or (:slug props) (slugify coll-name)))]
          (when-let [collection (common/create (merge props {:slug slug :name coll-name}) :collection)]
            (common/map-from-db collection)))
        validity))))

(defn- delete-collection-and-contents [id rev]
  ;; get all the id and rev of all the items in this collection
  (let [items (common/from-view :item :delete-by-coll-id id)]
    ;; create a bulk update request to delete all the items and the collection
    (let [delete-items (map #(common/delete-map (:value %)) items)]
      (clutch/bulk-update (common/db) (vec (conj delete-items (common/delete-map [id rev])))))))

(defn delete-collection
  "Given the slug of the collection, delete it and all its contents and return true,
  or return :bad-collection if no collection exists for the slug.

  If any portion of the bulk delete fails then the raw Couch DB bulk update response
  is returned."
  [slug]
  (if-let [coll (first (common/from-view :collection :delete-by-slug slug))]
    (let [result (delete-collection-and-contents (first (:value coll)) (last (:value coll)))]
      (if (every? #(and (map? %) (true? (:ok %))) result)
        true
        result))
    :bad-collection))

(defn valid-collection-update
  "Given the slug of the collection, and a map of updated
  properties for the collection, check if the everything
  is in order to update the collection.
  Ensure the collection exists or return :bad-collection.
  Ensure no reserved properties are used or return :property-conflict.
  If a new slug is provided in the properties, ensure it is
  valid or return :invalid-slug and ensure it is unused or
  return :slug-conflict. If no slug is specified in
  the properties it will be retain its current slug."
  [slug {provided-slug :slug :as props}]
    (let [id (:id (get-collection slug))]
      (cond
        (nil? id) :bad-collection
        (not-empty (intersection (set (keys (keywordize-keys props))) common/reserved-properties)) :property-conflict
        (not provided-slug) true
        (not (common/valid-slug? provided-slug)) :invalid-slug
        (= slug provided-slug) true
        :else (if (nil? (get-collection provided-slug)) true :slug-conflict))))

(defn- update
  "Update a collection retaining it's manufactured properties and replacing the rest with the provided properties"
  [slug updated-props]
  (if-let [coll (:doc (first (common/doc-from-view :collection :all-ids-by-slug slug)))]
    (let [retained-props (select-keys (:data coll) (conj common/retained-properties :version))]
      (common/map-from-db (common/update coll (merge retained-props updated-props))))
    :bad-collection))

(defn update-collection
  "Update the collection specified by its slug using the specified
  map of properties. If :slug is included in the properties
  the item will be moved to the new slug, otherwise the slug will remain the same.
  The same validity conditions and invalid return values as valid-item-update? apply."
  [slug props]
    (let [reason (valid-collection-update slug props)]
      (if (= reason true)
        (update slug props)
        reason)))

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
    (if-let [result (first (common/from-view-with-db :item :count-by-coll-id (:id collection)))]
      (:value result)
      0)))

(defn all-collections
  "Return all the collections in the system as a sequence of maps."
  []
  (when-let [collections (common/doc-from-view :collection :all-ids-by-slug)]
    (vec (map #(common/map-from-db (:doc %)) collections))))

(defn collection-count
  "Return the number of collections in the system."
  []
  (count (common/doc-from-view :collection :all-ids-by-slug)))