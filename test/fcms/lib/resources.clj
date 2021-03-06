(ns fcms.lib.resources
  "Namespace of data fixtures for use in tests."
  (:require [clojure.test :refer :all]
            [clj-time.format :refer (parse)]
            [fcms.resources.collection-resource :as resource]
            [fcms.resources.collection :as collection]
            [fcms.resources.item :as item]
            [fcms.resources.taxonomy :as taxonomy]))

;; ----- Names / slugs / constants -----

(def c "c")
(def e "e")
(def i "i")
(def one "one")
(def many "many")
(def slug "slug")

(def t "t")
(def t2 "food")

(def ascii-name "test this")
(def unicode-name "私はガラスを食")
(def mixed-name (str "test " unicode-name))
(def long-unicode-name " -tHiS #$is%?-----ελληνικήalso-მივჰხვდემასჩემსაãالزجاجوهذالايؤلمني-slüg♜-♛-☃-✄-✈  - ")
(def generated-slug "this-is-also-a-slug")

(def ascii-description "this is an item")
(def unicode-description "er stîget ûf mit grôzer kraft Τη γλώσσα μου έδωσαν ελληνική მივჰხვდე მას ჩემსა الزجاج و هذا لا يؤلمني. मैं काँच खा सकता ฉันกินกระจกได้ לא מזיק Mogę jeść szkło €")

(def foo "foo")
(def bar "bar")

(def bad-strings [nil 42 :foo "" "  " "\n" "\r\n\t\n"])

(def timestamp org.joda.time.DateTime)

;; ----- Collections -----

(defn reset-collection [collection]
  (collection/delete-collection collection)
  (collection/create-collection collection))

(defn delete-all-collections []
  (doseq [coll (collection/all-collections)]
    (collection/delete-collection (:slug coll))))

;; ----- Items -----

(defn existing-item-i []
  (item/create-item e unicode-name
    {:slug i :description unicode-description :custom foo}))

;; ----- Category maps -----

(def existing-categories-c [
  {:slug "foo" :name "Foo"}
  {:slug "bar" :name "Bar"}
  {:slug "fubar" :name "FUBAR" :categories [
    {:slug "a" :name "A"}
    {:slug "b" :name "B"}
  ]}])

(def existing-categories-c2 [
  {:slug "fruit" :name "Fruit" :categories [
    {:slug "apple" :name "Apple"}
    {:slug "pear" :name "Pear"}
  ]}
  {:slug "vegetable" :name "Vegetable" :categories [
    {:slug "carrot" :name "Carrot"}
    {:slug "brocolli" :name "Brocolli"}
  ]}])

;; ----- Taxonomies -----

(defn empty-taxonomy-et []
  (taxonomy/create-taxonomy e "Empty Taxonomy"
     {:slug "et"
      :description "Categorize it."}))

(defn existing-taxonomy-t []
  (resource/create-resource e "Taxonomy" :taxonomy []
    {:slug t
     :description "Categorize it."
     :categories existing-categories-c}))

(defn existing-taxonomy-t2 []
  (resource/create-resource e "Food" :taxonomy []
    {:slug t2
     :description "Yummy."
     :categories existing-categories-c2}))