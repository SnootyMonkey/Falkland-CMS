(ns fcms.unit.resources.item.item-list
  (:require [midje.sweet :refer :all]
            [fcms.lib.resources :refer :all]
            [fcms.lib.checks :refer (about-now?)]
            [fcms.resources.collection :as collection]
            [fcms.resources.item :refer :all]))

(with-state-changes [(before :facts (empty-collection-e))
                     (after :facts (collection/delete-collection e))]

  (facts "about item listing failures"

 	  (facts "when the specified collection doesn't exist"
      (doseq [coll-slug (conj bad-strings "not-here")]
        (all-items coll-slug) => :bad-collection)))

  (facts "about listing items"

  	(fact "returns an empty vector when the collection is empty"
  		(all-items e) => []))

	(with-state-changes [(before :facts (existing-item-i))]

		(fact "returns one item when there is one item in the collection"
			(count (all-items e)) => 1)

		(with-state-changes [(before :facts (create-item e slug))]

			(fact "returns two items when there are two item in the collection"
				(count (all-items e)) => 2)

      (fact "the item ids are strings"
        (doseq [item (all-items e)]
          (instance? String (:id item)) => true))
          
      (fact "the containing collection's slug is included in the items"
        (set (map :collection (all-items e))) => #{e})

      (fact "the items' slugs are included"
        (set (map :slug (all-items e))) => #{i slug})

      (fact "the items' names are included"
        (set (map :name (all-items e))) => #{unicode-name slug})

      (fact "the items' descriptions are included"
        (set (map :description (all-items e))) => #{unicode-description nil})

      (fact "the items' versions are 1"
        (set (map :version (all-items e))) => #{1})

      (fact "the custom property of an item in the list is included"
        (set (map :custom (all-items e))) => #{foo nil})

      (fact "the item's timestamps are included"
        (doseq [item (all-items e)]
          (instance? timestamp (:created-at item)) => true
          (about-now? (:created-at item)) => true
          (:created-at item) => (:updated-at item))))))