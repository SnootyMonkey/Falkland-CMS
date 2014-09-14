(ns fcms.api.collections
  (:require [clojure.core.match :refer (match)]
            [compojure.core :refer (defroutes ANY GET POST)]
            [liberator.core :refer (defresource by-method)]
            [liberator.representation :refer (ring-response)]
            [taoensso.timbre :refer (debug info warn error fatal spy)]
            [fcms.api.common :as common]
            [fcms.resources.collection :as collection]
            [fcms.representations.collections :refer (render-collection render-collections)]))

;; ----- Responses -----

(defn- collection-location-response [collection]
  (common/location-response [(:slug collection)] (render-collection collection) collection/collection-media-type))

(defn- unprocessable-reason [reason]
  (match reason
    :no-name (common/unprocessable-entity-response "Name is required.")
    :property-conflict (common/unprocessable-entity-response "A reserved property was used.")
    :slug-conflict (common/unprocessable-entity-response "Slug already used.")
    :invalid-slug (common/unprocessable-entity-response"Invalid slug.")
    :else (common/unprocessable-entity-response "Not processable.")))

;; ----- Get collections -----

(defn- get-collection [coll-slug]
  (if-let [collection (collection/get-collection coll-slug)]
    {:collection collection}))

(defn- get-collections []
  [true {:collections (collection/all-collections)}])

;; ----- Create a new collection -----

(defn- create-collection [coll]
  (when-let [collection (collection/create-collection (:name coll) coll)]
    {:collection collection}))

;; ----- Update a collection -----

(defn- update-collection [coll-slug collection]
  (when-let [result (collection/update-collection coll-slug collection)]
    {:updated-collection result}))

(defn- update-collection-response [coll-slug ctx]
  (if (= (get-in ctx [:updated-collection :slug]) (get-in ctx [:collection :slug]))
    ; it's in the same spot
    (render-collection (:updated-collection ctx))
    ; it moved
    (collection-location-response (:updated-collection ctx))))

;; ----- Resources -----
;; see: http://clojure-liberator.github.io/liberator/assets/img/decision-graph.svg

(def collection-resource-config {
  :available-charsets [common/UTF8]
  :handle-not-found (fn [_] common/missing-response)
  :handle-unprocessable-entity (fn [ctx] (unprocessable-reason (:reason ctx)))
})

(defresource collection [coll-slug]
  collection-resource-config
  :available-media-types [collection/collection-media-type]
  :handle-not-acceptable (fn [_] (common/only-accept 406 collection/collection-media-type))
  :allowed-methods [:get :put :delete]
  :exists? (fn [_] (get-collection coll-slug))
  :known-content-type? (fn [ctx] (common/known-content-type? ctx collection/collection-media-type))
  :handle-unsupported-media-type (fn [_] (common/only-accept 415 collection/collection-media-type))
  :respond-with-entity? (by-method {:put true :delete false})

  :processable? (by-method {
    :get true
    :delete true
    :put (fn [ctx] (common/check-input (collection/valid-collection-update coll-slug (:data ctx))))})

  :handle-ok (by-method {
    :get (fn [ctx] (render-collection (:collection ctx)))
    :put (fn [ctx] (update-collection-response coll-slug ctx))})

  ;; Delete a collection
  :delete! (fn [_] (collection/delete-collection coll-slug))

  ;; Update a collection
  :new? (by-method {:post true :put false})
  :malformed? (by-method {
    :get false
    :delete false
    :post (fn [ctx] (common/malformed-json? ctx))
    :put (fn [ctx] (common/malformed-json? ctx))})
  :can-put-to-missing? (fn [_] false) ; temporarily only use PUT for update
  :conflict? (fn [_] false)
  :put! (fn [ctx] (update-collection coll-slug (:data ctx))))

(defresource collection-list []
  collection-resource-config
  :available-media-types (by-method {
    :get [collection/collection-collection-media-type]
    :post [collection/collection-media-type]})
  :handle-not-acceptable (by-method {
    :get (fn [_] (common/only-accept 406 collection/collection-collection-media-type))
    :post (fn [_] (common/only-accept 406 collection/collection-media-type))})
  :allowed-methods [:get :post]

  ;; Get a list of collections
  :exists? (fn [_] (get-collections))
  :handle-ok (fn [ctx] (render-collections (:collections ctx)))

  ;; Create a new collection
  :malformed? (by-method {
    :get false
    :post (fn [ctx] (common/malformed-json? ctx))})
  :known-content-type? (by-method {
    :get (fn [ctx] (common/known-content-type? ctx collection/collection-collection-media-type))
    :post (fn [ctx] (common/known-content-type? ctx collection/collection-media-type))})
  :handle-unsupported-media-type (by-method {
    :get (fn [_] (common/only-accept 415 collection/collection-collection-media-type))
    :post (fn [_] (common/only-accept 415 collection/collection-media-type))})
  :processable? (by-method {
    :get true
    :post (fn [ctx] (common/check-input (collection/valid-new-collection (get-in ctx [:data :name]) (:data ctx))))})
  :post! (fn [ctx] (create-collection (:data ctx)))
  :handle-created (fn [ctx] (collection-location-response (:collection ctx))))

;; ----- Routes -----

(defroutes collection-routes
  (ANY "/:coll-slug" [coll-slug] (collection coll-slug))
  (POST "/" [] (collection-list))
  (GET "/" [] (collection-list)))