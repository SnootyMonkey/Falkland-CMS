(ns fcms.models.item
  (:require [com.ashafa.clutch :as clutch]
            [fcms.models.base :as base]
            [fcms.models.collection :as collection]))

(def item-media-type "application/vnd.fcms.item+json")

(defn create-item
  "Create a new item in the specified collection using the specified name
  and an optional map of properties. If :slug is included in the properties
  it will be used as the item's slug, otherwise one will be created from
  the name."
  ([coll-slug name] (create-item coll-slug name {}))
  ([coll-slug name props]
    (if-let [coll-id (:_id (collection/get-collection coll-slug))]
      (base/create (merge props {:collection coll-id :name name}) :item)
      :bad_collection)))

(defn get-item
  ""
  [coll-slug item-slug]
    (when-let [coll-id (:_id (collection/get-collection coll-slug))]
      (clutch/with-db (base/db)
        (:doc (first (clutch/get-view "item" :all {:key [coll-id, item-slug] :include_docs true}))))))

(defn all-items [coll-slug])