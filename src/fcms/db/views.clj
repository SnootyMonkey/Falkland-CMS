(ns fcms.db.views
  "Initialize CouchDB with the views needed for Falkland CMS."
  (:require [com.ashafa.clutch :as clutch]
            [fcms.resources.common :as common]
            [fcms.config :refer (db-name)]))

;; http://localhost:5984/falklandcms/_design/fcms/_view/all
;; http://localhost:5984/falklandcms/_design/fcms/_view/all?include_docs=true
;; http://localhost:5984/falklandcms/_design/fcms/_view/all?key="id"
;; http://localhost:5984/falklandcms/_design/fcms/_view/all?key="id"&include_docs=true
;; http://localhost:5984/falklandcms/_design/fcms/_view/all-by-type
;; http://localhost:5984/falklandcms/_design/fcms/_view/all-by-type?include_docs=true
;; http://localhost:5984/falklandcms/_design/fcms/_view/all-by-type?key=["type", "id"]
;; http://localhost:5984/falklandcms/_design/fcms/_view/all-by-type?key=["type", "id"]&include_docs=true
;; http://localhost:5984/falklandcms/_design/fcms/_view/all-by-rev
(defn- fcms-views []
  (clutch/save-view "fcms"
    (clutch/view-server-fns :javascript {
      :all {:map "function(doc) {
        if (['collection', 'item', 'taxonomy'].indexOf(doc.data.type) >= 0) emit(doc._id, doc.data.type) }"}
      :all-by-type {:map "function(doc) {
        if (['collection', 'item', 'taxonomy'].indexOf(doc.data.type) >= 0) emit([doc.data.type, doc._id], null) }"}
      :all-by-rev {:map "function(doc) {
        if (['collection', 'item', 'taxonomy'].indexOf(doc.data.type) >= 0) emit(null, doc._rev) }"}})))

;; http://localhost:5984/falklandcms/_design/collection/_view/all-ids-by-slug
;; http://localhost:5984/falklandcms/_design/collection/_view/all-ids-by-slug?include_docs=true
;; http://localhost:5984/falklandcms/_design/collection/_view/all-ids-by-slug?key="collection-slug"
;; http://localhost:5984/falklandcms/_design/collection/_view/delete-by-slug?key="collection-slug"
(defn- collection-views []
  (clutch/save-view "collection"
    (clutch/view-server-fns :javascript {
      :all-ids-by-slug {:map "function(doc) {
        if (doc.data.type == 'collection') emit(doc.data.slug, doc._id) }"}
      :delete-by-slug {:map "function(doc) {
        if (doc.data.type == 'collection') emit(doc.data.slug, [doc._id, doc._rev]) }"}})))

;; http://localhost:5984/falklandcms/_design/resource/_view/all-ids-by-coll-id-and-slug
;; http://localhost:5984/falklandcms/_design/resource/_view/all-ids-by-coll-id-and-slug?include_docs=true
;; http://localhost:5984/falklandcms/_design/resource/_view/all-ids-by-coll-id-and-slug?key=["collection-id", "resource-slug"]
;; http://localhost:5984/falklandcms/_design/resource/_view/all-ids-by-coll-id-and-slug?key=["collection-id", "resource-slug"]&include_docs=true
(defn- resource-views []
  (clutch/save-view "resource"
    (clutch/view-server-fns :javascript {
      :all-ids-by-coll-id-and-slug {:map "function(doc) {
        if (['item', 'taxonomy'].indexOf(doc.data.type) >= 0) emit([doc.data.collection, doc.data.slug], doc._id) }"}})))

;; http://localhost:5984/falklandcms/_design/item/_view/all-ids-by-coll-id-and-slug
;; http://localhost:5984/falklandcms/_design/item/_view/all-ids-by-coll-id-and-slug?include_docs=true
;; http://localhost:5984/falklandcms/_design/item/_view/all-ids-by-coll-id-and-slug?key=["collection-id", "item-slug"]
;; http://localhost:5984/falklandcms/_design/item/_view/all-slugs-by-coll-id?key="collection-id"
;; http://localhost:5984/falklandcms/_design/item/_view/all-slugs-by-coll-id?key="collection-id"&include_docs=true
;; http://localhost:5984/falklandcms/_design/item/_view/delete-by-coll-id?key="collection-id"
;; http://localhost:5984/falklandcms/_design/item/_view/count-by-coll-id?key="collection-id"
;; http://localhost:5984/falklandcms/_design/item/_view/all-ids-by-coll-id-and-category-path?key=["collection-id", "taxonomy-slug"]
;; http://localhost:5984/falklandcms/_design/item/_view/all-ids-by-coll-id-and-category-path?key=["collection-id", "taxonomy-slug"]&include_docs=true
;; http://localhost:5984/falklandcms/_design/item/_view/all-slugs-by-coll-id-and-category-path?key=["collection-id", "category-path"]
;; http://localhost:5984/falklandcms/_design/item/_view/all-slugs-by-coll-id-and-category-path?key=["collection-id", "category-path"]&include_docs=true
(defn- item-views []
  (clutch/save-view "item"
    (clutch/view-server-fns :javascript {
      :all-ids-by-coll-id-and-slug {:map "function(doc) {
        if (doc.data.type == 'item') emit([doc.data.collection, doc.data.slug], doc._id) }"}
      :all-slugs-by-coll-id {:map "function(doc) {
        if (doc.data.type == 'item') emit(doc.data.collection, doc.data.slug) }"}
      :delete-by-coll-id {:map "function(doc) {
        if (doc.data.type == 'item') emit(doc.data.collection, [doc._id, doc._rev]) }"}
      :count-by-coll-id {
        :map "function(doc) {
          if (doc.data.type == 'item') emit(doc.data.collection, 1) }"
        :reduce "_count"}
      :all-ids-by-coll-id-and-category-path {
        :map "function(doc) {
          if (doc.data.type == 'item') {
            // Enumerate all the unique paths that can be built from the item's categories
            var uniquePaths = {};
            doc.data.categories.forEach(function(categoryPath) {
              var parts = categoryPath.split('/');
              for (i in parts) {
                var path = [];
                for (part = 0; part <= i; part++) {
                  path.push(parts[part]);
                }
                uniquePaths[path.join('/')] = true;
              }
            });
            for(path in uniquePaths) {
              emit([doc.data.collection, path], doc._id);
            }
          }}"}
      :all-slugs-by-coll-id-and-category-path {
        :map "function(doc) {
          if (doc.data.type == 'item') {
            // Enumerate all the unique paths that can be built from the item's categories
            var uniquePaths = {};
            doc.data.categories.forEach(function(categoryPath) {
              var parts = categoryPath.split('/');
              for (i in parts) {
                var path = [];
                for (part = 0; part <= i; part++) {
                  path.push(parts[part]);
                }
                uniquePaths[path.join('/')] = true;
              }
            });
            for(path in uniquePaths) {
              emit([doc.data.collection, path], doc.data.slug);
            }
          }}"}})))

;; http://localhost:5984/falklandcms/_design/taxonomy/_view/all-ids-by-coll-id-and-slug
;; http://localhost:5984/falklandcms/_design/taxonomy/_view/all-ids-by-coll-id-and-slug?include_docs=true
;; http://localhost:5984/falklandcms/_design/taxonomy/_view/all-ids-by-coll-id-and-slug?key=["collection-id", "taxonomy-slug"]
;; http://localhost:5984/falklandcms/_design/taxonomy/_view/all-slugs-by-coll-id?key="collection-id"
;; http://localhost:5984/falklandcms/_design/taxonomy/_view/all-slugs-by-coll-id?key="collection-id"&include_docs=true
;; http://localhost:5984/falklandcms/_design/taxonomy/_view/delete-by-coll-id?key="collection-id"
(defn- taxonomy-views []
  (clutch/save-view "taxonomy"
    (clutch/view-server-fns :javascript {
      :all-ids-by-coll-id-and-slug {:map "function(doc) {
        if (doc.data.type == 'taxonomy') emit([doc.data.collection, doc.data.slug], doc._id) }"}
      :all-slugs-by-coll-id {:map "function(doc) {
        if (doc.data.type == 'taxonomy') emit(doc.data.collection, doc.data.slug) }"}
      :delete-by-coll-id {:map "function(doc) {
        if (doc.data.type == 'taxonomy') emit(doc.data.collection, [doc._id, doc._rev]) }"}
      :count-by-coll-id {
        :map "function(doc) {
          if (doc.data.type == 'taxonomy') emit(doc.data.collection, 1) }"
        :reduce "_count"}})))

(defn init []
  (println (str "\nFCMS: Initializing database - " db-name))
  (clutch/with-db (common/db)
     (fcms-views) (print ".")
     (collection-views) (print ".")
     (resource-views) (print ".")
     (item-views) (print ".")
     (taxonomy-views) (print "."))
  (println "\nFCMS: Database initialization complete - " db-name "\n"))

(defn -main []
  (init))