(ns fcms.resources.taxonomy
  (:require [clojure.string :refer (blank? split)]
            [fcms.resources.common :as common]
            [fcms.resources.collection-resource :as resource]
            [fcms.resources.collection :as collection]))

(def taxonomy-media-type "application/vnd.fcms.taxonomy+json;version=1")

(def reserved-properties
  "Properties that can't be specified during a create and are ignored during an update."
  (reduce conj common/reserved-properties [:collection])) 
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

(defn valid-new-taxonomy
  "Given the slug of the collection, the name of the taxonomy, and a map of a potential new taxonomy,
  check if the everything is in order to create the new taxonomy.
  Ensure the collection exists or return :bad-collection.
  Ensure the name of the taxonomy is specified or return :no-name.
  Ensure the slug is valid and doesn't already exist if it's specified,
  or return :invalid-slug or :slug-conflict respectively.
  If a property is included in the map of properties that is in the reserved-properties
  set, :property-conflict will be returned.
  If a tree of categories is provided in the :categories property, it is validated and the
  following errors may be returned: invalid-structure, :invalid-category-name, :invalid-category-slug"
  ([coll-slug taxonomy-name] (valid-new-taxonomy coll-slug taxonomy-name {}))
  ([coll-slug taxonomy-name props]
    (let [validity (if (:categories props) (valid-categories (:categories props)) true)]
      (if (true? validity)
        (resource/valid-new-resource coll-slug taxonomy-name reserved-properties type props)
        validity))))

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
  set, :property-conflict will be returned.
  If a tree of categories is provided in the :categories property, it is validated and the
  following errors may be returned: :invalid-structure, :invalid-category-name, :invalid-category-slug"
  ([coll-slug taxonomy-name] (create-taxonomy coll-slug taxonomy-name {}))
  ([coll-slug taxonomy-name props]
    (if (:categories props)
      (let [validity (valid-categories (:categories props))]
        (if (true? validity) 
          (resource/create-resource coll-slug taxonomy-name :taxonomy reserved-properties props)
          validity))
      (create-taxonomy coll-slug taxonomy-name (assoc props :categories [])))))

(defn delete-taxonomy
  "Given the slug of the collection containing the taxonomy and the slug of the taxonomy,
  delete the taxonomy, or return :bad-collection if there's no collection with that slug, or
  :bad-taxonomy if there is no taxonomy with that slug."
  [coll-slug slug]
  (resource/delete-resource coll-slug slug :taxonomy))

(defn valid-taxonomy-update
  "Given the slug of the collection, the slug of the taxonomy,
  and a map of updated properties for the taxonomy,
  check if the everything is in order to update the taxonomy.
  Ensure the collection exists or return :bad-collection.
  Ensure the item exists or return :bad-taxonomy.
  If a new slug is provided in the properties, ensure it is
  valid or return :invalid-slug and ensure it is unused or
  return :slug-conflict. If no item slug is specified in
  the properties it will be retain its current slug.
  If a tree of categories is provided in the :categories property, it is validated and the
  following errors may be returned: :invalid-structure, :invalid-category-name, :invalid-category-slug"
  [coll-slug slug props]
  (let [validity (if (:categories props) (valid-categories (:categories props)) true)]
    (if (true? validity)
      (resource/valid-resource-update coll-slug slug reserved-properties props :taxonomy)
      validity)))

(defn update-taxonomy
  "Update a taxonomy in the collection specified by its slug using the specified
  map of properties. If :slug is included in the properties
  the taxonomy will be moved to the new slug, otherwise the slug will remain the same.
  The same validity conditions and invalid return values as valid-taxonomy-update? apply."
  [coll-slug slug props]
    (let [reason (valid-taxonomy-update coll-slug slug props)]
      (if (true? reason)
        (resource/update-resource coll-slug slug 
          {:reserved reserved-properties
          :retained retained-properties 
          :updated props} :taxonomy)
        reason)))

(defn all-taxonomies
  "Given the slug of the collection, return all the taxonomies it contains as a sequence of maps,
  or return :bad-collection if there's no collection with that slug."
  [coll-slug]
  (resource/all-resources coll-slug :taxonomy))

(defn- taxonomy-slug-from-path [category-path]
  "Return the taxonomy slug given a category path such as: /taxonomy-slug/category-a/category-b"
  (if (or (nil? category-path) (not (string? category-path)))
    ""
    (let [path-parts (split category-path #"/")]
      (if (and (> (count path-parts) 1) (blank? (first path-parts)))
        (nth path-parts 1)
        (first path-parts)))))

(defn- category-slugs-from-path [category-path]
  "Return a sequence of the category slugs given a category path such as: /taxonomy-slug/cat-a/cat-b"
  (if (or (nil? category-path) (not (string? category-path)))
    []
    (let [path-parts (split category-path #"/")]
        ;; "" => []
        ;; "tax" => []
        ;; "" "tax" => []
        ;; "" "tax" "cat-a" "cat-b" => ["cat-a" "cat-b"]
        ;; "tax" "cat-a" "cat-b" => ["cat-a" "cat-b"]
      (cond 
        (= (count path-parts) 1) []
        (blank? (first path-parts)) (vec (rest (rest path-parts)))
        :else (vec (rest path-parts))))))

;; ToDo - hide from docs (needs to be public for testing)
(defn create-categories
   ([category-slugs categories] (create-categories category-slugs categories categories))
   ([category-slugs category categories]
    true))

;     (let category-slug [(first category-slugs)]
;       (if (nil? category-slug)
;         categories

;       ; it doesn't exist and this is the last one - add w/ name and w/o categories
;       ; it doesn't exist and there are more - add w/ categories
;       ; it exists and this is the last one - done
;       ; it exists and there are more and it has categories - move on
;       ; it exists and there are more and it doesn't have categories - add categories
;         (recur (rest category-slugs) next-category updated-categories)))))))

(defn create-category
  "Given the slug of the collection, a path to a new category, add an optional name for the category, create
  the category and any missing categories in the path to the category.
  For example, a path: /taxonomy-slug/existing-a/new-category-a/new-category-b
  would result in creating two new categories with the slugs new-category-a and new-category-b. If a name
  was provided it would be the name for the category with the slug new-category-b.
  The slug from the category path is used as the name if none is provided.
  :bad-collection is returned if there's no collection with that slug.
  :bad-taxonomy is returned if there's no taxonomy with that slug from the start of the category path."
  ([coll-slug category-path] (create-category coll-slug category-path (taxonomy-slug-from-path category-path)))
  ([coll-slug category-path category-name]
    (let [taxonomy-slug (taxonomy-slug-from-path category-path)
          result (get-taxonomy coll-slug taxonomy-slug)]
      (cond 
        (nil? result) :bad-taxonomy
        (keyword? result) result
        :else (update-taxonomy coll-slug taxonomy-slug 
          (assoc result :categories (create-categories (category-slugs-from-path category-path) (:categories result))))))))