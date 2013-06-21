(ns fcms.controllers.collections
  (:require [compojure.core :refer (defroutes ANY)]
            [liberator.core :refer (defresource)]
            [clj-json.core :as json]
            [clojure.walk :refer (keywordize-keys)]
            [fcms.models.collection :as collection]
            [fcms.models.item :as item]
            [fcms.views.collections :refer (render-collection)]))

(defn get-collection [coll-slug]
  (if-let [coll (collection/get-collection coll-slug)]
    (render-collection coll)))

(defn get-items [coll-slug]
  (format "Items in the collection: %s" coll-slug))

(defn extract-item [ctx]
  (keywordize-keys (json/parse-string (slurp (get-in ctx [:request :body])))))

(defn check-new-item [coll-slug ctx]
  (item/check-new-item coll-slug (extract-item ctx)))

(defn create-item [coll-slug ctx]
  (let [item (extract-item ctx)]
    (item/create-item coll-slug (:name item) item)))

(defresource collection [coll-slug]
    :available-media-types [collection/collection-media-type]
    :handle-ok (fn [ctx] (get-collection coll-slug)))

(defresource items-list [coll-slug]
    :available-media-types [item/item-media-type]
    :allowed-methods [:get :post]
    :handle-ok (fn [ctx] (get-items coll-slug))
    :malformed? (fn [ctx] false) ;(not (check-new-item coll-slug ctx))) 
    :post! (fn [ctx] (create-item coll-slug ctx)))

(defroutes collection-routes
  (ANY "/:coll-slug" [coll-slug] (collection coll-slug))
  (ANY "/:coll-slug/" [coll-slug] (items-list coll-slug)))