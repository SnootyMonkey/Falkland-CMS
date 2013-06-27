(ns fcms.controllers.common
  (:require [taoensso.timbre :refer (debug info warn error fatal spy)]
            [clojure.walk :refer (keywordize-keys)]
            [clj-json.core :as json]))

(def UTF8 "UTF-8")

(def malformed true)
(def good-json false)

(defn only-accept [media-type]
  (format "Acceptable media type: %s" media-type))

(def missing-collection-response
  (liberator.representation/ring-response
    {:status 404 :body "Not a valid collection." :headers {"Content-Type" "text/plain"}}))

(defn malformed-json?
  "Read in the body param from the request as a string, parse it into JSON, make sure all the
  keys are keywords, and then return it, mapped to :data as the 2nd value in a vector,
  with the first value indicating it's not malformed. Otherwise just indicate it's malformed."
  [ctx]
  (try 
    (if-let [data (-> (get-in ctx [:request :body]) slurp json/parse-string keywordize-keys)]
      [good-json {:data data}]
      malformed)
    (catch Exception e
      (debug "Request body not processable as JSON: " e)
      malformed)))