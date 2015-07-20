(ns fcms.app
  "Namespace for the FCMS web application which serves the REST API."
  (:require
    [liberator.dev :refer (wrap-trace)]
    [ring.middleware.reload :as reload]
    [compojure.core :refer (defroutes ANY)]
    [org.httpkit.server :refer (run-server)]
    [fcms.config :as c]
    [fcms.api.collections :refer (collection-routes)]
    [fcms.api.items :refer (item-routes)]
    [compojure.route :as route]))

(defroutes routes
  collection-routes
  item-routes
  (route/resources "/media/"))

(def trace-app
  (if c/liberator-trace
    (wrap-trace routes :header :ui)
    routes))

(def app
  (if c/hot-reload
    (reload/wrap-reload trace-app)
    trace-app))

(defn start [port]
  (run-server app {:port port :join? false})
    (println (str "\n"
      "====================\n"
      "| Falkland CMS API |\n"
      "====================\n\n"
      "Running on port: " port "\n"
      "Database: " c/db-name "\n"
      "Hot-reload: " c/hot-reload "\n"
      "Trace: " c/liberator-trace "\n\n"
      "Ready to serve...\n")))

(defn -main []
  (start c/web-server-port))