(ns fcms.controllers.common
  (:require [clojure.walk :refer (keywordize-keys)]
            [clj-json.core :as json]))

(defn malformed-json?
  "The request body can only be read once, so read in the body param from the request as
  a string, parse it into JSON, make sure all the keys are keywords, and return it as
  the 2nd element in a vector, with the first part as false, indicating it's not malformed,
  otherwise return true to indicate it's malformed."
  [ctx]
  (if-let [data (-> (get-in ctx [:request :body]) slurp json/parse-string keywordize-keys)]
    [false {:data data}]
    true))