(ns fcms.unit.resources.item.item-create
  (:require [midje.sweet :refer :all]
            [fcms.lib.resources :refer :all]
            [fcms.lib.check :refer (timestamp? about-now?)]
            [fcms.resources.collection :as collection]
            [fcms.resources.collection-resource :as resource]
            [fcms.resources.item :refer :all]))

(with-state-changes [(before :facts (reset-collection e))
                     (after :facts (collection/delete-collection e))]

  (facts "about validity checks of invalid new items"

    (fact "when the specified collection doesn't exist"
      (doseq [coll-slug (conj bad-strings "not-here")]
        (valid-new-item coll-slug i) => :bad-collection
        (valid-new-item coll-slug i {}) => :bad-collection))

    (fact "when a provided slug is already used"
      (create-item e "first" {:slug slug})
      (valid-new-item e "second" {:slug slug}) => :slug-conflict)

    (fact "with a provided slug that is invalid"
      (valid-new-item e i {:slug "i I"}) => :invalid-slug)

    (fact "when including a reserved property"
      (doseq [prop resource/reserved-properties]
        (valid-new-item e i {prop foo}) => :property-conflict
        (valid-new-item e i {(name prop) foo}) => :property-conflict)))

  (facts "about validity checks of valid new items"

      (fact "with no properties"
        (valid-new-item e i) => true
        (valid-new-item e i {}) => true)

      (fact "with custom properties"
        (valid-new-item e i {:a "b" "c" "d"}) => true))

  (facts "about item creation failures"

    (fact "with a provided slug that is already used"
      (create-item e "first" {:slug slug})
      (create-item e "second" {:slug slug}) => :slug-conflict)

    (fact "with a provided slug that is invalid"
      (create-item e i {:slug "i I"}) => :invalid-slug)

    (fact "with a collection that doesn't exist"
      (create-item "not-here" i) => :bad-collection
      (create-item "not-here" i {}) => :bad-collection)

    (fact "with a reserved property"
      (doseq [prop resource/reserved-properties]
        (create-item e i {prop foo}) => :property-conflict
        (create-item e i {(name prop) foo}) => :property-conflict)))

  (facts "about item creation"

    (fact "with a generated slug"
      (let [item-slug "test-this"]
        (create-item e ascii-name) => (contains {:name ascii-name :slug item-slug :version 1})
        (get-item e item-slug) => (contains {:name ascii-name :slug item-slug :version 1})))
        
    (fact "with a unicode name"
      (let [item-slug "1"]
        (create-item e unicode-name) => (contains {:name unicode-name :slug item-slug :version 1})
        (get-item e item-slug) => (contains {:name unicode-name :slug item-slug :version 1}))
      (let [item-slug "test"]
        (create-item e mixed-name) => (contains {:name mixed-name :slug item-slug :version 1})
        (get-item e item-slug) => (contains {:name mixed-name :slug item-slug :version 1})))

    (fact "with provided but empty properties"
      (create-item e i {}) => (contains {:name i :slug i :version 1})
      (get-item e i) => (contains {:name i :slug i :version 1}))

    (fact "with custom properties"
      (create-item e i {:a "b" "c" "d"}) => (contains {:name i :slug i :a "b" :c "d" :version 1})
      (get-item e i) => (contains {:name i :slug i :a "b" :c "d" :version 1}))

    (fact "with unicode properties"
      (create-item e i {:description unicode-description}) => (contains {:name i :slug i :description unicode-description :version 1})
      (get-item e i) => (contains {:name i :slug i :description unicode-description :version 1}))

    (fact "with a generated slug that is already used"
      (doseq [item-slug ["test-this" "test-this-1" "test-this-2"]]
        (create-item e ascii-name) => (contains {:name ascii-name :slug item-slug :version 1})
        (get-item e item-slug) => (contains {:name ascii-name :slug item-slug :version 1})))

    (fact "with a provided slug"
      (create-item e ascii-name {:slug slug}) => (contains {:name ascii-name :slug slug :version 1})
      (get-item e slug) => (contains {:name ascii-name :slug slug :version 1}))

    (facts "and timestamps"
      (let [item (create-item e i)
            created-at (:created-at item)
            updated-at (:updated-at item)
            retrieved-item (get-item e i)]
        (instance? timestamp created-at) => true
        (about-now? created-at) = true
        (= created-at updated-at) => true
        (= created-at (:created-at retrieved-item))
        (= updated-at (:updated-at retrieved-item))))))
