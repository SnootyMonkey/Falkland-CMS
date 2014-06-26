(ns fcms.unit.resources.collection.collection-retrieval
  (:require [midje.sweet :refer :all]
            [fcms.lib.resources :refer :all]
            [fcms.lib.check :refer (about-now?)]
            [fcms.resources.collection :refer :all]
            [fcms.resources.common :as common]))

(with-state-changes [(before :facts (reset-collection e))
                     (after :facts (delete-collection e))]
  (facts "about fetching a collection"
         (fact "when the specified collection doesn't exist"
               (doseq [coll-slug (conj bad-strings "not-here")]
                 (get-collection coll-slug) => nil))
         (with-state-changes [(before :facts (create-collection "custom" {:custom foo}))
                              (after :facts (delete-collection "custom"))]
           (facts "about fetching a collection sucessfully"
                  (let [coll (get-collection "custom")]
                    (fact "the id is a string"
                          (instance? String (:id coll)) => true)
                    (fact "the version is 1"
                          (:version coll) => 1)
                    (fact "its name is returned"
                          (:name coll) => "custom")
                    (fact "its slug is returned"
                          (:slug coll) => "custom")
                    (fact "its custom properties are returned"
                          (:custom coll) => foo)
                    (fact "its timestamps are turned"
                          (instance? timestamp (:created-at coll)) => true
                          (about-now? (:created-at coll)) => true
                          (:created-at coll) => (:updated-at coll)))))))
