(ns fcms.unit.resources.item.item-lifecycle
  (:require [clj-time.format :refer (parse)]
            [clojure.test :refer :all]
            [midje.sweet :refer :all]
            [fcms.lib.resources :refer (empty-collection-e)]
            [fcms.resources.collection :as collection]
            [fcms.resources.collection-resource :as resource]
            [fcms.resources.item :refer :all]))

;; ----- Fixtures -----

(def e "e")
(def i "i")
(def ascii-name "test this")
(def unicode-name "私はガラスを食")
(def mixed-name (str "test " unicode-name))
(def unicode-description "er stîget ûf mit grôzer kraft Τη γλώσσα μου έδωσαν ελληνική მივჰხვდე მას ჩემსა الزجاج و هذا لا يؤلمني. मैं काँच खा सकता ฉันกินกระจกได้ לא מזיק Mogę jeść szkło €")
(def slug "slug")

;; ----- Validation functions -----

(defn verify-new-resource [coll-slug resource]
  (is (= (:collection resource) e))
  (is (= (:version resource) 1))
  (is (instance? String (:id resource)))
  (is (instance? String (:created-at resource)))
  (is (instance? org.joda.time.DateTime (parse (:created-at resource))))
  (is (= (:created-at resource) (:updated-at resource))))

;; ----- Tests -----

;(deftest new-item-validity)

(facts "about item creation"

  (with-state-changes [(before :facts (empty-collection-e)) 
                       (after :facts (collection/delete-collection e))]

    (fact "with a generated slug"
      (let [item (create-item e ascii-name)]
        (:name item) => ascii-name
        (:slug item) => "test-this"
        (verify-new-resource e item)))

    (fact "with a unicode name"
      (let [item (create-item e unicode-name)]
        (:name item) => unicode-name
        (:slug item) => "1"
        (verify-new-resource e item))
      (let [item (create-item e mixed-name)]
        (:name item) => mixed-name
        (:slug item) => "test"
        (verify-new-resource e item)))
    
    (fact "with unicode properties"
      (let [item (create-item e i {:description unicode-description})]
        (:name item) => i
        (:slug item) => i
        (:description item) => unicode-description
        (verify-new-resource e item)))

    (fact "with a generated slug that is already used"
      (let [item (create-item e ascii-name)]
        (:name item) => ascii-name
        (:slug item) => "test-this"
        (verify-new-resource e item))
      (let [item (create-item e ascii-name)]
        (:name item) => ascii-name
        (:slug item) => "test-this-1"
        (verify-new-resource e item))
      (let [item (create-item e ascii-name)]
        (:name item) => ascii-name
        (:slug item) => "test-this-2"
        (verify-new-resource e item)))
    
    (fact "with a provided slug"
      (let [item (create-item e ascii-name {:slug slug})]
        (:name item) => ascii-name
        (:slug item) => slug
        (verify-new-resource e item)))
    
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
        (create-item e i {(name prop) "foo"}) => :property-conflict))))


; (deftest item-retrieval)

; (deftest updated-item-validity)

; (deftest item-updates)

; (deftest item-deletion)