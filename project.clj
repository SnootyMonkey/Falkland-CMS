(defproject falkland-cms "0.1.0-SNAPSHOT"
  :description "Falkland CMS is a Content/Collection Management System written in Clojure, ClojureScript and CouchDB."
  :url "https://github.com/SnootyMonkey/Falkland-CMS/"
  :license {:name "MIT License"
            :url "http://opensource.org/licenses/MIT"}
  :dependencies [
    [org.clojure/clojure "1.5.0"] ; Lisp on the JVM http://clojure.org/documentation
    [ring/ring-jetty-adapter "1.1.8"] ; Web Server https://github.com/ring-clojure/ring
    [compojure "1.1.5"] ; Web routing https://github.com/weavejester/compojure
    [liberator "0.9.0"] ; WebMachine (REST state machine) port to Clojure https://github.com/clojure-liberator/liberator
    [ibdknox/clojurescript "0.0-1534"] ; ClojureScript compiler and runtime https://github.com/clojure/clojurescript
    [com.ashafa/clutch "0.4.0-RC1"] ; CouchDB client https://github.com/clojure-clutch/clutch
    [clj-json "0.5.3"] ; JSON de/encoding https://github.com/mmcgrana/clj-json/
    [ring-mock "0.1.5"] ; Test Ring requests https://github.com/weavejester/ring-mock
  ]
  :plugins [
  	[lein-ring "0.8.3"] ; common ring tasks https://github.com/weavejester/lein-ring
    [lein-cucumber "1.0.2"] ; cucumber-jvm (BDD testing) tasks https://github.com/nilswloka/lein-cucumber
    [lein-cljsbuild "0.3.2"] ; ClojureScript compiler https://github.com/emezeske/lein-cljsbuild
  ]
  :cljsbuild {
    :crossovers [] ; compile for both Clojure and ClojureScript
    :builds
      [{
      :source-paths ["src/fcms/cljs" "src"] ; CLJS source code path
      ;; Google Closure (CLS) options configuration
      :compiler {
        :output-to "resources/public/js/fcms.js" ; generated JS script filename
        :optimizations :simple ; JS optimization directive
        :pretty-print false ; generated JS code prettyfication
      }}]
  }
  :ring {:handler fcms.app/app}
  :min-lein-version "2.0.0"
  :main fcms.app)