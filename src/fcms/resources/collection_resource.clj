(ns fcms.resources.collection-resource
  "Namespace for collection resources. Collection resources are either taxonomies or items, and are
  stored in a particular collection."
  (:require [clojure.core.typed :refer (ann HSet Kw)]
            [clojure.set :refer (intersection)]
            [clojure.string :refer (blank?)]
            [clojure.walk :refer (keywordize-keys)]
            [com.ashafa.clutch :as clutch]
            [fcms.resources.common :as common]
            [fcms.resources.collection :as collection]
            [fcms.lib.slugify :refer (slugify)]))

(ann reserved-properties (HSet))
(def reserved-properties
  "Properties that can't be specified during a create and are ignored during an update."
  (conj common/reserved-properties :collection :categories))
(ann retained-properties (HSet))
(def retained-properties
  "Properties that are retained during an update even if they aren't in the updated property set."
  (conj common/retained-properties :collection :categories))

(defn allow-category-reserved-properties []
  (vec (remove #(= :categories %) reserved-properties)))

(defn- get-resource-with-db [coll-id coll-slug slug type]
  (when-let [resource (common/resource-doc coll-id slug type)]
    (common/resource-from-db coll-slug resource)))

(defn get-resource
  "Given the slug of the collection containing the resource and the slug of the resource,
  return the resource as a map, or return :bad-collection if there's no collection with that slug, or
  nil if there is no resource with that slug."
  [coll-slug slug type]
    (collection/with-collection coll-slug
      (get-resource-with-db (:id collection) coll-slug slug type)))

(defn valid-new-resource
  "Given the slug of the collection, the name of the resource, a map of a potential new resource,
  and a retrieval function for the resource type, check if the everything is in order to create
  the new resource.
  Ensure the collection exists or return :bad-collection.
  Ensure the name of the resource is specified or return :no-name.
  Ensure the slug is valid and doesn't already exist if it's specified,
  or return :invalid-slug or :slug-conflict respectively.
  :property-conflict is returned if a property is included in the map of properties that is in
  the reserved-properties set.
  true is returned if the new resource is valid."
  ([coll-slug resource-name type reserved-properties] (valid-new-resource coll-slug resource-name reserved-properties type {}))
  ([coll-slug resource-name type reserved-properties {provided-slug :slug :as props}]
    (if-let [coll-id (:id (collection/get-collection coll-slug))]
      (cond
        (or (nil? resource-name) (blank? resource-name)) :no-name
        (not-empty (intersection (set (keys (keywordize-keys props))) reserved-properties)) :property-conflict
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
  :slug-conflict is returned if a :slug is included in the properties and a resource already exists
  in the collection with that slug.
  :invalid-slug is returned if a :slug is included in the properties and it's not valid.
  :property-conflict is returned if a property is included in the map of properties that is
  in the reserved-properties set."
  ([coll-slug resource-name type reserved-properties] (create-resource coll-slug resource-name type reserved-properties {}))
  ([coll-slug resource-name type reserved-properties properties]
    (let [props (keywordize-keys properties)
          validity (valid-new-resource coll-slug resource-name type reserved-properties props)]
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

(defn valid-resource-update
  "Given the slug of the collection, the slug of the resource,
  and a map of updated properties for the resource,
  check if the everything is in order to update the resource.
  Ensure the collection exists or return :bad-collection.
  Ensure the resource exists or return :bad-<resource>.
  If a new slug is provided in the properties, ensure it is
  valid or return :invalid-slug and ensure it is unused or
  return :slug-conflict. If no item slug is specified in
  the properties it will be retain its current slug."
  [coll-slug slug type reserved-properties {item-name :name provided-slug :slug :as props}]
    (let [coll-id (:id (collection/get-collection coll-slug))
          resource-id (:id (get-resource coll-slug slug type))]
      (cond
        (nil? coll-id) :bad-collection
        (nil? resource-id) (keyword (str "bad-" (name type)))
        (not-empty (intersection (set (keys (keywordize-keys props))) reserved-properties)) :property-conflict
        (not provided-slug) true
        (not (common/valid-slug? provided-slug)) :invalid-slug
        (= slug provided-slug) true
        :else (if (nil? (get-resource coll-slug provided-slug type)) true :slug-conflict))))

(defn update-resource
  "Update a resource retaining it's manufactured properties and replacing the rest with the provided properties"
  [coll-slug slug type properties]
  (collection/with-collection coll-slug
    (if-let [resource (common/resource-doc (:id collection) slug type)]
      (let [props (keywordize-keys properties)
            retained-props (select-keys (:data resource) (conj (:retained props) :version))
            updated-props (apply dissoc (:updated props) (:reserved props))
            new-props (merge retained-props updated-props)]
        (common/resource-from-db coll-slug (common/update-with-db resource new-props))
        (get-resource-with-db (:id collection) coll-slug (:slug new-props) type))
      (keyword (str "bad-" (name type))))))

(defn all-resources
  "Given the slug of the collection, return all the resources it contains of the type specified
  as a sequence of maps, or return :bad-collection if there's no collection with that slug."
  [coll-slug type]
  (collection/with-collection coll-slug
    (when-let [results (common/doc-from-view-with-db type :all-slugs-by-coll-id (:id collection))]
      (vec (map #(common/resource-from-db coll-slug (:doc %)) results)))))