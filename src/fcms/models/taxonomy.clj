(ns fcms.models.item
  (:require [fcms.models.base :as base]))
 
(defn create-taxonomy [params]
  (base/create (assoc params :type :taxonomy)))

(defn all [])

;;CoffeeScript
;;(doc) ->
;;  if (doc.data && doc.data.type && doc.data.type == "taxonomy") 
;;    emit(doc.data.name, doc)

;;JS
;;function(doc) {
;;  if (doc.data && doc.data.type && doc.data.type == "taxonomy") {
;;    emit(doc.data.name, doc);
;;  }
;;}

;;ClojureScript?

;;Erlang?
