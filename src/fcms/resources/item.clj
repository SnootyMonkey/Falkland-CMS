(ns fcms.resources.item
  (:require [clojure.set :refer (intersection)]
            [fcms.resources.common :as common]
            [fcms.resources.collection-resource :as resource]
            [fcms.resources.collection :as collection]))

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
  (resource/delete-resource coll-slug slug :item))

(defn valid-item-update
  "Given the slug of the collection, the slug of the item,
  and a map of updated properties for the item,
  check if the everything is in order to update the item.
  Ensure the collection exists or return :bad-collection.
  Ensure the item exists or return :bad-item.
  If a new slug is provided in the properties, ensure it is
  valid or return :invalid-slug and ensure it is unused or
  return :slug-conflict. If no item slug is specified in
  the properties it will be retain its current slug."
  [coll-slug slug props]
    (resource/valid-resource-update coll-slug slug reserved-properties props :item))

(defn update-item
  "Update an item in the collection specified by its slug using the specified
  map of properties. If :slug is included in the properties
  the item will be moved to the new slug, otherwise the slug will remain the same.
  The same validity conditions and invalid return values as valid-item-update apply."
  [coll-slug slug props]
    (let [reason (resource/valid-resource-update coll-slug slug reserved-properties props :item)]
      (if (true? reason)
        (resource/update-resource coll-slug slug retained-properties props :item)
        reason)))

(defn all-items
  "Given the slug of the collection, return all the items it contains as a sequence of maps,
  or return :bad-collection if there's no collection with that slug."
  [coll-slug]
  (resource/all-resources coll-slug :item))