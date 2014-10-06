(ns fcms.unit.resources.item.item-retrieval
  (:require [midje.sweet :refer :all]
            [fcms.lib.resources :refer :all]
            [fcms.lib.check :refer (about-now?)]
            [fcms.resources.collection :as collection]
            [fcms.resources.item :refer :all]))

(with-state-changes [(before :facts (reset-collection e))
                     (after :facts (collection/delete-collection e))]

  (facts "about item retrieval"

    (facts "when the specified collection doesn't exist"
      (doseq [coll-slug (conj bad-strings "not-here")]
        (get-item coll-slug i) => :bad-collection))

    (facts "when the specified item doesn't exist"
      (doseq [item-slug (conj bad-strings "not-here")]
        (get-item e item-slug) => nil))

    (with-state-changes [(before :facts (existing-item-i))
                         (after :facts (delete-item e i))]

      (fact "the id is a String"
        (instance? String (:id (get-item e i))) => true)

      (fact "the containing collection's slug is returned"
        (:collection (get-item e i)) => e)

      (fact "the item's slug is returned"
        (:slug (get-item e i)) => i)

      (fact "the item's name is returned"
        (:name (get-item e i)) => unicode-name)

      (fact "the item's description is returned"
        (:description (get-item e i)) => unicode-description)

      (fact "the item's version is 1"
        (:version (get-item e i)) => 1)

      (fact "a custom property of the item is returned"
        (:custom (get-item e i)) => foo)

      (facts "about the item's timestamps"
        (let [item (get-item e i)]
          (instance? timestamp (:created-at item)) => true
          (about-now? (:created-at item)) => true
          (:created-at item) => (:updated-at item))))))