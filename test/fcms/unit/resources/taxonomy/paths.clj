(ns fcms.unit.resources.taxonomy.paths
  (:require [midje.sweet :refer :all]
            [midje.util :refer (expose-testables)]))

(expose-testables fcms.resources.taxonomy)

(def tax "tax")

(tabular (facts "about taxonomy slug parsing from a category path"
  (taxonomy-slug-from-path ?category-path) => tax)
  ?category-path
  "tax"
  "tax/"
  "/tax"
  "/tax/"
  "tax/a"
  "tax/a/"
  "/tax/a"
  "/tax/a/"
  "tax/a/b"
  "tax/a/b/"
  "/tax/a/b"
  "/tax/a/b/")

(tabular (facts "about category slug parsing from a category path"
  (category-slugs-from-path ?category-path) => ?category-slugs)
  ?category-path        ?category-slugs
  "tax"                 []
  "tax/"                []
  "/tax"                []
  "/tax/"               []
  "tax/a"               ["a"]
  "tax/a/"              ["a"]
  "/tax/a"              ["a"]
  "/tax/a/"             ["a"]
  "tax/a/b"             ["a" "b"]
  "tax/a/b/"            ["a" "b"]
  "/tax/a/b"            ["a" "b"]
  "/tax/a/b/"           ["a" "b"]
  "tax/a/b/c-d-e/f/g"   ["a" "b" "c-d-e" "f" "g"]
  "tax/a/b/c-d-e/f/g/"  ["a" "b" "c-d-e" "f" "g"]
  "/tax/a/b/c-d-e/f/g"  ["a" "b" "c-d-e" "f" "g"]
  "/tax/a/b/c-d-e/f/g/" ["a" "b" "c-d-e" "f" "g"])