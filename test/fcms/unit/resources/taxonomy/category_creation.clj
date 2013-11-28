(ns fcms.unit.resources.taxonomy.category-creation
  (:require [midje.sweet :refer :all]
  			    [fcms.lib.resources :refer :all]
            [fcms.resources.collection :as collection]
            [fcms.resources.collection-resource :as resource]
            [fcms.resources.taxonomy :refer (create-category)]))

(with-state-changes [(before :facts (do (empty-collection-e) (empty-taxonomy-et) (existing-taxonomy-t)))
                     (after :facts (collection/delete-collection e))]

  (facts "about category creation failures"

    (fact "with a bad collection"
      (create-category "not-here" "/t/new") => :bad-collection
      (create-category "not-here" "/t/new" "New") => :bad-collection)

    (fact "with a bad taxonomy"
      (create-category e "not-here/new") => :bad-taxonomy
      (create-category e "not-here/new/") => :bad-taxonomy
      (create-category e "/not-here/new") => :bad-taxonomy
      (create-category e "/not-here/new/") => :bad-taxonomy
      (create-category e "not-here/new" "New") => :bad-taxonomy
      (create-category e "not-here/new/" "New") => :bad-taxonomy
      (create-category e "/not-here/new" "New") => :bad-taxonomy
      (create-category e "/not-here/new/" "New") => :bad-taxonomy)

    (fact "with a bad name"
       (create-category e "/t/new" nil) => :invalid-category-name
       (create-category e "/t/new" "") => :invalid-category-name
       (create-category e "/t/new" 42) => :invalid-category-name)

    (fact "with a bad category slug"
      (create-category e "t/new/sl ug/new") => :invalid-category-slug
      (create-category e "t/new/sl ug/new/") => :invalid-category-slug
      (create-category e "/t/new/sl ug/new") => :invalid-category-slug
      (create-category e "/t/new/sl ug/new/") => :invalid-category-slug

      (create-category e "t/new/s!ug/new") => :invalid-category-slug
      (create-category e "t/new/s!ug/new/") => :invalid-category-slug
      (create-category e "/t/new/s!ug/new") => :invalid-category-slug
      (create-category e "/t/new/s!ug/new/") => :invalid-category-slug

      (create-category e "t/new/slüg/new") => :invalid-category-slug
      (create-category e "t/new/slüg/new/") => :invalid-category-slug
      (create-category e "/t/new/slüg/new") => :invalid-category-slug
      (create-category e "/t/new/slüg/new/") => :invalid-category-slug

      (create-category e "t/new/sluG/new") => :invalid-category-slug
      (create-category e "t/new/sluG/new/") => :invalid-category-slug
      (create-category e "/t/new/sluG/new") => :invalid-category-slug
      (create-category e "/t/new/sluG/new/") => :invalid-category-slug

      (create-category e "t/new/-slug/new") => :invalid-category-slug
      (create-category e "t/new/-slug/new/") => :invalid-category-slug
      (create-category e "/t/new/-slug/new") => :invalid-category-slug
      (create-category e "/t/new/-slug/new/") => :invalid-category-slug

      (create-category e "t/new/sl--ug/new") => :invalid-category-slug
      (create-category e "t/new/sl--ug/new/") => :invalid-category-slug
      (create-category e "/t/new/sl--ug/new") => :invalid-category-slug
      (create-category e "/t/new/sl--ug/new/") => :invalid-category-slug))


  (facts "about creating categories 1 level at a time"

    (fact "without a name"
      ((create-category e "et/a") :categories) => [{:slug "a" :name "a" }])

    (fact "with a name"
      ((create-category e "et/b/" "B") :categories) => [{:slug "b" :name "B"}])

    (fact "with a long unicode name"
      ((create-category e "/et/c" " -tHiS #$is%?-----ελληνικήalso-მივჰხვდემასჩემსაãالزجاجوهذالايؤلمني-slüg♜-♛-☃-✄-✈  - ") :categories) =>
        [{:slug "c" :name " -tHiS #$is%?-----ελληνικήalso-მივჰხვდემასჩემსაãالزجاجوهذالايؤلمني-slüg♜-♛-☃-✄-✈  - "}])

    (fact "nested without a name"
      (first ((create-category e "/t/foo/foo/") :categories)) => {:slug "foo" :name "Foo" :categories [{:slug "foo" :name "foo"}]})

    (fact "nested with a name"
      (first ((create-category e "t/foo/b" "B") :categories)) => {:slug "foo" :name "Foo" :categories [{:slug "b" :name "B"}]})

    (fact "nested with siblings and without a name"
      (last ((create-category e "t/fubar/foo/") :categories)) =>
        {:slug "fubar" :name "FUBAR" :categories [{:slug "a" :name "A"} {:slug "b" :name "B"} {:slug "foo" :name "foo"}]})

    (fact "nested with siblings and with a name"
      (last ((create-category e "/t/fubar/c" "C") :categories)) =>
        {:slug "fubar" :name "FUBAR" :categories [{:slug "a" :name "A"} {:slug "b" :name "B"} {:slug "c" :name "C"}]}))

  (facts "about creating categories many levels at a time"

    (fact "without a name"
      ((create-category e "/et/a/a/") :categories) => [{:slug "a" :name "a" :categories [{:slug "a" :name "a"}]}])

    (fact "with a name"
      ((create-category e "et/a/b" "B") :categories) => [{:slug "a" :name "a" :categories [{:slug "b" :name "B"}]}])

    (fact "with a long unicode name"
      ((create-category e "et/a/c/" " -tHiS #$is%?-----ελληνικήalso-მივჰხვდემასჩემსაãالزجاجوهذالايؤلمني-slüg♜-♛-☃-✄-✈  - ") :categories) =>
        [{:slug "a" :name "a" :categories [{:slug "c" :name " -tHiS #$is%?-----ελληνικήalso-მივჰხვდემასჩემსაãالزجاجوهذالايؤلمني-slüg♜-♛-☃-✄-✈  - "}]}])

    (fact "with 5 levels"
      ((create-category e "/et/a/b/c/d/e/") :categories) =>
        [{:slug "a" :name "a" :categories [{:slug "b" :name "b" :categories [{:slug "c" :name "c" :categories [{:slug "d" :name "d" :categories [{:slug "e" :name "e"}]}]}]}]}])

    (fact "nested without a name"
      (first ((create-category e "t/foo/foo/foo") :categories)) =>
        {:slug "foo" :name "Foo" :categories [{:slug "foo" :name "foo" :categories [{:slug "foo" :name "foo"}]}]})

    (fact "nested with a name"
      (first ((create-category e "t/foo/b/c/" "C") :categories)) =>
        {:slug "foo" :name "Foo" :categories [{:slug "b" :name "b" :categories [{:slug "c" :name "C"}]}]})

    (fact "nested with siblings and without a name"
      (last ((create-category e "/t/fubar/foo/foo") :categories)) =>
        {:slug "fubar" :name "FUBAR" :categories [{:slug "a" :name "A"} {:slug "b" :name "B"} {:slug "foo" :name "foo" :categories [{:slug "foo" :name "foo"}]}]})

    (fact "nested with siblings and with a name"
      (last ((create-category e "/t/fubar/c/d/" "D") :categories)) =>
        {:slug "fubar" :name "FUBAR" :categories [{:slug "a" :name "A"} {:slug "b" :name "B"} {:slug "c" :name "c" :categories [{:slug "d" :name "D"}]}]})))