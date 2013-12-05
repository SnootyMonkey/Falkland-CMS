(ns fcms.unit.resources.item.item-create
  (:require [midje.sweet :refer :all]
            [fcms.lib.resources :refer :all]
            [fcms.resources.collection :as collection]
            [fcms.resources.collection-resource :as resource]
            [fcms.resources.item :refer :all]))

(with-state-changes [(before :facts (empty-collection-e))
                     (after :facts (collection/delete-collection e))]

  (facts "about checking validity of invalid new items"

    (facts "when the specified collection doesn't exist"
      (doseq [coll-slug (conj bad-strings "not-here")]
        (valid-new-item coll-slug i {}) => :bad-collection))

    (facts "when including a reserved property"
      (doseq [prop resource/reserved-properties]
        (valid-new-item e i {prop foo}) => :property-conflict
        (valid-new-item e i {(name prop) foo}) => :property-conflict)))

  (facts "about checking validity of valid new items"

      (fact "it's valid with no properties"
        (valid-new-item e i {}) => true)

      (fact "it's valid with custom properties"
        (valid-new-item e i {:a "b" "c" "d"}) => true))

  (facts "about item creation failures"

    (fact "it fails with a provided slug that is already used"
      (create-item e "first" {:slug slug})
      (create-item e "second" {:slug slug}) => :slug-conflict)

    (fact "it fails with a provided slug that is invalid"
      (create-item e ascii-name {:slug "i I"}) => :invalid-slug)

    (fact "it fails with a collection that doesn't exist"
      (create-item "not-here" ascii-name) => :bad-collection)

    (facts "with a reserved property"
      (doseq [prop resource/reserved-properties]
        (create-item e i {prop foo}) => :property-conflict
        (create-item e i {(name prop) foo}) => :property-conflict)))

  (facts "about item creation"

    (facts "with a generated slug"
      (create-item e ascii-name)
      (let [item-slug "test-this"
            item (get-item e item-slug)]
        (:name item) => ascii-name
        (:slug item) => item-slug))

    (facts "with a unicode name"
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

    (facts "with no properties"
      (create-item e i {})
      (let [item (get-item e i)]
        (:name item) => i
        (:slug item) => i))

    (facts "with custom properties"
      (create-item e i {:a "b" "c" "d"})
      (let [item (get-item e i)]
        (:name item) => i
        (:slug item) => i
        (:a item) => "b"
        (:c item) => "d"))

    (facts "with unicode properties"
      (create-item e i {:description unicode-description})
      (let [item (get-item e i)]
        (:name item) => i
        (:slug item) => i
        (:description item) => unicode-description))

    (facts "with a generated slug that is already used"
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

    (facts "with a provided slug"
      (let [item (create-item e ascii-name {:slug slug})]
        (:name item) => ascii-name
        (:slug item) => slug))))