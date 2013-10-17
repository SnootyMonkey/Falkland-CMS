(ns fcms.unit.resources.item-categorization
  (:require [clojure.test :refer :all]
            [fcms.lib.resources :refer :all]
            [fcms.resources.collection-resource :as resource]
            [fcms.resources.taxonomy :as taxonomy]
						[fcms.resources.item :as item]))

;; ----- Fixtures -----

(def i "i")
(def m "m")
(def u "u")

(defn existing-item-i [f]
  (item/create-item c i)
  (f)
  (item/delete-item c i))

(defn existing-item-u []
  (item/create-item c u)
  (taxonomy/categorize-item c u "t/foo")
  (taxonomy/categorize-item c u "t/bar")
  (taxonomy/categorize-item c u "t/fubar/a")
  (taxonomy/categorize-item c u "t/fubar/b"))

(defn existing-item-m []
  (item/create-item c m)
  (taxonomy/categorize-item c m "t/foo")
  (taxonomy/categorize-item c m "t/fubar/a")
  (taxonomy/categorize-item c m "food/fruit")
  (taxonomy/categorize-item c m "food/vegetable/carrot"))

;; ----- Validation functions -----

(defn- categorize [expectation coll-slug item-slug category-paths]
	(is (= expectation (taxonomy/categorize-item coll-slug item-slug (first category-paths))))
	(let [remaining-paths (rest category-paths)]
		(if-not (empty? remaining-paths) (recur expectation coll-slug item-slug remaining-paths))))

(defn- categorize-i [expectation coll-slug category-paths]
	(categorize expectation coll-slug i category-paths))

(defn- categorize-e [expectation coll-slug category-paths]
	(categorize expectation coll-slug e category-paths))

(defn- categorize-foo
  "Create a new item and categorize it by each category in the vector, validating with each add and then with a
  final check on the categories after getting the item."
  ([category-paths category-validations] (categorize-foo true category-paths category-validations))
  ([incremental-validation category-paths category-validations] 
    (item/create-item c foo)
    (categorize-foo incremental-validation category-paths [] category-validations []))
  ([incremental-validation category-paths added-paths category-validations added-validations]
    (let [category (first category-paths)
          validation (first category-validations)
          validations (vec (conj added-validations validation))]
      (if category
        (do
          (is (= (:categories (taxonomy/categorize-item c foo category)) validations))
          (categorize-foo incremental-validation (rest category-paths)
            (vec (conj added-paths category)) (rest category-validations) validations))
        (do
          (is (= (:categories (item/get-item c foo)) added-validations))
          (item/delete-item c foo))))))

