(ns fcms.unit.resources.taxonomy.category-creation
  (:require [midje.sweet :refer :all]
  			    [fcms.lib.resources :refer :all]
            [fcms.resources.collection :as collection]
            [fcms.resources.collection-resource :as resource]
            [fcms.resources.taxonomy :refer (create-category)]))

(with-state-changes [(before :facts (do (reset-collection e) (empty-taxonomy-et) (existing-taxonomy-t)))
                     (after :facts (collection/delete-collection e))]

  (facts "about category creation failures"

    (fact "with a bad collection"
      (create-category "not-here" "/t/new") => :bad-collection
      (create-category "not-here" "/t/new" "New") => :bad-collection)

    (fact "with a bad taxonomy"
      (doseq [category ["not-here/new" "not-here/new/"
                        "/not-here/new" "/not-here/new/"]]
        (create-category e category) => :bad-taxonomy
        (create-category e category "New") => :bad-taxonomy))

    (fact "with a bad name"
      (doseq [name bad-strings]
        (create-category e "/t/new" name) => :invalid-category-name))

    (tabular (fact "with a bad category slug"
      (create-category e ?slug) => :invalid-category-slug)
      ?slug
      "t/new/sl ug/new"
      "t/new/sl ug/new/"
      "/t/new/sl ug/new"
      "/t/new/sl ug/new/"
      "t/new/s!ug/new"
      "t/new/s!ug/new/"
      "/t/new/s!ug/new"
      "/t/new/s!ug/new/"
      "t/new/slüg/new"
      "t/new/slüg/new/"
      "/t/new/slüg/new"
      "/t/new/slüg/new/"
      "t/new/sluG/new"
      "t/new/sluG/new/"
      "/t/new/sluG/new"
      "/t/new/sluG/new/"
      "t/new/-slug/new"
      "t/new/-slug/new/"
      "/t/new/-slug/new"
      "/t/new/-slug/new/"
      "t/new/sl--ug/new"
      "t/new/sl--ug/new/"
      "/t/new/sl--ug/new"
      "/t/new/sl--ug/new/"))


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
