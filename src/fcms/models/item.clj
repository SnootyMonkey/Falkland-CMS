(ns fcms.models.item
  (:require [fcms.models.base :as base]))

(def item-type "application/vnd.fcms.item+json")

(defn create-item [collection params]
  (base/create (assoc params :type :item)))

(defn all [])

;;CoffeeScript
;;(doc) ->
;;  if (doc.data && doc.data.type && doc.data.type == "item")
;;    emit(doc.data.name, doc)

;;JS
;;function(doc) {
;;  if (doc.data && doc.data.type && doc.data.type == "item") {
;;    emit(doc.data.name, doc);
;;  }
;;}

;;ClojureScript?

;;Erlang?