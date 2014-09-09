(ns fcms.integration.rest-api.collection.collection-update
  "Integration tests for updating collections with the REST API."
  (:require [midje.sweet :refer :all]
            [fcms.lib.resources :refer :all]
            [fcms.lib.rest-api-mock :refer :all]
            [fcms.lib.body :refer (verify-collection-links)]
            [fcms.resources.collection :as collection]))

;; The system should update collections stored in the system and handle the following scenarios:
;;
;; all good - no slug
;; all good - with same slug
;; all good - with different slug
;; all good - unicode in the body
;; all good - no name in body
;; conflicting reserved properties
;; no accept
;; wrong accept
;; no content header
;; wrong content header
;; no charset
;; wrong charset
;; no body
;; body, but not valid JSON
;; collection doesn't exist
;; different slug specified in body is already used
;; different slug specified in body is invalid

(with-state-changes [(before :facts (reset-collection c))
                     (after :facts (collection/delete-collection c))]

  (facts "about using the REST API to update a collection"

    ;; all good - no slug
    ;; curl -i --header "Accept: application/vnd.fcms.collection+json;version=1" --header "Accept-Charset: utf-8" --header "Content-Type: application/vnd.fcms.collection+json;version=1" -X PUT -d '{"name":"c-prime", "c":"c"}' http://localhost:3000/c/
    (fact "without providing a new slug"
      (let [response (api-request :put "/c" {
        :headers {
          :Content-Type (mime-type :collection)
          :Accept (mime-type :collection)}
        :body {
          :name "c-prime"
          :c c}})]
        (:status response) => 200
        (response-mime-type response) => (mime-type :collection)
        (json? response) => true
        (verify-collection-links c (:links (body-from-response response))))
      ;; check that the update worked
      (let [coll (collection/get-collection c)]
        coll => (contains {
          :name "c-prime"
          :c c
          :slug c
          :version 2})
        (:description coll) => nil)
      ;; check it didn't create another collection
      (collection/collection-count) => 1))

(future-facts "about attempting to use the REST API to update a collection"))