(ns fcms.controllers.collections
  (:require [compojure.core :refer (defroutes ANY)]
            [liberator.core :refer (defresource)]
            [fcms.models.collection :as collection]
            [fcms.models.item :refer (item-media-type)]
            [fcms.views.collections :refer (render-collection)]))

(defn get-collection [coll-slug]
  (if-let [coll (collection/get-collection coll-slug)]
    (render-collection coll)))

(defn get-items [coll-slug]
  (format "Items in the collection: %s" coll-slug))

(defresource collection [coll-slug]
    :available-media-types [collection/collection-media-type]
    :handle-ok (fn [ctx] (get-collection coll-slug)))

(defresource items-list [coll-slug]
    :available-media-types [item-media-type]
    :handle-ok (fn [ctx] (get-items coll-slug)))

(defroutes collection-routes
  (ANY "/:coll-slug" [coll-slug] (collection coll-slug))
  (ANY "/:coll-slug/" [coll-slug] (items-list coll-slug)))