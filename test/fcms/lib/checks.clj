(ns fcms.lib.checks
  (:require [clojure.test :refer (is)]
            [clj-time.core :refer (now before? after? ago secs)]
            [clj-time.format :refer (parse)]))

(defmacro check [forms]
  `(assert (is ~forms)))

(defn about-now? [timestamp]
  (check
    (when-let [time (parse timestamp)]
      (and (after? time (-> 10 secs ago)) (before? time (now))))))

(defn timestamp? [timestamp]
  (check (parse timestamp)))