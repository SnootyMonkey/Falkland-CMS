(ns fcms.unit.resources.item.item-update
  (:require [midje.sweet :refer :all]
            [fcms.lib.resources :refer :all]
            [fcms.lib.checks :refer (about-now?)]
            [fcms.resources.collection :as collection]
            [fcms.resources.collection-resource :as resource]
            [fcms.resources.item :refer :all]))

;; ----- Tests -----

(with-state-changes [(before :facts (empty-collection-e))
                     (after :facts (collection/delete-collection e))]

  ; TODO positive cases of validity
  (facts "about validity of item updates"

    (fact "when the specified collection doesn't exist"
      (doseq [coll-slug (conj bad-strings "not-here")]
        (update-item coll-slug i {}) => :bad-collection))

    (fact "when the specified item doesn't exist"
      (doseq [item-slug (conj bad-strings "not-here")]
        (update-item e item-slug {}) => :bad-item))

    (with-state-changes [(before :facts (existing-item-i))
                         (after :facts (delete-item e i))]

      (fact "when updating a reserved property"
        (doseq [prop resource/reserved-properties]
          (update-item e i {prop foo}) => :property-conflict
          (update-item e i {(name prop) foo}) => :property-conflict))))

  (facts "about item update failures"

    (fact "when the specified collection doesn't exist"
      (doseq [coll-slug (conj bad-strings "not-here")]
        (update-item coll-slug i {}) => :bad-collection))

    (fact "when the specified item doesn't exist"
      (doseq [item-slug (conj bad-strings "not-here")]
        (update-item e item-slug {}) => :bad-item))

    (with-state-changes [(before :facts (existing-item-i))
                         (after :facts (delete-item e i))]

      (fact "with a provided slug that is invalid"
        (update-item e i {:slug "i I"}) => :invalid-slug)

      (with-state-changes [(before :facts (create-item e slug))
                           (after :facts (delete-item e slug))]

        (fact "with a provided slug that is already used"
          (update-item e i {:slug slug}) => :slug-conflict))

      (fact "when updating a reserved property"
        (doseq [prop resource/reserved-properties]
          (update-item e i {prop foo}) => :property-conflict
          (update-item e i {(name prop) foo}) => :property-conflict))))

  (future-facts "about updating items"))