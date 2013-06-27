(ns fcms.controllers.collections
  (:require [compojure.core :refer (defroutes ANY)]
            [liberator.core :refer (defresource)]            
            [taoensso.timbre :refer (debug info warn error fatal spy)]
            [fcms.controllers.common :as common]
            [fcms.models.collection :as collection]
            [fcms.views.collections :refer (render-collection)]))

(defn- get-collection [coll-slug]
  (if-let [collection (collection/get-collection coll-slug)]
    {:collection collection}))

(defresource collection [coll-slug]
    :available-media-types [collection/collection-media-type]
    :available-charsets [common/UTF8]
    :allowed-methods [:get :put :delete]
    :exists? (fn [ctx] (get-collection coll-slug))
    :handle-ok (fn [ctx] (render-collection (:collection ctx))))

(defroutes collection-routes
  (ANY "/:coll-slug" [coll-slug] (collection coll-slug)))