(ns fcms.unit.resources.category-creation
  (:require [midje.sweet :refer :all]
  			    [fcms.lib.resources :refer (c)]
            [fcms.unit.resources.taxonomy :refer (empty-collection-c existing-categories empty-taxonomy-et existing-taxonomy-t)]
            [fcms.resources.collection :as collection]
            [fcms.resources.collection-resource :as resource]
            [fcms.resources.taxonomy :refer (create-category)]))

(with-state-changes [(before :facts (do (empty-collection-c) (empty-taxonomy-et) (existing-taxonomy-t)))
                     (after :facts (collection/delete-collection c))]

  (facts "about category creation failures"

    (fact "with a bad collection"
      (create-category "not-here" "/t/new") => :bad-collection
      (create-category "not-here" "/t/new" "New") => :bad-collection)

    (fact "with a bad taxonomy"
      (create-category c "not-here/new") => :bad-taxonomy
      (create-category c "not-here/new/") => :bad-taxonomy
      (create-category c "/not-here/new") => :bad-taxonomy
      (create-category c "/not-here/new/") => :bad-taxonomy
      (create-category c "not-here/new" "New") => :bad-taxonomy
      (create-category c "not-here/new/" "New") => :bad-taxonomy
      (create-category c "/not-here/new" "New") => :bad-taxonomy
      (create-category c "/not-here/new/" "New") => :bad-taxonomy)

    (fact "with a bad name"
       (create-category c "/t/new" nil) => :invalid-category-name
       (create-category c "/t/new" "") => :invalid-category-name
       (create-category c "/t/new" 42) => :invalid-category-name)

    (fact "with a bad category slug"
      (create-category c "t/new/sl ug/new") => :invalid-category-slug
      (create-category c "t/new/sl ug/new/") => :invalid-category-slug
      (create-category c "/t/new/sl ug/new") => :invalid-category-slug
      (create-category c "/t/new/sl ug/new/") => :invalid-category-slug

      (create-category c "t/new/s!ug/new") => :invalid-category-slug
      (create-category c "t/new/s!ug/new/") => :invalid-category-slug
      (create-category c "/t/new/s!ug/new") => :invalid-category-slug
      (create-category c "/t/new/s!ug/new/") => :invalid-category-slug

      (create-category c "t/new/slüg/new") => :invalid-category-slug
      (create-category c "t/new/slüg/new/") => :invalid-category-slug
      (create-category c "/t/new/slüg/new") => :invalid-category-slug
      (create-category c "/t/new/slüg/new/") => :invalid-category-slug

      (create-category c "t/new/sluG/new") => :invalid-category-slug
      (create-category c "t/new/sluG/new/") => :invalid-category-slug
      (create-category c "/t/new/sluG/new") => :invalid-category-slug
      (create-category c "/t/new/sluG/new/") => :invalid-category-slug
      
      (create-category c "t/new/-slug/new") => :invalid-category-slug
      (create-category c "t/new/-slug/new/") => :invalid-category-slug
      (create-category c "/t/new/-slug/new") => :invalid-category-slug
      (create-category c "/t/new/-slug/new/") => :invalid-category-slug
      
      (create-category c "t/new/sl--ug/new") => :invalid-category-slug
      (create-category c "t/new/sl--ug/new/") => :invalid-category-slug
      (create-category c "/t/new/sl--ug/new") => :invalid-category-slug
      (create-category c "/t/new/sl--ug/new/") => :invalid-category-slug))


  (facts "about creating categories 1 level at a time"
    
    (fact "without a name"
      ((create-category c "et/a") :categories) => [{:slug "a" :name "a" }])

    (fact "with a name"
      ((create-category c "et/b/" "B") :categories) => [{:slug "b" :name "B"}])

    (fact "with a long unicode name"
      ((create-category c "/et/c" " -tHiS #$is%?-----ελληνικήalso-მივჰხვდემასჩემსაãالزجاجوهذالايؤلمني-slüg♜-♛-☃-✄-✈  - ") :categories) =>
        [{:slug "c" :name " -tHiS #$is%?-----ελληνικήalso-მივჰხვდემასჩემსაãالزجاجوهذالايؤلمني-slüg♜-♛-☃-✄-✈  - "}])

    (fact "nested without a name"
      (first ((create-category c "/t/foo/foo/") :categories)) => {:slug "foo" :name "Foo" :categories [{:slug "foo" :name "foo"}]})

    (fact "nested with a name"
      (first ((create-category c "t/foo/b" "B") :categories)) => {:slug "foo" :name "Foo" :categories [{:slug "b" :name "B"}]})

    (fact "nested with siblings and without a name"
      (last ((create-category c "t/fubar/foo/") :categories)) => 
        {:slug "fubar" :name "FUBAR" :categories [{:slug "a" :name "A"} {:slug "b" :name "B"} {:slug "foo" :name "foo"}]})

    (fact "nested with siblings and with a name"
      (last ((create-category c "/t/fubar/c" "C") :categories)) =>
        {:slug "fubar" :name "FUBAR" :categories [{:slug "a" :name "A"} {:slug "b" :name "B"} {:slug "c" :name "C"}]}))

  (facts "about creating categories many levels at a time"

    (fact "without a name"
      ((create-category c "/et/a/a/") :categories) => [{:slug "a" :name "a" :categories [{:slug "a" :name "a"}]}])

    (fact "with a name"
      ((create-category c "et/a/b" "B") :categories) => [{:slug "a" :name "a" :categories [{:slug "b" :name "B"}]}])

    (fact "with a long unicode name"
      ((create-category c "et/a/c/" " -tHiS #$is%?-----ελληνικήalso-მივჰხვდემასჩემსაãالزجاجوهذالايؤلمني-slüg♜-♛-☃-✄-✈  - ") :categories) =>
        [{:slug "a" :name "a" :categories [{:slug "c" :name " -tHiS #$is%?-----ελληνικήalso-მივჰხვდემასჩემსაãالزجاجوهذالايؤلمني-slüg♜-♛-☃-✄-✈  - "}]}])

    (fact "with 5 levels"
      ((create-category c "/et/a/b/c/d/e/") :categories) => 
        [{:slug "a" :name "a" :categories [{:slug "b" :name "b" :categories [{:slug "c" :name "c" :categories [{:slug "d" :name "d" :categories [{:slug "e" :name "e"}]}]}]}]}])

    (fact "nested without a name"
      (first ((create-category c "t/foo/foo/foo") :categories)) => 
        {:slug "foo" :name "Foo" :categories [{:slug "foo" :name "foo" :categories [{:slug "foo" :name "foo"}]}]})

    (fact "nested with a name"
      (first ((create-category c "t/foo/b/c/" "C") :categories)) =>
        {:slug "foo" :name "Foo" :categories [{:slug "b" :name "b" :categories [{:slug "c" :name "C"}]}]})

    (fact "nested with siblings and without a name"
      (last ((create-category c "/t/fubar/foo/foo") :categories)) => 
        {:slug "fubar" :name "FUBAR" :categories [{:slug "a" :name "A"} {:slug "b" :name "B"} {:slug "foo" :name "foo" :categories [{:slug "foo" :name "foo"}]}]})

    (fact "nested with siblings and with a name"
      (last ((create-category c "/t/fubar/c/d/" "D") :categories)) =>
        {:slug "fubar" :name "FUBAR" :categories [{:slug "a" :name "A"} {:slug "b" :name "B"} {:slug "c" :name "c" :categories [{:slug "d" :name "D"}]}]})))