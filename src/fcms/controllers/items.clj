(ns fcms.controllers.items
  (:require [compojure.core :refer (defroutes ANY)]
            [liberator.core :refer (defresource)]
            [fcms.models.item :as item]
            [fcms.views.items :refer (render-item)]))

(defn get-item [coll-slug item-slug]
  (if-let [item (item/get-item coll-slug item-slug)]
    (render-item item)))

(defresource item [coll-slug item-slug]
  :available-media-types [item/item-media-type]
  :handle-ok (fn [ctx] (get-item coll-slug item-slug)))

(defroutes item-routes
  (ANY "/:coll-slug/:item-slug" [coll-slug item-slug] (item coll-slug item-slug)))