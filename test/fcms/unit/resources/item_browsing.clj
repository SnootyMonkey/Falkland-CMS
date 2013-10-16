(ns fcms.unit.resources.item-browsing
  (:require [clojure.test :refer :all]
            [fcms.lib.resources :refer :all]
            [fcms.resources.collection-resource :as resource]
            [fcms.resources.taxonomy :as taxonomy]
						[fcms.resources.item :as item]))

;; fixtures

(def x "x")
(def y "y")
(def z "z")

(defn- existing-item [slug]
  (item/create-item c slug)
  (taxonomy/categorize-item c slug "t/bar")
  (taxonomy/categorize-item c slug "t/fubar/b")
  (taxonomy/categorize-item c slug "food/fruit/pear"))

(defn- existing-items [f]
  (let [items [x, y, z]]
    (doseq [item items] (existing-item item))
    (f)
    (doseq [item items] (item/delete-item c item))))

;; validation functions

(defn items [& slugs]
  (vec (map #(item/get-item c %) slugs)))

(defn- category-browse [expectation coll-slug category-paths]
  (is (= expectation (taxonomy/items-for-category coll-slug (first category-paths))))
  (let [remaining-paths (rest category-paths)]
    (if-not (empty? remaining-paths) (recur expectation coll-slug remaining-paths))))

;; tests

(use-fixtures :each empty-collection-c existing-taxonomy-t existing-taxonomy-t2 existing-item-e existing-items)

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
      (category-browse :bad-category c ["t/fubar/a/not-here" "t/fubar/a/not-here/" "/t/fubar/a/not-here" "/t/fubar/a/not-here/"])))

   (testing "item browsing"

    (testing "a root category with a single item"
      (category-browse (items e) c ["t/foo" "t/foo/" "/t/foo" "/t/foo/"]))

    (testing "a leaf category with a single item"
      (category-browse (items e) c ["t/fubar/a" "t/fubar/a/" "/t/fubar/a" "/t/fubar/a/"]))

    (testing "a root category with a few items"
      (category-browse (items x y z) c ["t/bar" "t/bar/" "/t/bar" "/t/bar/"]))

    (testing "a parent category with a few items"
      (category-browse (items e x y z) c ["t/fubar" "t/fubar/" "/t/fubar" "/t/fubar/"]))

    (testing "a leaf category with a few items"
      (category-browse (items x y z) c ["food/fruit/pear" "food/fruit/pear/" "/food/fruit/pear" "/food/fruit/pear/"]))))