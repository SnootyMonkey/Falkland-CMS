(ns fcms.resources.item
  (:require [clojure.set :refer (intersection)]
            [com.ashafa.clutch :as clutch]
            [fcms.resources.common :as common]
            [fcms.resources.collection-resource :as resource]
            [fcms.resources.collection :as collection]
            [fcms.lib.slugify :refer (slugify)]))

(def item-media-type "application/vnd.fcms.item+json;version=1")

(def reserved-properties
  "Properties that can't be specified during a create or update."
  (conj common/reserved-properties :collection)) 
(def retained-properties
  "Properties that are retained during an update even if they aren't in the updated property set."
  (conj common/retained-properties :collection))

(defn get-item
  "Given the slug of the collection containing the item and the slug of the item,
  return the item as a map, or return :bad-collection if there's no collection with that slug, or
  nil if there is no item with that slug."
  [coll-slug slug]
    (resource/get-resource coll-slug slug :item))

(defn valid-new-item
  "Given the slug of the collection, the name of the item, and a map of a potential new item,
  check if the everything is in order to create the new item.
  Ensure the collection exists or return :bad-collection.
  Ensure the name of the item is specified or return :no-name.
  Ensure the slug is valid and doesn't already exist if it's specified,
  or return :invalid-slug or :slug-conflict respectively.
  If a property is included in the map of properties that is in the reserved-properties
  set, :property-conflict will be returned."
  ([coll-slug item-name] (valid-new-item coll-slug item-name {}))
  ([coll-slug item-name props]
    (resource/valid-new-resource coll-slug item-name reserved-properties :item props)))

(defn create-item
  "Create a new item in the collection specified by its slug, using the specified
  item name and an optional map of properties.
  If :slug is included in the properties it will be used as the item's slug, otherwise
  the slug will be created from the name.
  If a :slug is included in the properties and an item already exists
  in the collection with that slug, a :slug-conflict will be returned.
  If a :slug is included in the properties and it's not valid,
  :invalid-slug will be returned.
  If a property is included in the map of properties that is in the reserved-properties
  set, :property-conflict will be returned."
  ([coll-slug item-name] (create-item coll-slug item-name {}))
  ([coll-slug item-name props]
    (resource/create-resource coll-slug item-name :item reserved-properties props)))

(defn delete-item
  "Given the slug of the collection containing the item and the slug of the item,
  delete the item, or return :bad-collection if there's no collection with that slug, or
  :bad-item if there is no item with that slug."
  [coll-slug slug]
  (if-let [coll-id (:id (collection/get-collection coll-slug))]
    (if-let [item (clutch/with-db (common/db) (common/resource-doc coll-id slug :item))]
      (common/delete item)
      :bad-item)
    :bad-collection))

(defn valid-item-update?
  "Given the slug of the collection, the slug of the item,
  and a map of updated properties for the item,
  check if the everything is in order to update the item.
  Ensure the collection exists or return :bad-collection.
  Ensure the item exists or return :bad-item.
  If a new slug is provided in the properties, ensure it is
  valid or return :invalid-slug and ensure it is unused or
  return :slug-conflict. If no item slug is specified in
  the properties it will be retain its current slug."
  [coll-slug slug {item-name :name provided-slug :slug :as props}]
    (let [coll-id (:id (collection/get-collection coll-slug))
          item-id (:id (get-item coll-slug slug))]
      (cond
        (nil? coll-id) :bad-collection
        (nil? item-id) :bad-item
        (not-empty (intersection (set (keys props)) reserved-properties)) :property-conflict
        (not provided-slug) true
        (not (common/valid-slug? provided-slug)) :invalid-slug
        (= slug provided-slug) true
        :else (if (nil? (get-item coll-slug provided-slug)) true :slug-conflict))))

(defn- update
  "Update an item retaining it's manufactured properties and replacing the rest with the provided properties"
  [coll-slug slug updated-props]
  (collection/with-collection coll-slug
    (if-let [item (common/resource-doc (:id collection) slug :item)]
      (let [retained-props (select-keys (:data item) (conj retained-properties :version))]
        (common/resource-from-db coll-slug (common/update-with-db item (merge retained-props updated-props))))
      :bad-item)))

(defn update-item
  "Update an item in the collection specified by its slug using the specified
  map of properties. If :slug is included in the properties
  the item will be moved to the new slug, otherwise the slug will remain the same.
  The same validity conditions and invalid return values as valid-item-update? apply."
  [coll-slug slug props]
    (let [reason (valid-item-update? coll-slug slug props)]
      (if (= reason true)
        (update coll-slug slug props)
        reason)))

(defn all-items
  "Given the slug of the collection, return all the items it contains as a sequence of maps,
  or return :bad-collection if there's no collection with that slug."
  [coll-slug]
  (collection/with-collection coll-slug
    (when-let [results (common/doc-from-view-with-db :item :all-slugs-by-coll-id (:id collection))]
      (map #(common/resource-from-db coll-slug (:doc %)) results))))