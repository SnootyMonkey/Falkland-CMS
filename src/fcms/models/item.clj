(ns fcms.models.item
  (:require [fcms.models.base :as base]))
 
(defn create-item [params]
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