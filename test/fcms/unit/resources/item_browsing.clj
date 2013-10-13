(ns fcms.unit.resources.item-browsing
  (:require [clojure.test :refer :all]
            [fcms.lib.resources :refer :all]
            [fcms.resources.collection-resource :as resource]
            [fcms.resources.taxonomy :as taxonomy]
						[fcms.resources.item :as item]))

(defn- category-browse [expectation coll-slug category-paths]
	(is (= expectation (taxonomy/items-for-category coll-slug (first category-paths))))
	(let [remaining-paths (rest category-paths)]
		(if-not (empty? remaining-paths) (recur expectation coll-slug remaining-paths))))

(use-fixtures :each empty-collection-c existing-taxonomy-t existing-item-i)

(deftest browse-items
  (testing "item browsing failures"

    (testing "item browsing with a non-existent collection"
      (category-browse :bad-collection "not-here" ["/t/foo"]))

    (testing "item browsing with a non-existent taxonomy"
      (category-browse :bad-taxonomy c ["not-here/foo" "not-here/foo/" "/not-here/foo" "/not-here/foo/"]))

    (testing "browsing with no category"
      (category-browse :bad-category c ["t" "t/" "/t" "/t/"]))

    (testing "item browsing with a non-existent root category"
      (category-browse :bad-category c ["t/not-here" "t/not-here/" "/t/not-here" "/t/not-here/"]))

    (testing "item browsing with a non-existent leaf category"
      (category-browse :bad-category c ["t/foo/not-here" "t/foo/not-here/" "/t/foo/not-here" "/t/foo/not-here/"])
      (category-browse :bad-category c ["t/fubar/a/not-here" "t/fubar/a/not-here/" "/t/fubar/a/not-here" "/t/fubar/a/not-here/"]))))

;   (testing "item browsing"

;     (testing "item categorization with a single root category"
;       (categorize-foo ["t/bar"] ["t/bar"])
;       (categorize-foo ["t/bar/"] ["t/bar"])
;       (categorize-foo ["/t/bar"] ["t/bar"])
;       (categorize-foo ["/t/bar/"] ["t/bar"]))

;     (testing "item categorization with multiple root categories"
;       (categorize-foo ["t/bar" "t/foo"] ["t/bar" "t/foo"])
;       (categorize-foo ["t/bar/" "t/foo/"] ["t/bar" "t/foo"])
;       (categorize-foo ["/t/bar" "/t/foo"] ["t/bar" "t/foo"])
;       (categorize-foo ["/t/bar/" "/t/foo/"] ["t/bar" "t/foo"]))

;     (testing "item categorization with a single leaf category"
;       (categorize-foo ["t/fubar/a"] ["t/fubar/a"])
;       (categorize-foo ["t/fubar/a/"] ["t/fubar/a"])
;       (categorize-foo ["/t/fubar/a"] ["t/fubar/a"])
;       (categorize-foo ["/t/fubar/a/"] ["t/fubar/a"]))

;     (testing "item categorization with multiple leaf categories"
;       (categorize-foo ["t/fubar/a" "t/fubar/b"] ["t/fubar/a" "t/fubar/b"])
;       (categorize-foo ["t/fubar/a/" "t/fubar/b/"] ["t/fubar/a" "t/fubar/b"])
;       (categorize-foo ["/t/fubar/a" "/t/fubar/b"] ["t/fubar/a" "t/fubar/b"])
;       (categorize-foo ["/t/fubar/a/" "/t/fubar/b/"] ["t/fubar/a" "t/fubar/b"]))

;     (testing "item categorization with a single root category and a single leaf category"
;       (categorize-foo ["t/bar" "t/fubar/b"] ["t/bar" "t/fubar/b"])
;       (categorize-foo ["t/bar/" "t/fubar/b/"] ["t/bar" "t/fubar/b"])
;       (categorize-foo ["/t/bar" "/t/fubar/b"] ["t/bar" "t/fubar/b"])
;       (categorize-foo ["/t/bar/" "/t/fubar/b/"] ["t/bar" "t/fubar/b"]))

;     (testing "item categorization with a single root category and a single leaf category in multiple taxonomies"
;       (categorize-foo ["t/bar" "t/fubar/b" "food/fruit" "food/vegetable/carrot"]
;                       ["t/bar" "t/fubar/b" "food/fruit" "food/vegetable/carrot"])
;       (categorize-foo ["t/bar/" "t/fubar/b/" "food/fruit/" "food/vegetable/carrot/"]
;                       ["t/bar" "t/fubar/b" "food/fruit" "food/vegetable/carrot"])
;       (categorize-foo ["/t/bar" "/t/fubar/b" "/food/fruit" "/food/vegetable/carrot"]
;                       ["t/bar" "t/fubar/b" "food/fruit" "food/vegetable/carrot"])
;       (categorize-foo ["/t/bar/" "/t/fubar/b/" "/food/fruit/" "/food/vegetable/carrot/"]
;                       ["t/bar" "t/fubar/b" "food/fruit" "food/vegetable/carrot"]))))