(defn- uncategorize-u 
  "Create a new categorized item and uncategorize it by each category in the vector, validating with a
  final check on the categories after getting the item."
  ([coll-slug category-paths] (uncategorize-u map? coll-slug category-paths))
  ([expectation coll-slug category-paths] 
    (existing-item-u)
    (uncategorize-u (if (keyword? expectation) #(= expectation %) #(expectation %))
      coll-slug (set (map taxonomy/normalize-category-path category-paths)) category-paths)
    (item/delete-item c u))
  ([expectation coll-slug removed-paths category-paths]
    (is (expectation (taxonomy/uncategorize-item coll-slug u (first category-paths))))
    (let [remaining-paths (rest category-paths)]
      (if (empty? remaining-paths)
        (is (not-any? removed-paths (:categories (item/get-item coll-slug u))))
        (recur expectation coll-slug removed-paths remaining-paths)))))

;; ----- Tests -----

(use-fixtures :each empty-collection-c existing-taxonomy-t existing-taxonomy-t2 existing-item-i existing-item-e)

(deftest item-categorization
  (testing "item categorization failures"

  	(testing "item categorization with a non-existent collection"
  		(categorize-i :bad-collection "not-here" ["/t/foo"]))

  	(testing "item categorization with a non-existent taxonomy"
  		(categorize-i :bad-taxonomy c ["not-here/foo" "not-here/foo/" "/not-here/foo" "/not-here/foo/"]))

  	(testing "item categorization with a non-existent item"
  		(is (= :bad-item (taxonomy/categorize-item c "not-here" "/t/foo"))))

  	(testing "item categorization with no category"
  		(categorize-i :bad-category c ["t" "t/" "/t" "/t/"]))

  	(testing "item categorization with a non-existent root category"
  		(categorize-i :bad-category c ["t/not-here" "t/not-here/" "/t/not-here" "/t/not-here/"]))

  	(testing "item categorization with a non-existent leaf category"
  		(categorize-i :bad-category c ["t/foo/not-here" "t/foo/not-here/" "/t/foo/not-here" "/t/foo/not-here/"])
  		(categorize-i :bad-category c ["t/fubar/a/not-here" "t/fubar/a/not-here/" "/t/fubar/a/not-here" "/t/fubar/a/not-here/"]))

  	(testing "item categorization with a duplicate category"
  		(categorize-e :duplicate-category c ["t/foo" "t/foo/" "/t/foo" "/t/foo/"])
  		(categorize-e :duplicate-category c ["t/fubar" "t/fubar/" "/t/fubar" "/t/fubar/"])
  		(categorize-e :duplicate-category c ["t/fubar/a" "t/fubar/a/" "/t/fubar/a" "/t/fubar/a/"])))

  (testing "item categorization sucesses"

  	(testing "item categorization with a single root category"
      (categorize-foo ["t/bar"] ["t/bar"])
      (categorize-foo ["t/bar/"] ["t/bar"])
      (categorize-foo ["/t/bar"] ["t/bar"])
      (categorize-foo ["/t/bar/"] ["t/bar"]))

  	(testing "item categorization with multiple root categories"
      (categorize-foo ["t/bar" "t/foo"] ["t/bar" "t/foo"])
      (categorize-foo ["t/bar/" "t/foo/"] ["t/bar" "t/foo"])
      (categorize-foo ["/t/bar" "/t/foo"] ["t/bar" "t/foo"])
      (categorize-foo ["/t/bar/" "/t/foo/"] ["t/bar" "t/foo"]))

  	(testing "item categorization with a single leaf category"
      (categorize-foo ["t/fubar/a"] ["t/fubar/a"])
      (categorize-foo ["t/fubar/a/"] ["t/fubar/a"])
      (categorize-foo ["/t/fubar/a"] ["t/fubar/a"])
      (categorize-foo ["/t/fubar/a/"] ["t/fubar/a"]))

  	(testing "item categorization with multiple leaf categories"
      (categorize-foo ["t/fubar/a" "t/fubar/b"] ["t/fubar/a" "t/fubar/b"])
      (categorize-foo ["t/fubar/a/" "t/fubar/b/"] ["t/fubar/a" "t/fubar/b"])
      (categorize-foo ["/t/fubar/a" "/t/fubar/b"] ["t/fubar/a" "t/fubar/b"])
      (categorize-foo ["/t/fubar/a/" "/t/fubar/b/"] ["t/fubar/a" "t/fubar/b"]))

  	(testing "item categorization with a single root category and a single leaf category"
      (categorize-foo ["t/bar" "t/fubar/b"] ["t/bar" "t/fubar/b"])
      (categorize-foo ["t/bar/" "t/fubar/b/"] ["t/bar" "t/fubar/b"])
      (categorize-foo ["/t/bar" "/t/fubar/b"] ["t/bar" "t/fubar/b"])
      (categorize-foo ["/t/bar/" "/t/fubar/b/"] ["t/bar" "t/fubar/b"]))

    (testing "item categorization with a single root category and a single leaf category in multiple taxonomies"
      (categorize-foo ["t/bar" "t/fubar/b" "food/fruit" "food/vegetable/carrot"]
                      ["t/bar" "t/fubar/b" "food/fruit" "food/vegetable/carrot"])
      (categorize-foo ["t/bar/" "t/fubar/b/" "food/fruit/" "food/vegetable/carrot/"]
                      ["t/bar" "t/fubar/b" "food/fruit" "food/vegetable/carrot"])
      (categorize-foo ["/t/bar" "/t/fubar/b" "/food/fruit" "/food/vegetable/carrot"]
                      ["t/bar" "t/fubar/b" "food/fruit" "food/vegetable/carrot"])
      (categorize-foo ["/t/bar/" "/t/fubar/b/" "/food/fruit/" "/food/vegetable/carrot/"]
                      ["t/bar" "t/fubar/b" "food/fruit" "food/vegetable/carrot"]))))

(deftest item-uncategorization
   (testing "item uncategorization failures"

    (testing "item uncategorization with a non-existent collection"
      (uncategorize-u :bad-collection "not-here" ["/t/foo"]))

    (testing "item uncategorization with a non-existent taxonomy"
      (uncategorize-u :bad-taxonomy c ["not-here/foo" "not-here/foo/" "/not-here/foo" "/not-here/foo/"]))

    (testing "item uncategorization with a non-existent item"
      (is (= :bad-item (taxonomy/uncategorize-item c "not-here" "/t/foo"))))

    (testing "item categorization with no category"
      (uncategorize-u :bad-category c ["t" "t/" "/t" "/t/"]))

    (testing "item categorization with a non-existent root category"
      (uncategorize-u :bad-category c ["t/not-here" "t/not-here/" "/t/not-here" "/t/not-here/"]))

    (testing "item uncategorization with a non-existent leaf category"
      (uncategorize-u :bad-category c ["t/foo/not-here" "t/foo/not-here/" "/t/foo/not-here" "/t/foo/not-here/"])
      (uncategorize-u :bad-category c ["t/fubar/a/not-here" "t/fubar/a/not-here/" "/t/fubar/a/not-here" "/t/fubar/a/not-here/"]))

    (testing "item uncategorization with a partial category path"
      (uncategorize-u :bad-category c ["t/fubar" "t/fubar/" "/t/fubar" "/t/fubar/"])))

  (testing "item uncategorization successes"

    (testing "item uncategorization with a single root category"
      (uncategorize-u c ["t/foo"])
      (uncategorize-u c ["t/foo/"])
      (uncategorize-u c ["/t/foo"])
      (uncategorize-u c ["/t/foo/"]))

    (testing "item uncategorization withmultiple root categories"
      (uncategorize-u c ["t/foo" "t/bar"])
      (uncategorize-u c ["t/foo/" "t/bar/"])
      (uncategorize-u c ["/t/foo" "/t/bar"])
      (uncategorize-u c ["/t/foo/" "/t/bar/"]))

    (testing "item uncategorization with a single leaf category"
      (uncategorize-u c ["t/fubar/a"])
      (uncategorize-u c ["t/fubar/a/"])
      (uncategorize-u c ["/t/fubar/a"])
      (uncategorize-u c ["/t/fubar/a/"]))

    (testing "item uncategorization with multiple leaf categories"
      (uncategorize-u c ["t/fubar/a" "t/fubar/b"])
      (uncategorize-u c ["t/fubar/a/" "t/fubar/b/"])
      (uncategorize-u c ["/t/fubar/a" "/t/fubar/b"])
      (uncategorize-u c ["/t/fubar/a/" "/t/fubar/b/"]))

    (testing "item uncategorization with a single root category and a single leaf category"
      (uncategorize-u c ["t/foo" "t/fubar/a"])
      (uncategorize-u c ["t/foo/" "t/fubar/a/"])
      (uncategorize-u c ["/t/foo" "/t/fubar/a"])
      (uncategorize-u c ["/t/foo/" "/t/fubar/a/"]))

    (testing "item categorization with a single root category and a single leaf category in multiple taxonomies")
      (existing-item-m)
      (is (= ["t/fubar/a" "food/fruit" "food/vegetable/carrot"] (:categories (taxonomy/uncategorize-item c m "t/foo"))))
      (is (= ["food/fruit" "food/vegetable/carrot"] (:categories (taxonomy/uncategorize-item c m "t/fubar/a/"))))
      (is (= ["food/vegetable/carrot"] (:categories (taxonomy/uncategorize-item c m "/food/fruit"))))
      (is (= [] (:categories (taxonomy/uncategorize-item c m "/food/vegetable/carrot/"))))
      (is (= [] (:categories (item/get-item c m))))
      (item/delete-item c m)))