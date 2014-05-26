(ns fcms.api.collections
  (:require [compojure.core :refer (defroutes ANY GET)]
            [liberator.core :refer (defresource)]
            [taoensso.timbre :refer (debug info warn error fatal spy)]
            [fcms.api.common :as common]
            [fcms.resources.collection :as collection]
            [fcms.representations.collections :refer (render-collection)]))

(defn- get-collection [coll-slug]
  (if-let [collection (collection/get-collection coll-slug)]
    {:collection collection}))

;; ----- Resources -----
;; see: http://clojure-liberator.github.io/liberator/assets/img/decision-graph.svg

(def collection-resource-config {
  :available-media-types [collection/collection-media-type]
  :available-charsets [common/UTF8]
})

(defresource collection [coll-slug]
    collection-resource-config
    :allowed-methods [:get :put :delete]
    :exists? (fn [_] (get-collection coll-slug))
    ;; Get a collection
    :handle-ok (fn [ctx] (render-collection (:collection ctx))))
    ;; Delete a collection
    ;; Update/create a collection

(defresource collection-list []
    collection-resource-config
    :allowed-methods [:get]
    ;; Get a list of collections
    :handle-ok (fn [ctx] (render-collection (:collection ctx))))

;; ----- Routes -----

(defroutes collection-routes
  (ANY "/:coll-slug" [coll-slug] (collection coll-slug))
  (GET "/" [] (collection-list)))