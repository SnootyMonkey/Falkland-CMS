(ns fcms.app
  "Namespace for the FCMS web application which serves the REST API."
  (:require
    [liberator.core :refer [resource defresource]]
    [liberator.dev :refer (wrap-trace)]
    [compojure.core :refer (defroutes ANY)]
    [ring.adapter.jetty :as ring]
    [fcms.config :refer (liberator-trace)]
    [fcms.api.collections :refer (collection-routes)]
    [fcms.api.items :refer (item-routes)]
    [fcms.db.views :as db-views]
    [compojure.route :as route]))

(defroutes routes
  collection-routes
  item-routes
  (route/resources "/media/"))

(def trace-app
  (wrap-trace routes :header :ui))

(def app
  (if liberator-trace trace-app routes))

(defn start [port]
  (ring/run-jetty app {:port port :join? false}))

(defn -main []
  (let [port (Integer/parseInt
   (or (System/getenv "PORT") "3000"))]
  (start port)))
