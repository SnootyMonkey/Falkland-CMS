(ns fcms.unit.resources.collection.collection-retrieval
  (:require [midje.sweet :refer :all]
            [fcms.lib.resources :refer :all]
            [fcms.lib.check :refer (about-now?)]
            [fcms.resources.collection :refer :all]
            [fcms.resources.common :as common]))

(fact "about unsuccessful collection retrieval when the specified collection doesn't exist"
  (reset-collection e)
  (doseq [coll-slug (conj bad-strings "not-here")]
    (get-collection coll-slug) => nil)
  (delete-collection e))

(with-state-changes [(after :facts (delete-collection "custom"))]
           
  (facts "about collection retrieval"

    (fact "successfully"
      (create-collection "custom" {:slug "custom" :custom foo :description ascii-description})
      (let [coll (get-collection "custom")]
        (fact "the id is a string"
          (instance? String (:id coll)) => true)
        (fact "the version is 1"
          (:version coll) => 1)
        (fact "its name is returned"
          (:name coll) => "custom")
        (fact "its description is returned"
          (:description coll) => ascii-description)
        (fact "its slug is returned"
          (:slug coll) => "custom")
        (fact "its custom properties are returned"
          (:custom coll) => foo)
        (fact "its timestamps are returned"
          (instance? timestamp (:created-at coll)) => true
          (about-now? (:created-at coll)) => true
          (:created-at coll) => (:updated-at coll))))

    (fact "with a collection containing unicode"
      (create-collection unicode-name {:slug "custom" :custom mixed-name :description unicode-description})
      (let [coll (get-collection "custom")]
        (fact "the id is a string"
          (instance? String (:id coll)) => true)
        (fact "the version is 1"
          (:version coll) => 1)
        (fact "its name is returned"
          (:name coll) => unicode-name)
        (fact "its description is returned"
          (:description coll) => unicode-description)
        (fact "its slug is returned"
          (:slug coll) => "custom")
        (fact "its custom properties are returned"
          (:custom coll) => mixed-name)
        (fact "its timestamps are returned"
          (instance? timestamp (:created-at coll)) => true
          (about-now? (:created-at coll)) => true
          (:created-at coll) => (:updated-at coll))))))