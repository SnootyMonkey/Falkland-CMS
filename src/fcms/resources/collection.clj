(ns fcms.resources.collection
  (:require [com.ashafa.clutch :as clutch]
            [fcms.lib.slugify :refer (slugify)]
            [fcms.resources.common :as common]))

(def collection-media-type "application/vnd.fcms.collection+json;version=1")

(defn get-collection
  "Given the slug of the collection, return the collection as a map, or nil if there's no collection with that slug"
  [slug]
  (clutch/with-db (common/db)
    (if-let [coll (:doc (first (clutch/get-view "collection" :all-ids-by-slug {:key slug :include_docs true})))]
      (common/map-from-db coll))))

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
  otherwise one will be created from the name. If a :slug is included in the
  properties and a collection already exists with that slug, a :slug-conflict will be returned."
  ([coll-name] (create-collection coll-name {}))
  ([coll-name props]
    (if (and (:slug props) (get-collection (:slug props)))
      :slug-conflict
      (let [slug (unique-slug (or (:slug props) (slugify coll-name)))]
        (when-let [collection (common/create (merge props {:slug slug :name coll-name}) :collection)]
          (common/map-from-db collection))))))

(defn- delete-collection-and-contents [id rev]
  ;; get all the id and rev of all the items in this collection
  (let [items (clutch/get-view (common/db) "item" :delete-by-coll-id {:key id :include_docs false})]
    ;; create a bulk update request to delete all the items and the collection
    (let [delete-items (map #(common/delete-map (:value %)) items)]
      (clutch/bulk-update (common/db) (vec (conj delete-items (common/delete-map [id rev])))))))

;; TODO this is returning the raw bulk update response, should return something else
(defn delete-collection
  "Given the slug of the collection, delete it and all its contents and return true,
  or return :bad-collection if the collection slug is not good"
  [slug]
  (if-let [coll (first (clutch/get-view (common/db) "collection" :delete-by-slug {:key slug :include_docs false}))]
    (delete-collection-and-contents (first (:value coll)) (last (:value coll)))
    :bad-collection))

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
    (if-let [result (first (clutch/get-view "item" :count-by-coll-id {:key (:id collection) :include_docs false}))]
      (:value result)
      0)))

(defn all-collections
  "Return all the collections in the system as a sequence of maps."
  []
  (clutch/with-db (common/db)
    (when-let [collections (clutch/get-view "collection" :all-ids-by-slug {:include_docs true})]
      (map #(common/map-from-db (:doc %)) collections))))