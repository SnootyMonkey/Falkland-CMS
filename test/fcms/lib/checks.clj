(ns fcms.lib.checks
  (:require [clojure.test :refer (is)]
            [clj-time.core :refer (now before? after? ago secs)]
            [clj-time.format :refer (parse)]))

(defmacro check [forms]
  `(assert (is ~forms)))

(defmacro check-not [forms]
  `(assert (is (not ~forms))))

(defn about-now? [timestamp]
  (and (after? timestamp (-> 10 secs ago)) (before? timestamp (now))))

(defn check-about-now? [timestamp]
	(check (about-now? (parse timestamp))))

(defn check-timestamp? [timestamp]
  (check (parse timestamp)))