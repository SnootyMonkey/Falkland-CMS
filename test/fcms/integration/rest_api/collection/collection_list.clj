(ns fcms.integration.rest-api.collection.collection-list
  "Integration tests for listing collections with the REST API."
  (:require [midje.sweet :refer :all]
            [clj-time.format :refer (parse)]
            [fcms.lib.resources :refer :all]
            [fcms.lib.rest-api-mock :refer :all]
            [fcms.resources.collection :as collection]))

;; The system should return a summary list of the collections stored in the system and handle the following scenarios:
;;
;; GET
;; all good, no collections
;; all good, 1 collection
;; all good, many collections
;; all good, force pagination
;; all good, additional page
;; no accept
;; wrong accept
;; no accept charset
;; wrong accept charset
;; using an invalid page

(future-facts "about using the REST API to list collections")

(future-facts "about attempting to use the REST API to list collections")