(ns fcms.resources.taxonomy
  (:require [clojure.string :refer (blank?)]
            [fcms.resources.common :as common]
            [fcms.resources.collection-resource :as resource]))

(def taxonomy-media-type "application/vnd.fcms.taxonomy+json;version=1")

(def reserved-properties
  "Properties that can't be specified during a create or update."
  (reduce conj common/reserved-properties [:collection :categories])) 
(def retained-properties
  "Properties that are retained during an update even if they aren't in the updated property set."
  (reduce conj common/retained-properties [:collection :categories]))

(defn get-taxonomy
  "Given the slug of the collection containing the taxonomy and the slug of the taxonomy,
  return the taxonomy as a map, or return :bad-collection if there's no collection with that slug, or
  nil if there is no taxonomy with that slug."
  [coll-slug slug]
    (resource/get-resource coll-slug slug :taxonomy))

(defn- valid-category-structure? [category]
  (= 1 (count (dissoc category :categories))))

(defn- valid-category-slug? [category]
  (common/valid-slug? (first (keys (dissoc category :categories)))))

(defn- valid-category-name? [category]
  (let [cat-name (first (vals (dissoc category :categories)))]
    (and (string? cat-name) (not (blank? cat-name)))))

;; Validate the category tree with the following logic:
;; is it a vector?
;; is everything in it a map (the representation of a category)?
;; are categories structured as valid categories?
;; are the slugs of the categories valid?
;; are the names of the categories valid?
;; gather all the categories with children and add the children to the accumulator
;; is the accumulator empty? then it's all valid
;; otherwise recurse on the first child in the accumulator
(defn valid-categories 
  ([categories] (valid-categories categories []))
  ([categories child-categories]
    (cond
      (not (vector? categories)) :invalid-structure
      (empty? categories) true
      (not-every? map? categories) :invalid-structure
      (not-every? valid-category-structure? categories) :invalid-structure
      (not-every? valid-category-slug? categories) :invalid-category-slug
      (not-every? valid-category-name? categories) :invalid-category-name
      :else
        (let [non-leaves (reduce conj child-categories (map :categories (filter :categories categories)))]
          (if (empty? non-leaves)
            true
            (recur (first non-leaves) (vec (rest non-leaves))))))))

;; TODO if a taxonomy structure is provided, make sure it's valid
(defn valid-new-taxonomy
  "Given the slug of the collection, the name of the taxonomy, and a map of a potential new taxonomy,
  check if the everything is in order to create the new taxonomy.
  Ensure the collection exists or return :bad-collection.
  Ensure the name of the taxonomy is specified or return :no-name.
  Ensure the slug is valid and doesn't already exist if it's specified,
  or return :invalid-slug or :slug-conflict respectively.
  If a property is included in the map of properties that is in the reserved-properties
  set, :property-conflict will be returned."
  ([coll-slug taxonomy-name] (valid-new-taxonomy coll-slug taxonomy-name {}))
  ([coll-slug taxonomy-name props]
    (resource/valid-new-resource coll-slug taxonomy-name reserved-properties type props)))

;; TODO if no taxonomy structure is provided create the initial empty one
;; TODO if a taxonomy structure is provided, make sure it's valid
(defn create-taxonomy
  "Create a new taxonomy in the collection specified by its slug, using the specified
  taxonomy name and an optional map of properties.
  If :slug is included in the properties it will be used as the taxonomy's slug, otherwise
  the slug will be created from the name.
  If a :slug is included in the properties and an taxonomy already exists
  in the collection with that slug, a :slug-conflict will be returned.
  If a :slug is included in the properties and it's not valid,
  :invalid-slug will be returned.
  If a property is included in the map of properties that is in the reserved-properties
  set, :property-conflict will be returned."
  ([coll-slug taxonomy-name] (create-taxonomy coll-slug taxonomy-name {}))
  ([coll-slug taxonomy-name props]
    (resource/create-resource coll-slug taxonomy-name :taxonomy reserved-properties props)))

(defn all-taxonomies [])