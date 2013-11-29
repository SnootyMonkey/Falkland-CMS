(ns fcms.representations.collections
  (:require [cheshire.core :as json]
            [clj-time.format :refer (unparse)]
            [fcms.resources.common :refer (timestamp-format)]
            [fcms.representations.common :as common]
            [fcms.resources.collection :as collection]
            [fcms.resources.item :refer (item-media-type)]))

(def ordered-keys [:name :created-at :updated-at :slug :description])

(defn- url
  ([coll]
    (str "/" (:slug coll)))
  ([coll suffix]
    (str "/" (:slug coll) suffix)))

(defn- self-link [coll]
  (common/self-link (url coll) collection/collection-media-type))

(defn- contains-link [coll]
  (common/link-map "contains" common/GET (url coll "/") item-media-type))

(defn- create-link [coll]
  (common/create-link (url coll "/") item-media-type))

(defn- update-link [coll]
  (common/update-link (url coll) collection/collection-media-type))

(defn- delete-link [coll]
  (common/delete-link (url coll)))

(defn links
  "Add the HATEAOS links to the collection"
  [coll]
  (apply array-map (concat (flatten (vec coll)) [:links [
    (self-link coll)
    (contains-link coll)
    (create-link coll)
    (update-link coll)
    (delete-link coll)]])))

(defn render-collection
  "Create a JSON representation of a collection for the REST API"
  [coll]
  ;; Generate JSON from the sorted array map that results from:
  ;; 1) render timestamps as strings
  ;; 2) removing unneeded :id key
  ;; 3) making an ordered array hash of the known ordered keys
  ;; 4) adding a sorted hash of any remaining keys
  ;; 5) adding the HATEAOS links to the array hash
  (let [coll-props (dissoc coll :id)]
    (json/generate-string
      (-> coll-props
        (update-in [:created-at] #(unparse timestamp-format %))
        (update-in [:updated-at] #(unparse timestamp-format %))
        (common/ordered ordered-keys)
        (common/append-sorted (common/remaining-keys coll-props ordered-keys))
        links)) {:pretty true}))