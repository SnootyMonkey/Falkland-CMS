(ns fcms.controllers.items
  (:require [compojure.core :refer (defroutes ANY)]
            [liberator.core :refer (defresource)]
            [clj-json.core :as json]
            [fcms.models.collection :as collection]
            [fcms.models.item :as item]
    		    [fcms.models.item :refer (item-media-type)]))

(defn get-item [coll-slug item-slug]
  (if-let [item (item/get-item coll-slug item-slug)]
    (json/generate-string item)))

(defresource item [coll-slug item-slug]
  :available-media-types [item-media-type]
  :handle-ok (fn [ctx] (get-item coll-slug item-slug)))

(defroutes item-routes
  (ANY "/:coll-slug/:item-slug" [coll-slug item-slug] (item coll-slug item-slug)))