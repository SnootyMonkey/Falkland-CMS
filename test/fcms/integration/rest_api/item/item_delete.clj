(ns fcms.integration.rest-api.item.item-delete
  "Integration tests for deleting items with the REST API."
  (:require [midje.sweet :refer :all]
            [fcms.lib.resources :refer :all]
            [fcms.lib.rest-api-mock :refer :all]
            [fcms.resources.collection :as collection]
            [fcms.resources.collection-resource :as resource]
            [fcms.resources.item :as item]))

;; The system should delete items from a collection and handle the following scenarios:
;;
;; all good
;; item doesn't exist
;; collection doesn't exist

;; ----- Utilities -----

(defn- create-collection-c-items []
  (item/create-item c i {:slug i :description ascii-description})
  (item/create-item c "another-i" {:slug "another-i" :description "this is another item!"}))

;; ----- Tests -----

(with-state-changes [(before :facts (do (reset-collection c) (create-collection-c-items)))
                     (after :facts (collection/delete-collection c))]

  (fact "about using the REST API to delete an item"     
    ;; all good - 204 No Content
    ;; curl -i -X DELETE http://localhost:3000/c/i
    (let [response (api-request :delete "/c/i" {:headers {}})]
      (:status response) => 204
      (body-from-response response) => nil)
    ;; Check that it's really gone
    (item/get-item c i) => nil
    ;; Check that the other item in the collection wasn't deleted
    (collection/item-count c) => 1
    (item/get-item c "another-i") => (contains {
      :collection c
      :name "another-i"
      :slug "another-i"
      :version 1}))

  (facts "about attempting to use the REST API to delete an item"     

    ;; collection doesn't exist - 404 Not Found
    ;; curl -i -X DELETE http://localhost:3000/not-here/i
    (fact "from a collection that does not exist"
      (let [response (api-request :delete "/not-here/i" {:headers {}})]
        (:status response) => 404
        (response-mime-type response) => (mime-type :text)
        (body-from-response response) => "Collection not found.")
      ;; Check that no items in the collection were deleted
      (collection/item-count c) => 2
      (doseq [item-name ["i" "another-i"]]
        (item/get-item c item-name) => (contains {
          :collection c
          :name item-name
          :slug item-name
          :version 1})))

    ;; item doesn't exist - 404 Not Found
    ;; curl -i -X DELETE http://localhost:3000/c/not-here
    (fact "that doesn't exist"
      (let [response (api-request :delete "/c/not-here" {:headers {}})]
        (:status response) => 404
        (body-from-response response) => nil)
      ;; Check that no items in the collection were deleted
      (collection/item-count c) => 2
      (doseq [item-name ["i" "another-i"]]
        (item/get-item c item-name) => (contains {
          :collection c
          :name item-name
          :slug item-name
          :version 1})))))