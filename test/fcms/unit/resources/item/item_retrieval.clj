(ns fcms.unit.resources.item.item-retrieval
  (:require [midje.sweet :refer :all]
            [fcms.lib.resources :refer :all]
            [fcms.lib.checks :refer (about-now?)]
            [fcms.resources.collection :as collection]
            [fcms.resources.item :refer :all]))

;; ----- Tests -----

(with-state-changes [(before :facts (empty-collection-e))
                     (after :facts (collection/delete-collection e))]

  (facts "about item retrieval"

    (fact "when the specified collection doesn't exist"
      (doseq [coll-slug (conj bad-strings "not-here")]
        (get-item coll-slug i) => :bad-collection))

    (fact "when the specified item doesn't exist"
      (doseq [item-slug (conj bad-strings "not-here")]
        (get-item e item-slug) => nil))

    (with-state-changes [(before :facts (existing-item-i))
                         (after :facts (delete-item e i))]

      (fact "when retrieving the item's id"
        (instance? String (:id (get-item e i))) => true)

      (fact "when retrieving the item's collection"
        (:collection (get-item e i)) => e)

      (fact "when retrieving the item's slug"
        (:slug (get-item e i)) => i)

      (fact "when retrieving the item's name"
        (:name (get-item e i)) => unicode-name)

      (fact "when retrieving the item's description"
        (:description (get-item e i)) => unicode-description)

      (fact "when retrieving the item's version"
        (:version (get-item e i)) => 1)

      (fact "when retrieving a custom property of the item"
        (:custom (get-item e i)) => foo)

      (fact "when retrieving the item's timestamps"
        (instance? timestamp (:created-at (get-item e i))) => true
        (about-now? (:created-at (get-item e i))) => true
        (:created-at (get-item e i)) => (:updated-at (get-item e i))))))