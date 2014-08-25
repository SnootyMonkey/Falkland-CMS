(ns fcms.integration.rest-api.collection.collection-retrieval
  (:require [midje.sweet :refer :all]
    [fcms.lib.resources :refer :all]
    [fcms.lib.rest-api-mock :refer :all]
    [clj-time.format :refer (parse)]
    [fcms.lib.check :refer (about-now?)]
    [fcms.resources.collection :as collection]))

;; Retrieving collections with the REST API

;; The system should return the detail of the stored collection and handle the following scenarios:

;; GET
;; all good
;; all good with unicode
;; no accept
;; wrong accept
;; no accept charset
;; wrong accept charset
;; collection doesn't exist

(with-state-changes
  [(before :facts
     (do
       (collection/create-collection c {:description "A collection."})
       (collection/create-collection unicode-name {:slug "unicode" :description unicode-description})))
    (after :facts
      (do
        (collection/delete-collection c)
        (collection/delete-collection "unicode")))]

  (facts "about using the REST API to retrieve a collection"
    (fact "normally"
      (let [response (api-request :get "/c" {:headers {:Accept (mime-type :collection)
                                                       :Accept-Charset "utf-8"}})]
        (:status response) => 200
        (response-mime-type response) => (mime-type :collection)
        (json? response) => true
        (let [coll (body-from-response response)]
          (:name coll) => c
          (:slug coll) => c
          (:description coll) => "A collection."
          (:version coll) => 1
          (instance? timestamp (parse (:created-at coll))) => true
          (about-now? (:created-at coll)) => true
          (:created-at coll) => (:updated-at coll))))
    
    (fact "containing unicode"
      (let [response (api-request :get "/unicode" {:headers {:Accept (mime-type :collection)
                                                             :Accept-Charset "utf-8"}})]
        (:status response) => 200
        (response-mime-type response) => (mime-type :collection)
        (json? response) => true
        (let [coll (body-from-response response)]
          (:name coll) => unicode-name
          (:slug coll) => "unicode"
          (:description coll) => unicode-description
          (:version coll) => 1
          (instance? timestamp (parse (:created-at coll))) => true
          (about-now? (:created-at coll)) => true
          (:created-at coll) => (:updated-at coll)))))

  (facts "about attempting to use the REST API to retrieve a collection"
    (fact "no Accept header"
      (let [response (api-request :get "/c" {:headers {:Accept-Charset "utf-8"}})]
        (:status response) => 200
        (response-mime-type response) => (mime-type :collection)
        (json? response) => true
        (let [coll (body-from-response response)]
          (:name coll) => c
          (:slug coll) => c
          (:description coll) => "A collection."
          (:version coll) => 1
          (instance? timestamp (parse (:created-at coll))) => true
          (about-now? (:created-at coll)) => true
          (:created-at coll) => (:updated-at coll))))

    (fact "wrong Accept header"
      (let [response (api-request :get "/c" {
        :headers {
          :Accept (mime-type :item)
          :Accept-Charset "utf-8"}})]
        (:status response) => 406
        (response-mime-type response) => (mime-type :text)
        (let [coll (body-from-response response)]
          (.contains coll "Acceptable media type: application/vnd.fcms.collection+json;version=1") => true
          (.contains coll "Acceptable charset: utf-8") => true)))

    (fact "no charset"
      (let [response (api-request :get "/c" {:headers {:Accept (mime-type :collection)}})]
        (:status response) => 200
        (response-mime-type response) => (mime-type :collection)
        (json? response) => true
        (let [coll (body-from-response response)]
          (:name coll) => c
          (:slug coll) => c
          (:description coll) => "A collection."
          (:version coll) => 1
          (instance? timestamp (parse (:created-at coll))) => true
          (about-now? (:created-at coll)) => true
          (:created-at coll) => (:updated-at coll))))
    
    (fact "wrong charset"
      (let [response (api-request :get "/c" {
        :headers {
          :Accept (mime-type :collection)
          :Accept-Charset "iso-8859-1"}})]
        (:status response) => 406
        (response-mime-type response) => (mime-type :text)
        (let [coll (body-from-response response)]
          (.contains coll "Acceptable media type: application/vnd.fcms.collection+json;version=1") => true
          (.contains coll "Acceptable charset: utf-8") => true)))

    (fact "collection doesn't exist"
      (let [response (api-request :get "/not-here" {:headers {:Accept (mime-type :collection)
                                                              :Accept-Charset "utf-8"}})]
        (:status response) => 404
        (response-mime-type response) => (mime-type :text)
        (let [coll (body-from-response response)]
          (.contains coll "Collection not found") => true)))))

