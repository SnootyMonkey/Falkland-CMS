(ns fcms.app
  "Namespace for the FCMS web application which serves the REST API."
  (:require
    [liberator.dev :refer (wrap-trace)]
    [ring.middleware.reload :as reload]
    [compojure.core :refer (defroutes ANY)]
    [org.httpkit.server :refer (run-server)]
    [fcms.config :refer (liberator-trace hot-reload web-server-port)]
    [fcms.api.collections :refer (collection-routes)]
    [fcms.api.items :refer (item-routes)]
    [compojure.route :as route]))

(defroutes routes
  collection-routes
  item-routes
  (route/resources "/media/"))

(def trace-app
  (if liberator-trace
    (wrap-trace routes :header :ui)
    routes))

(def app
  (if hot-reload
    (reload/wrap-reload trace-app)
    trace-app))

(defn start [port]
  (run-server app {:port port :join? false})
  (println (str "\nFCMS: Server running on port - " port ", hot-reload - " hot-reload)))

(defn -main []
  (start web-server-port))