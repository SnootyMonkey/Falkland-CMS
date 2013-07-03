(ns fcms.controllers.items
  (:require [compojure.core :refer (defroutes ANY)]
            [liberator.core :refer (defresource)]
            [clojure.core.match :refer (match)]
            [taoensso.timbre :refer (debug info warn error fatal spy)]
            [fcms.controllers.common :as common]
            [fcms.models.item :as item]
            [fcms.views.items :refer (render-item render-items)]))

;; Get item(s)

(defn- get-item [coll-slug item-slug]
  (let [item (item/get-item coll-slug item-slug)]
    (match item
      :bad-collection [false {:bad-collection true}]
      nil false
      :else {:item item})))

(defn- get-items [coll-slug]
  (format "Items in the collection: %s" coll-slug))

;; Create new item

(defn- check-new-item [coll-slug item]
  (let [reason (item/valid-new-item? coll-slug (:name item) item)]
    (if (= reason :OK)
      true
      [false {:reason reason}])))

(defn- create-item [coll-slug item]
  (when-let [item (item/create-item coll-slug (:name item) item)]
    {:item item}))

(defn- unprocessable-reason [reason]
  (match reason 
    :bad-collection (common/missing-collection-response)
    :no-name "Name is required."
    :slug-conflict "Slug already used in collection."
    :invalid-slug "Invalid slug."
    else ))

(defn- item-location-response [coll-slug item]
  (common/location-response [coll-slug (:slug item)] (render-item item) item/item-media-type))

;; Resources & routes

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
  ;; TODO
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
    :handle-unprocessable-entity (fn [ctx] (unprocessable-reason (:reason ctx)))
    :post! (fn [ctx] (create-item coll-slug (:data ctx)))
    :handle-created (fn [ctx] (item-location-response coll-slug (:item ctx))))

(defroutes item-routes
  (ANY "/:coll-slug/:item-slug" [coll-slug item-slug] (item coll-slug item-slug))
  (ANY "/:coll-slug/" [coll-slug] (items-list coll-slug)))