(ns fcms.app
  (:require [liberator.core :refer [resource defresource]]
            [compojure.core :refer (defroutes ANY)]
            [ring.adapter.jetty :as ring]
            [fcms.api.collections :as collections]
            [fcms.api.items :as items]
            [fcms.db.views :as db-views]
            [compojure.route :as route]))

(defroutes app
  collections/collection-routes
  items/item-routes
  (route/resources "/"))

(defn start [port]
  (db-views/init) ; make sure DB is created and has latest views
  (ring/run-jetty app {:port port :join? false}))

(defn -main []
  (let [port (Integer/parseInt
       (or (System/getenv "PORT") "3000"))]
  (start port)))