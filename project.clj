(defproject falkland-cms "0.2.0-SNAPSHOT"
  :description "Falkland CMS is a Curation Management System written in Clojure, ClojureScript and CouchDB."
  :url "http://falkland-cms.com/"
  :license {:name "Mozilla Public License v2.0"
            :url "http://www.mozilla.org/MPL/2.0/"}
  
  :min-lein-version "2.2" ;; highest version supported by Travis-CI as of 9/20/13

  :dependencies [
    [org.clojure/clojure "1.5.1"] ; Lisp on the JVM http://clojure.org/documentation
    [org.clojure/core.incubator "0.1.3"] ; Functions proposed for inclusion in Clojure https://github.com/clojure/core.incubator
    [org.clojure/core.match "0.2.0"] ; Erlang-esque pattern matching https://github.com/clojure/core.match
    [org.clojure/clojurescript "0.0-2080"] ; ClojureScript compiler https://github.com/clojure/clojurescript
    [org.clojure/tools.nrepl "0.2.3"] ; REPL server and client https://github.com/clojure/tools.nrepl
    [cheshire "5.2.0"] ; JSON de/encoding https://github.com/dakrone/cheshire
    [org.flatland/ordered "1.5.2"] ; Ordered hash map https://github.com/flatland/ordered
    [ring/ring-jetty-adapter "1.2.1"] ; Web Server https://github.com/ring-clojure/ring
    [compojure "1.1.6"] ; Web routing https://github.com/weavejester/compojure
    [liberator "0.10.0"] ; WebMachine (REST state machine) port to Clojure https://github.com/clojure-liberator/liberator
    [com.ashafa/clutch "0.4.0-RC1"] ; CouchDB client https://github.com/clojure-clutch/clutch
    [clojurewerkz/elastisch "1.3.0-rc2"] ; Client for ElasticSearch https://github.com/clojurewerkz/elastisch
    [environ "0.4.0"] ; Get environment settings from different sources https://github.com/weavejester/environ
    [com.taoensso/timbre "3.0.0-RC2"] ; Logging https://github.com/ptaoussanis/timbre
  ]
  
  :profiles {
    :qa {
      :env {
        :db-name "falklandcms-test"
        :liberator-trace false
      }
      :dependencies [
        [midje "1.6.0"] ; Example-based testing https://github.com/marick/Midje
        [ring-mock "0.1.5"] ; Test Ring requests https://github.com/weavejester/ring-mock
        [speclj "2.8.1"] ; BDD testing https://github.com/slagyr/speclj
      ]
    }

    :dev [:qa {
      :env ^:replace {
        :db-name "falklandcms"
        :liberator-trace true
      }
      :dependencies [
        [print-foo "0.4.6"] ; Old school print debugging https://github.com/danielribeiro/print-foo
        [org.clojure/tools.trace "0.7.6"] ; Tracing macros/fns https://github.com/clojure/tools.trace
        [com.cemerick/piggieback "0.1.2"] ; ClojureScript bREPL from the nREPL https://github.com/cemerick/piggieback
        [clj-ns-browser "1.3.1"] ; Doc browser https://github.com/franks42/clj-ns-browser
      ]
      ; REPL injections
      :injections [
        (require '[clojure.pprint :refer :all]
                 '[clojure.stacktrace :refer (print-stack-trace)]
                 '[clojure.test :refer :all]
                 '[print.foo :refer :all]
                 '[clj-time.format :as t]
                 '[clojure.string :as s]
                 '[cljs.repl.browser :as b-repl]
                 '[cemerick.piggieback :as pb])
        (defn brepl [] (pb/cljs-repl :repl-env (b-repl/repl-env :port 9000)))
      ]
    }]

    :prod {
      :env {
        :db-name "falklandcms"
        :liberator-trace false
      }
    }
  }

  :aliases {
    "init-db" ["run" "-m" "fcms.db.views"] ; create CouchDB views
    "init-test-db" ["with-profile" "qa" "run" "-m" "fcms.db.views"] ; create CouchDB views for test DB
    ;"build" ["do" "clean," "deps," "git-deps,", "compile," "init-db"] ; clean and build
    "build" ["do" "clean," "deps," "compile," "init-db"] ; clean and build
    "test" ["with-profile" "qa" "do" "test"] ; run unit tests
    "midje" ["with-profile" "qa" "midje"] ; run unit tests
    "spec" ["with-profile" "qa" "spec"] ; run integration tests
    "cucumber" ["with-profile" "qa" "cucumber"] ; run integration tests
    "test-all" ["with-profile" "qa" "do" "midje," "test," "cucumber"] ; run all tests
    "test-all!" ["with-profile" "qa" "do" "build,", "test-all"] ; clean and build and run all tests
    "start" ["do" "build," "ring" "server-headless"] ; start an FCMS server
    "start!" ["with-profile" "prod" "run"] ; start an FCMS server in production
    "spell" ["spell" "-n"] ; check spelling in docs and docstrings
    "ancient" ["with-profile" "dev" "do" "ancient" ":allow-qualified," "ancient" ":plugins" ":allow-qualified"] ; check for out of date dependencies
  }

  :plugins [
    [lein-ring "0.8.8"] ; common ring tasks https://github.com/weavejester/lein-ring
    [lein-environ "0.4.0"] ; Get environment settings from different sources https://github.com/weavejester/environ
    ;[lein-git-deps "0.0.1-SNAPSHOT"] ; dependencies from GitHub https://github.com/tobyhede/lein-git-deps
    [lein-cljsbuild "1.0.0"] ; ClojureScript compiler https://github.com/emezeske/lein-cljsbuild
    [lein-cucumber "1.0.2"] ; BDD testing https://github.com/nilswloka/lein-cucumber
    [speclj "2.8.1"] ; BDD testing https://github.com/slagyr/speclj
    [codox "0.6.6"] ; Generate Clojrue API docs https://github.com/weavejester/codox
    [lein-midje "3.1.3"] ; Example-based testing https://github.com/marick/lein-midje
    [lein-bikeshed "0.1.3"] ; Check for code smells https://github.com/dakrone/lein-bikeshed
    [lein-kibit "0.0.8"] ; Static code search for non-idiomatic code https://github.com/jonase/kibit
    [jonase/eastwood "0.0.2"] ; Clojure linter https://github.com/jonase/eastwood
    [lein-checkall "0.1.1"] ; Runs bikeshed, kibit and eastwood https://github.com/itang/lein-checkall
    [lein-pprint "1.1.1"] ; pretty-print the lein project map https://github.com/technomancy/leiningen/tree/master/lein-pprint
    [lein-ancient "0.5.4"] ; Check for outdated dependencies https://github.com/xsc/lein-ancient
    [lein-spell "0.1.0"] ; Catch spelling mistakes in docs and docstrings https://github.com/cldwalker/lein-spell
  ]

  ;; ----- Run libraries from source -----

  ; :git-dependencies [
  ;   ["https://github.com/clojure-liberator/liberator.git"] ; WebMachine (REST state machine) port to Clojure https://github.com/clojure-liberator/liberator
  ; ]

  ; :source-paths [
  ;   ".lein-git-deps/liberator/src/"
  ;   "src/"
  ; ]

  ;; ----- Location of tests -----

  :cucumber-feature-paths ["test/fcms/features"]
  :test-paths ["test"]

  ;; ----- Code check configuration -----

  :eastwood {:exclude-linters [:keyword-typos]}

  ;; ----- Clojure API Documentation -----

  :codox {
    :sources ["src/fcms/resources"]
    :exclude [fcms.resources.collection-resource]
    :output-dir "../Falkland-CMS-docs/API/Clojure"
    :src-dir-uri "http://github.com/SnootyMonkey/Falkland-CMS/blob/master/"
    :src-linenum-anchor-prefix "L" ; for Github
  }

  ;; ----- ClojureScript -----

  :repl-options {:nrepl-middleware [cemerick.piggieback/wrap-cljs-repl]}

  :cljsbuild {
    :crossovers [] ; compile for both Clojure and ClojureScript
    :builds {
      :dev {
        :source-paths ["src/fcms/cljs" "src/brepl"] ; CLJS source code path
        ;; Google Closure (CLS) options configuration
        :compiler {
          :output-to "resources/public/js/fcms.js"
          :optimizations :whitespace
          :pretty-print true
        }
      }
      :prod {
        :source-paths ["src/fcms/cljs"] ; CLJS source code path
        ;; Google Closure (CLS) options configuration
        :compiler {
          :output-to "resources/public/js/fcms.js"
          :optimizations :advanced
          :pretty-print false
        }
      }
    }
  }

  ;; ----- Web Application -----

  :ring {
    :handler fcms.app/app
    :init fcms.db.views/init
  }

  :main fcms.app
)