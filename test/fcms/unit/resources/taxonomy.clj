(ns fcms.unit.resources.taxonomy
  (:require [clojure.test :refer :all]
            [midje.sweet :refer :all]
            [fcms.resources.taxonomy :refer :all]))

(def invalid-structure [:invalid-structure structure-msg])

(facts "about valid category trees"

  (fact "anything but a vector is invalid"
    (valid-categories nil) => invalid-structure
    (valid-categories "") => invalid-structure
    (valid-categories "foo") => invalid-structure
    (valid-categories 42) => invalid-structure
    (valid-categories {}) => invalid-structure)

  (fact "anything in the tree that's not a map makes the tree invalid"
    (valid-categories [nil]) => invalid-structure
    (valid-categories [""]) => invalid-structure
    (valid-categories ["foo"]) => invalid-structure
    (valid-categories [42]) => invalid-structure
    (valid-categories [[]]) => invalid-structure
    (valid-categories 
      [
        {"florida" "Florida"}
        ["georgia" "Georgia"]
        {"north-carolina" "North Carolina"}
      ]) => invalid-structure
    (valid-categories
      [
        {"south-east" "South East" "categories" [
          {"florida" "Florida"}
          "foo"
          {"north-carolina" "North Carolina"}]}
      ]) => invalid-structure
    (valid-categories
      [
        {"south-east" "South East" "categories" [
          {"florida" "Florida"}
          ["georgia" "Georgia"]
          {"north-carolina" "North Carolina"}]}
      ]) => invalid-structure)
  
  (fact "an empty tree is valid"
    (valid-categories []) => true)

  (fact "a tree of just leaves is valid"
    (valid-categories 
      [
        {"florida" "Florida"}
        {"georgia" "Georgia"}
        {"north-carolina" "North Carolina"}
        {"south-carolina" "South Carolina"}
        {"tennessee" "Tennessee"}
        {"alabama" "Alabama"}
      ]) => true)
  
  (fact "a tree of 1 parent with no children is valid"
    (valid-categories [{"non-continental-us" "Outiside Continental US" "categories" []}]) => true)

  (fact "a tree of 1 parent with 1 child is valid"
    (valid-categories
      [
        {"south-east" "South East" "categories" [
          {"florida" "Florida"}]}
      ]) => true)

  (fact "a tree of 1 parent and many children is valid"
    (valid-categories
      [
        {"south-east" "South East" "categories" [
          {"florida" "Florida"}
          {"georgia" "Georgia"}
          {"north-carolina" "North Carolina"}
          {"south-carolina" "South Carolina"}
          {"tennessee" "Tennessee"}
          {"alabama" "Alabama"}]}
      ]) => true)

  (fact "a typical tree of many parents and many children is valid"
    (valid-categories 
      [
        {"continental-us" "Continental US" "categories" [
          {"north-east" "North East"}
          {"south-east" "South East" "categories" [
            {"florida" "Florida"}
            {"georgia" "Georgia"}
            {"north-carolina" "North Carolina"}
            {"south-carolina" "South Carolina"}
            {"tennessee" "Tennessee"}
            {"alabama" "Alabama"}
          ]}
          {"midwest" "Midwest"}
          {"south" "South"}
          {"southwest" "Southwest"}
          {"west" "West"}]}
        {"non-continental-us" "Outiside Continental US" "categories" []}
      ]) => true)

  
  (fact "a category with non-strings makes the tree invalid")
  (fact "a category with an invalid slug makes the tree invalid")
  (fact "a category with no name makes the tree invalid")
  (fact "a category with something besides 'categories' as a 2nd item in the map makes the tree invalid")
  (fact "a category with something besides an array as the value for 'categories' makes the tree invalid"))