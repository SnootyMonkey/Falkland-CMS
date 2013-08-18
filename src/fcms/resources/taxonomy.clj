(ns fcms.resources.taxonomy
  (:require [clojure.set :refer (intersection)]
            [clojure.string :refer (blank?)]
            [fcms.resources.common :as common]
            [fcms.resources.collection :as collection]
            [fcms.lib.slugify :refer (slugify)]))

(def taxonomy-media-type "application/vnd.fcms.taxonomy+json;version=1")

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
    (collection/with-collection coll-slug
      (when-let [taxonomy (common/resource-doc (:id collection) slug :taxonomy)]
        (common/resource-from-db coll-slug taxonomy))))

(defn valid-new-taxonomy
  "Given the slug of the collection, and a map of a potential new taxonomy,
  check if the everything is in order to create the new taxonomy.
  Ensure the collection exists or return :bad-collection.
  Ensure the name of the taxonomy is specified or return :no-name.
  Ensure the slug is valid and doesn't already exist if it's specified,
  or return :invalid-slug or :slug-conflict respectively.
  If no taxonomy slug is specified it will be generated from the name.
  If a property is included in the map of properties that is in the reserved-properties
  set, :property-conflict will be returned."
  ([coll-slug taxonomy-name] (valid-new-taxonomy coll-slug taxonomy-name {}))
  ([coll-slug taxonomy-name {provided-slug :slug :as props}]
    (if-let [coll-id (:id (collection/get-collection coll-slug))]
      (cond
        (or (nil? taxonomy-name) (blank? taxonomy-name)) :no-name
        (not-empty (intersection (set (keys props)) reserved-properties)) :property-conflict
        (not provided-slug) true
        (not (common/valid-slug? provided-slug)) :invalid-slug
        (nil? (get-taxonomy coll-slug provided-slug)) true
        :else :slug-conflict)
      :bad-collection)))

;; TODO if no taxonomy structure is provided create the initial empty one
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
    (let [validity (valid-new-taxonomy coll-slug taxonomy-name props)]
      (if (true? validity) 
        (collection/with-collection coll-slug
          (let [slug (common/unique-slug (:id collection) (or (:slug props) (slugify taxonomy-name)))]
            (when-let [taxonomy (common/create-with-db
              (merge props {:slug slug :collection (:id collection) :name taxonomy-name}) :taxonomy)]
              (common/resource-from-db coll-slug taxonomy))))
        validity))))

(defn all-taxonomies [])