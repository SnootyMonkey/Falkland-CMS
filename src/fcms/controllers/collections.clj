(ns fcms.controllers.collections
  (:require [compojure.core :refer (defroutes ANY)]
            [liberator.core :refer (defresource)]
            [fcms.models.collection :as collection]
            [fcms.models.collection :refer (collection-type)]
            [fcms.models.item :refer (item-type)]))

(defn show [coll-name]
  (format "The collection: %s" coll-name))

(defn index [coll-name]
  (format "Items in the collection: %s" coll-name))

(defn accept-switch [coll-name {{accept :media-type} :representation}]
  (cond
    (= accept collection-type) (show coll-name)
    (= accept item-type) (index coll-name)))

(defresource collection [coll-name]
  :available-media-types [item-type collection-type]
  :handle-ok (fn [ctx] (accept-switch coll-name ctx)))

(defroutes collection-routes
  (ANY "/:coll-name" [coll-name] (collection coll-name)))

;; {:representation
;;  {:media-type "application/vnd.fcms.item+json"},
;;  :resource
;;  {:put-to-different-url? #<core$constantly$fn__4059 clojure.core$constantly$fn__4059@181feb71>, :put! #<core$constantly$fn__4059 clojure.core$constantly$fn__4059@579a5416>, :available-charsets #<core$constantly$fn__4059 clojure.core$constantly$fn__4059@55069d48>, :allowed? #<core$constantly$fn__4059 clojure.core$constantly$fn__4059@25400d94>, :valid-content-header? #<core$constantly$fn__4059 clojure.core$constantly$fn__4059@103b82a7>, :available-languages #<core$constantly$fn__4059 clojure.core$constantly$fn__4059@32feaa44>, :conflict? #<core$constantly$fn__4059 clojure.core$constantly$fn__4059@6d5c7083>, :existed? #<core$constantly$fn__4059 clojure.core$constantly$fn__4059@4fdee08c>, :service-available? #<core$constantly$fn__4059 clojure.core$constantly$fn__4059@3168304b>, :known-methods #<core$constantly$fn__4059 clojure.core$constantly$fn__4059@2f3aa544>, :delete-enacted? #<core$constantly$fn__4059 clojure.core$constantly$fn__4059@33382b95>, :allowed-methods #<core$constantly$fn__4059 clojure.core$constantly$fn__4059@2cb55d78>, :exists? #<core$constantly$fn__4059 clojure.core$constantly$fn__4059@370b8836>, :handle-moved-temporarily #<core$handle_moved liberator.core$handle_moved@29f64de>, :handle-moved-permanently #<core$handle_moved liberator.core$handle_moved@29f64de>, :can-post-to-missing? #<core$constantly$fn__4059 clojure.core$constantly$fn__4059@561bad75>, :handle-see-other #<core$handle_moved liberator.core$handle_moved@29f64de>, :known-content-type? #<core$constantly$fn__4059 clojure.core$constantly$fn__4059@3319087d>, :malformed? #<core$constantly$fn__4059 clojure.core$constantly$fn__4059@307c60c0>, :moved-permanently? #<core$constantly$fn__4059 clojure.core$constantly$fn__4059@35b3f61e>, :post! #<core$constantly$fn__4059 clojure.core$constantly$fn__4059@39e3471d>, :multiple-representations? #<core$constantly$fn__4059 clojure.core$constantly$fn__4059@7851de9b>, :delete! #<core$constantly$fn__4059 clojure.core$constantly$fn__4059@46edcfd3>, :respond-with-entity? #<core$constantly$fn__4059 clojure.core$constantly$fn__4059@26e74a06>, :method-allowed? #<core$test_request_method$fn__2847 liberator.core$test_request_method$fn__2847@1a305adc>, :uri-too-long? #<core$constantly$fn__4059 clojure.core$constantly$fn__4059@1aace3de>, :authorized? #<core$constantly$fn__4059 clojure.core$constantly$fn__4059@4ca41180>, :new? #<core$constantly$fn__4059 clojure.core$constantly$fn__4059@277911cf>, :available-media-types #<core$constantly$fn__4059 clojure.core$constantly$fn__4059@7d804147>, :handle-ok #<collections$collection$fn__4488$fn__4489 fcms.controllers.collections$collection$fn__4488$fn__4489@6f3ddcaf>, :post-redirect? #<core$constantly$fn__4059 clojure.core$constantly$fn__4059@46639e37>, :moved-temporarily? #<core$constantly$fn__4059 clojure.core$constantly$fn__4059@362850fb>, :valid-entity-length? #<core$constantly$fn__4059 clojure.core$constantly$fn__4059@10dcaa84>, :known-method? #<core$test_request_method$fn__2847 liberator.core$test_request_method$fn__2847@3361477a>, :available-encodings #<core$constantly$fn__4059 clojure.core$constantly$fn__4059@73ec8c2>, :can-put-to-missing? #<core$constantly$fn__4059 clojure.core$constantly$fn__4059@2aee3c45>},
;;  :request
;;  {:ssl-client-cert nil, :remote-addr "0:0:0:0:0:0:0:1", :scheme :http, :request-method :get, :query-string nil,
;;   :route-params {:coll-name "mudskippers"},
;;   :content-type nil, :uri "/mudskippers",
;;   :server-name "localhost",
;;   :params {:coll-name "mudskippers"},
;;   :headers {"user-agent" "curl/7.21.4 (universal-apple-darwin11.0) libcurl/7.21.4 OpenSSL/0.9.8r zlib/1.2.5", "accept" "application/vnd.fcms.item+json", "host" "localhost:3000"},
;;   :content-length nil, :server-port 3000, :character-encoding nil, :body #<HttpInput org.eclipse.jetty.server.HttpInput@7eb6ec07>},
;;  :status 200,
;;  :message "OK"}