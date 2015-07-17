;; productive set of development namespaces (Clojure API)
(require '[cheshire.core :as json])
(require '[com.ashafa.clutch :as clutch])
(require '[fcms.config :as config] :reload)
(require '[fcms.resources.common :as common] :reload)
(require '[fcms.resources.collection-resource :as resource] :reload)
(require '[fcms.resources.collection :as collection] :reload)
(require '[fcms.resources.item :as item] :reload)
(require '[fcms.resources.taxonomy :as taxonomy] :reload)

;; productive set of development namespaces (REST API)
(require '[ring.mock.request :refer (request body content-type header)])
(require '[fcms.lib.rest-api-mock :refer (mime-type api-request)] :reload)
(require '[fcms.app :refer (app)] :reload-all)

;; make a REST API request
(api-request :get "c/i" {:headers {:Accept (mime-type :item)}})

;; print last exception
(print-stack-trace *e)

;; run unit tests on a specific namespace
(require '[fcms.unit.resources.item] :reload-all)
-or-
(require '[fcms.unit.resources.item] :reload)

(require '[fcms.integration.api.item.item-create] :reload)
-or-
(require '[fcms.integration.api.item.item-create] :reload-all)

;; auto-run tests in the repl
(use 'midje.repl)
(autotest)
(autotest :pause)
(autotest :resume)

;; view docs
(require '[clj-ns-browser.sdoc :refer (sdoc)])
(sdoc)

;; connect to a loaded cljs page in the browser
lein start
open http://localhost:3000/html/test.html
(brepl)