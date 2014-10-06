(ns fcms.unit.resources.collection.collection-delete
  (:require [midje.sweet :refer :all]
            [fcms.lib.resources :refer :all]
            [fcms.lib.check :refer (about-now?)]
            [fcms.resources.collection :refer :all]
            [fcms.resources.item :as i]
            [fcms.resources.taxonomy :as t]))

;; ----- Tests -----

(with-state-changes [(before :facts (do
                                      (delete-all-collections)
                                      (create-collection c)
                                      (i/create-item c "foo")
                                      (t/create-taxonomy c "bar")))
                      (after :facts (delete-collection c))]
  
  (facts "about collection deletion failures"
    
    (fact "when the specified collection doesn't exist"
      (doseq [coll-slug (conj bad-strings "not-here")]
        (delete-collection coll-slug) => :bad-collection)))

  (facts "about deleting collections"
    
    (fact "it returns true"
      (delete-collection c) => true)

    (fact "the collection can no longer be retrieved"
      (delete-collection c)
      (get-collection c) => nil)

    (fact "the contained items can no longer be retrieved"
      (delete-collection c)
      (i/get-item c "foo") => :bad-collection
      (t/get-taxonomy c "bar") => :bad-collection)

    (fact "the collection is no longer listed"
      (delete-collection c)
      (all-collections) => [])))