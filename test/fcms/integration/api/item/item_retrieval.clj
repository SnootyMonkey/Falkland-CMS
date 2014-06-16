(ns fcms.integration.api.item.item-retrieval
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

(with-state-changes [(before :facts (empty-collection-e))
                     (after :facts (collection/delete-collection e))]

  (facts "about retrieving"

    (fact "a normal item"
      ;; Create a plain item
      (item/create-item e i {:description "An item."})
      ;; Retrieve the item
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
          (verify-item-links e i (:links item)))))))