(ns fcms.unit.resources.taxonomy.item-browsing
  (:require [midje.sweet :refer :all]
            [fcms.lib.resources :refer :all]
            [fcms.resources.collection :as collection]
            [fcms.resources.collection-resource :as resource]
            [fcms.resources.taxonomy :as taxonomy]
						[fcms.resources.item :as item]))

;; ----- Fixtures -----

(def x "x")
(def y "y")
(def z "z")

(def c2 "c2")
(def t3 "t3")
(def a "a")

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

(defn- create-single-item-taxonomy []
  (collection/create-collection c2)
  (taxonomy/create-taxonomy c2 t3)
  (item/create-item c2 a)
  (taxonomy/create-category c2 "t3/a")
  (taxonomy/categorize-item c2 a "t3/a"))

;; ----- Helper functions -----

(defn items [& slugs]
  (vec (map #(item/get-item c %) slugs)))

;; ----- Tests -----

(facts "about item browsing failures"

  (with-state-changes [(before :facts (do (empty-collection-e) (existing-taxonomy-t)))
                       (after :facts (collection/delete-collection e))]

    (fact "with a non-existent collection"
      (taxonomy/items-for-taxonomy "not-here" "t") => :bad-collection
      (taxonomy/items-for-category "not-here" "/t/foo") => :bad-collection)

    (fact "with a non-existent taxonomy"
      (taxonomy/items-for-taxonomy e "not-here") => :bad-taxonomy
      (doseq [category ["not-here/foo" "not-here/foo/" "/not-here/foo" "/not-here/foo/"]]
        (taxonomy/items-for-category e category) => :bad-taxonomy))

    (fact "with a non-existent category"
      (doseq [category ["t" "t/" "/t" "/t/"]]
        (taxonomy/items-for-category e category) => :bad-category))

    (fact "with a non-existent root category"
      (doseq [category ["t/not-here" "t/not-here/" "/t/not-here" "/t/not-here/"]]
        (taxonomy/items-for-category e category) => :bad-category))

    (fact "with a non-existent leaf category"
      (doseq [category ["t/foo/not-here" "t/foo/not-here/" "/t/foo/not-here" "/t/foo/not-here/"]]
        (taxonomy/items-for-category e category) => :bad-category)
      (doseq [category ["t/fubar/a/not-here" "t/fubar/a/not-here/" "/t/fubar/a/not-here" "/t/fubar/a/not-here/"]]
        (taxonomy/items-for-category e category) => :bad-category))))

;(use-fixtures :each empty-collection-c existing-taxonomy-t existing-taxonomy-t2 existing-item-e existing-items)

; (facts "about item browsing"

;     (testing "a taxonomy with a single item"
;       (create-single-item-taxonomy)
;       (taxonomy-browse [(item/get-item c2 a)] c2 t3)
;       (collection/delete-collection c2))

;     (testing "a taxonomy with multiple items"
;       (taxonomy-browse (items e x y z) c "t"))

;     (testing "a root category with a single item"
;       (category-browse (items e) c ["t/foo" "t/foo/" "/t/foo" "/t/foo/"]))

;     (testing "a leaf category with a single item"
;       (category-browse (items e) c ["t/fubar/a" "t/fubar/a/" "/t/fubar/a" "/t/fubar/a/"]))

;     (testing "a root category with a few items"
;       (category-browse (items x y z) c ["t/bar" "t/bar/" "/t/bar" "/t/bar/"]))

;     (testing "a parent category with a few items"
;       (category-browse (items e x y z) c ["t/fubar" "t/fubar/" "/t/fubar" "/t/fubar/"]))

;     (testing "a leaf category with a few items"
;       (category-browse (items x y z) c ["food/fruit/pear" "food/fruit/pear/" "/food/fruit/pear" "/food/fruit/pear/"]))))