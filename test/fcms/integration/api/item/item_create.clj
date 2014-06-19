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

(defn- create-item-with-api
  "Makes an API request to create the item and returns the response."  
  ([body]
     (api-request :post "/e/" {:headers
                                {:Accept (mime-type :item)
                                 :Content-Type (mime-type :item)}
                                :body body}))
  ([headers body]
     (api-request :post "/e/" {:headers headers
                                :body body})))

(with-state-changes [(before :facts (empty-collection-e))
                     (after :facts (collection/delete-collection e))]

  (facts "about creating valid new items"
    ;; all good, no slug - 201 Created
    ;; curl -i --header "Accept: application/vnd.fcms.item+json;version=1" --header "Accept-Charset: utf-8" --header "Content-Type: application/vnd.fcms.item+json;version=1" -X POST -d '{"name":"i"}' http://localhost:3000/c/
    (fact "when no slug is specified"
      ;; Create the item
      (let [response (create-item-with-api {:name i})]
        (:status response) => 201
        (response-mime-type response) => (mime-type :item)
        (response-location response) => "/e/i"
        (json? response) => true
        ;; Get the created item and make sure it's right
        (item/get-item e i) => (contains {
          :collection e
          :name i
          :slug i
          :version 1})
        (collection/item-count e) => 1))

    ;; all good, generated slug is different than the provided name - 201 Created
    ;; curl -i --header "Accept: application/vnd.fcms.item+json;version=1" --header "Accept-Charset: utf-8" --header "Content-Type: application/vnd.fcms.item+json;version=1" -X POST -d '{"name":" -tHiS #$is%?-----ελληνικήalso-მივჰხვდემასჩემსაãالزجاجوهذالايؤلمني-slüg♜-♛-☃-✄-✈  - "}' http://localhost:3000/c/
    (fact "when the generated slug is different than the provided name"
      ;; Create the item
      (let [response (create-item-with-api {:name " -tHiS #$is%?-----ελληνικήalso-მივჰხვდემასჩემსაãالزجاجوهذالايؤلمني-slüg♜-♛-☃-✄-✈  - "})]
        (:status response) => 201
        (response-mime-type response) => (mime-type :item)
        (response-location response) => "/e/this-is-also-a-slug"
        (json? response) => true
        ;; Get the created item and make sure it's right
        (item/get-item e "this-is-also-a-slug") => (contains {
          :collection e
          :name " -tHiS #$is%?-----ελληνικήalso-მივჰხვდემასჩემსაãالزجاجوهذالايؤلمني-slüg♜-♛-☃-✄-✈  - "
          :slug "this-is-also-a-slug"
          :version 1})
        (collection/item-count "e") => 1))

    ;; all good, generated slug is already used - 201 Created
    ;; curl -i --header "Accept: application/vnd.fcms.item+json;version=1" --header "Accept-Charset: utf-8" --header "Content-Type: application/vnd.fcms.item+json;version=1" -X POST -d '{"name":"i"}' http://localhost:3000/c/
    ;; curl -i --header "Accept: application/vnd.fcms.item+json;version=1" --header "Accept-Charset: utf-8" --header "Content-Type: application/vnd.fcms.item+json;version=1" -X POST -d '{"name":"i"}' http://localhost:3000/c/
    ;; curl -i --header "Accept: application/vnd.fcms.item+json;version=1" --header "Accept-Charset: utf-8" --header "Content-Type: application/vnd.fcms.item+json;version=1" -X POST -d '{"name":"i"}' http://localhost:3000/c/
    (fact "when the generated slug is already used"
      ;; Create the first item
      (let [response (create-item-with-api {:name i})]
        (:status response) => 201
        (response-mime-type response) => (mime-type :item)
        (response-location response) => "/e/i"
        (json? response) => true
        ;; Get the created item and make sure it's right
        (item/get-item e i) => (contains
                                 {
                                   :collection e
                                   :name i
                                   :slug i
                                   :version 1})
        (collection/item-count e) => 1)
      ;; Create the second item with the same name
      (let [response (create-item-with-api {:name i})]
        (:status response) => 201
        (response-mime-type response) => (mime-type :item)
        (response-location response) => "/e/i-1"
        (json? response) => true
        (item/get-item e "i-1") => (contains
                                 {
                                   :collection e
                                   :name i
                                   :slug "i-1"
                                   :version 1})
        (collection/item-count e) => 2)
      ;; Create the third item with the same name
      (let [response (create-item-with-api {:name i})]
        (:status response) => 201
        (response-mime-type response) => (mime-type :item)
        (response-location response) => "/e/i-2"
        (json? response) => true
        (item/get-item e "i-2") => (contains
                                 {
                                   :collection e
                                   :name i
                                   :slug "i-2"
                                   :version 1})
        (collection/item-count e) => 3))

    ;; all good, with slug - 201 Created
    ;;curl -i --header "Accept: application/vnd.fcms.item+json;version=1" --header "Accept-Charset: utf-8" --header "Content-Type: application/vnd.fcms.item+json;version=1" -X POST -d '{"name":"i", "slug":"another-i"}' http://localhost:3000/c/
    (fact "creating an item with a provided slug"
      (let [response (create-item-with-api {:name i :slug "another-i"})]
        (:status response) => 201
        (response-mime-type response) => (mime-type :item)
        (response-location response) => "/e/another-i"
        (json? response) => true
        ;; Get the created item and make sure it's right
        (item/get-item e "another-i") => (contains
                                           {
                                             :collection e
                                             :name i
                                             :slug "another-i"
                                             :version 1})
        (collection/item-count e) => 1))

    ;; all good, unicode in the body - 201 Created
    ;; curl -i --header "Accept: application/vnd.fcms.item+json;version=1" --header "Accept-Charset: utf-8" --header "Content-Type: application/vnd.fcms.item+json;version=1" -X POST -d '{"name":"私はガラスを食", "slug":"i", "description":"er stîget ûf mit grôzer kraft Τη γλώσσα μου έδωσαν ελληνική მივჰხვდე მას ჩემსა الزجاج و هذا لا يؤلمني. मैं काँच खा सकता ฉันกินกระจกได้ לא מזיק Mogę jeść szkło €"}' http://localhost:3000/c/
    (fact "creating an item containing unicode"
      (let [response (create-item-with-api {:name unicode-name :slug i :description unicode-description})]
        (:status response) => 201
        (response-mime-type response) => (mime-type :item)
        (response-location response) => "/e/i"
        (json? response) => true
        ;; Get the created item and make sure it's right
        (item/get-item e i) => (contains
                                 {
                                   :collection e
                                   :name unicode-name
                                   :slug i
                                   :version 1})
        (collection/item-count e) => 1))
    
    ;; conflicting reserved properties - 422 Unprocessable Entity
    ;; curl -i --header "Accept: application/vnd.fcms.item+json;version=1" --header "Accept-Charset: utf-8" --header "Content-Type: application/vnd.fcms.item+json;version=1" -X POST -d '{"name":"i", "version":"foo"}' http://localhost:3000/c/
    ;; curl -i --header "Accept: application/vnd.fcms.item+json;version=1" --header "Accept-Charset: utf-8" --header "Content-Type: application/vnd.fcms.item+json;version=1" -X POST -d '{"name":"i", "collection":"foo"}' http://localhost:3000/c/
    ;; curl -i --header "Accept: application/vnd.fcms.item+json;version=1" --header "Accept-Charset: utf-8" --header "Content-Type: application/vnd.fcms.item+json;version=1" -X POST -d '{"name":"i", "id":"foo"}' http://localhost:3000/c/
    ;; curl -i --header "Accept: application/vnd.fcms.item+json;version=1" --header "Accept-Charset: utf-8" --header "Content-Type: application/vnd.fcms.item+json;version=1" -X POST -d '{"name":"i", "type":"foo"}' http://localhost:3000/c/
    ;; curl -i --header "Accept: application/vnd.fcms.item+json;version=1" --header "Accept-Charset: utf-8" --header "Content-Type: application/vnd.fcms.item+json;version=1" -X POST -d '{"name":"i", "updated-at":"foo"}' http://localhost:3000/c/
    ;; curl -i --header "Accept: application/vnd.fcms.item+json;version=1" --header "Accept-Charset: utf-8" --header "Content-Type: application/vnd.fcms.item+json;version=1" -X POST -d '{"name":"i", "created-at":"foo"}' http://localhost:3000/c/
    (fact "creating an item with a property that conflicts with a reserved property"
      ;; conflicts with version property
      (let [response (create-item-with-api {:name i :version "foo"})]
        (:status response) => 422
        (response-mime-type response) => (mime-type :text)
        (let [body (body-from-response response)]
          (.contains body "A reserved property was used.") => true)
        (collection/item-count e) => 0)
      ;; conflicts with collection property
      (let [response (create-item-with-api {:name i :collection "foo"})]
        (:status response) => 422
        (response-mime-type response) => (mime-type :text)
        (let [body (body-from-response response)]
          (.contains body "A reserved property was used.") => true)
        (collection/item-count e) => 0)
      ;; conflicts with id property
      (let [response (create-item-with-api {:name i :id "foo"})]
        (:status response) => 422
        (response-mime-type response) => (mime-type :text)
        (let [body (body-from-response response)]
          (.contains body "A reserved property was used.") => true)
        (collection/item-count e) => 0)
      ;; conflicts with type property
      (let [response (create-item-with-api {:name i :type "foo"})]
        (:status response) => 422
        (response-mime-type response) => (mime-type :text)
        (let [body (body-from-response response)]
          (.contains body "A reserved property was used.") => true)
        (collection/item-count e) => 0)
      ;; conflicts with created-at property      
      (let [response (create-item-with-api {:name i :created-at "foo"})]
        (:status response) => 422
        (response-mime-type response) => (mime-type :text)
        (let [body (body-from-response response)]
          (.contains body "A reserved property was used.") => true)
        (collection/item-count e) => 0)
      ;; conflicts with updated-at property 
      (let [response (create-item-with-api {:name i :updated-at "foo"})]
        (:status response) => 422
        (response-mime-type response) => (mime-type :text)
        (let [body (body-from-response response)]
          (.contains body "A reserved property was used.") => true)
        (collection/item-count e) => 0)
      ;; check if collection is still empty
      (let [response (api-request :get "/e/i" {
        :headers {
          :Accept (mime-type :item)
        }})]
        (:status response) => 404
        (body? response) => false))
    
    ;; no accept type - 201 Created
    ;; curl -i --header "Accept-Charset: utf-8" --header "Content-Type: application/vnd.fcms.item+json;version=1" -X POST -d '{"name":"i"}' http://localhost:3000/c/
    (fact "creating an item without an Accept header"
      (let [response (create-item-with-api
                       {:Content-Type (mime-type :item)}
                       {:name i})]
        (:status response) => 201
        (response-mime-type response) => (mime-type :item)
        (response-location response) => "/e/i"
        (json? response) => true
        ;; Get the created item and make sure it's right
        (item/get-item e i) => (contains
                                 {:collection e
                                   :name i
                                   :slug i
                                   :version 1})
        (collection/item-count e) => 1))
    
    ;; wrong accept type - 406 Not Acceptable
    ;; curl -i --header "Accept: application/vnd.fcms.collection+json;version=1" --header "Accept-Charset: utf-8" --header "Content-Type: application/vnd.fcms.item+json;version=1" -X POST -d '{"name":"i"}' http://localhost:3000/c/
    (fact "creating an item with wrong Accept header"
      (let [response (create-item-with-api
                       {:Accept (mime-type :collection)
                        :Content-Type (mime-type :item)}
                       {:name i})]
        (:status response) => 406
        (response-mime-type response) => (mime-type :text)
        (response-location response) => nil
        (let [body (body-from-response response)]
          (.contains body "Acceptable media type: application/vnd.fcms.item+json;version=1") => true
          (.contains body "Acceptable charset: utf-8") => true)
        (collection/item-count e) => 0        
        ;; Get the created item and make sure it's right
        (let [response (api-request :get "/e/i" {:headers
                                                  {:Accept (mime-type :item)}})]
          (:status response) => 404
          (body? response) => false)))
    
    ;; no content type - 201 Created
    ;; curl -i --header "Accept: application/vnd.fcms.item+json;version=1" --header "Accept-Charset: utf-8" -X POST -d '{"name":"i"}' http://localhost:3000/c/
    (fact "create an item without a Content-Type header"
      (let [response (create-item-with-api
                       {:Accept (mime-type :item)}
                       {:name i})]
        (:status response) => 201
        (response-mime-type response) => (mime-type :item)
        (response-location response) => "/e/i"
        (json? response) => true
        ;; Get the created item and make sure it's right
        (item/get-item e i) => (contains
                                 {:collection e
                                   :name i
                                   :slug i
                                   :version 1})
        (collection/item-count e) => 1))
    
    ;; wrong content type - 415 Unsupported Media Type
    ;; curl -i --header "Accept: application/vnd.fcms.item+json;version=1" --header "Accept-Charset: utf-8" --header "Content-Type: application/vnd.fcms.collection+json;version=1" -X POST -d '{"name":"i"}' http://localhost:3000/c/    
    (fact "create an item with the wrong Content-Type header"
      (let [response (create-item-with-api
                       {:Accept (mime-type :item)
                        :Content-Type (mime-type :collection)}
                       {:name i})]
        (:status response) => 415
        (response-mime-type response) => (mime-type :text)
        (response-location response) => nil
        (let [body (body-from-response response)]
          (.contains body "Acceptable media type: application/vnd.fcms.item+json;version=1") => true
          (.contains body "Acceptable charset: utf-8") => true)        
        ;; Get the created item and make sure it's right
        (let [response (api-request :get "/e/i" {:headers
                                                  {:Accept (mime-type :item)}})]
          (:status response) => 404
          (body? response) => false)
        (collection/item-count e) => 0))))