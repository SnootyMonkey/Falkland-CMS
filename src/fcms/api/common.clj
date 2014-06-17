(ns fcms.api.common
  (:require [taoensso.timbre :refer (debug info warn error fatal spy)]
            [clojure.string :refer (join)]
            [cheshire.core :as json]
            [liberator.core :refer (run-resource)]
            [liberator.representation :refer (ring-response)]))

(def UTF8 "utf-8")

(def malformed true)
(def good-json false)

;; ----- Stijn's Liberator shared config macro -----

(defn split-args
  "Split the given key-values into a pair of arguments and remaining key values"
  [kvs]
   (if (vector? (first kvs))
     [(first kvs) (rest kvs)]
     [[] kvs]))

(defmacro defresource
  "Stijn's Liberator shared config macro"
  [name & kvs]
  (let [[args kvs] (split-args kvs)
        options (if (keyword? (first kvs))
                  (apply hash-map kvs)
                  `(merge ~(first kvs) ~(apply hash-map (rest kvs))))]
    `(defn ~name [~@args]
       (fn [request#]
         (run-resource request# ~options)))))

;; ----------

(defn unprocessable-entity-response [reason]
  (ring-response
    {:status 422
      :body reason
      :headers {"Content-Type" (format "text/plain;charset=%s" UTF8)}}))

(defn only-accept [media-type]
  (ring-response
    {:status 406
     :body (format "Acceptable media type: %s\nAcceptable charset: %s" media-type UTF8)
     :headers {"Content-Type" (format "text/plain;charset=%s" UTF8)}}))

(def missing-collection-response
  (ring-response
    {:status 404
     :body "Collection not found."
     :headers {"Content-Type" (format "text/plain;charset=%s" UTF8)}}))

(defn location-response [path-parts body media-type]
  (ring-response
    {:body body
     :headers {"Location" (format "/%s" (join "/" path-parts))
               "Content-Type" (format "%s;charset=%s" media-type UTF8)}}))

(defn malformed-json?
  "Read in the body param from the request as a string, parse it into JSON, make sure all the
  keys are keywords, and then return it, mapped to :data as the 2nd value in a vector,
  with the first value indicating it's not malformed. Otherwise just indicate it's malformed."
  [ctx]
  (try
    (if-let [data (-> (get-in ctx [:request :body]) slurp (json/parse-string true))]
      ; handle case of a string which is valid JSON, but still malformed for us
      (do (when-not (map? data) (throw (Exception.)))
        [good-json {:data data}])
      malformed)
    (catch Exception e
      (debug "Request body not processable as JSON: " e)
      malformed)))

(defn known-content-type
  [ctx content-type]
  (if-let [request-type (get-in ctx [:request :content-type])]
    (= request-type content-type)
    true))

(defn check-input [check]
  (if (= check true) true [false {:reason check}]))