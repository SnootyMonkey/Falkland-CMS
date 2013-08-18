;; CouchDB Views
(ns fcms.db.views
  (:require [com.ashafa.clutch :as clutch]
            [fcms.resources.common :as common]))

(defn init []

  (println "FCMS: Initializing database")
  (clutch/with-db (common/db)

    ;; http://localhost:5984/falklandcms/_design/fcms/_view/all
    ;; http://localhost:5984/falklandcms/_design/collection/_view/all?include_docs=true
    ;; http://localhost:5984/falklandcms/_design/fcms/_view/all?key="id"
    ;; http://localhost:5984/falklandcms/_design/fcms/_view/all-by-type
    ;; http://localhost:5984/falklandcms/_design/collection/_view/all-by-type?include_docs=true
    ;; http://localhost:5984/falklandcms/_design/fcms/_view/all-by-type?key=["type", "id"]
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
            (js/emit (js/Array (aget data "collection") (aget data "slug")) (aget doc "_id"))))}}))

    ;; http://localhost:5984/falklandcms/_design/collection/_view/all-ids-by-slug
    ;; http://localhost:5984/falklandcms/_design/collection/_view/all-ids-by-slug?include_docs=true
    ;; http://localhost:5984/falklandcms/_design/collection/_view/all-ids-by-slug?key="collection-slug"
    ;; http://localhost:5984/falklandcms/_design/collection/_view/delete-by-slug?key="collection-slug"
    (clutch/save-view "collection"
      (clutch/view-server-fns :cljs {
        :all-ids-by-slug {:map (fn [doc]
          (let [data (aget doc "data")]
            (when (and data (= (aget data "type") "collection"))
              (js/emit (aget data "slug") (aget doc "_id")))))}
        :delete-by-slug {:map (fn [doc]
          (let [data (aget doc "data")]
            (when (and data (= (aget data "type") "collection"))
              (js/emit (aget data "slug") (js/Array (aget doc "_id")(aget doc "_rev"))))))}}))

    ;; http://localhost:5984/falklandcms/_design/item/_view/all-ids-by-coll-id-and-slug
    ;; http://localhost:5984/falklandcms/_design/item/_view/all-ids-by-coll-id-and-slug?include_docs=true
    ;; http://localhost:5984/falklandcms/_design/item/_view/all-ids-by-coll-id-and-slug?key=["collection-id", "item-slug"]
    ;; http://localhost:5984/falklandcms/_design/item/_view/all-slugs-by-coll-id?key="collection-id"
    ;; http://localhost:5984/falklandcms/_design/item/_view/all-slugs-by-coll-id?key="collection-id"&include_docs=true
    ;; http://localhost:5984/falklandcms/_design/item/_view/delete-by-coll-id?key="collection-id"
    ;; http://localhost:5984/falklandcms/_design/item/_view/count-by-coll-id?key="collection-id"
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
          :reduce (fn [_ values _] (reduce + values))}}))

    ;; http://localhost:5984/falklandcms/_design/taxonomy/_view/all-ids-by-coll-id-and-slug
    ;; http://localhost:5984/falklandcms/_design/taxonomy/_view/all-ids-by-coll-id-and-slug?include_docs=true
    ;; http://localhost:5984/falklandcms/_design/taxonomy/_view/all-ids-by-coll-id-and-slug?key=["collection-id", "taxonomy-slug"]
    ;; http://localhost:5984/falklandcms/_design/taxonomy/_view/all-slugs-by-coll-id?key="collection-id"
    ;; http://localhost:5984/falklandcms/_design/taxonomy/_view/all-slugs-by-coll-id?key="collection-id"&include_docs=true
    ;; http://localhost:5984/falklandcms/_design/taxonomy/_view/delete-by-coll-id?key="collection-id"    
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

  (println "FCMS: Database initialization complete")

  ;; TODO want something more like below, so with less repition, but type needs to be a string literal by the time it makes it into the generated JS
  ;; at a minimum, need a macro that takes care of the with-db save-view view-server-fns boilerplate.

  ; (def fcms-types #{"collection" "item" "taxonomy"})

  ; (defn- type-of [doc type]
  ;   (and (aget doc "data") (= (aget (aget doc "data") "type") type)))

  ; (defn all [type]
  ;   (clutch/with-db (common/db)
  ;     (clutch/save-view type-name
  ;       (clutch/view-server-fns :cljs {:all {:map (fn [doc]
  ;         (if (type-of doc type)
  ;           (js/emit (str (aget (aget doc "data") "slug") " | " (aget doc "_id") " | " (aget doc "_rev")) nil))}}))))

  ; (println "FCMS: Initializing CouchDB views.")
  ; (doseq [type ["collection", "item", "taxonomy"]] (all type))
  ; (println "FCMS: CouchDB view initialization complete.")
)

(defn -main []
  (init))