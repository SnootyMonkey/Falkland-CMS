(ns fcms.resources.taxonomy
  (:require [clojure.core.match :refer (match)]
            [clojure.string :as s]
            [flatland.ordered.map :refer (ordered-map)]
            [fcms.lib.ordered-map :refer (zip-ordered-map)]
            [fcms.resources.common :as common]
            [fcms.resources.collection-resource :as resource]
            [fcms.resources.collection :as collection]
            [fcms.resources.item :as item]))

(def taxonomy-media-type "application/vnd.fcms.taxonomy+json;version=1")

(defn get-taxonomy
  "Given the slug of the collection containing the taxonomy and the slug of the taxonomy,
  return the taxonomy as a map, or return :bad-collection if there's no collection with that slug, or
  nil if there is no taxonomy with that slug."
  [coll-slug slug]
    (resource/get-resource coll-slug slug :taxonomy))

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
    (resource/valid-new-resource coll-slug taxonomy-name resource/reserved-properties type props)))

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
    (resource/create-resource coll-slug taxonomy-name :taxonomy (resource/allow-category-reserved-properties) (assoc props :categories []))))

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
  the properties it will be retain its current slug."
  [coll-slug slug props]
  (resource/valid-resource-update coll-slug slug resource/reserved-properties props :taxonomy))

(defn update-taxonomy
  "Update a taxonomy in the collection specified by its slug using the specified
  map of properties. If :slug is included in the properties
  the taxonomy will be moved to the new slug, otherwise the slug will remain the same.
  The same validity conditions and invalid return values as valid-taxonomy-update? apply."
  [coll-slug slug props]
    (let [reason (valid-taxonomy-update coll-slug slug props)]
      (if (true? reason)
        (resource/update-resource coll-slug slug 
          {:reserved resource/reserved-properties
          :retained resource/retained-properties 
          :updated props} :taxonomy)
        reason)))

(defn all-taxonomies
  "Given the slug of the collection, return all the taxonomies it contains as a sequence of maps,
  or return :bad-collection if there's no collection with that slug."
  [coll-slug]
  (resource/all-resources coll-slug :taxonomy))

;; Category functions

(defn- valid-category-slug? [category]
  (common/valid-slug? (:slug category)))

(defn- valid-category-structure? [category]
  (and (contains? :slug) (contains? :name)
    (= 2 (count (dissoc category :categories)))))

(defn- valid-category-name? [category]
  (common/valid-name? (:name category)))

