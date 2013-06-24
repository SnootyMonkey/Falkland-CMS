(ns fcms.controllers.collections
  (:require [compojure.core :refer (defroutes ANY)]
            [liberator.core :refer (defresource)]            
            [clojure.core.match :refer (match)]
            [fcms.controllers.common :as common]
            [fcms.models.collection :as collection]
            [fcms.models.item :as item]
            [fcms.views.collections :refer (render-collection)]))

(defn get-collection [coll-slug]
  (if-let [coll (collection/get-collection coll-slug)]
    (render-collection coll)))

(defn get-items [coll-slug]
  (format "Items in the collection: %s" coll-slug))

(defn check-new-item [coll-slug item]
  (match (item/check-new-item coll-slug item)
    :bad-collection [false {:status 404}]
    false false
    :else true))

(defn create-item [coll-slug item]
  (item/create-item coll-slug (:name item) item))

(defresource collection [coll-slug]
    :available-media-types [collection/collection-media-type]
    :handle-ok (fn [ctx] (get-collection coll-slug)))

(defresource items-list [coll-slug]
    :available-media-types [item/item-media-type]
    :allowed-methods [:get :post]
    :handle-ok (fn [ctx] (get-items coll-slug))
    :malformed? (fn [ctx] (common/malformed-json? ctx)) 
    :processable? (fn [ctx] (check-new-item coll-slug (:data ctx)))
    :post! (fn [ctx] (create-item coll-slug (:data ctx))))

(defroutes collection-routes
  (ANY "/:coll-slug" [coll-slug] (collection coll-slug))
  (ANY "/:coll-slug/" [coll-slug] (items-list coll-slug)))