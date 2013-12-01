(ns fcms.unit.resources.item.item-lifecycle
  (:require [clj-time.format :refer (parse)]
            [clojure.test :refer :all]
            [midje.sweet :refer :all]
            [fcms.lib.resources :refer :all]
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

;; ----- Validation functions -----

(def timestamp org.joda.time.DateTime)

;; ----- Tests -----

(with-state-changes [(before :facts (empty-collection-e))
                     (after :facts (collection/delete-collection e))]

  (future-facts "about the validity of new items")

  (facts "about successful item creation"

    (with-state-changes [(before :facts (create-item e i))
                         (after :facts (delete-item e i))]

      (fact "resulting in a generated id"
        (instance? String (:id (get-item e i))) => true)

      (fact "resulting in membership in the collection"
        (:collection (get-item e i)) => e)

      (fact "resulting in an initial version"
        (:version (get-item e i)) => 1)

      (fact "resulting in good initial timestamps"
        (instance? timestamp (:created-at (get-item e i))) => true
        (:created-at (get-item e i)) => (:updated-at (get-item e i))))

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

      (fact "when retriieving the item's timestamps"
        (instance? timestamp (:created-at (get-item e i))) => true
        (:created-at (get-item e i)) => (:updated-at (get-item e i)))))

  (future-facts "about validity of item updates")

  (future-facts "about updating items")

  (future-facts "about deleting items"))