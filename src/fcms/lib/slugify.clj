(ns fcms.lib.slugify
  (:require [clojure.string :as s])
  (:import [java.text Normalizer Normalizer$Form]))

(def max-slug-length 256)

(defn- replace-whitespace [slug]
  (s/join "-" (s/split slug #"[\p{Space}]+")))

(defn- normalize-characters [slug]
  (Normalizer/normalize slug Normalizer$Form/NFD))

(defn- replace-punctuation [slug]
  (s/replace slug #"[\p{Punct}]" "-"))

(defn- remove-non-alpha-numeric [slug]
  (s/replace slug #"[^\w^\-]+" ""))

(defn- normalize-dashes [slug]
  (s/replace (s/join "-" (s/split slug #"\-+")) #"^-+" ""))

(defn- truncate [slug n]
  (apply str (take n slug)))

;; Slugify Rules:
;; trim prefixed and trailing white space
;; replace internal white space with dash
;; replace accented characters with normalized characters
;; replace any punctuation with dash
;; remove any remaining non-alpha-numberic characters
;; replace A-Z with a-z
;; replace multiple dashes with dash and dash at the beginning and end with nothing
;; truncate
;; replace dash at the end with nothing (in case we left a - at the end by truncating)
(defn slugify
  ([resource-name] (slugify resource-name max-slug-length))
  ([resource-name max-length]
    (-> resource-name
      s/trim
      replace-whitespace
      normalize-characters
      replace-punctuation
      remove-non-alpha-numeric
      s/lower-case
      normalize-dashes
      (truncate max-length)
      normalize-dashes)))