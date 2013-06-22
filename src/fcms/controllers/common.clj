(ns fcms.controllers.common
  (:require [clojure.walk :refer (keywordize-keys)]
            [clj-json.core :as json]))

(def ^:dynamic body nil)

(defn extract-json-body
  "The request body can only be read once, so return the thread-local (request local)
  var body if it's already set, otherwise read in the body param from the request as
  a string, parse it into JSON, make sure all the keys are keywords, and store it as
  a thread-local (request local) var named body."
  [ctx]
  (or body (deref (def ^:dynamic body (keywordize-keys (json/parse-string (slurp (get-in ctx [:request :body]))))))))