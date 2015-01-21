(defproject falkland-cms "0.3.0-SNAPSHOT"
  :description "Falkland CMS is a Curation Management System written in Clojure, ClojureScript and CouchDB."
  :url "http://falkland-cms.com/"
  :license {
    :name "Mozilla Public License v2.0"
    :url "http://www.mozilla.org/MPL/2.0/"
  }
  :support {
    :name "Sean Johnson"
    :email "sean@snootymonkey.com"
  }
  
  :min-lein-version "2.5.0" ; highest version supported by Travis-CI as of 10/28/2014

  :dependencies [
    ;; Server-side
    [org.clojure/clojure "1.7.0-alpha5"] ; Lisp on the JVM http://clojure.org/documentation
    [org.clojure/core.match "0.3.0-alpha4"] ; Erlang-esque pattern matching https://github.com/clojure/core.match
    [defun "0.2.0-RC"] ; Erlang-esque pattern matching for Clojure functions https://github.com/killme2008/defun
    [org.clojure/core.incubator "0.1.3"] ; Functions proposed for inclusion in Clojure https://github.com/clojure/core.incubator
    [org.clojure/tools.nrepl "0.2.7"] ; REPL server and client https://github.com/clojure/tools.nrepl
    [cheshire "5.4.0"] ; JSON de/encoding https://github.com/dakrone/cheshire
    [org.flatland/ordered "1.5.2"] ; Ordered hash map https://github.com/flatland/ordered
    [ring/ring-devel "1.3.2"] ; Web application library https://github.com/ring-clojure/ring
    [ring/ring-core "1.3.2"] ; Web application library https://github.com/ring-clojure/ring
    [http-kit "2.1.19"] ; Web Server http://http-kit.org/
    [compojure "1.3.1"] ; Web routing https://github.com/weavejester/compojure
    [liberator "0.12.2"] ; WebMachine (REST API server) port to Clojure https://github.com/clojure-liberator/liberator
    [com.ashafa/clutch "0.4.0"] ; CouchDB client https://github.com/clojure-clutch/clutch
    [clojurewerkz/elastisch "2.1.0"] ; Client for ElasticSearch https://github.com/clojurewerkz/elastisch
    [environ "1.0.0"] ; Get environment settings from different sources https://github.com/weavejester/environ
    [com.taoensso/timbre "3.3.1-1cd4b70"] ; Logging https://github.com/ptaoussanis/timbre
    ;; Web Client-side
    [org.clojure/clojurescript "0.0-2665"] ; ClojureScript compiler https://github.com/clojure/clojurescript
    [secretary "2.0.0.1-5a007a"] ; Client-side routing for ClojureScript https://github.com/gf3/secretary
  ]
  
  :profiles {
    :qa {
      :env {
        :db-name "falklandcms-test"
        :liberator-trace false
        :hot-reload false
      }
      :dependencies [
        [midje "1.7.0-SNAPSHOT"] ; Example-based testing https://github.com/marick/Midje
        [ring-mock "0.1.5"] ; Test Ring requests https://github.com/weavejester/ring-mock
      ]
    }

    :dev [:qa {
      :env ^:replace {
        :db-name "falklandcms-dev" ; CouchDB database name
        :liberator-trace true ; liberator debug data in HTTP response headers
        :hot-reload true ; reload code when changed on the file system
      }
      :dependencies [
        [print-foo "1.0.1"] ; Old school print debugging https://github.com/danielribeiro/print-foo
        [aprint "0.1.1"] ; Pretty printing in the REPL (aprint thing) https://github.com/razum2um/aprint
        [org.clojure/tools.trace "0.7.8"] ; Tracing macros/fns https://github.com/clojure/tools.trace
        [com.cemerick/piggieback "0.1.3"] ; ClojureScript bREPL from the nREPL https://github.com/cemerick/piggieback
      ]
      ;; REPL injections
      :injections [
        (require '[aprint.core :refer (aprint ap)]
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
        :db-name "falklandcms" ; CouchDB database name
        :liberator-trace false
        :hot-reload false
      }
    }
  }

  :aliases {
    "init-db" ["run" "-m" "fcms.db.views"] ; create CouchDB views
    "init-test-db" ["with-profile" "qa" "run" "-m" "fcms.db.views"] ; create CouchDB views for test DB
    "clean-test-db" ["with-profile" "qa" "run" "-m" "fcms.db.clean"] ; clean the CouchDB test DB
    "build" ["do" "clean," "deps," "compile," "init-db"] ; clean and build code
    "midje" ["with-profile" "qa" "midje"] ; run all tests
    "test" ["with-profile" "qa" "do" "clean-test-db," "midje," "clean-test-db"] ; run all tests with clean test DB
    "test!" ["with-profile" "qa" "do" "build," "test"] ; build and run all tests
    "start" ["do" "build," "run"] ; start a development FCMS server
    "start!" ["with-profile" "prod" "do" "init-db," "run"] ; start an FCMS server in production
    "spell!" ["spell" "-n"] ; check spelling in docs and docstrings
    "bikeshed!" ["bikeshed" "-v" "-m" "120"] ; code check with max line length warning of 120 characters
    "ancient" ["with-profile" "dev" "do" "ancient" ":allow-qualified," "ancient" ":plugins" ":allow-qualified"] ; check for out of date dependencies
  }

  :plugins [
    [lein-ring "0.9.1"] ; common ring tasks https://github.com/weavejester/lein-ring
    [lein-environ "1.0.0"] ; Get environment settings from different sources https://github.com/weavejester/environ
    [lein-cljsbuild "1.0.4"] ; ClojureScript compiler https://github.com/emezeske/lein-cljsbuild
    [codox "0.8.10"] ; Generate Clojure API docs https://github.com/weavejester/codox
    [lein-midje "3.1.3"] ; Example-based testing https://github.com/marick/lein-midje
    [lein-bikeshed "0.2.0"] ; Check for code smells https://github.com/dakrone/lein-bikeshed
    [lein-kibit "0.0.8"] ; Static code search for non-idiomatic code https://github.com/jonase/kibit
    [jonase/eastwood "0.2.1"] ; Clojure linter https://github.com/jonase/eastwood
    [lein-checkall "0.1.1"] ; Runs bikeshed, kibit and eastwood https://github.com/itang/lein-checkall
    [lein-pprint "1.1.2"] ; pretty-print the lein project map https://github.com/technomancy/leiningen/tree/master/lein-pprint
    [lein-ancient "0.6.1"] ; Check for outdated dependencies https://github.com/xsc/lein-ancient
    [lein-spell "0.1.0"] ; Catch spelling mistakes in docs and docstrings https://github.com/cldwalker/lein-spell
    [lein-deps-tree "0.1.2"] ; Print a tree of project dependencies https://github.com/the-kenny/lein-deps-tree
  ]

  ;; ----- Code check configuration -----

  :eastwood {
    ;; Enable some linters that are disabled by default
    :add-linters [:unused-namespaces :unused-private-vars :unused-locals]

    ;; More extensive lintering that will have a few false positives
    ;; :add-linters [:unused-namespaces :unused-private-vars :unused-locals :unused-fn-args]

    ;; Exclude testing namespaces
    :tests-paths ["test"]
    :exclude-namespaces [:test-paths]
  }

  ;; ----- Clojure API Documentation -----

  :codox {
    :include [fcms.resources.common fcms.resources.collection fcms.resources.item fcms.resources.taxonomy]
    :output-dir "../Falkland-CMS-docs/API/Clojure"
    :src-dir-uri "http://github.com/SnootyMonkey/Falkland-CMS/blob/master/"
    :src-linenum-anchor-prefix "L" ; for Github
    :defaults {:doc/format :markdown}
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
    :reload-paths ["src"] ; work around issue https://github.com/weavejester/lein-ring/issues/68
  }

  :main fcms.app
)