(ns fcms.unit.resources.collection.collection-delete
  (:require [midje.sweet :refer :all]
            [fcms.lib.resources :refer :all]
            [fcms.lib.check :refer (about-now?)]
            [fcms.resources.collection :refer :all]
            [fcms.resources.common :as common]))

;; ----- Tests -----

(with-state-changes [(before :facts (do
                                      (delete-collection c)
                                      (create-collection c {:custom foo})))
                      (after :facts (delete-collection c))]
  
  (facts "about collection deletion failures"
    
    (fact "when the specified collection doesn't exist"
      (doseq [coll-slug (conj bad-strings "not-here")]
        (delete-collection coll-slug) => :bad-collection)))

  (facts "about deleting collections"
    
    (fact "the collection can no longer be retrieved"
      (delete-collection c)
      (get-collection c) => nil)

    (fact "the collection is no longer listed"
      (delete-collection c)
      (all-collections) => [])))