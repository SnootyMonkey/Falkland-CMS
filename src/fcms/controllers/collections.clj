(ns fcms.controllers.collections
  (:require [compojure.core :refer (defroutes ANY)]
            [liberator.core :refer (defresource)]            
            [clojure.core.match :refer (match)]
            [taoensso.timbre :refer (debug info warn error fatal spy)]
            [fcms.controllers.common :as common]
            [fcms.models.collection :as collection]
            [fcms.models.item :as item]
            [fcms.views.collections :refer (render-collection)]))

(def missing-response {:status 404 :body "Not a valid collection." :headers {"Content-Type" "text/plain"}})

(defn get-collection [coll-slug]
  (if-let [coll (collection/get-collection coll-slug)]
    (render-collection coll)))

(defn get-items [coll-slug]
  (format "Items in the collection: %s" coll-slug))

(defn check-new-item [coll-slug item]
  (match (item/valid-new-item? coll-slug (:name item) item)
    :bad-collection [false {:bad-collection true}]
    :OK true
    :else false))

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
    :handle-unprocessable-entity (fn [ctx] (if (:bad-collection ctx) missing-response))
    :post! (fn [ctx] (create-item coll-slug (:data ctx))))

(defroutes collection-routes
  (ANY "/:coll-slug" [coll-slug] (collection coll-slug))
  (ANY "/:coll-slug/" [coll-slug] (items-list coll-slug)))