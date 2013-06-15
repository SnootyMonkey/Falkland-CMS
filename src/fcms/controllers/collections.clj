(ns fcms.controllers.collections
  (:require [compojure.core :refer (defroutes ANY)]
            [liberator.core :refer (defresource)]
            [fcms.models.collection :as collection]
            [fcms.models.collection :refer (collection-media-type)]
            [fcms.models.item :refer (item-media-type)]))

(defn get-collection [coll-slug]
  (format "The collection: %s" coll-slug))

(defn items-list [coll-slug]
  (format "Items in the collection: %s" coll-slug))

(defn accept-switch [coll-slug {{accept :media-type} :representation}]
  (cond
    (= accept collection-media-type) (get-collection coll-slug)
    (= accept item-media-type) (items-list coll-slug)))

(defresource collection [coll-slug]
  :available-media-types [item-media-type collection-media-type]
  :handle-ok (fn [ctx] (accept-switch coll-slug ctx)))

(defroutes collection-routes
  (ANY "/:coll-slug" [coll-slug] (collection coll-slug)))