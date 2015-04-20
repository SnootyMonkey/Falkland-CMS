(ns fcms.lib.check
  "Namespace for utility functions used in tests to check that things are as expected."
  (:require [clojure.test :refer (is)]
            [clj-time.core :refer (now before? after? ago seconds within?)]
            [clj-time.format :refer (parse)]))

(defmacro check [forms]
  `(assert (is ~forms)))

(defn- time-for [timestamp]
  (if (instance? org.joda.time.DateTime timestamp)
    timestamp
    (parse timestamp)))

(defn timestamp?
  "True if the argument is a joda Date/Time instance or can be parsed into one."
  [timestamp]
  (or (instance? org.joda.time.DateTime timestamp)
      (instance? org.joda.time.DateTime (parse timestamp))))

(defn about-now?
  "True if the argument is a joda Date/Time instance for a time within the last 10 seconds."
  [timestamp]
  (if (timestamp? timestamp)
    (let [compare-time (time-for timestamp)]
      (within? (-> 10 seconds ago) (now) compare-time))
    false))