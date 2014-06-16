(ns fcms.lib.body
  (:require [fcms.lib.check :refer (check)]
            [fcms.representations.common :refer (GET PUT DELETE)]
            [fcms.resources.item :refer (item-media-type)]
            [fcms.resources.collection :refer (collection-media-type)]))

(defn- find-link [rel links]
  (some (fn [link] (if (= rel (:rel link)) link nil)) links))

(defn verify-link [rel method href type links]
  (if-let [link (find-link rel links)]
    (do
      (check (= method (:method link)))
      (check (= href (:href link)))
      (if (= :no type)
        (check (nil? (:type link)))
        (check (= type (:type link)))))
    (check (= rel :link_not_present))))

(defn verify-item-links [coll-slug item-slug links]
  (verify-link "self" GET (str "/" coll-slug "/" item-slug) item-media-type links)
  (verify-link "update" PUT (str "/" coll-slug "/" item-slug) item-media-type links)
  (verify-link "delete" DELETE (str "/" coll-slug "/" item-slug) :no links)
  (verify-link "collection" GET (str "/" coll-slug) collection-media-type links))