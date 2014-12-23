(ns fcms.resources.item
  "
  An item is any resource that's been stored in a collection.
  
  Items may also be organized in one or more taxonomies.
  "
  (:require [fcms.resources.collection-resource :as resource]))

(def
  ^{:no-doc true}
  item-media-type "application/vnd.fcms.item+json;version=1")
(def
  ^{:no-doc true}
  item-collection-media-type "application/vnd.collection+vnd.fcms.item+json;version=1")

(defn get-item
  "Given the slug of the collection containing the item and the slug of the item,
  return the item as a map, or return :bad-collection if there's no collection with that slug, or
  nil if there is no item with that slug."
  [coll-slug slug]
    (resource/get-resource coll-slug slug :item))

(defn valid-new-item
  "Given the slug of the collection, the name of the new item, and a map of the new item's properties,
  check if the everything is in order to create the new item.
  Ensure the collection exists or return :bad-collection.
  Ensure the name of the item is specified or return :no-name.
  Ensure the slug is valid and doesn't already exist if it's specified,
  or return :invalid-slug or :slug-conflict respectively.
  :property-conflict is returned if a property is included in the map of properties that is in the reserved-properties
  set."
  ([coll-slug item-name] (valid-new-item coll-slug item-name {}))
  ([coll-slug item-name props]
    (resource/valid-new-resource coll-slug item-name :item resource/reserved-properties props)))

(defn create-item
  "Create a new item in the collection specified by its slug, using the specified
  item name and an optional map of properties.
  If :slug is included in the properties it will be used as the item's slug, otherwise
  the slug will be created from the name.
  :slug-conflict is returned if a :slug is included in the properties and an item already exists
  in the collection with that slug.
  :invalid-slug is returned if a :slug is included in the properties and it's not valid.
  :property-conflict is returned if a property is included in the map of properties that is in
  the reserved-properties set."
  ([coll-slug item-name] (create-item coll-slug item-name {}))
  ([coll-slug item-name props]
    (resource/create-resource coll-slug item-name :item resource/reserved-properties props)))

(defn delete-item
  "Given the slug of the collection containing the item and the slug of the item,
  delete the item and return true. 
  :bad-collection is returned if there's no collection with that slug.
  :bad-item is returned if there is no item with the specified slug."
  [coll-slug slug]
  (let [result (resource/delete-resource coll-slug slug :item)]
    (if (and (map? result) (true? (:ok result)))
      true
      result)))

(defn valid-item-update
  "Given the slug of the collection, the slug of the item,
  and a map of updated properties for the item,
  check if the everything is in order to update the item.
  It ensures the collection exists or returns :bad-collection.
  It ensures the item exists or returns :bad-item.
  If a new slug is provided in the properties, it ensures it's
  valid or returns :invalid-slug and ensures it's unused or
  returns :slug-conflict.
  :property-conflict is returned if a property is included in the map of properties that is
  in the reserved-properties set."
  [coll-slug slug props]
    (resource/valid-resource-update coll-slug slug :item resource/reserved-properties props))

(defn update-item
  "Update an item in the collection specified by its slug using the specified
  map of properties. If :slug is included in the properties
  the item will be moved to the new slug, otherwise the slug will remain the same.
  The same validity conditions and invalid return values as valid-item-update apply.
  If the item is updated successfully, the newly updated item is returned."
  [coll-slug slug props]
    (let [reason (resource/valid-resource-update coll-slug slug :item resource/reserved-properties props)]
      (if (true? reason)
        (resource/update-resource coll-slug slug :item
          {:reserved resource/reserved-properties
          :retained resource/retained-properties
          :updated props})
        reason)))

(defn all-items
  "Given the slug of the collection, return all the items it contains as a sequence of maps,
  or return :bad-collection if there's no collection with that slug."
  [coll-slug]
  (resource/all-resources coll-slug :item))