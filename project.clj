(defproject falkland-cms "0.1.0-SNAPSHOT"
  :description "Falkland CMS is a Content/Collection Management System written in Clojure, ClojureScript and CouchDB."
  :url "https://github.com/SnootyMonkey/Falkland-CMS/"
  :license {:name "MIT License"
            :url "http://opensource.org/licenses/MIT"}
  :dependencies [
    [org.clojure/clojure "1.5.1"] ; Lisp on the JVM http://clojure.org/documentation
    [org.clojure/core.incubator "0.1.3"] ; functions proposed for inclusion in Clojure https://github.com/clojure/core.incubator
    [org.clojure/core.match "0.2.0-rc3"] ; Erlang-esque pattern matching https://github.com/clojure/core.match
    [ring/ring-jetty-adapter "1.2.0"] ; Web Server https://github.com/ring-clojure/ring
    [compojure "1.1.5"] ; Web routing https://github.com/weavejester/compojure
    [liberator "0.9.0"] ; WebMachine (REST state machine) port to Clojure https://github.com/clojure-liberator/liberator
    [ibdknox/clojurescript "0.0-1534"] ; ClojureScript compiler https://github.com/clojure/clojurescript
    [com.ashafa/clutch "0.4.0-RC1"] ; CouchDB client https://github.com/clojure-clutch/clutch
    [clojurewerkz/elastisch "1.1.1"] ; Client for ElasticSearch https://github.com/clojurewerkz/elastisch
    [com.taoensso/timbre "2.3.0"] ; Logging https://github.com/ptaoussanis/timbre
    [clj-json "0.5.3"] ; JSON de/encoding https://github.com/mmcgrana/clj-json/
  ]
  :profiles {
    :dev {
      :dependencies [
        [ring-mock "0.1.5"] ; Test Ring requests https://github.com/weavejester/ring-mock
        [print-foo "0.3.7"] ; Old school print debugging https://github.com/danielribeiro/print-foo
      ]
    }
  }
  :git-dependencies [
    ["https://github.com/clojure-liberator/liberator.git"] ; WebMachine (REST state machine) port to Clojure https://github.com/clojure-liberator/liberator
  ]
  :plugins [
    [lein-ancient "0.4.0"] ; Check for outdated dependencies https://github.com/xsc/lein-ancient
    [lein-git-deps "0.0.1-SNAPSHOT"] ; dependencies from GitHub https://github.com/tobyhede/lein-git-deps
  	[lein-ring "0.8.3"] ; common ring tasks https://github.com/weavejester/lein-ring
    [lein-cucumber "1.0.2"] ; BDD testing https://github.com/nilswloka/lein-cucumber
    [lein-cljsbuild "0.3.2"] ; ClojureScript compiler https://github.com/emezeske/lein-cljsbuild
    [lein-spell "0.1.0"] ; Catch spelling mistakes in docs and docstrings https://github.com/cldwalker/lein-spell
  ]
  :source-paths [
    ".lein-git-deps/liberator/src/"
    "src/"
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
  :cucumber-feature-paths ["test/fcms/features"]
  :ring {:handler fcms.app/app}
  :min-lein-version "2.0.0"
  :main fcms.app)