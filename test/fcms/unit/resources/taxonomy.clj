(ns fcms.unit.resources.taxonomy
  (:require [clojure.test :refer :all]
            [midje.sweet :refer :all]
            [midje.util :refer (expose-testables)]
  			    [fcms.lib.resources :refer (c verify-new-resource)]
            [fcms.resources.collection :as collection]
            [fcms.resources.collection-resource :as resource]
            [fcms.resources.taxonomy :refer :all]))

(expose-testables fcms.resources.taxonomy)

;; ----- Fixtures -----

(def tax "tax")

(defn empty-collection-c []
  (collection/delete-collection c)
  (collection/create-collection c))

(def existing-categories [
  {:slug "foo" :name "Foo"}
  {:slug "bar" :name "Bar"}
  {:slug "fubar" :name "FUBAR" :categories [
    {:slug "a" :name "A"}
    {:slug "b" :name "B"}
  ]}])

(defn empty-taxonomy-et []
  (create-taxonomy c "Empty Taxonomy" 
     {:slug "et"
      :description "Categorize it."}))

(defn existing-taxonomy-t []
  (resource/create-resource c "Taxonomy" :taxonomy [] 
    {:slug "t"
     :description "Categorize it."
     :categories existing-categories}))

;; ----- Tests -----

(facts "about taxonomy slug parsing from a category path"
  (taxonomy-slug-from-path "tax") => tax
  (taxonomy-slug-from-path "tax/") => tax
  (taxonomy-slug-from-path "/tax") => tax
  (taxonomy-slug-from-path "/tax/") => tax
  (taxonomy-slug-from-path "tax/a") => tax
  (taxonomy-slug-from-path "tax/a/") => tax
  (taxonomy-slug-from-path "/tax/a") => tax
  (taxonomy-slug-from-path "/tax/a/") => tax
  (taxonomy-slug-from-path "tax/a/b") => tax
  (taxonomy-slug-from-path "tax/a/b/") => tax
  (taxonomy-slug-from-path "/tax/a/b") => tax
  (taxonomy-slug-from-path "/tax/a/b/") => tax)

(facts "about category slug parsing from a category path"
  (category-slugs-from-path "tax") => []
  (category-slugs-from-path "tax/") => []
  (category-slugs-from-path "/tax") => []
  (category-slugs-from-path "/tax/") => []
  (category-slugs-from-path "tax/a") => ["a"]
  (category-slugs-from-path "tax/a/") => ["a"]
  (category-slugs-from-path "/tax/a") => ["a"]
  (category-slugs-from-path "/tax/a/") => ["a"]
  (category-slugs-from-path "tax/a/b") => ["a" "b"]
  (category-slugs-from-path "tax/a/b/") => ["a" "b"]
  (category-slugs-from-path "/tax/a/b") => ["a" "b"]
  (category-slugs-from-path "/tax/a/b/") => ["a" "b"]
  (category-slugs-from-path "tax/a/b/c-d-e/f/g") => ["a" "b" "c-d-e" "f" "g"]
  (category-slugs-from-path "tax/a/b/c-d-e/f/g/") => ["a" "b" "c-d-e" "f" "g"]
  (category-slugs-from-path "/tax/a/b/c-d-e/f/g") => ["a" "b" "c-d-e" "f" "g"]
  (category-slugs-from-path "/tax/a/b/c-d-e/f/g/") => ["a" "b" "c-d-e" "f" "g"])