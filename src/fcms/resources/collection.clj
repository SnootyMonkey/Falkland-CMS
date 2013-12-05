(ns fcms.resources.collection
  (:require [com.ashafa.clutch :as clutch]
            [fcms.lib.slugify :refer (slugify)]
            [fcms.resources.common :as common]))

(def
  ^{:no-doc true}
  collection-media-type "application/vnd.fcms.collection+json;version=1")
(def
  ^{:no-doc true}
  collection-collection-media-type "application/vnd.collection+vnd.fcms.collection+json;version=1")

(defn get-collection
  "Given the slug of the collection, return the collection as a map, or nil if there's no collection with that slug"
  [slug]
  (if-let [coll (:doc (first (common/doc-from-view :collection :all-ids-by-slug slug)))]
    (common/map-from-db coll)))

(defn- unique-slug
  "Look for a conflicting collection slug and increment an appended slug counter
  numeral until you have a unique collection slug."
  ([slug] (unique-slug slug 0))
  ([slug counter]
    (if-not (get-collection slug)
      slug
      ;; recur with the next possible slug
      (recur (common/next-slug slug counter) (inc counter)))))

(defn create-collection
  "Create a new collection using the specified name and optional map of properties.
  If :slug is included in the properties it will be used as the collection's slug,
  otherwise one will be created from the name.
  :slug-conflict is returned if a :slug is included in the
  properties and a collection already exists with that slug."
  ([coll-name] (create-collection coll-name {}))
  ([coll-name props]
    (if (and (:slug props) (get-collection (:slug props)))
      :slug-conflict
      (let [slug (unique-slug (or (:slug props) (slugify coll-name)))]
        (when-let [collection (common/create (merge props {:slug slug :name coll-name}) :collection)]
          (common/map-from-db collection))))))

(defn- delete-collection-and-contents [id rev]
  ;; get all the id and rev of all the items in this collection
  (let [items (common/from-view :item :delete-by-coll-id id)]
    ;; create a bulk update request to delete all the items and the collection
    (let [delete-items (map #(common/delete-map (:value %)) items)]
      (clutch/bulk-update (common/db) (vec (conj delete-items (common/delete-map [id rev])))))))

;; TODO this is returning the raw bulk update response, should return something else
(defn delete-collection
  "Given the slug of the collection, delete it and all its contents and return true,
  or return :bad-collection if the collection slug is not good"
  [slug]
  (if-let [coll (first (common/from-view :collection :delete-by-slug slug))]
    (delete-collection-and-contents (first (:value coll)) (last (:value coll)))
    :bad-collection))

(defn valid-collection-update?
  "Given the slug of the collection, and a map of updated
  properties for the collection, check if the everything
  is in order to update the collection.
  Ensure the collection exists or return :bad-collection.
  If a new slug is provided in the properties, ensure it is
  valid or return :invalid-slug and ensure it is unused or
  return :slug-conflict. If no slug is specified in
  the properties it will be retain its current slug."
  [slug {coll-name :name provided-slug :slug}]
    (let [id (:id (get-collection slug))]
      (cond
        (nil? id) :bad-collection
        (not provided-slug) true
        (not (common/valid-slug? provided-slug)) :invalid-slug
        (= slug provided-slug) true
        :else (if (nil? (get-collection provided-slug)) true :slug-conflict))))

(defn- update
  "Update a collection retaining it's manufactured properties and replacing the rest with the provided properties"
  [slug updated-props]
  (if-let [coll (:doc (first (common/doc-from-view :collection :all-ids-by-slug slug)))]
    (let [retained-props (select-keys (:data coll) (conj common/retained-properties :version))]
      (common/map-from-db (common/update coll (merge retained-props updated-props))))
    :bad-collection))

(defn update-collection
  "Update the collection specified by its slug using the specified
  map of properties. If :slug is included in the properties
  the item will be moved to the new slug, otherwise the slug will remain the same.
  The same validity conditions and invalid return values as valid-item-update? apply."
  [slug props]
    (let [reason (valid-collection-update? slug props)]
      (if (= reason true)
        (update slug props)
        reason)))

(defmacro with-collection
  "Given the slug of the collection, execute some code with the retrieved collection
  lexically scoped as collection, or return :bad-collection if the collection slug is no good"
  [coll-slug & body]
  `(if-let [~'collection (get-collection ~coll-slug)]
    (clutch/with-db (common/db)
      ~@body)
    :bad-collection))

(defn item-count
  "Given the slug of the collection, return the number of items it contains,
  or return :bad-collection if the collection slug is no good"
  [coll-slug]
  (with-collection coll-slug
    (if-let [result (first (common/from-view-with-db :item :count-by-coll-id (:id collection)))]
      (:value result)
      0)))

(defn all-collections
  "Return all the collections in the system as a sequence of maps."
  []
  (when-let [collections (common/doc-from-view :collection :all-ids-by-slug)]
    (vec (map #(common/map-from-db (:doc %)) collections))))