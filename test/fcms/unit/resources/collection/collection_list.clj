(ns fcms.unit.resources.collection.collection-list
  (:require [midje.sweet :refer :all]
            [fcms.lib.resources :refer :all]
            [fcms.lib.check :refer (about-now?)]
            [fcms.resources.collection :refer :all]
            [fcms.resources.common :as common]))

;; ----- Utilities -----

(defn- delete-all-collections []
  (doseq [coll (all-collections)]
    (delete-collection (:slug coll))))

(defn- two-collections []
  (create-collection "custom" {:slug "custom" :description ascii-description :custom foo})
  (create-collection "coll" {:slug "coll"}))

;; ----- Tests -----

(with-state-changes [(before :facts (delete-all-collections))
                     (after :facts (delete-all-collections))]

  (facts "about listing collections"

    (fact "when there are no collections"
      (all-collections) => [])

    (fact "when there is one collection"
      (reset-collection e)
      (count (all-collections)) => 1)
          
    (facts "when there are two collections"
      
      (with-state-changes [(before :facts (two-collections))]
      
        (fact "it returns two collections"
          (count (all-collections)) => 2)
        
        (fact "the containers' slugs are included"
          (set (map :slug (all-collections))) => #{"custom" "coll"})

        (fact "the containers' names are included"
          (set (map :name (all-collections))) => #{"custom" "coll"})
        
        (fact "the containers' descriptions are included"
          (set (map :description (all-collections))) => #{ascii-description nil})
        
        (fact "the containers' version are 1"
          (set (map :version (all-collections))) => #{1})
        
        (fact "the custom property of a collection in the list is included"
          (set (map :custom (all-collections))) => #{foo nil})
        
        (fact "the collections' timestamps are included"
          (doseq [coll (all-collections)]
           (instance? timestamp (:created-at coll)) => true
           (about-now? (:created-at coll)) => true))))))