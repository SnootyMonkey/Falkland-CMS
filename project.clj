(defproject falkland-cms "0.1.0-SNAPSHOT"
  :description "Falkland CMS is a Content/Collection Management System written in Clojure, ClojureScript and CouchDB."
  :url "https://github.com/SnootyMonkey/Falkland-CMS/"
  :license {:name "MIT License"
            :url "http://opensource.org/licenses/MIT"}
  :dependencies [
    [org.clojure/clojure "1.4.0"]
    [ring "1.1.8"] ; Web middleware
    [com.ashafa/clutch "0.4.0-RC1"] ; CouchDB client
  ]
  :plugins [
  	[lein-ring "0.8.3"]
  	[lein-cucumber "1.0.2"]  
  ])