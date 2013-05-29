(ns fcms.app
  (:require [liberator.core :refer [resource defresource]]
            [compojure.core :refer (defroutes ANY)]
            [ring.adapter.jetty :as ring]
            [fcms.controllers.collections :as collections]
            [fcms.controllers.items :as items]
            [compojure.route :as route]))

(defroutes app
  collections/collection-routes
  items/item-routes
  (route/resources "/"))

(defn start [port]
  (ring/run-jetty app {:port port :join? false}))

(defn -main []
  (let [port (Integer/parseInt
       (or (System/getenv "PORT") "8000"))]
  (start port)))