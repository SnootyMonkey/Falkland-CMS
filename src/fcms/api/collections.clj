(ns fcms.api.collections
  (:require [compojure.core :refer (defroutes ANY GET POST)]
            [liberator.core :refer (by-method)]
            [taoensso.timbre :refer (debug info warn error fatal spy)]
            [fcms.api.common :refer (defresource)]
            [fcms.api.common :as common]
            [fcms.resources.collection :as collection]
            [fcms.representations.collections :refer (render-collection render-collections)]))

(defn- collection-location-response [collection]
  (common/location-response [(:slug collection)] (render-collection collection) collection/collection-media-type))

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

;; ----- Resources -----
;; see: http://clojure-liberator.github.io/liberator/assets/img/decision-graph.svg

(def collection-resource-config {
  :available-charsets [common/UTF8]
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
    :post true;(fn [ctx] (common/check-input (collection/valid-collection-update coll-slug (:data ctx))))
    :put true ;(fn [ctx] (common/check-input (collection/valid-collection-update coll-slug (:data ctx))))
    })

    :handle-ok (by-method {
      :get (fn [ctx] (render-collection (:collection ctx)))})

    ;; Delete a collection
    :delete! (fn [_] (collection/delete-collection coll-slug))

    ;; Update/create a collection

    )

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