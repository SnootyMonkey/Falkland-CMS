(ns fcms.unit.resources.taxonomy.category-existence
  (:require [midje.sweet :refer :all]
            [fcms.lib.resources :refer :all]
            [fcms.resources.collection :as collection]
            [fcms.resources.taxonomy :refer (category-exists)]))

(with-state-changes [(before :facts (do (reset-collection e) (empty-taxonomy-et) (existing-taxonomy-t)))
                     (after :facts (collection/delete-collection e))]

  (facts "about non-existent categories"
    (category-exists "not-here" "/t/foo") => :bad-collection

    (fact "when the provided taxonomy doesn't exist"
      (doseq [category ["not-here" "not-here/" "/not-here" "/not-here/"]]
        (category-exists e category) => :bad-taxonomy)

      (doseq [category ["not-here/foo" "not-here/foo/" "/not-here/foo" "/not-here/foo/"]]
        (category-exists e category) => :bad-taxonomy))

    (fact "when the category is not provided"
      (doseq [category ["et" "et/" "/et" "/et/"]]
        (category-exists e category) => false)

      (doseq [category ["t" "t/" "/t" "/t/"]]
        (category-exists e category) => false))

    (fact "when the provided category doesn't exist"
      (doseq [category ["et/not-here" "et/not-here/" "/et/not-here" "/et/not-here/"]]
        (category-exists e category) => false)

      (doseq [category ["et/not-here/not-here" "et/not-here/not-here/"
                        "/et/not-here/not-here" "/et/not-here/not-here/"]]
        (category-exists e category) => false)

      (doseq [category ["t/not-here" "t/not-here/" "/t/not-here" "/t/not-here/"]]
        (category-exists e category) => false)

      (doseq [category ["t/foo/not-here" "t/foo/not-here/"
                        "/t/foo/not-here" "/t/foo/not-here/"]]
        (category-exists e category) => false)

      (doseq [category ["t/fubar/a/not-here" "t/fubar/a/not-here/"
                        "/t/fubar/a/not-here" "/t/fubar/a/not-here/"]]
        (category-exists e category) => false)))

  (facts "about existing categories"
    (doseq [category ["t/foo" "t/foo/" "/t/foo" "/t/foo/"]]
      (category-exists e category) => true)

    (doseq [category ["t/fubar" "t/fubar/" "/t/fubar" "/t/fubar/"]]
      (category-exists e category) => true)

    (doseq [category ["t/fubar/a" "t/fubar/a/" "/t/fubar/a" "/t/fubar/a/"]]
      (category-exists e category) => true)))