(ns fcms.unit.resources.taxonomy.category-existence
  (:require [midje.sweet :refer :all]
            [fcms.lib.resources :refer :all]
            [fcms.resources.collection :as collection]
            [fcms.resources.taxonomy :refer (category-exists)]))

(with-state-changes [(before :facts (do (empty-collection-e) (empty-taxonomy-et) (existing-taxonomy-t)))
                     (after :facts (collection/delete-collection e))]

  (facts "about non-existent categories"
    (category-exists "not-here" "/t/foo") => :bad-collection

    (category-exists e "not-here") => :bad-taxonomy
    (category-exists e "not-here/") => :bad-taxonomy
    (category-exists e "/not-here") => :bad-taxonomy
    (category-exists e "/not-here/") => :bad-taxonomy
    (category-exists e "not-here/foo") => :bad-taxonomy
    (category-exists e "not-here/foo/") => :bad-taxonomy
    (category-exists e "/not-here/foo") => :bad-taxonomy
    (category-exists e "/not-here/foo/") => :bad-taxonomy

    (category-exists e "et") => false
    (category-exists e "et/") => false
    (category-exists e "/et") => false
    (category-exists e "/et/") => false
    (category-exists e "t") => false
    (category-exists e "t/") => false
    (category-exists e "/t") => false
    (category-exists e "/t/") => false

    (category-exists e "et/not-here") => false
    (category-exists e "et/not-here/") => false
    (category-exists e "/et/not-here") => false
    (category-exists e "/et/not-here/") => false

    (category-exists e "et/not-here/not-here") => false
    (category-exists e "et/not-here/not-here/") => false
    (category-exists e "/et/not-here/not-here") => false
    (category-exists e "/et/not-here/not-here/") => false

    (category-exists e "t/not-here") => false
    (category-exists e "t/not-here/") => false
    (category-exists e "/t/not-here") => false
    (category-exists e "/t/not-here/") => false

    (category-exists e "t/foo/not-here") => false
    (category-exists e "t/foo/not-here/") => false
    (category-exists e "/t/foo/not-here") => false
    (category-exists e "/t/foo/not-here/") => false

    (category-exists e "t/fubar/a/not-here") => false
    (category-exists e "t/fubar/a/not-here/") => false)
    (category-exists e "/t/fubar/a/not-here") => false
    (category-exists e "/t/fubar/a/not-here/") => false

  (facts "about existing categories"
    (category-exists e "t/foo") => true
    (category-exists e "t/foo/") => true
    (category-exists e "/t/foo") => true
    (category-exists e "/t/foo/") => true

    (category-exists e "t/fubar") => true
    (category-exists e "t/fubar/") => true
    (category-exists e "/t/fubar") => true
    (category-exists e "/t/fubar/") => true

    (category-exists e "t/fubar/a") => true
    (category-exists e "t/fubar/a/") => true
    (category-exists e "/t/fubar/a") => true
    (category-exists e "/t/fubar/a/") => true))