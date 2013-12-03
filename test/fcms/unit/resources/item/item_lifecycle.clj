(ns fcms.unit.resources.item.item-lifecycle
  (:require [clj-time.format :refer (parse)]
            [midje.sweet :refer :all]
            [fcms.lib.resources :refer :all]
            [fcms.lib.checks :refer (about-now?)]
            [fcms.resources.collection :as collection]
            [fcms.resources.collection-resource :as resource]
            [fcms.resources.item :refer :all]))

;; ----- Fixtures -----

(def i "i")
(def ascii-name "test this")
(def unicode-name "私はガラスを食")
(def mixed-name (str "test " unicode-name))
(def unicode-description "er stîget ûf mit grôzer kraft Τη γλώσσα μου έδωσαν ελληνική მივჰხვდე მას ჩემსა الزجاج و هذا لا يؤلمني. मैं काँच खा सकता ฉันกินกระจกได้ לא מזיק Mogę jeść szkło €")
(def slug "slug")

(defn existing-item-i []
  (create-item e unicode-name
    {:slug i :description unicode-description :custom "foo"}))

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
        (create-item e i {prop "foo"}) => :property-conflict
        (create-item e i {(name prop) "foo"}) => :property-conflict)))

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
        (:slug item) => slug)))

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
        (:custom (get-item e i)) => "foo")

      (fact "when retrieving the item's timestamps"
        (instance? timestamp (:created-at (get-item e i))) => true
        (about-now? (:created-at (get-item e i))) => true
        (:created-at (get-item e i)) => (:updated-at (get-item e i)))))

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
          (update-item e i {prop "foo"}) => :property-conflict
          (update-item e i {(name prop) "foo"}) => :property-conflict))))

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
          (update-item e i {prop "foo"}) => :property-conflict
          (update-item e i {(name prop) "foo"}) => :property-conflict))))

  (future-facts "about updating items")
  (future-facts "about item deletion failures")
  (future-facts "about deleting items"))