(ns fcms.models.collection
  (:require [fcms.models.base :as base]))

(defn create-collection [name]
  (base/create {:name name :type :collection}))

(defn all [])

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