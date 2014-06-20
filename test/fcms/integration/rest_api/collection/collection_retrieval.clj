(ns fcms.integration.rest-api.collection.collection-retrieval
  (:require [midje.sweet :refer :all]
            [fcms.lib.resources :refer :all]
            [fcms.lib.rest-api-mock :refer :all]
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

(with-state-changes [(after :facts (collection/delete-collection c))]

  (future-facts "about retrieving collections"))
