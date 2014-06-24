; (ns fcms.unit.resources.taxonomy.category-validation
;   (:require [clojure.test :refer :all]
;             [midje.sweet :refer :all]
;             [fcms.resources.taxonomy :refer :all]))

; (facts "about valid and invalid category trees"

		; TODO use bad-strings
;   (fact "anything but a vector is invalid"
;     (valid-categories nil) => :invalid-structure
;     (valid-categories "") => :invalid-structure
;     (valid-categories "foo") => :invalid-structure
;     (valid-categories 42) => :invalid-structure
;     (valid-categories {}) => :invalid-structure)

		; TODO use bad-strings
;   (fact "anything in the tree that's not a map makes the tree invalid"
;     (valid-categories [nil]) => :invalid-structure
;     (valid-categories [""]) => :invalid-structure
;     (valid-categories ["foo"]) => :invalid-structure
;     (valid-categories [42]) => :invalid-structure
;     (valid-categories [[]]) => :invalid-structure
;     (valid-categories
;       [
;         {"florida" "Florida"}
;         "foo"
;         {"north-carolina" "North Carolina"}
;       ]) => :invalid-structure
;     (valid-categories 
;       [
;         {"florida" "Florida"}
;         42
;         {"north-carolina" "North Carolina"}
;       ]) => :invalid-structure
;     (valid-categories 
;       [
;         {"florida" "Florida"}
;         ["georgia" "Georgia"]
;         {"north-carolina" "North Carolina"}
;       ]) => :invalid-structure
;     (valid-categories
;       [
;         {"south-east" "South East" :categories [
;           {"florida" "Florida"}
;           "foo"
;           {"north-carolina" "North Carolina"}]}
;       ]) => :invalid-structure
;     (valid-categories
;       [
;         {"south-east" "South East" :categories [
;           {"florida" "Florida"}
;           ["georgia" "Georgia"]
;           {"north-carolina" "North Carolina"}]}
;       ]) => :invalid-structure)

;   (fact "an empty tree is valid"
;     (valid-categories []) => true)

;   (fact "a tree of just leaves is valid"
;     (valid-categories 
;       [
;         {"florida" "Florida"}
;         {"georgia" "Georgia"}
;         {"north-carolina" "North Carolina"}
;         {"south-carolina" "South Carolina"}
;         {"tennessee" "Tennessee"}
;         {"alabama" "Alabama"}
;       ]) => true)
  
;   (fact "a tree of 1 parent with no children is valid"
;     (valid-categories [{"non-continental-us" "Outiside Continental US" :categories []}]) => true)

;   (fact "a tree of 1 parent with 1 child is valid"
;     (valid-categories
;       [
;         {"south-east" "South East" :categories [
;           {"florida" "Florida"}]}
;       ]) => true)

;   (fact "a tree of 1 parent and many children is valid"
;     (valid-categories
;       [
;         {"south-east" "South East" :categories [
;           {"florida" "Florida"}
;           {"georgia" "Georgia"}
;           {"north-carolina" "North Carolina"}
;           {"south-carolina" "South Carolina"}
;           {"tennessee" "Tennessee"}
;           {"alabama" "Alabama"}]}
;       ]) => true)

;   (fact "a typical tree of many parents and many children is valid"
;     (valid-categories 
;       [
;         {"continental-us" "Continental US" :categories [
;           {"north-east" "North East"}
;           {"south-east" "South East" :categories [
;             {"florida" "Florida"}
;             {"georgia" "Georgia"}
;             {"north-carolina" "North Carolina"}
;             {"south-carolina" "South Carolina"}
;             {"tennessee" "Tennessee"}
;             {"alabama" "Alabama"}
;           ]}
;           {"midwest" "Midwest"}
;           {"south" "South"}
;           {"southwest" "Southwest"}
;           {"west" "West"}]}
;         {"non-continental-us" "Outiside Continental US" :categories []}
;       ]) => true)

  
;   (fact "a category with an invalid name makes the tree invalid"
;     (valid-categories [{"florida" ""}]) => :invalid-category-name
;     (valid-categories [{"florida" 42}]) => :invalid-category-name
;     (valid-categories [{"florida" []}]) => :invalid-category-name
;     (valid-categories [{"florida" {}}]) => :invalid-category-name
;     (valid-categories [{"florida" nil}]) => :invalid-category-name
;     (valid-categories 
;       [{"south-east" "South East" :categories [
;         {"florida" ""}]}
;       ]) => :invalid-category-name
;     (valid-categories 
;       [{"south-east" "South East" :categories [
;         {"florida" 42}]}
;       ]) => :invalid-category-name
;     (valid-categories 
;       [{"south-east" "South East" :categories [
;         {"florida" []}]}
;       ]) => :invalid-category-name
;     (valid-categories 
;       [{"south-east" "South East" :categories [
;         {"florida" {}}]}
;       ]) => :invalid-category-name
;     (valid-categories 
;       [{"south-east" "South East" :categories [
;         {"florida" nil}]}
;       ]) => :invalid-category-name
;     (valid-categories 
;       [
;         {"continental-us" "Continental US" :categories [
;           {"north-east" "North East"}
;           {"south-east" "South East" :categories [
;             {"florida" "Florida" :categories [
;               {"tampa" "Tampa" :categories [
;                 {"town-and-country" ""}]}]}]}]}
;       ]) => :invalid-category-name)

;   (fact "a category with an invalid slug makes the tree invalid"
;     (valid-categories [{"" "Florida"}]) => :invalid-category-slug
;     (valid-categories [{"flo rida" "Florida"}]) => :invalid-category-slug
;     (valid-categories [{"Florida" "Florida"}]) => :invalid-category-slug
;     (valid-categories [{"flôrida" "Florida"}]) => :invalid-category-slug
;     (valid-categories [{"floridá" "Florida"}]) => :invalid-category-slug
;     (valid-categories [{42 "Florida"}]) => :invalid-category-slug
;     (valid-categories [{[] "Florida"}]) => :invalid-category-slug
;     (valid-categories [{{} "Florida"}]) => :invalid-category-slug
;     (valid-categories [{nil "Florida"}]) => :invalid-category-slug)
;     ; TODO bad slugs down in the hierarchy

;   (fact "a category with a slug that conflicts with another category makes the tree invalid")
  
;   (fact "a category with something besides :categories as a 2nd item in the map makes the tree invalid"
;     (valid-categories [{"florida" "Florida" :category []}]) => :invalid-structure
;     (valid-categories [{"florida" "Florida" "foo" []}]) => :invalid-structure
;     (valid-categories [{"florida" "Florida" "categories" []}]) => :invalid-structure
;     (valid-categories [{"florida" "Florida" [] []}]) => :invalid-structure
;     (valid-categories [{"florida" "Florida" {} []}]) => :invalid-structure
;     (valid-categories [{"florida" "Florida" nil []}]) => :invalid-structure)
;   ; TODO bad categories down in the hierarchy

;   (fact "a category with something besides an array as the value for 'categories' makes the tree invalid"))