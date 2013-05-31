(ns fcms.models.collection
  (:require [fcms.models.base :as base]
            [fcms.models.item :as item]))

(def collection-type "application/vnd.fcms.collection+json")

(defn create-collection [name]
  (base/create {:name name :type :collection}))

(defn all [])

(defn create-items [collection items]
  (when (seq items)
    (item/create-item collection (first items))
    (recur collection (rest items))))

;; need a with-collection macro

;;CoffeeScript
;;(doc) ->
;;  if (doc.data && doc.data.type && doc.data.type == "collection")
;;    emit(doc.data.name, doc)

;;JS
;;function(doc) {
;;  if (doc.data && doc.data.type && doc.data.type == "collection") {
;;    emit(doc.data.name, doc);
;;  }
;;}

;;ClojureScript?

;;Erlang?