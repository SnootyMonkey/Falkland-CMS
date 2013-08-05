(ns fcms.lib.body
  (:require [fcms.lib.http-mock :as http-mock]
            [fcms.representations.common :refer (GET PUT DELETE)]
            [fcms.resources.item :refer (item-media-type)]
            [fcms.resources.collection :refer (collection-media-type)]
            [fcms.lib.checks :refer (check)]))

(defn value-of [prop]
  (prop (http-mock/body)))

(defn verify [prop value]
  (check (= value (value-of prop))))

(defn verify-relation [func prop1 prop2]
  (check (func (value-of prop1) (value-of prop2))))

(defn- find-link [rel links]
  (some (fn [link] (if (= rel (:rel link)) link nil)) links))

(defn verify-link
  ([rel method href] (verify-link rel method href :no))
  ([rel method href type] (verify-link rel method href type (value-of :links)))
  ([rel method href type links]
    (if-let [link (find-link rel links)]
      (do
        (check (= method (:method link)))
        (check (= href (:href link)))
        (if (= :no type)
          (check (nil? (:type link)))
          (check (= type (:type link)))))
      (check (= rel :link_not_present)))))

(defn verify-item-links [coll-slug item-slug links]
  (verify-link "self" GET (str "/" coll-slug "/" item-slug) item-media-type links)
  (verify-link "update" PUT (str "/" coll-slug "/" item-slug) item-media-type links)
  (verify-link "delete" DELETE (str "/" coll-slug "/" item-slug) :no links)
  (verify-link "collection" GET (str "/" coll-slug) collection-media-type links))

(defn verify-length [prop length]
  (check (sequential? (value-of prop)))
  (check (= length (count (value-of prop)))))