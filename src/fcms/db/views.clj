;; CouchDB Views
(ns fcms.db.views
  (:require [com.ashafa.clutch :as clutch]
            [fcms.resources.common :as common]))

;; http://localhost:5984/falklandcms/_design/fcms/_view/all
;; http://localhost:5984/falklandcms/_design/collection/_view/all?include_docs=true
;; http://localhost:5984/falklandcms/_design/fcms/_view/all?key="id"
;; http://localhost:5984/falklandcms/_design/fcms/_view/all-by-type
;; http://localhost:5984/falklandcms/_design/collection/_view/all-by-type?include_docs=true
;; http://localhost:5984/falklandcms/_design/fcms/_view/all-by-type?key=["type", "id"]
(defn- fcms-views []
  (clutch/save-view "fcms"
    (clutch/view-server-fns :cljs {
      :all {:map (fn [doc]
        (if-let [data (aget doc "data")]
          (if-let [type (aget data "type")]
            (if (#{"collection" "item" "taxonomy"} type)
              (js/emit (aget doc "_id") (aget data "type"))))))}
      :all-by-type {:map (fn [doc]
        (if-let [data (aget doc "data")]
          (if-let [type (aget data "type")]
            (if (#{"collection" "item" "taxonomy"} type)
              (js/emit (js/Array (aget data "type") (aget doc "_id")) nil)))))}
      :all-ids-by-coll-id-and-slug {:map (fn [doc]
        (let [data (aget doc "data")]
          (js/emit (js/Array (aget data "collection") (aget data "slug")) (aget doc "_id"))))}})))

;; http://localhost:5984/falklandcms/_design/collection/_view/all-ids-by-slug
;; http://localhost:5984/falklandcms/_design/collection/_view/all-ids-by-slug?include_docs=true
;; http://localhost:5984/falklandcms/_design/collection/_view/all-ids-by-slug?key="collection-slug"
;; http://localhost:5984/falklandcms/_design/collection/_view/delete-by-slug?key="collection-slug"
(defn- collection-views []
  (clutch/save-view "collection"
    (clutch/view-server-fns :cljs {
      :all-ids-by-slug {:map (fn [doc]
        (let [data (aget doc "data")]
          (when (and data (= (aget data "type") "collection"))
            (js/emit (aget data "slug") (aget doc "_id")))))}
      :delete-by-slug {:map (fn [doc]
        (let [data (aget doc "data")]
          (when (and data (= (aget data "type") "collection"))
            (js/emit (aget data "slug") (js/Array (aget doc "_id")(aget doc "_rev"))))))}})))

;; http://localhost:5984/falklandcms/_design/item/_view/all-ids-by-coll-id-and-slug
;; http://localhost:5984/falklandcms/_design/item/_view/all-ids-by-coll-id-and-slug?include_docs=true
;; http://localhost:5984/falklandcms/_design/item/_view/all-ids-by-coll-id-and-slug?key=["collection-id", "item-slug"]
;; http://localhost:5984/falklandcms/_design/item/_view/all-slugs-by-coll-id?key="collection-id"
;; http://localhost:5984/falklandcms/_design/item/_view/all-slugs-by-coll-id?key="collection-id"&include_docs=true
;; http://localhost:5984/falklandcms/_design/item/_view/delete-by-coll-id?key="collection-id"
;; http://localhost:5984/falklandcms/_design/item/_view/count-by-coll-id?key="collection-id"
(defn- item-views []
  (clutch/save-view "item"
    (clutch/view-server-fns :cljs {
      :all-ids-by-coll-id-and-slug {:map (fn [doc]
        (let [data (aget doc "data")]
          (when (and data (= (aget data "type") "item"))
            (js/emit (js/Array (aget data "collection") (aget data "slug")) (aget doc "_id")))))}
      :all-slugs-by-coll-id {:map (fn [doc]
        (let [data (aget doc "data")]
          (when (and data (= (aget data "type") "item"))
            (js/emit (aget data "collection") (aget data "slug")))))}
      :delete-by-coll-id {:map (fn [doc]
        (let [data (aget doc "data")]
          (when (and data (= (aget data "type") "item"))
            (js/emit (aget data "collection") (js/Array (aget doc "_id")(aget doc "_rev"))))))}
      :count-by-coll-id {
        :map (fn [doc]
          (let [data (aget doc "data")]
            (when (and data (= (aget data "type") "item"))
              (js/emit (aget data "collection") 1))))
        :reduce (fn [_ values _] (reduce + values))}})))

;; http://localhost:5984/falklandcms/_design/taxonomy/_view/all-ids-by-coll-id-and-slug
;; http://localhost:5984/falklandcms/_design/taxonomy/_view/all-ids-by-coll-id-and-slug?include_docs=true
;; http://localhost:5984/falklandcms/_design/taxonomy/_view/all-ids-by-coll-id-and-slug?key=["collection-id", "taxonomy-slug"]
;; http://localhost:5984/falklandcms/_design/taxonomy/_view/all-slugs-by-coll-id?key="collection-id"
;; http://localhost:5984/falklandcms/_design/taxonomy/_view/all-slugs-by-coll-id?key="collection-id"&include_docs=true
;; http://localhost:5984/falklandcms/_design/taxonomy/_view/delete-by-coll-id?key="collection-id"    
(defn- taxonomy-views []
  (clutch/save-view "taxonomy"
    (clutch/view-server-fns :cljs {
      :all-ids-by-coll-id-and-slug {:map (fn [doc]
        (let [data (aget doc "data")]
          (when (and data (= (aget data "type") "taxonomy"))
            (js/emit (js/Array (aget data "collection") (aget data "slug")) (aget doc "_id")))))}
      :all-slugs-by-coll-id {:map (fn [doc]
        (let [data (aget doc "data")]
          (when (and data (= (aget data "type") "taxonomy"))
            (js/emit (aget data "collection") (aget data "slug")))))}
      :delete-by-coll-id {:map (fn [doc]
        (let [data (aget doc "data")]
          (when (and data (= (aget data "type") "taxonomy"))
            (js/emit (aget data "collection") (js/Array (aget doc "_id")(aget doc "_rev"))))))}})))

(defn init []
  (println "FCMS: Initializing database")
  (clutch/with-db (common/db)
     (fcms-views)
     (collection-views)
     (item-views)
     (taxonomy-views))
  (println "FCMS: Database initialization complete"))

(defn -main []
  (init))