(ns fcms.integration.rest-api.item.item-retrieval
  "Integration tests for retrieving items with the REST API."
  (:require [clojure.walk :refer (keywordize-keys)]
            [clj-time.format :refer (parse)]
            [midje.sweet :refer :all]
            [cheshire.core :as json]
            [fcms.lib.resources :refer :all]
            [fcms.lib.rest-api-mock :refer :all]
            [fcms.lib.body :refer (verify-item-links)]
            [fcms.lib.check :refer (about-now?)]
            [fcms.resources.collection :as collection]
            [fcms.resources.item :as item]))

;; The system should return the detail of items stored in a collection and handle the following scenarios:

;; all good
;; all good with unicode
;; no accept
;; wrong accept
;; no accept charset
;; wrong accept charset
;; collection doesn't exist
;; item doesn't exist

(with-state-changes [(before :facts (do (empty-collection-e) (item/create-item e i {:description "An item."})))
                     (after :facts (collection/delete-collection e))]

  (facts "about retrieving"

    ;; all good - 200 OK
    ;; curl -i --header "Accept: application/vnd.fcms.item+json;version=1" --header "Accept-Charset: utf-8" -X GET http://localhost:3000/e/i
    (fact "a normal item"
      (let [response (api-request :get "/e/i" {
        :headers {
          :Accept (mime-type :item)
          :Content-Type (mime-type :item)
        }})]
        (:status response) => 200
        (response-mime-type response) => (mime-type :item)
        (json? response) => true
        (let [item (body-from-response response)]
          (:name item) => i
          (:slug item) => i
          (:collection item) => e
          (:description item) => "An item."
          (:version item) = 1
          (instance? timestamp (parse (:created-at item))) => true
          (about-now? (:created-at item)) => true
          (:created-at item) => (:updated-at item)
          (verify-item-links e i (:links item)))))

    ;; all good with unicode - 200 OK
    ;; curl -i --header "Accept: application/vnd.fcms.item+json;version=1" --header "Accept-Charset: utf-8" -X GET http://localhost:3000/e/another-i
    (fact "an item containing unicode"
      ;; Create a unicode item
      (item/create-item e unicode-name {:slug "another-i" :description unicode-description})
      ;; Retrieve and verify the item
      (let [response (api-request :get "/e/another-i" {:headers {:Accept (mime-type :item)}})]
        (:status response) => 200
        (response-mime-type response) => (mime-type :item)
        (json? response) => true
        (let [item (body-from-response response)]
          (:name item) => unicode-name
          (:slug item) => "another-i"
          (:collection item) => e
          (:description item) => unicode-description
          (:version item) = 1
          (instance? timestamp (parse (:created-at item))) => true
          (about-now? (:created-at item)) => true
          (:created-at item) => (:updated-at item)
          (verify-item-links e "another-i" (:links item)))))
    
    ;; no accept - 200 OK
    ;; curl -i --header "Accept-Charset: utf-8" -X GET http://localhost:3000/e/i
    (fact "an item without using an Accept header"
      (let [response (api-request :get "/e/i" {:headers {}})]
        (:status response) => 200
        (response-mime-type response) => (mime-type :item)
        (json? response) => true
        (let [item (body-from-response response)]
          (:name item) => i
          (:slug item) => i
          (:collection item) => e
          (:description item) => "An item."
          (:version item) = 1
          (instance? timestamp (parse (:created-at item))) => true
          (about-now? (:created-at item)) => true
          (:created-at item) => (:updated-at item)
          (verify-item-links e i (:links item)))))

    ;; wrong accept - 406 Not Acceptable
    ;; curl -i --header "Accept: application/vnd.fcms.collection+json;version=1" curl -i --header "Accept-Charset: utf-8" -X GET http://localhost:3000/e/i
    (fact "a normal item with the wrong Accept type"
      (let [response (api-request :get "/e/i" {:headers {:Accept (mime-type :collection)}})]
        (:status response) => 406
        (response-mime-type response) => (mime-type :text)
        (let [item (body-from-response response)]
          (.contains item "Acceptable media type: application/vnd.fcms.item+json;version=1") => true
          (.contains item "Acceptable charset: utf-8") => true)))

    ;; no accept charset - 200 OK
    ;; curl -i --header "Accept: application/vnd.fcms.item+json;version=1" -X GET http://localhost:3000/e/i
    (fact "a normal item without an Accept-Charset header"
      (let [response (api-request :get "/e/i" {:headers {:Accept (mime-type :item)} :skip-charset true})]
        (:status response) => 200
        (response-mime-type response) => (mime-type :item)
        (json? response) => true
        (let [item (body-from-response response)]
          (:name item) => i
          (:slug item) => i
          (:collection item) => e
          (:description item) => "An item."
          (:version item) = 1
          (instance? timestamp (parse (:created-at item))) => true
          (about-now? (:created-at item)) => true
          (:created-at item) => (:updated-at item)
          (verify-item-links e i (:links item)))))

    ;; wrong accept charset - 406 Not Acceptable
    ;; curl -i --header "Accept: application/vnd.fcms.item+json;version=1" --header "Accept-Charset: iso-8859-1" -X GET http://localhost:3000/e/i
    ;; Fails: Returns content type of
    ;; "application/vnd.fcms.item+json;charset=UTF-8" instead of text/plain
    (fact "a normal item with the wrong Accept-Charset header"
      (let [response (api-request :get "/e/i" {
        :headers {
          :Accept (mime-type :item)
          :Accept-Charset "iso-8859-1"}})]
        (:status response) => 406
        (response-mime-type response) => (mime-type :text)
        (let [item (body-from-response response)]
          (.contains item "Acceptable media type: application/vnd.fcms.item+json;version=1") => true
          (.contains item "Acceptable charset: utf-8") => true)))

    ;; collection doesn't exist - 404 Not Found
    ;; curl -i --header "Accept: application/vnd.fcms.item+json;version=1" --header "Accept-Charset: utf-8" -X GET http://localhost:3000/not-here/i
    (fact "a collection doesn't exist"
      (let [response (api-request :get "/not-here/i" {:headers {:Accept (mime-type :item)}})]
        (:status response) => 404
        (response-mime-type response) => (mime-type :text)
        (let [item (body-from-response response)]
          (.contains item "Collection not found") => true)))

    ;; item doesn't exist - 404 Not Found
    ;; curl -i --header "Accept: application/vnd.fcms.item+json;version=1" --header "Accept-Charset: utf-8" -X GET http://localhost:3000/e/not-here
    (fact "an item doesn't exist"
      (let [response (api-request :get "/e/not-here" {:headers {:Accept (mime-type :item)}})]
        (:status response) => 404
        (body? response) => false))))