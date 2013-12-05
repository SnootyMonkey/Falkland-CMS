(ns fcms.unit.resources.item.item-list
  (:require [midje.sweet :refer :all]
            [fcms.lib.resources :refer :all]
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

      (future-fact "the item ids are strings")

      (future-fact "the containing collection's slug is included in the items")

      (future-fact "the items' slugs are included")

      (future-fact "the items' names are included")

      (future-fact "the items' descriptions are included")

      (future-fact "the items' versions are 1")

      (future-fact "the custom property of an item in the list is included")

      (future-fact "the item's timestamps are included"))))