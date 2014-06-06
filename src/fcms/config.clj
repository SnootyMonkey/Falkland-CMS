(ns fcms.config
  "Namespace for the FCMS configuration parameters."
  (:require [environ.core :refer (env)]
            [com.ashafa.clutch :as clutch]))

;; ----- CouchDB config -----

(defonce db-host (or (env :db-host) "http://localhost:5984/"))
(defonce db-name (or (env :db-name) "falklandcms"))
(defonce db-user (or (env :db-user) nil))
(defonce db-password (or (env :db-password) nil))

;; ----- CouchDB connect map -----

(def db-resource (assoc (cemerick.url/url db-host db-name)
                    :username db-user
                    :password db-password))

;; ----- Liberator config -----

(def liberator-trace (or (env :liberator-trace) false))