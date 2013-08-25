(ns fcms.resources.collection-resource
  (:require [clojure.set :refer (intersection)]
            [clojure.string :refer (blank?)]
            [com.ashafa.clutch :as clutch]
            [fcms.resources.common :as common]
            [fcms.resources.collection :as collection]
            [fcms.lib.slugify :refer (slugify)]))

(defn get-resource
  "Given the slug of the collection containing the resource and the slug of the resource,
  return the resource as a map, or return :bad-collection if there's no collection with that slug, or
  nil if there is no resource with that slug."
  [coll-slug slug type]
    (collection/with-collection coll-slug
      (when-let [resource (common/resource-doc (:id collection) slug type)]
        (common/resource-from-db coll-slug resource))))

(defn valid-new-resource
  "Given the slug of the collection, the name of the resource, a map of a potential new resource,
  and a retrieval function for the resource type, check if the everything is in order to create
  the new resource.
  Ensure the collection exists or return :bad-collection.
  Ensure the name of the resource is specified or return :no-name.
  Ensure the slug is valid and doesn't already exist if it's specified,
  or return :invalid-slug or :slug-conflict respectively.
  If a property is included in the map of properties that is in the reserved-properties
  set, :property-conflict will be returned."
  ([coll-slug resource-name reserved-properties type] (valid-new-resource coll-slug resource-name reserved-properties type {}))
  ([coll-slug resource-name reserved-properties type {provided-slug :slug :as props}]
    (if-let [coll-id (:id (collection/get-collection coll-slug))]
      (cond
        (or (nil? resource-name) (blank? resource-name)) :no-name
        (not-empty (intersection (set (keys props)) reserved-properties)) :property-conflict
        (not provided-slug) true
        (not (common/valid-slug? provided-slug)) :invalid-slug
        (nil? (get-resource coll-slug provided-slug type)) true
        :else :slug-conflict)
      :bad-collection)))

(defn create-resource
  "Create a new resource in the collection specified by its slug, using the specified
  resource name, resource type and an optional map of properties.
  If :slug is included in the properties it will be used as the resource's slug, otherwise
  the slug will be created from the name.
  If a :slug is included in the properties and a resource already exists
  in the collection with that slug, a :slug-conflict will be returned.
  If a :slug is included in the properties and it's not valid,
  :invalid-slug will be returned.
  If a property is included in the map of properties that is in the reserved-properties
  set, :property-conflict will be returned."
  ([coll-slug resource-name type reserved-properties] (create-resource coll-slug resource-name type reserved-properties {}))
  ([coll-slug resource-name type reserved-properties props]
    (let [validity (valid-new-resource coll-slug resource-name reserved-properties type props)]
      (if (true? validity) 
        (collection/with-collection coll-slug
          (let [slug (common/unique-slug (:id collection) (or (:slug props) (slugify resource-name)))]
            (when-let [resource (common/create-with-db
              (merge props {:slug slug :collection (:id collection) :name resource-name}) type)]
              (common/resource-from-db coll-slug resource))))
        validity))))

(defn delete-resource
  "Given the slug of the collection containing the resource and the slug of the resource,
  delete the resource, or return :bad-collection if there's no collection with that slug, or
  the provided bad resource keyword if there is no resource with that slug."
  [coll-slug slug type]
  (if-let [coll-id (:id (collection/get-collection coll-slug))]
    (if-let [resource (clutch/with-db (common/db) (common/resource-doc coll-id slug type))]
      (common/delete resource)
      (keyword (str "bad-" (name type))))
    :bad-collection))