(defn- valid-categories
  "Validate a tree of categories, the following errors may be returned:
  invalid-structure, :invalid-category-name, :invalid-category-slug"
  ([categories] (valid-categories categories []))
  ([categories child-categories]
    ;; Validate a category tree with the following logic:
    ;; is it a vector?
    ;; is everything in it a map (the representation of a category)?
    ;; are categories structured as valid categories?
    ;; are the slugs of the categories valid?
    ;; are the names of the categories valid?
    ;; gather all the categories with children and add the children to the accumulator
    ;; is the accumulator empty? then it's all valid
    ;; otherwise recurse on the first child in the accumulator
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

(defn- 
  ^{:testable true}
  taxonomy-slug-from-path [category-path]
  "Return the taxonomy slug given a category path such as: /taxonomy-slug/category-a/category-b"
  (if (or (nil? category-path) (not (string? category-path)))
    ""
    (let [path-parts (s/split category-path #"/")]
      (if (and (> (count path-parts) 1) (s/blank? (first path-parts)))
        (nth path-parts 1)
        (first path-parts)))))

(defn- 
  ^{:testable true}
  category-slugs-from-path [category-path]
  "Return a sequence of the category slugs given a category path such as: /taxonomy-slug/cat-a/cat-b"
  (if (or (nil? category-path) (not (string? category-path)))
    []
    (let [path-parts (s/split category-path #"/")]
        ;; "" => []
        ;; "tax" => []
        ;; "" "tax" => []
        ;; "" "tax" "cat-a" "cat-b" => ["cat-a" "cat-b"]
        ;; "tax" "cat-a" "cat-b" => ["cat-a" "cat-b"]
      (cond 
        (= (count path-parts) 1) []
        (s/blank? (first path-parts)) (vec (rest (rest path-parts)))
        :else (vec (rest path-parts))))))

(declare hash-category-slugs)

(defn- category-from-map [m]
  "Return the category map with its :categories vector replaced by an ordered map (if it has one)"
  (if-let [categories (:categories m)]
    (assoc m :categories (hash-category-slugs categories))
    m))

(defn- hash-category-slugs
  "Replace all the vectors of maps in the category tree with ordered maps of maps keyed by the category slug"
  [categories]
    ; create an ordered map with the slug as the key and the name and categories as values
    (zip-ordered-map (map :slug categories) (map category-from-map categories)))

(defn- new-categories [slugs category-name]
  "Create the new portion of a category path as an ordered-map (:name, :slug, :categories) of the slugs in
  a vector with each subsequent slug being a child of the prior slug,
  keyed by :categories, and the final slug getting the provided category-name."
  (let [slug (first slugs)
        tail (rest slugs)]
    (if (empty? tail)
      {:slug slug :name category-name}
      {:slug slug :name slug :categories (ordered-map (first tail) (new-categories tail category-name))})))

(declare vectorize-category-slugs)

(defn- categories-vector-from-map [m]
  "Replace the hash map of categories at the :categories key with a vector of categories"
  (if-let [categories (:categories m)]
    (assoc m :categories (vectorize-category-slugs categories))
    m))

(defn- vectorize-category-slugs
  "Replace all the ordered maps of maps in the category tree with vectors of maps"
  [categories]
  (vec (map categories-vector-from-map (vec (vals categories)))))

(defn- create-categories
  "Given a category name, a vector of the slugs of a desired category path,
  and the categories that already exist in the taxonomy, add the portions of the 
  desired category path that don't already exist."
  ([category-name category-slugs categories] 
    (vectorize-category-slugs (create-categories category-name [] category-slugs (hash-category-slugs categories))))
 
  ([category-name category-path category-slugs categories]
    (let [category-slug (first category-slugs)
          category (get-in categories (conj category-path category-slug))
          remaining-path (vec (rest category-slugs))]
      (match [category-slug remaining-path category]
        ;; the category exists and its the last one in the path we are adding
        [_ [] {}] categories ; all done with the existing categories as they are

        ;; the category doesn't exist
        [_ _ nil] 
          ; add it
          (assoc-in categories (conj category-path category-slug) 
                               (new-categories (vec (cons category-slug remaining-path)) category-name)) 
        
        ;; the category exists and it has categories already
        [_ _ ({} :guard :categories)] 
          ; recurse (not tail recursion)
          (create-categories category-name (conj category-path category-slug :categories) remaining-path categories)
        
        ;; else, the category exists but it doesn't have categories
        :else
          ; add categories
          (assoc-in categories (conj category-path category-slug :categories) 
                               (ordered-map (first remaining-path) (new-categories remaining-path category-name)))))))

(defn create-category
  "Given the slug of the collection, a path to a new category, add an optional name for the category, create
  the category and any missing categories in the path to the category.
  For example, a path: /taxonomy-slug/existing-a/new-category-a/new-category-b
  would result in creating two new categories with the slugs new-category-a and new-category-b. If a name
  is provided, it is the name for the last new category, in this case the new category with the slug new-category-b.
  The slug from the category path is used as the name where none is provided.
  :bad-collection is returned if there's no collection with that slug.
  :bad-taxonomy is returned if there's no taxonomy with that slug at the start of the category path.
  :invalid-category-slug is returned if any of the slugs in the category path are not valid FCMS slugs.
  :invalid-category-name is returned if a category name is provided and it's not a non-empty string."
  ([coll-slug category-path] (create-category coll-slug category-path (last (category-slugs-from-path category-path))))
  ([coll-slug category-path category-name]
    (let [taxonomy-slug (taxonomy-slug-from-path category-path)
          category-slugs (category-slugs-from-path category-path)
          result (get-taxonomy coll-slug taxonomy-slug)]
      (cond 
        (keyword? result) result
        (nil? result) :bad-taxonomy
        (not-every? common/valid-slug? category-slugs) :invalid-category-slug
        (not (common/valid-name? category-name)) :invalid-category-name
        :else (resource/update-resource coll-slug taxonomy-slug
                {:reserved (resource/allow-category-reserved-properties)
                 :retained resource/retained-properties
                 :updated (assoc result :categories (create-categories category-name category-slugs (:categories result)))}
                :taxonomy)))))

(defn- category-exists?
  "Recursive function to ensure that each category in the path exists in the category tree."
  [path-so-far remaining-path categories]
    (if (empty? remaining-path)
      true
      (let [category-slug (first remaining-path)
            category (get-in categories (conj path-so-far category-slug))]
        (if-not category
          false
          (recur (conj path-so-far category-slug :categories) (rest remaining-path) categories)))))

(defn category-exists
  "Given the slug of the collection, and a path to a category return true if the category exists and false if it does not.
  :bad-collection is returned if there's no collection with that slug.
  :bad-taxonomy is returned if there's no taxonomy with that slug at the start of the category path."
  [coll-slug category-path]
  (let [taxonomy-slug (taxonomy-slug-from-path category-path)
        category-slugs (category-slugs-from-path category-path)
        result (get-taxonomy coll-slug taxonomy-slug)]
    (cond 
      (keyword? result) result
      (nil? result) :bad-taxonomy
      (empty? category-slugs) false
      :else (category-exists? [] category-slugs (hash-category-slugs (:categories result))))))

(defn normalize-category-path
  "Remove the prefix slash and trailing slash if they are present."
  [category-path]
  ;; "/tax/cat/cat/" => "tax/cat/cat"
  (s/replace (s/replace category-path #"^/" "") #"/$" ""))

(defn- duplicate-category?
  "Determine if the item as already categorized by this category, or by one of its children."
  [category-path categories]
  (let [path (normalize-category-path category-path)]
    (if (some #(re-find (re-pattern (str "^" path)) %) categories) true false)))

(defn- validate-category-request [coll-slug category-path item-slug f]
  (let [path (normalize-category-path category-path)
        taxonomy-slug (taxonomy-slug-from-path path)
        category-slugs (category-slugs-from-path path)
        result (get-taxonomy coll-slug taxonomy-slug)
        item (item/get-item coll-slug item-slug)]
    (cond 
      (keyword? result) result
      (nil? item) :bad-item
      (nil? result) :bad-taxonomy
      (empty? category-slugs) :bad-category
      :else (f path taxonomy-slug category-slugs result item))))

(defn categorize-item
  "Given the slug of the collection, a slug of an item in the collection, and a path to a category in a taxonomy,
  categorize the item as a member of the category. This function is idempotent and categorizing the item again won't
  change the item.
  If this request is to categorize the item with a child of a parent category that is already categorized on the item,
  then the parent categorization will be removed.
  :bad-collection is returned if there's no collection with that slug.
  :bad-item is returned if there's no item in the collection with that slug.
  :bad-taxonomy is returned if there's no taxonomy with that slug at the start of the category path.
  :bad-category is returned if there's no category in the taxonomy with that category path.
  :duplicate-category is returned if item is already a member of the provided category or one of its children."
  [coll-slug category-path item-slug]
  (validate-category-request coll-slug category-path item-slug
    (fn [path taxonomy-slug category-slugs result item] 
      (cond 
        (not (true? (category-exists coll-slug path))) :bad-category
        (duplicate-category? path (:categories item)) :duplicate-category
        :else
          ;; add the category to the categories vector and update the item
          (resource/update-resource coll-slug item-slug
            {:reserved (resource/allow-category-reserved-properties)
             :retained resource/retained-properties
             :updated (assoc item :categories (vec (conj (:categories item) path)))}
             :item)))))

(defn uncategorize-item
  "Given the slug of the collection, a slug of an item in the collection, and a path to a category in a taxonomy,
  remove the item as a member of the category.
  :bad-collection is returned if there's no collection with that slug.
  :bad-item is returned if there's no item in the collection with that slug.
  :bad-taxonomy is returned if there's no taxonomy with that slug at the start of the category path.
  :bad-category is returned if the item is not categorized with the provided category path."
  [coll-slug category-path item-slug]
  (validate-category-request coll-slug category-path item-slug
    (fn [path taxonomy-slug category-slugs result item] 
      (if (nil? (some #{path} (:categories item)))
        :bad-category
        ;; else, remove the category from the categories vector and update the item
        (resource/update-resource coll-slug item-slug
          {:reserved (resource/allow-category-reserved-properties)
           :retained resource/retained-properties
           :updated (assoc item :categories (filterv #(not(= path %)) (:categories item)))}
              :item)))))