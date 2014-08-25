(ns fcms.integration.rest-api.collection.collection-retrieval
  "Integration tests for retrieving collections with the REST API."
  (:require [midje.sweet :refer :all]
    [fcms.lib.resources :refer :all]
    [fcms.lib.rest-api-mock :refer :all]
    [fcms.lib.body :refer (verify-collection-links)]
    [clj-time.format :refer (parse)]
    [fcms.lib.check :refer (about-now?)]
    [fcms.resources.collection :as collection]))

;; The system should return the detail of the stored collection and handle the following scenarios:
;;
;; all good
;; all good with unicode
;; no accept
;; wrong accept
;; no accept charset
;; wrong accept charset
;; collection doesn't exist

;; ----- Utilities -----

(def unicode "unicode")

;; ----- Tests -----

(with-state-changes
  [(before :facts
     (do
       (collection/create-collection c {:description "A collection."})
       (collection/create-collection unicode-name {:slug unicode :description unicode-description})))
    (after :facts
      (do
        (collection/delete-collection c)
        (collection/delete-collection unicode)))]

  (facts "about using the REST API to retrieve a collection"

    ;; all good - 200 OK
    ;; curl -i --header "Accept: application/vnd.fcms.collection+json;version=1" --header "Accept-Charset: utf-8" -X GET http://localhost:3000/c
    (fact "normally"
      (let [response (api-request :get (str "/" c) {:headers {:Accept (mime-type :collection)}})]
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
          (:created-at coll) => (:updated-at coll)
          (verify-collection-links c (:links coll)))))
    
    ;; all good with unicode - 200 OK
    ;; curl -i --header "Accept: application/vnd.fcms.collection+json;version=1" --header "Accept-Charset: utf-8" -X GET http://localhost:3000/unicode
    (fact "containing unicode"
      (let [response (api-request :get (str "/" unicode) {:headers {:Accept (mime-type :collection)}})]
        (:status response) => 200
        (response-mime-type response) => (mime-type :collection)
        (json? response) => true
        (let [coll (body-from-response response)]
          (:name coll) => unicode-name
          (:slug coll) => unicode
          (:description coll) => unicode-description
          (:version coll) => 1
          (instance? timestamp (parse (:created-at coll))) => true
          (about-now? (:created-at coll)) => true
          (:created-at coll) => (:updated-at coll)
          (verify-collection-links unicode (:links coll)))))

    ;; no accept - 200 OK
    ;; curl -i --header "Accept-Charset: utf-8" -X GET http://localhost:3000/c
    (fact "without an Accept header"
      (let [response (api-request :get (str "/" c) {:headers {}})]
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
          (:created-at coll) => (:updated-at coll)
          (verify-collection-links c (:links coll)))))

    ;; no accept charset - 200 OK
    ;; curl -i --header "Accept: application/vnd.fcms.collection+json;version=1" -X GET http://localhost:3000/c
    (fact "without an Accept-Charset header"
      (let [response (api-request :get (str "/" c) {:headers {:Accept (mime-type :collection)} :skip-charset true})]
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
          (:created-at coll) => (:updated-at coll)
          (verify-collection-links c (:links coll))))))
    
  (facts "about attempting to use the REST API to retrieve a collection"

    ;; wrong accept - 406 Not Acceptable
    ;; curl -i --header "Accept: application/vnd.fcms.item+json;version=1" --header "Accept-Charset: utf-8" -X GET http://localhost:3000/c
    (fact "with the wrong Accept header"
      (let [response (api-request :get (str "/" c) {:headers {:Accept (mime-type :item)}})]
        (:status response) => 406
        (response-mime-type response) => (mime-type :text)
        (let [coll (body-from-response response)]
          (.contains coll "Acceptable media type: application/vnd.fcms.collection+json;version=1") => true
          (.contains coll "Acceptable charset: utf-8") => true)))

    ;; wrong accept charset - 406 Not Acceptable
    ;; curl -i --header "Accept: application/vnd.fcms.collection+json;version=1" --header "Accept-Charset: iso-8859-1" -X GET http://localhost:3000/c
    (fact "with the wrong Accept-Charset header"
      (let [response (api-request :get (str "/" c) {
        :headers {
          :Accept (mime-type :collection)
          :Accept-Charset "iso-8859-1"}})]
        (:status response) => 406
        (response-mime-type response) => (mime-type :text)
        (let [coll (body-from-response response)]
          (.contains coll "Acceptable media type: application/vnd.fcms.collection+json;version=1") => true
          (.contains coll "Acceptable charset: utf-8") => true)))

    ;; collection doesn't exist - 404 Not Found
    ;; curl -i --header "Accept: application/vnd.fcms.collection+json;version=1" --header "Accept-Charset: utf-8" -X GET http://localhost:3000/not-here
    (fact "that doesn't exist"
      (let [response (api-request :get "/not-here" {:headers {:Accept (mime-type :collection)}})]
        (:status response) => 404
        (body? response) => false))))