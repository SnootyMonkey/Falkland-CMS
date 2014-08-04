(defproject falkland-cms-docs "0.2.0-SNAPSHOT"
  :description "Documentation for the Falkland CMS is a Content/Collection Management System written in Clojure, ClojureScript and CouchDB."
  :url "http://falkland-cms.com/"
  :license {:name "Mozilla Public License v2.0"
            :url "http://www.mozilla.org/MPL/2.0/"}
  
  :min-lein-version "2.2" ;; highest version supported by Travis-CI as of 9/20/13

  :dependencies [
    [org.clojure/clojure "1.5.1"] ; Lisp on the JVM http://clojure.org/documentation
  ]
  
  :aliases {
    "spell" ["spell" "-n"] ; check spelling in docs and docstrings
    "ancient" ["with-profile" "dev" "do" "ancient" ":allow-qualified," "ancient" ":plugins" ":allow-qualified"] ; check for out of date dependencies
  }

  :plugins [
    [lein-sphinx "1.0.1"] ; Build Sphinx documentation https://github.com/SnootyMonkey/lein-sphinx
    [lein-spell "0.1.0"] ; Catch spelling mistakes in docs and docstrings https://github.com/cldwalker/lein-spell
    [lein-ancient "0.5.5"] ; Check for outdated dependencies https://github.com/xsc/lein-ancient
  ]

  ;; ----- REST API Documentation -----

  :sphinx {
    :source "API/REST"
  }

)