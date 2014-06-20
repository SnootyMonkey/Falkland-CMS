(ns fcms.integration.rest-api.collection.collection-create
  (:require [midje.sweet :refer :all]
            [fcms.lib.resources :refer :all]
            [fcms.lib.rest-api-mock :refer :all]
            [fcms.resources.collection :as collection]))

;; Creating collections with the REST API

;; The system should store newly created valid collections and handle the following scenarios:

;; POST
;; all good - no slug
;; all good - generated slug is different than the provided name
;; all good - generated slug is already used
;; all good - with slug
;; all good - unicode in the body
;; conflicting reserved properties
;; no accept
;; wrong accept
;; no content type
;; wrong content type
;; no charset
;; wrong charset
;; no body
;; body not valid JSON
;; no name in body
;; slug specified in body is already used
;; slug specified in body is invalid

(with-state-changes [(after :facts (collection/delete-collection c))]

  (future-facts "about creating valid new collections"

    ;; all good, no slug - 201 Created
    ;; curl -i --header "Accept: application/vnd.fcms.collection+json;version=1" --header "Accept-Charset: utf-8" --header "Content-Type: application/vnd.fcms.collection+json;version=1" -X POST -d '{"name":"c"}' http://localhost:3000/
    (fact "when no slug is specified"
      ;; Create the collection
      (let [response (api-request :post "/" {
        :headers {
          :Accept (mime-type :collection)
          :Content-Type (mime-type :collection)
        }
        :body {
          :name "c"
        }})]
        (:status response) => 201
        (response-mime-type response) => (mime-type :collection)
        (response-location response) => "/c"
        (json? response) => true
        (collection/get-collection c) => (contains {
          :name "c"
          :slug "c"
          :version 1}))
      ;; Get the created collection and make sure it's right
      (let [collection (collection/get-collection c)]
        (:slug collection) => c
        (:name collection) => c
        (:version collection) => 1)
      ;; Collection is empty?
      (collection/item-count c) => 0)))