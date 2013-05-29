(ns fcms.controllers.collections
  (:require [compojure.core :refer (defroutes ANY)]
            [liberator.core :refer (defresource)]
            [fcms.models.collection :as collection]
    		    [fcms.views.collections :as view]))

(defresource collection [coll-name]
  :available-media-types ["application/json;schema=vnd.fcms.collection;version=1"]
  :handle-ok (fn [_] (format "The collection is %s" coll-name)))

(defroutes collection-routes
  (ANY "/:coll-name" [coll-name] (collection coll-name)))