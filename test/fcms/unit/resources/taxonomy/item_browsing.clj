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
(def a "a")

(defn- existing-item [slug]
  (item/create-item e slug)
  (taxonomy/categorize-item e slug "t/bar")
  (taxonomy/categorize-item e slug "t/fubar/b")
  (taxonomy/categorize-item e slug "food/fruit/pear"))

(defn- existing-items []
  (doseq [item [x, y, z]] (existing-item item)))

(defn- create-single-item []
  (item/create-item e a)
  (taxonomy/categorize-item e a "t/fubar/a"))

;; ----- Helper functions -----

(defn items [coll-slug & item-slugs]
  (vec (map #(item/get-item coll-slug %) item-slugs)))

;; ----- Tests -----

(with-state-changes [(before :facts (do (empty-collection-e) (existing-taxonomy-t)))
                     (after :facts (collection/delete-collection e))]

  (facts "about item browsing failures"

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
        (taxonomy/items-for-category e category) => :bad-category)))

  (facts "about browsing items"

    (with-state-changes [(before :facts (create-single-item))]

      (fact "in a taxonomy with a single item"
        (taxonomy/items-for-taxonomy  e t) => [(item/get-item e a)])

      (fact "in an empty root category"
        (doseq [category ["t/foo" "t/foo/" "/t/foo" "/t/foo/"]]
          (taxonomy/items-for-category e category) => []))

      (fact "in a root category with a single item"
        (doseq [category ["t/fubar" "t/fubar/" "/t/fubar" "/t/fubar/"]]
          (taxonomy/items-for-category e category) => (items e a)))

      (fact "in an empty leaf category"
        (doseq [category ["t/fubar/b" "t/fubar/b/" "/t/fubar/b" "/t/fubar/b/"]]
          (taxonomy/items-for-category e category) => []))

      (fact "in a leaf category with a single item"
        (doseq [category ["t/fubar/a" "t/fubar/a/" "/t/fubar/a" "/t/fubar/a/"]]
          (taxonomy/items-for-category e category) => (items e a))))

    (with-state-changes [(before :facts (do (existing-taxonomy-t2) (existing-items)))]

      (fact "in a taxonomy with multiple items"
        (taxonomy/items-for-taxonomy e "t") => (items e x y z))

      (fact "in a root category with a few items"
        (doseq [category ["t/bar" "t/bar/" "/t/bar" "/t/bar/"]]
          (taxonomy/items-for-category e category) => (items e x y z)))

      (fact "in a parent category with a few items"
        (doseq [category ["t/fubar" "t/fubar/" "/t/fubar" "/t/fubar/"]]
          (taxonomy/items-for-category e category) => (items e x y z)))

      (fact "in a leaf category with a few items"
        (doseq [category ["food/fruit/pear" "food/fruit/pear/" "/food/fruit/pear" "/food/fruit/pear/"]]
          (taxonomy/items-for-category e category) => (items e x y z))))))