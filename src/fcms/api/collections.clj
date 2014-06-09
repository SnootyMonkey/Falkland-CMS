(ns fcms.api.collections
  (:require [compojure.core :refer (defroutes ANY GET)]
            [liberator.core :refer (by-method)]
            [taoensso.timbre :refer (debug info warn error fatal spy)]
            [fcms.api.common :refer (defresource)]
            [fcms.api.common :as common]
            [fcms.resources.collection :as collection]
            [fcms.representations.collections :refer (render-collection)]))

(defn- get-collection [coll-slug]
  (if-let [collection (collection/get-collection coll-slug)]
    {:collection collection}))

;; ----- Resources -----
;; see: http://clojure-liberator.github.io/liberator/assets/img/decision-graph.svg

(def collection-resource-config {
  :available-charsets [common/UTF8]
})

(defresource collection [coll-slug]
    collection-resource-config
    :available-media-types [collection/collection-media-type]
    :handle-not-acceptable (fn [_] (common/only-accept collection/collection-media-type))
    :allowed-methods [:get :put :delete]
    :exists? (fn [_] (get-collection coll-slug))
    :known-content-type? (fn [ctx] (common/known-content-type ctx collection/collection-media-type))
    :handle-unsupported-media-type (fn [_] (common/only-accept collection/collection-media-type))
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
    :available-media-types [collection/collection-collection-media-type]
    :allowed-methods [:get]
    ;; Get a list of collections
    :handle-ok (fn [ctx] (render-collection (:collection ctx))))

;; ----- Routes -----

(defroutes collection-routes
  (ANY "/:coll-slug" [coll-slug] (collection coll-slug))
  (GET "/" [] (collection-list)))