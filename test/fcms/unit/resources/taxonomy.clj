(ns fcms.unit.resources.taxonomy
  (:require [clojure.test :refer :all]
  					[fcms.lib.resource :refer (verify-new-resource empty-collection-c)]
            [fcms.resources.taxonomy :refer :all]
            [fcms.resources.collection :as collection]))

(defn- existing-taxonomy-t [f]
	(create-taxonomy "c" "Taxonomy" 
		{:slug "t"
		:description "Categorize it."
		:categories [
			{"foo" "Foo"}
			{"bar" "Bar"}
			{"fubar" "FUBAR" :categories [
				{"a" "A"}
				{"b" "B"}
			]}
		]})
		(f))

(use-fixtures :each empty-collection-c existing-taxonomy-t)

(deftest category-creation
  (testing "category creation"

    (testing "with a bad collection"
    	(is (= :bad-collection (create-category "not-here" "/t/new")))
    	(is (= :bad-collection (create-category "not-here" "/t/new" "New"))))

    (testing "with a bad taxonomy"
    	(is (= :bad-taxonomy (create-category "c" "/not-here/new")))
    	(is (= :bad-taxonomy (create-category "c" "/not-here/new" "New"))))

    ; (testing "with a bad category"
    ; 	(is (= :bad-category (create-category "c" "/t/not-here/new")))
    ; 	(is (= :bad-category (create-category "c" "/t/not-here/new" "New")))
    ; 	(is (= :bad-category (create-category "c" "/t/fubar/a/not-here/new")))
    ; 	(is (= :bad-category (create-category "c" "/t/fubar/a/not-here/new" "New"))))))

    (testing "a new top level category")

    (testing "a new nested category")))