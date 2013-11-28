(ns fcms.representations.items
  (:require [cheshire.core :as json]
            [fcms.representations.common :as common]
            [fcms.resources.item :as item]
            [fcms.resources.collection :refer (collection-media-type)]
            [fcms.resources.taxonomy :refer (category-media-type)]))

(def ordered-keys [:name :slug :collection :version :created-at :updated-at])

(defn- collection-url [item]
  (str "/" (:collection item)))

(defn- item-url [item]
  (str (collection-url item) "/" (:slug item)))

(defn- category-url [item category]
  (str (collection-url item) "/" category))

(defn- self-link [item]
  (common/self-link (item-url item) item/item-media-type))

(defn- update-link [item]
  (common/update-link (item-url item) item/item-media-type))

(defn- delete-link [item]
  (common/delete-link (item-url item)))

(defn category-links [item]
  (vec (map
    #(common/link-map "category" common/GET (category-url item %) category-media-type)
    (:categories item))))

(defn- collection-link [item]
  (common/link-map "collection" common/GET (collection-url item) collection-media-type))

(defn- create-item-link [coll-slug]
  (common/create-link (str "/" coll-slug "/") item/item-media-type))

(defn- item-links
  "Add the HATEAOS links to the item"
  [item]
  (let [links [:links (flatten [
    (self-link item)
    (update-link item)
    (delete-link item)
    (category-links item)
    (collection-link item)])]]
    (apply array-map (concat (flatten (vec (dissoc item :categories))) links))))

(defn- item-list-links
  "Array of HATEAOS links for the item list"
  [coll-slug]
  [(create-item-link coll-slug)])

(defn- item-to-json-map [item]
  ;; Generate JSON from the sorted array map that results from:
  ;; 1) removing unneeded :id key
  ;; 2) making an ordered array hash of the known ordered keys
  ;; 3) adding a sorted hash of any remaining keys except the categories key
  ;; 4) add back in the categories
  ;; 5) add the HATEAOS links to the array hash
  (let [item-props (dissoc item :id)]
    (-> item-props
      (common/ordered ordered-keys)
      (common/append-sorted (common/remaining-keys (dissoc item-props :categories) ordered-keys))
      (assoc :categories (:categories item))
      item-links)))

(defn render-items
  "Create a JSON representation of a group of items for the REST API"
  [coll-slug items]
  (json/generate-string {
    :collection (array-map
      :version common/json-collection-version
      :href (str "/" coll-slug)
      :links (item-list-links coll-slug)
      :items (map item-to-json-map items))}
    {:pretty true}))

(defn render-item
  "Create a JSON representation of an item for the REST API"
  [item]
  (json/generate-string (item-to-json-map item) {:pretty true}))