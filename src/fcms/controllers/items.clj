(ns fcms.controllers.items
  (:require [compojure.core :refer (defroutes ANY)]
            [liberator.core :refer (defresource)]
            [fcms.models.collection :as collection]
            [fcms.models.item :as item]
    		    [fcms.models.item :refer (item-type)]))

(defn get-item [coll-slug item-slug]
  (format "The item: %s/%s" coll-slug item-slug))

(defresource item [coll-slug item-slug]
  :available-media-types [item-type]
  :handle-ok (fn [ctx] (get-item coll-slug item-slug)))

(defroutes item-routes
  (ANY "/:coll-slug/:item-slug" [coll-slug item-slug] (item coll-slug item-slug)))