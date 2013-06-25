(ns fcms.controllers.items
  (:require [compojure.core :refer (defroutes ANY)]
            [liberator.core :refer (defresource)]
            [clojure.core.match :refer (match)]
            [taoensso.timbre :refer (debug info warn error fatal spy)]
            [fcms.controllers.common :as common]
            [fcms.models.item :as item]
            [fcms.views.items :refer (render-item)]))

(defn get-item [coll-slug item-slug]
  (if-let [item (item/get-item coll-slug item-slug)]
    (render-item item)))

(defn get-items [coll-slug]
  (format "Items in the collection: %s" coll-slug))

(defn check-new-item [coll-slug item]
  (match (item/valid-new-item? coll-slug (:name item) item)
    :bad-collection [false {:bad-collection true}]
    :OK true
    :else false))

(defn create-item [coll-slug item]
  (item/create-item coll-slug (:name item) item))

(defresource item [coll-slug item-slug]
  :available-media-types [item/item-media-type]
  :handle-ok (fn [ctx] (get-item coll-slug item-slug)))

(defresource items-list [coll-slug]
    :available-media-types [item/item-media-type]
    :allowed-methods [:get :post]
    :handle-ok (fn [ctx] (get-items coll-slug))
    :malformed? (fn [ctx] (common/malformed-json? ctx)) 
    :processable? (fn [ctx] (check-new-item coll-slug (:data ctx)))
    :handle-unprocessable-entity (fn [ctx] (if (:bad-collection ctx) common/missing-collection-response))
    :post! (fn [ctx] (create-item coll-slug (:data ctx))))

(defroutes item-routes
  (ANY "/:coll-slug/:item-slug" [coll-slug item-slug] (item coll-slug item-slug))
  (ANY "/:coll-slug/" [coll-slug] (items-list coll-slug)))