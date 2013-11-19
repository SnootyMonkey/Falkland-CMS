(ns fcms.unit.resources.taxonomy.paths
  (:require [midje.sweet :refer :all]
            [midje.util :refer (expose-testables)]))

(expose-testables fcms.resources.taxonomy)

(def tax "tax")

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