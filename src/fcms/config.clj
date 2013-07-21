(ns fcms.config
  (:require [environ.core :refer (env)]))

(def db-host (or (env :db-host) "http://localhost:5984/"))
(def db-name (or (env :db-name) "falklandcms"))
(def db-user (or (env :db-user) nil))
(def db-password (or (env :db-password) nil))

(def db-resource (assoc (cemerick.url/url db-host db-name)
                    :username db-user
                    :password db-password))