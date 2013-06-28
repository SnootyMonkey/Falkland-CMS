(ns fcms.models.item
  (:require [com.ashafa.clutch :as clutch]
            [fcms.models.common :as common]
            [fcms.models.collection :as collection]))

(def item-media-type "application/vnd.fcms.item+json;version=1")

(defn- item-doc
  ""
  [coll-id item-slug]
  (:doc (first (clutch/get-view "item" :all {:key [coll-id, item-slug] :include_docs true}))))

(defn- item-from-db 
  "Turn an item from its CouchDB map representation into its FCMS map representation."
  [coll-slug item]
  (common/map-from-db (assoc-in item [:data :collection] coll-slug)))

(defn create-item
  "Create a new item in the collection specified by its slug, using the specified
  item name and an optional map of properties. If :slug is included in the properties
  it will be used as the item's slug, otherwise the slug will be created from
  the name."
  ([coll-slug item-name] (create-item coll-slug item-name {}))
  ([coll-slug item-name props]
    (if-let [coll-id (:id (collection/get-collection coll-slug))]
      (when-let [item (common/create (merge props {:collection coll-id :name item-name}) :item)]
        (item-from-db coll-slug item))
      :bad-collection)))

(defn get-item
  "Given the slug of the collection containing the item and the slug of the item,
  return the item as a map, or :bad-collection if there's no collection with that slug, or
  nil if there is no item with that slug."
  [coll-slug item-slug]
    (if-let [coll-id (:id (collection/get-collection coll-slug))]
      (clutch/with-db (common/db)
        (when-let [item (item-doc coll-id item-slug)]
          (item-from-db coll-slug item)))
      :bad-collection))

(defn delete-item
  ""
  [coll-slug item-slug]
  (if-let [coll-id (:id (collection/get-collection coll-slug))]
    (if-let [item (clutch/with-db (common/db) (item-doc coll-id item-slug))]
      (:ok (common/delete item))
      :bad-item)
    :bad-collection))

(defn valid-new-item?
  "Given the slug of the collection, and a map of a potential new item,
  check if the everything is in order to create the new item.
  Ensure the collection exists, the name of the item is specified,
  and the slug doesn't already exist if it's specified."
  ([coll-slug item-name] (valid-new-item? coll-slug item-name {}))
  ([coll-slug item-name {provided-slug :slug}]
  (if-let [coll-id (:id (collection/get-collection coll-slug))]
    (if (and item-name (or (not provided-slug) (not (get-item coll-slug provided-slug))))
      :OK
      :bad-item)
    :bad-collection)))

(defn all-items [coll-slug])