(ns fcms.unit.resources.item.item-create
  (:require [midje.sweet :refer :all]
            [fcms.lib.resources :refer :all]
            [fcms.resources.collection :as collection]
            [fcms.resources.collection-resource :as resource]
            [fcms.resources.item :refer :all]))

;; ----- Tests -----

(with-state-changes [(before :facts (empty-collection-e))
                     (after :facts (collection/delete-collection e))]

  (future-facts "about the validity of new items")

  (facts "about item creation failures"

    (fact "with a provided slug that is already used"
      (create-item e "first" {:slug slug})
      (create-item e "second" {:slug slug}) => :slug-conflict)

    (fact "with a provided slug that is invalid"
      (create-item e ascii-name {:slug "i I"}) => :invalid-slug)

    (fact "with a collection that doesn't exist"
      (create-item "not-here" ascii-name) => :bad-collection)

    (fact "with a reserved property"
      (doseq [prop resource/reserved-properties]
        (create-item e i {prop foo}) => :property-conflict
        (create-item e i {(name prop) foo}) => :property-conflict)))

  (facts "about item creation"

    (fact "with a generated slug"
      (create-item e ascii-name)
      (let [item-slug "test-this"
            item (get-item e item-slug)]
        (:name item) => ascii-name
        (:slug item) => item-slug))

    (fact "with a unicode name"
      (create-item e unicode-name)
      (let [item-slug "1"
            item (get-item e item-slug)]
        (:name item) => unicode-name
        (:slug item) => item-slug)
      (create-item e mixed-name)
      (let [item-slug "test"
            item (get-item e item-slug)]
        (:name item) => mixed-name
        (:slug item) => "test"))

    (fact "with unicode properties"
      (create-item e i {:description unicode-description})
      (let [item (get-item e i)]
        (:name item) => i
        (:slug item) => i
        (:description item) => unicode-description))

    (fact "with a generated slug that is already used"
      (create-item e ascii-name)
      (let [item-slug "test-this"
            item (get-item e item-slug)]
        (:name item) => ascii-name
        (:slug item) => item-slug)
      (create-item e ascii-name)
      (let [item-slug "test-this-1"
            item (get-item e item-slug)]
        (:name item) => ascii-name
        (:slug item) => item-slug)
      (create-item e ascii-name)
      (let [item-slug "test-this-2"
            item (get-item e item-slug)]
        (:name item) => ascii-name
        (:slug item) => item-slug))

    (fact "with a provided slug"
      (let [item (create-item e ascii-name {:slug slug})]
        (:name item) => ascii-name
        (:slug item) => slug))))