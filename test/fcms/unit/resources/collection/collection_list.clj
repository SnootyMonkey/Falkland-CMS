(ns fcms.unit.resources.collection.collection-list
  (:require [midje.sweet :refer :all]
            [fcms.lib.resources :refer :all]
            [fcms.lib.check :refer (about-now?)]
            [fcms.resources.collection :refer :all]
            [fcms.resources.common :as common]))

(defn- setup []
  (create-collection "custom" {:slug "custom" :description ascii-description :custom foo})
  (create-collection "coll" {:slug "coll"}))

(defn- teardown []
  (delete-collection "custom")
  (delete-collection "coll"))

(facts "about listing collections"
       (fact "when the specified collection doesn't exist"
             (doseq [coll-slug (conj bad-strings "not-here")]
               (get-collection coll-slug) => nil))
       (facts "about listing collections"
              (fact "returns an empty vector when there are no collections"
                    (all-collections) => [])
              (with-state-changes [(before :facts (reset-collection e))
                                   (after :facts (delete-collection e))]
                (fact "returns one collection when there is one collection"
                      (count (all-collections)) => 1))
              (facts "about two collections"
                     (with-state-changes [(before :facts (setup))
                                          (after :facts (teardown))]
                       (fact "returns two collections when there are two"
                             (count (all-collections)) => 2)
                       (fact "the containers' slugs are included"
                             (set (map :slug (all-collections))) => #{"custom" "coll"})
                       (fact "the containers' names are included"
                             (set (map :name (all-collections))) => #{"custom" "coll"})
                       (fact "the containers' descriptions are included"
                             (set (map :description (all-collections))) => #{ascii-description nil}
                       (fact "the containers' version are 1"
                             (set (map :version (all-collections))) => #{1})
                       (fact "the custom property of a collection in the list is included"
                             (set (map :custom (all-collections))) => #{foo nil})
                       (fact "the collections' timestamps are included"
                             (doseq [coll (all-collections)]
                               (instance? timestamp (:created-at coll)) => true
                               (about-now? (:created-at coll)) => true
                               (:created-at coll) => (:updated-at coll))))))))
