(ns fcms.controllers.items
  (:require [compojure.core :refer (defroutes ANY)]
            [liberator.core :refer (defresource)]
            [clojure.core.match :refer (match)]
            [taoensso.timbre :refer (debug info warn error fatal spy)]
            [fcms.controllers.common :as common]
            [fcms.models.item :as item]
            [fcms.views.items :refer (render-item render-items)]))

(defn- get-item [coll-slug item-slug]
  (let [item (item/get-item coll-slug item-slug)]
    (match item
      :bad-collection [false {:bad-collection true}]
      nil false
      :else {:item item})))

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
  :available-charsets [common/UTF8]
  :available-media-types [item/item-media-type]
  :handle-not-acceptable (fn [ctx] (common/only-accept item/item-media-type))
  :allowed-methods [:get :put :delete]
  :exists? (fn [ctx] (get-item coll-slug item-slug))
  :handle-not-found (fn [ctx] (when (:bad-collection ctx) common/missing-collection-response))
  ;; Get an item
  :handle-ok (fn [ctx] (render-item (:item ctx)))
  ;; Delete an item
  :delete! (fn [ctx] (item/delete-item coll-slug item-slug))
  ;; Update an item
  )

(defresource items-list [coll-slug]
    :available-charsets [common/UTF8]
    :available-media-types [item/item-media-type]
    :handle-not-acceptable (fn [ctx] (common/only-accept item/item-media-type))
    :allowed-methods [:get :post]
    ;; Get list of items  
    :exists? (fn [ctx] (get-items coll-slug))
    :handle-ok (fn [ctx] (render-items (:items ctx)))
    ;; Create new item
    :known-content-type? (fn [ctx] (=  (get-in ctx [:request :content-type]) item/item-media-type))
    :malformed? (fn [ctx] (common/malformed-json? ctx)) 
    :processable? (fn [ctx] (check-new-item coll-slug (:data ctx)))
    :handle-unprocessable-entity (fn [ctx] (when (:bad-collection ctx) common/missing-collection-response))
    :post! (fn [ctx] (create-item coll-slug (:data ctx)))
    :handle-created (fn [ctx] (item-location-response coll-slug (:item ctx))))

(defroutes item-routes
  (ANY "/:coll-slug/:item-slug" [coll-slug item-slug] (item coll-slug item-slug))
  (ANY "/:coll-slug/" [coll-slug] (items-list coll-slug)))