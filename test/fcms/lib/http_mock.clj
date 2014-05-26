(ns fcms.lib.http-mock
  (:require [fcms.resources.collection :refer (collection-media-type)]
            [fcms.resources.item :refer (item-media-type item-collection-media-type)]
            [fcms.app :refer (app)]
            [ring.mock.request :refer (request body content-type header)]
            [cheshire.core :as json]))
            
            ; [clojure.string :refer (lower-case blank?)]
            ; [clojure.core.incubator :refer (dissoc-in)]
            ; [fcms.lib.checks :refer (check check-not)]
      
; (defn- body-from-response [resp-map]
;   (try
;     ;; treat the body as JSON
;     (json/parse-string (:body resp-map) true)
;     (catch Exception e
;       ;; must not be valid JSON
;       (:body resp-map))))

(defn mime-type [res-type]
  (case res-type
    :item item-media-type
    :item-collection item-collection-media-type
    :collection collection-media-type))

(defn apply-headers [request headers]
  (if (= headers {})
    request
    (let [key (first (keys headers))]
      (recur (header request key (get headers key)) (dissoc headers key)))))

;; pretends to execute an API request using ring mock
(defn api-request [method url options]
  (let [initial-request (request method url)
        headers (merge (:headers options) {"Accept-Charset" "utf-8"})
        headers-request (apply-headers initial-request headers)
        body-value (:body options)
        body-request (if body-value (body headers-request (json/generate-string body-value)) headers-request)]
    (app body-request)))