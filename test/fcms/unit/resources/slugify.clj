(ns fcms.unit.resources.slugify
  (:require [midje.sweet :refer :all]
            [fcms.lib.slugify :refer (slugify max-slug-length)]))

(facts "about making slugs"

  (fact "upper case letters are replaced with lower case letters"
    (slugify "slug") => "slug"
    (slugify "Slug") => "slug"
    (slugify "SLUG") => "slug"
    (slugify "sLUG") => "slug"
    (slugify "sluG") => "slug"
    (slugify "sLuG") => "slug")

  (fact "internal white space is replaced with a single dash"
    (slugify "this is a slug") => "this-is-a-slug"
    (slugify "this is-a slug") => "this-is-a-slug"
    (slugify "this  is  a  slug") => "this-is-a-slug"
    (slugify "this is a          slug") => "this-is-a-slug"
    (slugify "this\t\tis\r\ralso\n\na\r\n\r\nslug") => "this-is-also-a-slug")

  (fact "prefixed and trailing spaces are removed"
    (slugify " this is a slug") => "this-is-a-slug"
    (slugify " this is a slug") => "this-is-a-slug"
    (slugify "this is a slug ") => "this-is-a-slug"
    (slugify " this is a slug ") => "this-is-a-slug"
    (slugify "          this is a slug          ") => "this-is-a-slug"
    (slugify "\t\tthis is a slug\t\t\r\n\r\n") => "this-is-a-slug")

  (fact "prefixed and trailing dashes are removed"
    (slugify "-slug") => "slug"
    (slugify "slug-") => "slug"
    (slugify "-slug-") => "slug"
    (slugify "--slug--") => "slug"
    (slugify "----------slug----------") => "slug")

  (fact "sequential dashes are replaced with a single dash"
    (slugify "this--is----also---a-slug") => "this-is-also-a-slug")

  (fact "punctuation is replaced with a dash"
    ;; use !"#$%&'()*+,-./:;<=>?@[\]^_`{|}~
    (slugify "a!b\"c#d$e%f&g'h(i)j*k+l,m-n.o/p:q;r<s=t>u?v@w[x\\y]z^1_2`3{4|5}6~") =>
      "a-b-c-d-e-f-g-h-i-j-k-l-m-n-o-p-q-r-s-t-u-v-w-x-y-z-1-2-3-4-5-6")

  (fact "accented latin characters are replaced with ascii"
    ;; Works for the other letters too, but not all are tested here since then I'd
    ;; just be testing java.text.Normalizer.
    ;;
    ;; See:
    ;;   http://docs.oracle.com/javase/7/docs/api/java/text/Normalizer.html
    ;;   http://www.unicode.org/reports/tr15/tr15-23.html
    ;;
    (slugify "à-á-â-ã-ā-ă-ȧ-ä-ả-å-ǎ-ȁ-ą-ạ-ḁ-ẚ-ầ-ấ-ẫ-ẩ-ằ-ắ-ẵ-ẳ-ǡ-ǟ-ǻ-ậ-ặ") =>
      "a-a-a-a-a-a-a-a-a-a-a-a-a-a-a-a-a-a-a-a-a-a-a-a-a-a-a-a")

  (fact "other unicode characters are replaced with nothing"
    (slugify "γλώσσαthis-μουέδωσανis-ελληνικήalso-მივჰხვდეaმასჩემსაالزجاجوهذالايؤلمني.slug मैकाँचखासकताฉันกินกระจกได้לאמזיק")  => "this-is-also-a-slug"
    (slugify "æ-ǽ-ǣ-this-♜-♛-☃-✄-✈-is-→-☞-➩-⇏-⇉-also-•-✪-▼-❊-✔-a-∑-∏-∛-≃-≈-⅋-⋶-slug") => "this-is-also-a-slug")

  (fact "slugs that are too long are truncated"
    (let [long-slug (apply str (range 0 500))]
      ; default max
      (slugify long-slug) => (apply str (take max-slug-length long-slug))
      ; small max
      (slugify long-slug 10) => (apply str (take 10 long-slug))
      ; shouldn't end on a - if that's where it gets cut off for length
      (slugify (str (apply str (take (- max-slug-length 1) long-slug)) "-")) => (apply str (take (- max-slug-length 1) long-slug))
      (slugify (str (apply str (take (- max-slug-length 2) long-slug)) "--")) => (apply str (take (- max-slug-length 2) long-slug))))

  (fact "all these slug rules work together"
    ; upper lower
    ; internal white space
    ; prefixed and trailing space
    ; prefixed and trailing dashes
    ; sequential dashes
    ; punctuation
    ; accented latin
    ; unicode
    ; truncate for length
    (let [all-in-one " -tHiS #$is%?-----ελληνικήalso-მივჰხვდ-ემასჩემსაãالزجاجوه---ذالايؤلمني-slüg♜-♛--☃-✄-✈  - "
          long-string (apply str (range 0 200))]
      (slugify all-in-one) => "this-is-also-a-slug"
      (slugify (str all-in-one long-string)) => (str "this-is-also-a-slug-" (apply str (take 236 long-string)))))

  (fact "perfectly good slugs are unaffected"
    (slugify "slug") => "slug"
    (slugify "42") => "42"
    (slugify "slug42") => "slug42"
    (slugify "slug-42") => "slug-42"
    (slugify "this-is-a-slug-42") => "this-is-a-slug-42"))