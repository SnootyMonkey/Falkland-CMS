(ns fcms.controllers.items
  (:require [compojure.core :refer (defroutes ANY)]
            [liberator.core :refer (defresource)]
            [fcms.models.collection :as collection]
            [fcms.models.item :as item]
    		    [fcms.views.items :as view]))

(defresource item [coll-name item-name]
  :available-media-types ["application/json;schema=vnd.fcms.item;version=1"]
  :handle-ok (fn [_] (format "The collection is %s and the item is %s" coll-name item-name)))

(defroutes item-routes
  (ANY "/:coll-name/:item-name" [coll-name item-name] (item coll-name item-name)))