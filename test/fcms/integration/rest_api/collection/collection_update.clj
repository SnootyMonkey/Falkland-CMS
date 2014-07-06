(ns fcms.integration.rest-api.collection.collection-update
  "Integration tests for updating collections with the REST API."
  (:require [midje.sweet :refer :all]
            [fcms.lib.resources :refer :all]
            [fcms.lib.rest-api-mock :refer :all]
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

(future-facts "about using the REST API to update a collection")

(future-facts "about attempting to use the REST API to update a collection")