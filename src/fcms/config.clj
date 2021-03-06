(ns fcms.config
  "Namespace for the FCMS configuration parameters."
  (:require [environ.core :refer (env)]
            [com.ashafa.clutch :as clutch]))

(defn- bool
  "Handle the fact that we may have 'true'/'false' strings, when we want booleans."
  [val]
  (boolean (Boolean/valueOf val)))

;; ----- CouchDB config -----

(defonce db-host (or (env :db-host) "http://localhost:5984/"))
(defonce db-name (or (env :db-name) "falklandcms-dev"))
(defonce db-user (or (env :db-user) nil))
(defonce db-password (or (env :db-password) nil))

;; ----- CouchDB connect map -----

(defonce db-resource (assoc (cemerick.url/url db-host db-name)
                      :username db-user
                      :password db-password))

;; ----- Web server config -----

(defonce hot-reload (bool (or (env :hot-reload) false)))
(defonce web-server-port (Integer/parseInt (or (env :port) "3000")))

;; ----- Liberator config -----

(defonce liberator-trace (bool (or (env :liberator-trace) false)))