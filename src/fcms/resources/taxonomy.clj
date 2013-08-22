(ns fcms.resources.taxonomy
  (:require [fcms.resources.common :as common]
            [fcms.resources.collection-resource :as resource]))

(def taxonomy-media-type "application/vnd.fcms.taxonomy+json;version=1")

(def structure-msg "Not a valid category structure.")

(def reserved-properties
  "Properties that can't be specified during a create or update."
  (reduce conj common/reserved-properties [:collection :categories])) 
(def retained-properties
  "Properties that are retained during an update even if they aren't in the updated property set."
  (reduce conj common/retained-properties [:collection :categories]))

(defn get-taxonomy
  "Given the slug of the collection containing the taxonomy and the slug of the taxonomy,
  return the taxonomy as a map, or return :bad-collection if there's no collection with that slug, or
  nil if there is no taxonomy with that slug."
  [coll-slug slug]
    (resource/get-resource coll-slug slug :taxonomy))

(defn valid-categories [categories]
  (cond
    (not (vector? categories)) [:invalid-structure structure-msg]
    :else true))

;; TODO if a taxonomy structure is provided, make sure it's valid
(defn valid-new-taxonomy
  "Given the slug of the collection, the name of the taxonomy, and a map of a potential new taxonomy,
  check if the everything is in order to create the new taxonomy.
  Ensure the collection exists or return :bad-collection.
  Ensure the name of the taxonomy is specified or return :no-name.
  Ensure the slug is valid and doesn't already exist if it's specified,
  or return :invalid-slug or :slug-conflict respectively.
  If a property is included in the map of properties that is in the reserved-properties
  set, :property-conflict will be returned."
  ([coll-slug taxonomy-name] (valid-new-taxonomy coll-slug taxonomy-name {}))
  ([coll-slug taxonomy-name props]
    (resource/valid-new-resource coll-slug taxonomy-name reserved-properties type props)))

;; TODO if no taxonomy structure is provided create the initial empty one
;; TODO if a taxonomy structure is provided, make sure it's valid
(defn create-taxonomy
  "Create a new taxonomy in the collection specified by its slug, using the specified
  taxonomy name and an optional map of properties.
  If :slug is included in the properties it will be used as the taxonomy's slug, otherwise
  the slug will be created from the name.
  If a :slug is included in the properties and an taxonomy already exists
  in the collection with that slug, a :slug-conflict will be returned.
  If a :slug is included in the properties and it's not valid,
  :invalid-slug will be returned.
  If a property is included in the map of properties that is in the reserved-properties
  set, :property-conflict will be returned."
  ([coll-slug taxonomy-name] (create-taxonomy coll-slug taxonomy-name {}))
  ([coll-slug taxonomy-name props]
    (resource/create-resource coll-slug taxonomy-name :taxonomy reserved-properties props)))

(defn all-taxonomies [])