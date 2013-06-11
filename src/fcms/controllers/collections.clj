(ns fcms.controllers.collections
  (:require [compojure.core :refer (defroutes ANY)]
            [liberator.core :refer (defresource)]
            [fcms.models.collection :as collection]
            [fcms.models.collection :refer (collection-type)]
            [fcms.models.item :refer (item-type)]))

(defn get-collection [coll-slug]
  (format "The collection: %s" coll-slug))

(defn items-list [coll-slug]
  (format "Items in the collection: %s" coll-slug))

(defn accept-switch [coll-slug {{accept :media-type} :representation}]
  (cond
   (= accept collection-type) (get-collection coll-slug)
    (= accept item-type) (items-list coll-slug)))

(defresource collection [coll-slug]
  :available-media-types [item-type collection-type]
  :handle-ok (fn [ctx] (accept-switch coll-slug ctx)))

(defroutes collection-routes
  (ANY "/:coll-slug" [coll-slug] (collection coll-slug)))