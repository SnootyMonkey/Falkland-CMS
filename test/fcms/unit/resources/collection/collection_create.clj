(ns fcms.unit.resources.collection.collection-create
  (:require [midje.sweet :refer :all]
            [fcms.lib.resources :refer :all]
            [fcms.lib.check :refer (about-now?)]
            [fcms.resources.collection :refer :all]
            [fcms.resources.common :as common]))

(with-state-changes [(before :facts (reset-collection e))
                     (after :facts (doseq [coll-slug [e c slug "1" "test" "test-this"]] (delete-collection coll-slug)))]

  (facts "about validity checks of invalid new collections"

    (fact "when no name is provided"
      (valid-new-collection "") => :no-name
      (valid-new-collection :keyword) => :no-name
      (valid-new-collection nil) => :no-name
      (valid-new-collection false) => :no-name
      (valid-new-collection 1) => :no-name
      (valid-new-collection 1.1) => :no-name
      (valid-new-collection ["foo"]) => :no-name
      (valid-new-collection {:foo "bar"}) => :no-name)

    (fact "when a provided slug is already used"
      (valid-new-collection "second" {:slug e}) => :slug-conflict)

    (fact "with a provided slug that is invalid"
      (valid-new-collection c {:slug "i I"}) => :invalid-slug)

    (fact "when including a reserved property"
      (doseq [prop common/reserved-properties]
        (valid-new-collection c {prop foo}) => :property-conflict
        (valid-new-collection c {(name prop) foo}) => :property-conflict)))

  (facts "about collection creation failures"

    (fact "with no name is provided"
      (create-collection "") => :no-name
      (create-collection nil) => :no-name
      (create-collection false) => :no-name
      (create-collection 1) => :no-name
      (create-collection 1.1) => :no-name
      (create-collection ["foo"]) => :no-name
      (create-collection {:foo "bar"}) => :no-name)

    (fact "with a provided slug that is already used"
      (create-collection c {:slug "e"}) => :slug-conflict)

    (fact "with a provided slug that is invalid"
      (create-collection c {:slug "i I"}) => :invalid-slug)

    (fact "with a reserved property"
      (doseq [prop common/reserved-properties]
        (create-collection c {prop foo}) => :property-conflict
        (create-collection c {(name prop) foo}) => :property-conflict)))

  (facts "about collection creation"

    (fact "with a generated slug"
      (let [coll-slug "test-this"]
        (create-collection ascii-name) => (contains {:name ascii-name :slug coll-slug})
        (get-collection coll-slug) => (contains {:name ascii-name :slug coll-slug})))
        
    (fact "with a unicode name"
      (let [coll-slug "1"]
        (create-collection unicode-name) => (contains {:name unicode-name :slug coll-slug :version 1})
        (get-collection coll-slug) => (contains {:name unicode-name :slug coll-slug :version 1}))
      (let [coll-slug "test"]
        (create-collection mixed-name) => (contains {:name mixed-name :slug coll-slug :version 1})
        (get-collection coll-slug) => (contains {:name mixed-name :slug coll-slug :version 1})))

    (fact "with provided but empty properties"
      (create-collection c {}) => (contains {:name c :slug c :version 1})
      (get-collection c) => (contains {:name c :slug c :version 1}))

    (fact "with custom properties"
      (create-collection c {:a "b" "c" "d"}) => (contains {:name c :slug c :a "b" :c "d" :version 1})
      (get-collection c) => (contains {:name c :slug c :a "b" :c "d" :version 1}))

    (fact "with unicode properties"
      (create-collection c {:description unicode-description}) => (contains {:name c :slug c :description unicode-description :version 1})
      (get-collection c) => (contains {:name c :slug c :description unicode-description :version 1}))

    (fact "with a generated slug that is already used"
      (doseq [coll-slug ["test-this" "test-this-1" "test-this-2"]]
        (create-collection "test-this") => (contains {:name "test-this" :slug coll-slug :version 1})
        (get-collection coll-slug) => (contains {:name "test-this" :slug coll-slug :version 1}))
      (doseq [coll-slug ["test-this" "test-this-1" "test-this-2"]]
        (delete-collection coll-slug)))

    (fact "with a provided slug"
      (create-collection ascii-name {:slug slug}) => (contains {:name ascii-name :slug slug :version 1})
      (get-collection slug) => (contains {:name ascii-name :slug slug :version 1}))

    (facts "and timestamps"
      (let [item (create-collection c)
            created-at (:created-at item)
            updated-at (:updated-at item)
            retrieved-coll (get-collection c)]
        (instance? timestamp created-at) => true
        (about-now? created-at) = true
        (= created-at updated-at) => true
        (= created-at (:created-at retrieved-coll)) => true
        (= updated-at (:updated-at retrieved-coll)) => true))))