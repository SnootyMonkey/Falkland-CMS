(ns fcms.unit.resources.taxonomy
  (:require [clojure.test :refer :all]
  					[fcms.lib.resource :refer (verify-new-resource empty-collection-c)]
            [fcms.resources.taxonomy :refer :all]
            [fcms.resources.collection :as collection]))

(def existing-categories [
    {"foo" "Foo"}
    {"bar" "Bar"}
    {"fubar" "FUBAR" :categories [
        {"a" "A"}
        {"b" "B"}
    ]}])

(defn- existing-taxonomy-t [f]
	(create-taxonomy "c" "Taxonomy" 
		{:slug "t"
		:description "Categorize it."
		:categories existing-categories})
		(f))

(defn- validate-categories [valid-categories actual-categories])

(use-fixtures :each empty-collection-c existing-taxonomy-t)

(deftest category-creation
  (testing "category creation"

    (testing "with a bad collection"
    	(is (= :bad-collection (create-category "not-here" "/t/new")))
    	(is (= :bad-collection (create-category "not-here" "/t/new" "New"))))

    (testing "with a bad taxonomy"
    	(is (= :bad-taxonomy (create-category "c" "/not-here/new")))
    	(is (= :bad-taxonomy (create-category "c" "/not-here/new" "New"))))

    ; (testing "creating new top level categories"
    ;     (is (true? (create-category "c" "/t/new-a")))
    ;     (let [categories (:categories (get-taxonomy "c" "t"))]
    ;         (validate-categories [{"new-a" "new-a"}] categories)
    ;         (validate-categories existing-categories categories)
    ;         (is (= 4 (count categories))))
    ;     (is (true? (create-category "c" "/t/new-b" "New B")))
    ;     (let [categories (:categories (get-taxonomy "c" "t"))]
    ;         (validate-categories [{"new-b" "New B"}] categories)
    ;         (validate-categories [{"new-a" "new-a"}] categories)
    ;         (validate-categories existing-categories categories)
    ;         (is (= 5 (count categories)))))))

    (testing "a new nested category")
        ; name and no name

    (testing "multiple new categories at once")))
        ; top level and nested
        ; name and no name
