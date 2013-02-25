(defproject fc "0.1.0-SNAPSHOT"
  :description "Falkland CMS is a Content/Collection Management System written in Clojure, ClojureScript and Couchbase."
  :url "https://github.com/SnootyMonkey/falkland-cms"
  :license {:name "MIT License"
            :url "http://opensource.org/licenses/MIT"}
  :dependencies [
    [org.clojure/clojure "1.4.0"]
    [ring "1.1.8"]
  ]
  :plugins [[lein-ring "0.8.3"]])