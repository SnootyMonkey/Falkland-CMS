(defproject falkland-cms "0.1.0-SNAPSHOT"
  :description "Falkland CMS is a Content/Collection Management System written in Clojure, ClojureScript and CouchDB."
  :url "https://github.com/SnootyMonkey/Falkland-CMS/"
  :license {:name "MIT License"
            :url "http://opensource.org/licenses/MIT"}
  :dependencies [
    [org.clojure/clojure "1.5.0"] ; Lisp on the JVM http://clojure.org/documentation
    [ring "1.1.8"] ; Web middleware https://github.com/ring-clojure/ring
    [ring/ring-json "0.2.0"] ; handling JSON requests and responses https://github.com/ring-clojure/ring-json
    [compojure "1.1.5"] ; Web routing https://github.com/weavejester/compojure
    [lib-noir "0.4.8"] ; utilities and helpers for ring apps https://github.com/noir-clojure/lib-noir
    [com.ashafa/clutch "0.4.0-RC1"] ; CouchDB client https://github.com/clojure-clutch/clutch
  ]
  :plugins [
  	[lein-ring "0.8.3"] ; common ring tasks https://github.com/weavejester/lein-ring
  	[lein-cucumber "1.0.2"] ; cucumber-jvm (BDD testing) tasks https://github.com/nilswloka/lein-cucumber
  ])