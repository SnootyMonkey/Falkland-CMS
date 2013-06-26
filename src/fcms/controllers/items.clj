(ns fcms.controllers.items
  (:require [compojure.core :refer (defroutes ANY)]
            [liberator.core :refer (defresource)]
            [clojure.core.match :refer (match)]
            [taoensso.timbre :refer (debug info warn error fatal spy)]
            [fcms.controllers.common :as common]
            [fcms.models.item :as item]
            [fcms.views.items :refer (render-item render-items)]))

(defn- get-item [coll-slug item-slug]
  (when-let [item (item/get-item coll-slug item-slug)]
    {:item item}))

(defn- get-items [coll-slug]
  (format "Items in the collection: %s" coll-slug))

(defn- check-new-item [coll-slug item]
  (match (item/valid-new-item? coll-slug (:name item) item)
    :bad-collection [false {:bad-collection true}]
    :OK true
    :else false))

(defn- create-item [coll-slug item]
  (when-let [item (item/create-item coll-slug (:name item) item)]
    {:item item}))

(defn- item-location-response [coll-slug item]
  (liberator.representation/ring-response
    {:body (render-item item)
    :headers {"Location" (format "/%s/%s" coll-slug (:slug item))}}))

(defresource item [coll-slug item-slug]
  :available-media-types [item/item-media-type]
  :allowed-methods [:get :put :delete]
  :exists? (fn [ctx] (get-item coll-slug item-slug))
  :handle-ok (fn [ctx] (render-item (:item ctx))))

(defresource items-list [coll-slug]
    :available-media-types [item/item-media-type]
    :allowed-methods [:get :post]
    :exists? (fn [ctx] (get-items coll-slug))
    :handle-ok (fn [ctx] (render-items (:items ctx)))
    :malformed? (fn [ctx] (common/malformed-json? ctx)) 
    :processable? (fn [ctx] (check-new-item coll-slug (:data ctx)))
    :handle-unprocessable-entity (fn [ctx] (when (:bad-collection ctx) common/missing-collection-response))
    :post! (fn [ctx] (create-item coll-slug (:data ctx)))
    :handle-created (fn [ctx] (item-location-response coll-slug (:item ctx))))

(defroutes item-routes
  (ANY "/:coll-slug/:item-slug" [coll-slug item-slug] (item coll-slug item-slug))
  (ANY "/:coll-slug/" [coll-slug] (items-list coll-slug)))