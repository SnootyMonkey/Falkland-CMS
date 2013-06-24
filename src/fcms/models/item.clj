(ns fcms.models.item
  (:require [com.ashafa.clutch :as clutch]
            [fcms.models.common :as common]
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
      (common/create (merge props {:collection coll-id :name name}) :item)
      :bad_collection)))

(defn get-item
  "Given the slug of the collection containing the item and the slug of the item,
  return the item as a map, or :bad_collection if there's no collection with that slug, or
  nil if there is no item with that slug"
  [coll-slug item-slug]
    (if-let [coll-id (:id (collection/get-collection coll-slug))]
      (clutch/with-db (common/db)
        (when-let [item (:doc (first (clutch/get-view "item" :all {:key [coll-id, item-slug] :include_docs true})))]
          (common/map-from-db (assoc-in item [:data :collection] coll-slug)))))
      :bad_collection)

;; TODO test for no name, and specified but already used slug
(defn check-new-item
  "Given the slug of the collection, and a map of a new item,
  check if the everything is in order to create this new item.
  Ensure the collection exists, the name of the item is specified,
  and the slug doesn't already exist if it's specified."
  [coll-slug item]
  (if-let [coll-id (:id (collection/get-collection coll-slug))]
    true
    :bad_collection))

(defn all-items [coll-slug])