(ns fcms.unit.resources.item-categorization
  (:require [clojure.test :refer :all]
            [fcms.lib.resource :refer (empty-collection-c)]
            [fcms.resources.collection-resource :as resource]
            [fcms.resources.taxonomy :as taxonomy]
						[fcms.resources.item :as item]))

(def c "c")
(def t "t")
(def i "i")
(def e "e")

(def existing-categories [
  {:slug "foo" :name "Foo"}
  {:slug "bar" :name "Bar"}
  {:slug "fubar" :name "FUBAR" :categories [
    {:slug "a" :name "A"}
    {:slug "b" :name "B"}
  ]}])

(defn existing-taxonomy-t [f]
  (resource/create-resource c "Taxonomy" :taxonomy [] 
    {:slug t
     :description "Categorize it."
     :categories existing-categories})
  (f)
  (taxonomy/delete-taxonomy c t))

(defn existing-item-i [f]
  (item/create-item c i)
  (f)
  (item/delete-item c i))

(defn existing-item-e [f]
  (item/create-item c e)
  (taxonomy/categorize-item c "/foo" e)
  (taxonomy/categorize-item c "/fubar/a" e)
  (f)
  (item/delete-item c e))

(use-fixtures :each empty-collection-c existing-taxonomy-t existing-item-i existing-item-e)

(deftest item-categorization
  (testing "item categorization failures"
  	(testing "item categorization with a non-existent collection"
  		(is (= :bad-collection (taxonomy/categorize-item "not-here" "/t/foo" i))))

  	(testing "item categorization with a non-existent taxonomy"
  		(is (= :bad-taxonomy (taxonomy/categorize-item c "not-here/foo" i)))
  		(is (= :bad-taxonomy (taxonomy/categorize-item c "not-here/foo/" i)))
  		(is (= :bad-taxonomy (taxonomy/categorize-item c "/not-here/foo" i)))
  		(is (= :bad-taxonomy (taxonomy/categorize-item c "/not-here/foo/" i))))

  	(testing "item categorization with a non-existent item"
  		(is (= :bad-item (taxonomy/categorize-item c "/t/foo" "not-here"))))

  	(testing "item categorization with no category"
  		(is (= :bad-category (taxonomy/categorize-item c "t" i)))
  		(is (= :bad-category (taxonomy/categorize-item c "t/" i)))
  		(is (= :bad-category (taxonomy/categorize-item c "/t" i)))
  		(is (= :bad-category (taxonomy/categorize-item c "/t/" i))))

  	(testing "item categorization with a non-existent root category"
  		(is (= :bad-category (taxonomy/categorize-item c "t/not-here" i)))
  		(is (= :bad-category (taxonomy/categorize-item c "t/not-here/" i)))
  		(is (= :bad-category (taxonomy/categorize-item c "/t/not-here" i)))
  		(is (= :bad-category (taxonomy/categorize-item c "/t/not-here/" i))))

  	(testing "item categorization with a non-existent leaf category"
  		(is (= :bad-category (taxonomy/categorize-item c "t/foo/not-here" i)))
  		(is (= :bad-category (taxonomy/categorize-item c "t/foo/not-here/" i)))
  		(is (= :bad-category (taxonomy/categorize-item c "/t/foo/not-here" i)))
  		(is (= :bad-category (taxonomy/categorize-item c "/t/foo/not-here/" i)))
  		(is (= :bad-category (taxonomy/categorize-item c "t/fubar/a/not-here" i)))
  		(is (= :bad-category (taxonomy/categorize-item c "t/fubar/a/not-here/" i)))
  		(is (= :bad-category (taxonomy/categorize-item c "/t/fubar/a/not-here" i)))
  		(is (= :bad-category (taxonomy/categorize-item c "/t/fubar/a/not-here/" i)))))

  (testing "item categorization sucesses"
  	(testing "item categorization with a single root category")
  	(testing "item categorization with multiple root categories")
  	(testing "item categorization with a single leaf category")
  	(testing "item categorization with multiple leaf categories")
  	(testing "item categorization with a single root category and a single leaf category")))

(deftest item-uncategorization
  (testing "item uncategorization failures")
  (testing "item uncategorization successes"))