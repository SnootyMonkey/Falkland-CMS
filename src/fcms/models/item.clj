(ns fcms.models.item
  (:require [com.ashafa.clutch :as clutch]
            [fcms.models.base :as base]
            [fcms.models.collection :as collection]))

(def item-media-type "application/vnd.fcms.item+json")

(defn create-item
  "Create a new item in the collection specified by its slug, using the specified
  item name and an optional map of properties. If :slug is included in the properties
  it will be used as the item's slug, otherwise the slug will be created from
  the name."
  ([coll-slug name] (create-item coll-slug name {}))
  ([coll-slug name props]
    (if-let [coll-id (:id (collection/get-collection coll-slug))]
      (base/create (merge props {:collection coll-id :name name}) :item)
      :bad_collection)))

(defn get-item
  "Given the slug of the collection containing the item and the slug of the item,
  return the item as a map, or :bad_collection if there's no collection with that slug, or
  nil if there is no item with that slug"
  [coll-slug item-slug]
    (when-let [coll-id (:id (collection/get-collection coll-slug))]
      (clutch/with-db (base/db)
        (if-let [item (:doc (first (clutch/get-view "item" :all {:key [coll-id, item-slug] :include_docs true})))]
          (base/map-from-db item)))))

(defn all-items [coll-slug])