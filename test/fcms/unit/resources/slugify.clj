(ns fcms.unit.resources.slugify
  (:use [midje.sweet])
  (:require [fcms.resources.common :refer (slugify)]))

(fact "upper case letters are replaced with lower case letters"
  (slugify "slug" identity) => "slug"
  (slugify "Slug" identity) => "slug"
  (slugify "SLUG" identity) => "slug"
  (slugify "sLUG" identity) => "slug"
  (slugify "sluG" identity) => "slug"
  (slugify "sLuG" identity) => "slug")

(fact "white space is replaced with a single dash"
  (slugify "this is a slug" identity) => "this-is-a-slug"
  (slugify "this is-a slug" identity) => "this-is-a-slug"
  (slugify "this  is  a  slug" identity) => "this-is-a-slug"
  (slugify "this is a          slug" identity) => "this-is-a-slug"
  (slugify "this\t\tis\r\ralso\n\na\r\n\r\nslug" identity) => "this-is-also-a-slug")

(fact "prefixed and trailing spaces are ignored"
  (slugify " this is a slug" identity) => "this-is-a-slug"
  (slugify " this is a slug" identity) => "this-is-a-slug"
  (slugify "this is a slug " identity) => "this-is-a-slug"
  (slugify " this is a slug " identity) => "this-is-a-slug"
  (slugify "          this is a slug          " identity) => "this-is-a-slug"
  (slugify "\t\tthis is a slug\t\t\r\n\r\n" identity) => "this-is-a-slug")

(fact "punctuation is replaced with a dash")

(fact "sequential dashes are replaced with a single dash")

(fact "accented latin characters are replaced with ascii")

(fact "unicode characters are replaced with ")

(fact "slug collisions are detected and resolved by a function")

(fact "perfectly good slugs are unaffected"
  (slugify "slug" identity) => "slug"
  (slugify "42" identity) => "42"
  (slugify "slug42" identity) => "slug42"
  (slugify "slug-42" identity) => "slug-42"
  (slugify "this-is-a-slug-42" identity) => "this-is-a-slug-42")