(ns fcms.integration.api.item.item-create
  (:require [midje.sweet :refer :all]
            [fcms.lib.resources :refer :all]
            [fcms.lib.rest-api-mock :refer :all]
            [fcms.resources.collection :as collection]
            [fcms.resources.item :as item]))

;; Creating items with the REST API

;; The system should store newly created valid items into a collection and handle the following scenarios:

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
;; collection doesn't exist
;; no name in body
;; slug specified in body is already used
;; slug specified in body is invalid

(with-state-changes [(before :facts (empty-collection-e))
                     (after :facts (collection/delete-collection "e"))]

  (facts "about creating valid new items"

    ;; all good, no slug - 201 Created
    ;; curl -i --header "Accept: application/vnd.fcms.item+json;version=1" --header "Accept-Charset: utf-8" --header "Content-Type: application/vnd.fcms.item+json;version=1" -X POST -d '{"name":"i"}' http://localhost:3000/c/
    (fact "when no slug is specified"
      ;; Create the item
      (let [response (api-request :post "/e/" {
        :headers {
          :Accept (mime-type :item)
          :Content-Type (mime-type :item)
        }
        :body {
          :name i
        }})]
        (:status response) => 201
        (base-mime-type (get-in response [:headers "Content-Type"])) => (mime-type :item)
        (get-in response [:headers "Location"]) => "/e/i"
        (json? response) => true
        (item/get-item e i) => (contains {
          :collection e
          :name i
          :slug i
          :version 1})
        (collection/item-count e) => 1)
      ;; Get the created item and make sure it's right
      (let [item (item/get-item e i)]
        (:slug item) => i
        (:name item) => i
        (:version item) => 1
        (:collection item) => e))

    ;; all good, generated slug is different than the provided name - 201 Created
    ;; curl -i --header "Accept: application/vnd.fcms.item+json;version=1" --header "Accept-Charset: utf-8" --header "Content-Type: application/vnd.fcms.item+json;version=1" -X POST -d '{"name":" -tHiS #$is%?-----ελληνικήalso-მივჰხვდემასჩემსაãالزجاجوهذالايؤلمني-slüg♜-♛-☃-✄-✈  - "}' http://localhost:3000/c/
    (fact "when the generated slug is different than the provided name"
      ;; Create the item
      (let [response (api-request :post "/e/" {
        :headers {
          :Accept (mime-type :item)
          :Content-Type (mime-type :item)
        }
        :body {
          :name " -tHiS #$is%?-----ελληνικήalso-მივჰხვდემასჩემსაãالزجاجوهذالايؤلمني-slüg♜-♛-☃-✄-✈  - "
        }})]
        (:status response) => 201
        (base-mime-type (get-in response [:headers "Content-Type"])) => (mime-type :item)
        (get-in response [:headers "Location"]) => "/e/this-is-also-a-slug"
        (json? response) => true
        (item/get-item e "this-is-also-a-slug") => (contains {
          :collection e
          :name " -tHiS #$is%?-----ελληνικήalso-მივჰხვდემასჩემსაãالزجاجوهذالايؤلمني-slüg♜-♛-☃-✄-✈  - "
          :slug "this-is-also-a-slug"
          :version 1})
        (collection/item-count "e") => 1)
      ;; Get the created item and make sure it's right
      (let [item (item/get-item e "this-is-also-a-slug")]
        (:slug item) => "this-is-also-a-slug"
        (:name item) => " -tHiS #$is%?-----ελληνικήalso-მივჰხვდემასჩემსაãالزجاجوهذالايؤلمني-slüg♜-♛-☃-✄-✈  - "
        (:version item) => 1
        (:collection item) => e))))