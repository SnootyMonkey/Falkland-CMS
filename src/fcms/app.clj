(ns fcms.app
  (:require [liberator.core :refer [resource defresource]]
            [liberator.dev :refer (wrap-trace)]
            [compojure.core :refer (defroutes ANY)]
            [ring.adapter.jetty :as ring]
            [fcms.api.collections :as collections]
            [fcms.api.items :as items]
            [fcms.db.views :as db-views]
            [compojure.route :as route]))

(def env 
  "Global Environment: ENV=debug/dev/prod (dev is default)"
  (or (System/getenv "ENV") "dev"))

(defroutes routes
  collections/collection-routes
  items/item-routes
  (route/resources "/"))

(def trace-app
  (-> routes (wrap-trace :header :ui)))

(def app
  (if (= env "debug") trace-app routes))

(defn start [port]
  (db-views/init) ; make sure DB is created and has latest views
  (ring/run-jetty app {:port port :join? false}))

(defn -main []
  (let [port (Integer/parseInt
       (or (System/getenv "PORT") "3000"))]
  (start port)))