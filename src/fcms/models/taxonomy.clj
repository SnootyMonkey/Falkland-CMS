(ns fcms.models.taxonomy
  (:require [fcms.models.base :as base]))

(def taxonomy-media-type "application/vnd.fcms.taxonomy+json")

(defn create-taxonomy
  "Create a new taxonomy using the specified name and optional map of properties.
  If :slug is included in the properties it will be used as the taxonomy's slug,
  otherwise one will be created from the name."
  ([name] (create-taxonomy name {}))
  ([name props] (base/create (merge props {:name name}) :taxonomy)))

(defn get-taxonomy
  ""
  [taxonomy-slug])

(defn all-taxonomies [])