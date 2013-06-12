(ns fcms.models.item
  (:require [fcms.models.base :as base]))

(def item-type "application/vnd.fcms.item+json")

;; TODO account for the collection the item is in
(defn create-item
  "Create a new item using the specified name and optional map of properties.
  If :slug is included in the properties it will be used as the item's slug,
  otherwise one will be created from the name."
  ([name] (create-item name {}))
  ([name props] (base/create (merge props {:name name}) :item)))

(defn get-item
  ""
  [coll-slug item-slug])

(defn all [])