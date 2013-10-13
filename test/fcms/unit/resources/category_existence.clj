(ns fcms.unit.resources.category-existence
  (:require [midje.sweet :refer :all]
            [fcms.lib.resources :refer (c)]
            [fcms.unit.resources.taxonomy :refer (empty-collection-c existing-categories empty-taxonomy-et existing-taxonomy-t)]
            [fcms.resources.collection :as collection]
            [fcms.resources.taxonomy :refer (category-exists)]))

(with-state-changes [(before :facts (do (empty-collection-c) (empty-taxonomy-et) (existing-taxonomy-t)))
                     (after :facts (collection/delete-collection c))]

  (facts "about non-existent categories"
    (category-exists "not-here" "/t/foo") => :bad-collection

    (category-exists c "not-here") => :bad-taxonomy
    (category-exists c "not-here/") => :bad-taxonomy
    (category-exists c "/not-here") => :bad-taxonomy
    (category-exists c "/not-here/") => :bad-taxonomy
    (category-exists c "not-here/foo") => :bad-taxonomy
    (category-exists c "not-here/foo/") => :bad-taxonomy
    (category-exists c "/not-here/foo") => :bad-taxonomy
    (category-exists c "/not-here/foo/") => :bad-taxonomy

    (category-exists c "et") => false
    (category-exists c "et/") => false
    (category-exists c "/et") => false
    (category-exists c "/et/") => false
    (category-exists c "t") => false
    (category-exists c "t/") => false
    (category-exists c "/t") => false
    (category-exists c "/t/") => false
    
    (category-exists c "et/not-here") => false
    (category-exists c "et/not-here/") => false
    (category-exists c "/et/not-here") => false
    (category-exists c "/et/not-here/") => false
    
    (category-exists c "et/not-here/not-here") => false
    (category-exists c "et/not-here/not-here/") => false    
    (category-exists c "/et/not-here/not-here") => false
    (category-exists c "/et/not-here/not-here/") => false
    
    (category-exists c "t/not-here") => false
    (category-exists c "t/not-here/") => false
    (category-exists c "/t/not-here") => false
    (category-exists c "/t/not-here/") => false
    
    (category-exists c "t/foo/not-here") => false
    (category-exists c "t/foo/not-here/") => false
    (category-exists c "/t/foo/not-here") => false
    (category-exists c "/t/foo/not-here/") => false
    
    (category-exists c "t/fubar/a/not-here") => false
    (category-exists c "t/fubar/a/not-here/") => false)
    (category-exists c "/t/fubar/a/not-here") => false
    (category-exists c "/t/fubar/a/not-here/") => false

  (facts "about existing categories"
    (category-exists c "t/foo") => true
    (category-exists c "t/foo/") => true
    (category-exists c "/t/foo") => true
    (category-exists c "/t/foo/") => true

    (category-exists c "t/fubar") => true
    (category-exists c "t/fubar/") => true
    (category-exists c "/t/fubar") => true
    (category-exists c "/t/fubar/") => true

    (category-exists c "t/fubar/a") => true
    (category-exists c "t/fubar/a/") => true
    (category-exists c "/t/fubar/a") => true
    (category-exists c "/t/fubar/a/") => true))