(ns fcms.controllers.collections
  (:require [compojure.core :refer (defroutes ANY)]
            [liberator.core :refer (defresource)]            
            [taoensso.timbre :refer (debug info warn error fatal spy)]
            [fcms.controllers.common :as common]
            [fcms.models.collection :as collection]
            [fcms.views.collections :refer (render-collection)]))

(defn get-collection [coll-slug]
  (if-let [coll (collection/get-collection coll-slug)]
    (render-collection coll)))

(defresource collection [coll-slug]
    :available-media-types [collection/collection-media-type]
    :handle-ok (fn [ctx] (get-collection coll-slug)))

(defroutes collection-routes
  (ANY "/:coll-slug" [coll-slug] (collection coll-slug)))