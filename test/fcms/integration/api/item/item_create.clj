(ns fcms.integration.api.item.item-create
  (:require [midje.sweet :refer :all]
            [fcms.lib.resources :refer :all]
            [fcms.lib.http-mock :refer :all]
            [fcms.resources.collection :as collection]))

;; Creating items with the REST API

;; The system should store valid items into a collection and handle the following scenarios:

;; POST
;; all good - no slug
;; all good - generated slug is different than the name
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


  ; Scenario: Create an item without providing a slug
  ;   When I have a "POST" request to URL "/c/"
  ;   And I provide an "item"
  ;   And I accept an "item"
  ;   And I set the "name" to "i"
  ;   Then the status will be "201"
  ;   And I will receive an "item"
  ;   And the "Location" header will be "/c/i"
  ;   And the body will be JSON
  ;   And the new item "i" in collection "c" will be named "i"
  ;   And the collection "c" will have an item count of 1
  ;   When I have a "GET" request to URL "/c/i"
  ;   And I accept an "item"
  ;   Then the status will be "200"
  ;   And I will receive an "item"
  ;   And the body will be JSON
  ;   And the new item "i" in collection "c" will be named "i"


(with-state-changes [(before :facts (empty-collection-e))
                     (after :facts (collection/delete-collection "e"))]

  (facts "about creating valid new items")

    ;; all good, no slug - 201 Created
    ;; curl -i --header "Accept: application/vnd.fcms.item+json;version=1" --header "Accept-Charset: utf-8" --header "Content-Type: application/vnd.fcms.item+json;version=1" -X POST -d '{"name":"i"}' http://localhost:3000/c/
    (fact "when no slug is specified"
      (let [response (api-request :post "/e/" {
        :headers {
          :Accept (mime-type :item)
          :Content-Type (mime-type :item)
        }
        :body {
          :name "i"
        }})]
        (:status response) => 201
        (get-in response [:headers "Content-Type"]) => (str (mime-type :item) ";charset=utf-8")
        (get-in response [:headers "Location"]) => "/e/i"
  ;   And the body will be JSON
  ;   And the new item "i" in collection "c" will be named "i"
  ;   And the collection "c" will have an item count of 1
  ;   When I have a "GET" request to URL "/c/i"
  ;   And I accept an "item"
  ;   Then the status will be "200"
  ;   And I will receive an "item"
  ;   And the body will be JSON
  ;   And the new item "i" in collection "c" will be named "i"        
        ))

  ; Scenario: "Create an item with a complex name that won't match the generated slug"
  ;   When I have a "POST" request to URL "/c/"
  ;   And I provide an "item"
  ;   And I accept an "item"
  ;   And I set the "name" to " -tHiS #$is%?-----ελληνικήalso-მივჰხვდემასჩემსაãالزجاجوهذالايؤلمني-slüg♜-♛-☃-✄-✈  - "
  ;   Then the status will be "201"
  ;   And I will receive an "item"
  ;   And the "Location" header will be "/c/this-is-also-a-slug"
  ;   And the body will be JSON
  ;   And the new item "this-is-also-a-slug" in collection "c" will be named " -tHiS #$is%?-----ελληνικήalso-მივჰხვდემასჩემსაãالزجاجوهذالايؤلمني-slüg♜-♛-☃-✄-✈  - "
  ;   And the collection "c" will have an item count of 1
  ;   When I have a "GET" request to URL "/c/this-is-also-a-slug"
  ;   And I accept an "item"
  ;   Then the status will be "200"
  ;   And I will receive an "item"
  ;   And the body will be JSON
  ;   And the new item "this-is-also-a-slug" in collection "c" will be named " -tHiS #$is%?-----ελληνικήalso-მივჰხვდემასჩემსაãالزجاجوهذالايؤلمني-slüg♜-♛-☃-✄-✈  - "

    ;; all good, generated slug is different than the name - 201 Created
    ;; curl -i --header "Accept: application/vnd.fcms.item+json;version=1" --header "Accept-Charset: utf-8" --header "Content-Type: application/vnd.fcms.item+json;version=1" -X POST -d '{"name":" -tHiS #$is%?-----ελληνικήalso-მივჰხვდემასჩემსაãالزجاجوهذالايؤلمني-slüg♜-♛-☃-✄-✈  - "}' http://localhost:3000/c/
    (fact "when the generated slug is different than the name"))