(ns fcms.resources.collection
  (:require [com.ashafa.clutch :as clutch]
            [fcms.lib.slugify :refer (slugify)]
            [fcms.resources.common :as common]))

(def collection-media-type "application/vnd.fcms.collection+json;version=1")

(defn get-collection
  "Given the slug of the collection, return the collection as a map, or nil if there's no collection with that slug"
  [slug]
  (clutch/with-db (common/db)
    (if-let [coll (:doc (first (clutch/get-view "collection" :all {:key slug :include_docs true})))]
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

;; TODO delete the items as well
;; (collection/with-collection "c"
;;    (clutch/get-view "item" :delete-by-collection {:key (:id collection) :include_docs false}))
;; clutch/bulk-update (common/db) [{:_id "90266b5200cbea828bffa934c440fcf0" :_rev "3-a88db101ac4c9a986b63f38c57cc8ba3" :_deleted true} {:_id "90266b5200cbea828bffa934c45204f8" :rev "1-a2d62a6566335d34b40aabe622456c0b" :_deleted true}])
(defn delete-collection
  "Given the slug of the collection, delete it and all its contents and return true,
  or return :bad-collection if the collection slug is not good"
  [slug]
  (if-let [id (clutch/with-db (common/db)
    (:id (first (clutch/get-view "collection" :all {:key slug :include_docs false}))))]
      (do (common/delete-by-id id) true)
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
    (if-let [result (first (clutch/get-view "item" :count-by-collection {:key (:id collection) :include_docs false}))]
      (:value result)
      0)))

(defn all-collections
  "Return all the collections in the system as a sequence of maps."
  []
  (clutch/with-db (common/db)
    (when-let [results (clutch/get-view "collection" :all {:include_docs true})]
      (map #(common/map-from-db (:doc %)) results))))