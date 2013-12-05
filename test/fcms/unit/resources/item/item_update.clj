(ns fcms.unit.resources.item.item-update
  (:require [clj-time.core :refer (before?)]
            [midje.sweet :refer :all]
            [fcms.lib.resources :refer :all]
            [fcms.lib.checks :refer (about-now?)]
            [fcms.resources.collection :as collection]
            [fcms.resources.collection-resource :as resource]
            [fcms.resources.item :refer :all]))

;; ----- Fixtures -----

(def new-name (str ascii-name unicode-name))

;; ----- Tests -----

(with-state-changes [(before :facts (empty-collection-e))
                     (after :facts (collection/delete-collection e))]

  (facts "about checking validity of invalid item updates"

    (facts "when the specified collection doesn't exist"
      (doseq [coll-slug (conj bad-strings "not-here")]
        (valid-item-update coll-slug i {}) => :bad-collection))

    (facts "when the specified item doesn't exist"
      (doseq [item-slug (conj bad-strings "not-here")]
        (valid-item-update e item-slug {}) => :bad-item))

    (with-state-changes [(before :facts (existing-item-i))
                         (after :facts (delete-item e i))]

      (facts "when updating a reserved property"
        (doseq [prop resource/reserved-properties]
          (valid-item-update e i {prop foo}) => :property-conflict
          (valid-item-update e i {(name prop) foo}) => :property-conflict))))

  (facts "about checking validity of valid item updates"

    (with-state-changes [(before :facts (existing-item-i))
                         (after :facts (delete-item e i))]

      (fact "it's valid with no properties"
        (valid-item-update e i {}) => true)

      (fact "it's valid when updating the name"
        (valid-item-update e i {:name new-name}) => true)

      (fact "it's valid when updating the slug"
        (valid-item-update e i {:slug slug}) => true)

      (fact "it's valid when updating a custom property"
        (valid-item-update e i {:custom bar}) => true)

      (fact "it's valid when adding new custom properties"
        (valid-item-update e i {:custom2 foo "custom3" bar}) => true)

      (fact "it's valid when updating many things at once"
        (valid-item-update e i {
          :name new-name
          :slug slug
          :custom bar
          :custom2 foo}) => true)))

  (facts "about item update failures"

    (facts "when the specified collection doesn't exist"
      (doseq [coll-slug (conj bad-strings "not-here")]
        (update-item coll-slug i {}) => :bad-collection))

    (facts "when the specified item doesn't exist"
      (doseq [item-slug (conj bad-strings "not-here")]
        (update-item e item-slug {}) => :bad-item))

    (with-state-changes [(before :facts (existing-item-i))
                         (after :facts (delete-item e i))]

      (fact "it fails with a provided slug that is invalid"
        (update-item e i {:slug "i I"}) => :invalid-slug)

      (with-state-changes [(before :facts (create-item e slug))
                           (after :facts (delete-item e slug))]

        (fact "it fails with a provided slug that is already used"
          (update-item e i {:slug slug}) => :slug-conflict))

      (facts "when updating a reserved property"
        (doseq [prop resource/reserved-properties]
          (update-item e i {prop foo}) => :property-conflict
          (update-item e i {(name prop) foo}) => :property-conflict))))

  (facts "about updating items"

    (with-state-changes [(before :facts (existing-item-i))
                         (after :facts (delete-item e i))]

      (facts "when updating with no properties"
        (update-item e i {})
        (let [item (get-item e i)]
          (:custom item) => nil ; custom drops out because it wasn't in the update
          (:version item) => 2))

      (facts "when updating the name"
        (let [new-name (str ascii-name unicode-name)]
          (:name (update-item e i {:name new-name})) => new-name
          (let [item (get-item e i)]
            (:name item) => new-name
            (:custom item) => nil ; custom drops out because it wasn't in the update
            (:version item) => 2)))

      (facts "when updating the slug"
        (:slug (update-item e i {:slug slug})) => slug
        (get-item e i) => nil ; can no longer retrieve the item by the old slug
        (let [item (get-item e slug)]
          (:slug item) => slug
          (:custom item) => nil ; custom drops out because it wasn't in the update
          (:version item) => 2))

      (facts "when updating a custom property"
        (:custom (update-item e i {:custom bar})) => bar
        (let [item (get-item e i)]
          (:custom item) => bar
          (:version item) => 2))

      (facts "when updating with new custom properties"
        (:custom2 (update-item e i {:custom2 foo "custom3" bar})) => foo
        (let [item (get-item e i)]
          (:custom2 item) => foo
          (:custom3 item) => bar
          (:custom item) => nil ; custom drops out because it wasn't in the update
          (:version item) => 2))

      (facts "when updating with multiple updates"
        (update-item e i {:name new-name :slug slug :custom2 foo "custom3" bar}) => truthy
        (let [item (get-item e slug)]
          (:name item) => new-name
          (:slug item) => slug
          (:custom item) => nil ; custom drops out because it wasn't in the update
          (:custom2 item) => foo
          (:custom3 item) => bar
          (:version item) => 2))

      (facts "about changes to the item's timestamps"
        (Thread/sleep 1000) ; delay 1 sec. since the timestamps are at second resolution
        (update-item e i {})
        (let [item (get-item e i)]
          (instance? timestamp (:created-at item)) => true
          (instance? timestamp (:updated-at item)) => true
          (about-now? (:updated-at item)) => true
          (before? (:created-at item) (:updated-at item)) => true
          (:created-at (get-item e i)) =not=> (:updated-at (get-item e i)))))))