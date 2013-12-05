(ns fcms.unit.resources.item.item-delete
  (:require [midje.sweet :refer :all]
            [fcms.lib.resources :refer :all]
            [fcms.resources.collection :as collection]
            [fcms.resources.item :refer :all]))

(with-state-changes [(before :facts (empty-collection-e))
                     (after :facts (collection/delete-collection e))]

  (facts "about item deletion failures"

    (facts "when the specified collection doesn't exist"
      (doseq [coll-slug (conj bad-strings "not-here")]
        (delete-item coll-slug i) => :bad-collection))

    (facts "when the specified item doesn't exist"
      (doseq [item-slug (conj bad-strings "not-here")]
        (delete-item e item-slug) => :bad-item)))

	(facts "about deleting items"

	  (with-state-changes [(before :facts (existing-item-i))]

		  (fact "it returns true"
		  	(delete-item e i) => true)

		  (fact "the item can no longer be retrieved"
		  	(delete-item e i)
		  	(get-item e i) => nil)

		  (fact "the item is no longer listed"
		  	(delete-item e i)
		  	(all-items e) => []))))