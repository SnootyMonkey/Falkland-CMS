(ns fcms.lib.check
  (:require [clojure.test :refer (is)]))

(defmacro check [forms]
  `(assert (is ~forms)))