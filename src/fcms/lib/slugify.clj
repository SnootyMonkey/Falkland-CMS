(ns fcms.lib.slugify 
  (:require [clojure.string :as s])
  (:import [java.text Normalizer Normalizer$Form]))

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

(defn- limit [slug n]
  (apply str (take n slug)))

;; Slugify Rules:
;; trim prefixed and trailing white space
;; replace internal white space with dash
;; replace accented characters with normalized characters
;; replace any punctuation with dash
;; remove any remaining non-alpha-numberic characters
;; replace A-Z with a-z
;; replace multiple dashes with dash and dash at the beginning and end with nothing
;; limit to 256 characters
;; replace dash at the end with nothing (in case we left a - at the end by truncating)
;; call make-unique function
(defn slugify 
  ([resource-name] (slugify resource-name identity))
  ([resource-name make-unique] (slugify resource-name make-unique 256))
  ([resource-name make-unique max-length]
    (-> resource-name
      s/trim
      replace-whitespace
      normalize-characters
      replace-punctuation
      remove-non-alpha-numeric
      s/lower-case
      normalize-dashes
      (limit max-length)
      normalize-dashes
      make-unique)))