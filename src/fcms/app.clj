(ns fcms.app
  (:require [liberator.core :refer [resource defresource]]
            [liberator.dev :refer (wrap-trace)]
            [compojure.core :refer (defroutes ANY)]
            [ring.adapter.jetty :as ring]
            [fcms.config :refer (lib-trace)]
            [fcms.api.collections :refer (collection-routes)]
            [fcms.api.items :refer (item-routes)]
            [fcms.db.views :as db-views]
            [compojure.route :as route]))

(defroutes routes
  collection-routes
  item-routes
  (route/resources "/"))

(def trace-app
  (-> routes (wrap-trace :header :ui)))

(def app
  (if lib-trace trace-app routes))

(defn start [port]
  (db-views/init) ; make sure DB is created and has latest views
  (ring/run-jetty app {:port port :join? false}))

(defn -main []
  (let [port (Integer/parseInt
       (or (System/getenv "PORT") "3000"))]
  (start port)))