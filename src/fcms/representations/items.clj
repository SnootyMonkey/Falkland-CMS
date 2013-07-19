(ns fcms.representations.items
  (:require [clj-json.core :as json]
            [fcms.representations.common :as common]
            [fcms.resources.item :as item]
            [fcms.resources.collection :refer (collection-media-type)]))

(def ordered-keys [:name :created-at :updated-at :slug :collection])

(defn- collection-url [item]
  (str "/" (:collection item)))

(defn- url [item]
  (str (collection-url item) "/" (:slug item)))

(defn- self-link [item]
  (common/self-link (url item) item/item-media-type))

(defn- update-link [item]
  (common/update-link (url item) item/item-media-type))

(defn- delete-link [item]
  (common/delete-link (url item)))

(defn- collection-link [item]
  (common/link-map "collection" common/GET (collection-url item) collection-media-type))

(defn links [item]
  "Add the HATEAOS links to the item"
  (apply array-map (concat (flatten (into [] item)) [:links [
    (self-link item)
    (update-link item)
    (delete-link item)
    (collection-link item)]])))

(defn render-items
  "Create a JSON representation of a group of items for the REST API"
  [items])

(defn render-item 
  "Create a JSON representation of an item for the REST API"
  [item]
  ;; Generate JSON from the sorted array map that results from:
  ;; 1) removing unneeded :id key
  ;; 2) making an ordered array hash of the known ordered keys
  ;; 3) adding a sorted hash of any remaining keys
  ;; 4) adding the HATEAOS links to the array hash
  (let [item-props (dissoc item :id)]
    (json/generate-string
      (-> item-props
        (common/ordered ,,, ordered-keys)
        (common/append-sorted ,,, (common/remaining-keys item-props ordered-keys))
        links))))