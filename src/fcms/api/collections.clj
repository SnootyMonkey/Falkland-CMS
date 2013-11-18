(ns fcms.api.collections
  (:require [compojure.core :refer (defroutes ANY)]
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

(defresource collection [coll-slug]
    :available-media-types [collection/collection-media-type]
    :available-charsets [common/UTF8]
    :allowed-methods [:get :put :delete]
    :exists? (fn [ctx] (get-collection coll-slug))
    :handle-ok (fn [ctx] (render-collection (:collection ctx))))

;; ----- Routes -----

(defroutes collection-routes
  (ANY "/:coll-slug" [coll-slug] (collection coll-slug)))