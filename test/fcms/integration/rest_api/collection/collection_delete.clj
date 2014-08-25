(ns fcms.integration.rest-api.collection.collection-delete
  "Integration tests for deleting collections with the REST API."
  (:require [midje.sweet :refer :all]
            [fcms.lib.resources :refer :all]
            [fcms.lib.rest-api-mock :refer :all]
            [fcms.resources.collection :as collection]
            [fcms.resources.item :as item]))

;; The system should delete collections and handle the following scenarios:
;;
;; all good
;; collection doesn't exist

(with-state-changes [(before :facts (reset-collection e))
                     (after :facts (collection/delete-collection e))]

  (fact "about using the REST API to delete a collection"
    ;; all good - 204 No Content
    ;; curl -i -X DELETE http://localhost:3000/c
    (collection/create-collection "c")
    (collection/collection-count) => 2
    ;; Delete the collection
    (let [response (api-request :delete "/c" {:headers {}})]
      (:status response) => 204
      (body-from-response response) => nil)
    ;; Check that it's really gone
    (collection/get-collection c) => nil
    ;; Check that the other collection wasn't deleted
    (collection/collection-count) => 1
    (collection/get-collection e) => (contains {
      :name e
      :slug e
      :version 1}))

  (fact "about attempting to use the REST API to delete a collection that doesn't exist"
    ;; collection doesn't exist - 404 Not Found
    ;; curl -i -X DELETE http://localhost:3000/not-here
    (let [response (api-request :delete "/not-here" {:headers {}})]
      (:status response) => 404
      (body-from-response response) => "Collection not found.")
      ;; Check that the other collection wasn't deleted
      (collection/collection-count) => 1
      (collection/get-collection e) => (contains {
        :name e
        :slug e
        :version 1})))