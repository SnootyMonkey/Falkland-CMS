(ns fcms.models.collection
  (:require [fcms.models.base :as base]
            [fcms.models.item :as item]))

(def collection-type "application/vnd.fcms.collection+json")

(defn create-collection
  "Create a new collection using the specified name and optional map of properties.
  If :slug is included in the properties it will be used as the collection's slug,
  otherwise one will be created from the name."
  ([name] (create-collection name {}))
  ([name props] (base/create (merge props {:name name}) :collection)))

(defn all [])

(defn create-items [collection items]
  (when (seq items)
    (item/create-item collection (first items))
    (recur collection (rest items))))

;;ClojureScript
;;(fn [doc] (js/emit (aget doc "slug") nil))

;;CoffeeScript
;;(doc) ->
;;  if (doc.data && doc.data.type && doc.data.type == "collection")
;;    emit(doc.data.slug, doc)

;;JS
;;function(doc) {
;;  if (doc.data && doc.data.type && doc.data.type == "collection") {
;;    emit(doc.data.name, doc);
;;  }
;;}

;;Erlang?