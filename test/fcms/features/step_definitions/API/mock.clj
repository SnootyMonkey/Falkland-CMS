(ns fcms.features.step-definitions.api.mock
  (:require [clojure.walk :refer (keywordize-keys)]
            [clj-json.core :as json]))

(def req) ; mock HTTP request
(def bod) ; body of the mock HTTP request or response
(def resp) ; mock HTTP response

(defn request
  ""
  ([] req)
  ([new-req] (def req new-req)))

(defn body
  ""
  ([] bod)
  ([new-body] (def bod new-body)))

(defn response
  ""
  ([] resp)
  ([new-resp]
    (def resp new-resp)
    (if-let [new-body (:body new-resp)]
      (def bod (keywordize-keys (json/parse-string new-body)))
      (def bod nil))))