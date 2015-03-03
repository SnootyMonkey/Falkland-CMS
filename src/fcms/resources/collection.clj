(ns fcms.resources.collection
  "
  A collection is a container for all the items that you'd like to be organized and searched together.

  A collection may have none, one or many taxonomies and they are independent of each other.
  "
  (:require [clojure.set :refer (intersection)]
            [clojure.string :refer (blank?)]
            [clojure.walk :refer (keywordize-keys)]
            [com.ashafa.clutch :as clutch]
            [fcms.lib.slugify :refer (slugify)]
            [fcms.resources.common :as common]))

(def
  ^{:no-doc true}
  collection-media-type "application/vnd.fcms.collection+json;version=1")
(def
  ^{:no-doc true}
  collection-collection-media-type "application/vnd.collection+vnd.fcms.collection+json;version=1")

(defn get-collection
  "Given the slug of the collection, return the collection as a map, or `nil` if there's no collection with the specified slug."
  [slug]
  (if-let [coll (:doc (first (common/doc-from-view :collection :all-ids-by-slug slug)))]
    (common/map-from-db coll)))

(defn- unique-slug
  "Look for a conflicting collection slug and increment an appended slug counter
  numeral until you have a unique collection slug."
  ([slug] (unique-slug slug 0))
  ([slug counter]
    (if-not (or (blank? slug) (get-collection slug))
      slug
      ;; recur with the next possible slug
      (recur (common/next-slug slug counter) (inc counter)))))

(defn valid-new-collection
  "
  Given the name of a new collection, and a map of the new collection's properties,
  check if the everything is in order to create the new collection.

  Ensures the name of the item is specified or returns `:no-name`.

  Ensures the slug is valid and doesn't already exist if it's specified,
  or returns `:invalid-slug` or `:slug-conflict` respectively.

  If a property is included in the map of properties that is in the `common/reserved-properties`
  set of reserved property names `:property-conflict` is returned.

  If everything is OK with the proposed new collection, `true` is returned.
  "
  ([collection-name] (valid-new-collection collection-name {}))
  ([collection-name properties]
  (let [provided-slug (:slug properties)]
    (cond
      (or (not (string? collection-name)) (blank? collection-name)) :no-name
      (not-empty (intersection (set (keys (keywordize-keys properties))) common/reserved-properties)) :property-conflict
      (not provided-slug) true
      (not (common/valid-slug? provided-slug)) :invalid-slug
      :else (if (nil? (get-collection provided-slug)) true :slug-conflict)))))

(defn create-collection
  "Create a new collection using the specified name and an optional map of properties.

  If a `:slug` is included in the properties it will be used as the collection's slug,
  otherwise a slug will be created from the name.

  The same validity conditions and invalid return values as the `valid-new-collection` function
  apply.

  If everything is OK, a map representing the newly created collection is returned.
  "
  ([collection-name] (create-collection collection-name {}))
  ([collection-name properties]
    (let [props (keywordize-keys properties)
          validity (valid-new-collection collection-name props)]
      (if (true? validity)
        (let [slug (unique-slug (or (:slug props) (slugify collection-name)))]
          (when-let [collection (common/create-resource (merge props {:slug slug :name collection-name}) :collection)]
            (common/map-from-db collection)))
        validity))))

(defn- delete-collection-and-contents [id rev]
  ;; get all the id and rev of all the items in this collection
  (let [items (common/from-view :item :delete-by-coll-id id)]
    ;; create a bulk update request to delete all the items and the collection
    (let [delete-items (map #(common/delete-map (:value %)) items)]
      (clutch/bulk-update (common/db) (vec (conj delete-items (common/delete-map [id rev])))))))

(defn delete-collection
  "Given the slug of the collection, delete it and all its contents and return `true`,
  or return `:bad-collection` if no collection exists for the specified slug.

  If any portion of the bulk delete fails then the raw Couch DB bulk update response
  is returned."
  [slug]
  (if-let [coll (first (common/from-view :collection :delete-by-slug slug))]
    (let [result (delete-collection-and-contents (first (:value coll)) (last (:value coll)))]
      (if (every? #(and (map? %) (true? (:ok %))) result)
        true
        result))
    :bad-collection))

(defn valid-collection-update
  "
  Given the slug of the collection, and a map of updated
  properties for the collection, checks if the everything
  is in order to update the collection.

  Ensures the collection exists or returns `:bad-collection`.

  Ensures no reserved properties from `common/reserved-properties` are used or returns `:property-conflict`.

  If a new slug is provided in the properties, ensures it is
  valid and unused or returns `:invalid-slug` or
  `:slug-conflict` respectively.

  If everything is OK with the proposed update, `true` is returned.
  "
  [slug properties]
    (let [provided-slug (:slug properties)
          id (:id (get-collection slug))]
      (cond
        (nil? id) :bad-collection
        (not-empty (intersection (set (keys (keywordize-keys properties))) common/reserved-properties)) :property-conflict
        (not provided-slug) true
        (not (common/valid-slug? provided-slug)) :invalid-slug
        (= slug provided-slug) true
        :else (if (nil? (get-collection provided-slug)) true :slug-conflict))))

(defn- update-collection-in-db
  "Update a collection retaining it's manufactured properties and replacing the rest with the provided properties"
  [slug updated-props]
  (if-let [coll (:doc (first (common/doc-from-view :collection :all-ids-by-slug slug)))]
    (let [retained-props (select-keys (:data coll) (conj common/retained-properties :version))]
      (common/map-from-db (common/update-resource coll (merge retained-props updated-props))))
    :bad-collection))

(defn update-collection
  "
  Update the collection specified by its slug using the specified
  map of properties.

  If `:slug` is included in the properties the item will be moved to the
  new slug, otherwise the slug will remain the same.

  The same validity conditions and invalid return values as the `valid-collection-update` function
  apply.

  If everything is OK, a map representing the newly updated collection is returned.
  "
  [slug properties]
    (let [reason (valid-collection-update slug properties)]
      (if (= reason true)
        (update-collection-in-db slug properties)
        reason)))

(defmacro with-collection
  "Given the slug of a collection, execute the code in the body with the retrieved collection
  lexically scoped as `collection`, or return `:bad-collection` if there is no collection
  with the specified slug."
  [slug & body]
  `(if-let [~'collection (get-collection ~slug)]
    (clutch/with-db (common/db)
      ~@body)
    :bad-collection))

(defn item-count
  "Given the slug of the collection, return the number of items it contains,
  or return `:bad-collection` if there is no collection with the specified slug."
  [slug]
  (with-collection slug
    (if-let [result (first (common/from-view-with-db :item :count-by-coll-id (:id collection)))]
      (:value result)
      0)))

(defn all-collections
  "Return all the collections in the system as a sequence of maps."
  []
  (when-let [collections (common/doc-from-view :collection :all-ids-by-slug)]
    (vec (map #(common/map-from-db (:doc %)) collections))))

(defn collection-count
  "Return the number of collections in the system."
  []
  (count (common/doc-from-view :collection :all-ids-by-slug)))