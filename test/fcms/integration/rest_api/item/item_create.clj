(ns fcms.integration.rest-api.item.item-create
  "Integration tests for creating items with the REST API."
  (:require [clj-time.format :refer (parse)]
            [midje.sweet :refer :all]
            [fcms.lib.resources :refer :all]
            [fcms.lib.rest-api-mock :refer :all]
            [fcms.lib.body :refer (verify-item-links)]
            [fcms.lib.check :refer (about-now?)]
            [fcms.resources.collection :as collection]
            [fcms.resources.collection-resource :as resource]
            [fcms.resources.item :as item]))

;; The system should store newly created valid items into a collection and handle the following scenarios:
;;
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

;; ----- Utilities -----

(defn- create-item-with-api
  "Makes an API request to create the item and returns the response."  
  ([body]
     (api-request :post "/e/" {
      :headers {
        :Accept (mime-type :item)
        :Content-Type (mime-type :item)}
      :body body}))
  ([headers body]
     (api-request :post "/e/" {
        :headers headers
        :body body})))

;; ----- Tests -----

(with-state-changes [(before :facts (reset-collection e))
                     (after :facts (collection/delete-collection e))]

  (facts "about using the REST API to create a valid new item"

    ;; all good, no slug - 201 Created
    ;; curl -i --header "Accept: application/vnd.fcms.item+json;version=1" --header "Accept-Charset: utf-8" --header "Content-Type: application/vnd.fcms.item+json;version=1" -X POST -d '{"name":"i"}' http://localhost:3000/c/
    (fact "when no slug is specified"
      ;; Create the item
      (let [response (create-item-with-api {:name i})]
        (:status response) => 201
        (response-mime-type response) => (mime-type :item)
        (response-location response) => "/e/i"
        (json? response) => true
        (let [item (body-from-response response)]
          (:name item) => i
          (:slug item) => i
          (:collection item) => e
          (:version item) => 1
          (instance? timestamp (parse (:created-at item))) => true
          (about-now? (:created-at item)) => true
          (:created-at item) => (:updated-at item)
          (verify-item-links e i (:links item))))
      ;; Get the created item and make sure it's right
      (item/get-item e i) => (contains {
        :collection e
        :name i
        :slug i
        :version 1})
      (collection/item-count e) => 1)

    ;; all good, generated slug is different than the provided name - 201 Created
    ;; curl -i --header "Accept: application/vnd.fcms.item+json;version=1" --header "Accept-Charset: utf-8" --header "Content-Type: application/vnd.fcms.item+json;version=1" -X POST -d '{"name":" -tHiS #$is%?-----ελληνικήalso-მივჰხვდემასჩემსაãالزجاجوهذالايؤلمني-slüg♜-♛-☃-✄-✈  - "}' http://localhost:3000/c/
    (fact "when the generated slug is different than the provided name"
      ;; Create the item
      (let [response (create-item-with-api {:name long-unicode-name})]
        (:status response) => 201
        (response-mime-type response) => (mime-type :item)
        (response-location response) => (str "/e/" generated-slug)
        (json? response) => true
        (let [item (body-from-response response)]
          (:name item) => long-unicode-name
          (:slug item) => generated-slug
          (:collection item) => e
          (:version item) => 1
          (instance? timestamp (parse (:created-at item))) => true
          (about-now? (:created-at item)) => true
          (:created-at item) => (:updated-at item)
          (verify-item-links e generated-slug (:links item))))
      ;; Get the created item and make sure it's right
      (item/get-item e generated-slug) => (contains {
        :collection e
        :name long-unicode-name
        :slug generated-slug
        :version 1})
      (collection/item-count "e") => 1)

    ;; all good, generated slug is already used - 201 Created
    ;; curl -i --header "Accept: application/vnd.fcms.item+json;version=1" --header "Accept-Charset: utf-8" --header "Content-Type: application/vnd.fcms.item+json;version=1" -X POST -d '{"name":"i"}' http://localhost:3000/c/
    ;; curl -i --header "Accept: application/vnd.fcms.item+json;version=1" --header "Accept-Charset: utf-8" --header "Content-Type: application/vnd.fcms.item+json;version=1" -X POST -d '{"name":"i"}' http://localhost:3000/c/
    ;; curl -i --header "Accept: application/vnd.fcms.item+json;version=1" --header "Accept-Charset: utf-8" --header "Content-Type: application/vnd.fcms.item+json;version=1" -X POST -d '{"name":"i"}' http://localhost:3000/c/
    (fact "when the generated slug is already used"
      ;; Create the first item
      (item/create-item e i)
      ;; Create the second item with the same name
      (let [response (create-item-with-api {:name i})]
        (:status response) => 201
        (response-mime-type response) => (mime-type :item)
        (response-location response) => "/e/i-1"
        (json? response) => true
        (item/get-item e "i-1") => (contains {
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
        (item/get-item e "i-2") => (contains {
          :collection e
          :name i
          :slug "i-2"
          :version 1}))
      ;; Make sure all 3 exist in the collection
      (collection/item-count e) => 3)

    ;; all good, with slug - 201 Created
    ;;curl -i --header "Accept: application/vnd.fcms.item+json;version=1" --header "Accept-Charset: utf-8" --header "Content-Type: application/vnd.fcms.item+json;version=1" -X POST -d '{"name":"i", "slug":"another-i"}' http://localhost:3000/c/
    (fact "with a provided slug"
      (let [response (create-item-with-api {:name i :slug "another-i"})]
        (:status response) => 201
        (response-mime-type response) => (mime-type :item)
        (response-location response) => "/e/another-i"
        (json? response) => true)
      ;; Get the created item and make sure it's right
      (item/get-item e "another-i") => (contains {
       :collection e
       :name i
       :slug "another-i"
       :version 1})
      (collection/item-count e) => 1)

    ;; all good, unicode in the body - 201 Created
    ;; curl -i --header "Accept: application/vnd.fcms.item+json;version=1" --header "Accept-Charset: utf-8" --header "Content-Type: application/vnd.fcms.item+json;version=1" -X POST -d '{"name":"私はガラスを食", "slug":"i", "description":"er stîget ûf mit grôzer kraft Τη γλώσσα μου έδωσαν ελληνική მივჰხვდე მას ჩემსა الزجاج و هذا لا يؤلمني. मैं काँच खा सकता ฉันกินกระจกได้ לא מזיק Mogę jeść szkło €"}' http://localhost:3000/c/
    (fact "containing unicode"
      (let [response (create-item-with-api {:name unicode-name :slug i :description unicode-description})]
        (:status response) => 201
        (response-mime-type response) => (mime-type :item)
        (response-location response) => "/e/i"
        (json? response) => true)
      ;; Get the created item and make sure it's right
      (item/get-item e i) => (contains {
        :collection e
        :name unicode-name
        :description unicode-description
        :slug i
        :version 1})
      (collection/item-count e) => 1)

    ;; no accept type - 201 Created
    ;; curl -i --header "Accept-Charset: utf-8" --header "Content-Type: application/vnd.fcms.item+json;version=1" -X POST -d '{"name":"i"}' http://localhost:3000/c/
    (fact "without an Accept header"
      (let [response (create-item-with-api {:Content-Type (mime-type :item)} {:name i})]
        (:status response) => 201
        (response-mime-type response) => (mime-type :item)
        (response-location response) => "/e/i"
        (json? response) => true)
      ;; Get the created item and make sure it's right
      (item/get-item e i) => (contains {
        :collection e
        :name i
        :slug i
        :version 1})
      (collection/item-count e) => 1)

    ;; no content type - 201 Created
    ;; curl -i --header "Accept: application/vnd.fcms.item+json;version=1" --header "Accept-Charset: utf-8" -X POST -d '{"name":"i"}' http://localhost:3000/c/
    (fact "without a Content-Type header"
      (let [response (create-item-with-api {:Accept (mime-type :item)} {:name i})]
        (:status response) => 201
        (response-mime-type response) => (mime-type :item)
        (response-location response) => "/e/i"
        (json? response) => true)
      ;; Get the created item and make sure it's right
      (item/get-item e i) => (contains {
        :collection e
        :name i
        :slug i
        :version 1})
      (collection/item-count e) => 1)

    ;; no charset - 201 Created
    ;; curl -i --header "Accept: application/vnd.fcms.item+json;version=1" --header "Content-Type: application/vnd.fcms.item+json;version=1" -X POST -d '{"name":"i"}' http://localhost:3000/c/
    (fact "without an Accept-Charset header"
      (let [response (api-request :post "/e/" {
        :skip-charset true
        :headers {
          :Accept (mime-type :item)
          :Content-Type (mime-type :item)}
        :body {:name i}})]
        (:status response) => 201
        (response-mime-type response) => (mime-type :item)
        (response-location response) => "/e/i"
        (json? response) => true)
      ;; Get the created item and make sure it's right
      (item/get-item e i) => (contains {
        :collection e
        :name i
        :slug i
        :version 1})        
      (collection/item-count e) => 1))

  (facts "about attempting to use the REST API to create valid new items"
    
    ;; conflicting reserved properties - 422 Unprocessable Entity
    ;; curl -i --header "Accept: application/vnd.fcms.item+json;version=1" --header "Accept-Charset: utf-8" --header "Content-Type: application/vnd.fcms.item+json;version=1" -X POST -d '{"name":"i", "id":"foo"}' http://localhost:3000/c/
    ;; curl -i --header "Accept: application/vnd.fcms.item+json;version=1" --header "Accept-Charset: utf-8" --header "Content-Type: application/vnd.fcms.item+json;version=1" -X POST -d '{"name":"i", "version":"foo"}' http://localhost:3000/c/
    ;; curl -i --header "Accept: application/vnd.fcms.item+json;version=1" --header "Accept-Charset: utf-8" --header "Content-Type: application/vnd.fcms.item+json;version=1" -X POST -d '{"name":"i", "collection":"foo"}' http://localhost:3000/c/
    ;; curl -i --header "Accept: application/vnd.fcms.item+json;version=1" --header "Accept-Charset: utf-8" --header "Content-Type: application/vnd.fcms.item+json;version=1" -X POST -d '{"name":"i", "links":"foo"}' http://localhost:3000/c/
    ;; curl -i --header "Accept: application/vnd.fcms.item+json;version=1" --header "Accept-Charset: utf-8" --header "Content-Type: application/vnd.fcms.item+json;version=1" -X POST -d '{"name":"i", "categories":"foo"}' http://localhost:3000/c/
    ;; curl -i --header "Accept: application/vnd.fcms.item+json;version=1" --header "Accept-Charset: utf-8" --header "Content-Type: application/vnd.fcms.item+json;version=1" -X POST -d '{"name":"i", "type":"foo"}' http://localhost:3000/c/
    ;; curl -i --header "Accept: application/vnd.fcms.item+json;version=1" --header "Accept-Charset: utf-8" --header "Content-Type: application/vnd.fcms.item+json;version=1" -X POST -d '{"name":"i", "created-at":"foo"}' http://localhost:3000/c/
    ;; curl -i --header "Accept: application/vnd.fcms.item+json;version=1" --header "Accept-Charset: utf-8" --header "Content-Type: application/vnd.fcms.item+json;version=1" -X POST -d '{"name":"i", "updated-at":"foo"}' http://localhost:3000/c/
    (fact "with a property that conflicts with a reserved property"
      ;; conflicts with each reserved property
      (doseq [keyword-name resource/reserved-properties]
        (let [response (create-item-with-api {:name i keyword-name "foo"})
              body (body-from-response response)]
          (:status response) => 422
          (response-mime-type response) => (mime-type :text)
          (.contains body "A reserved property was used.") => true)
        ;; check if collection is still empty
        (collection/item-count e) => 0))
        
    ;; wrong accept type - 406 Not Acceptable
    ;; curl -i --header "Accept: application/vnd.fcms.collection+json;version=1" --header "Accept-Charset: utf-8" --header "Content-Type: application/vnd.fcms.item+json;version=1" -X POST -d '{"name":"i"}' http://localhost:3000/c/
    (fact "with the wrong Accept header"
      (let [response (create-item-with-api {
        :Accept (mime-type :collection)
        :Content-Type (mime-type :item)}
        {:name i})]
        (:status response) => 406
        (response-mime-type response) => (mime-type :text)
        (response-location response) => nil
        (let [body (body-from-response response)]
          (.contains body "Acceptable media type: application/vnd.fcms.item+json;version=1") => true
          (.contains body "Acceptable charset: utf-8") => true)
        (collection/item-count e) => 0)      
      ;; check if collection is still empty
      (collection/item-count e) => 0)
        
    ;; wrong content type - 415 Unsupported Media Type
    ;; curl -i --header "Accept: application/vnd.fcms.item+json;version=1" --header "Accept-Charset: utf-8" --header "Content-Type: application/vnd.fcms.collection+json;version=1" -X POST -d '{"name":"i"}' http://localhost:3000/c/
    (fact "with the wrong Content-Type header"
      (let [response (create-item-with-api {
        :Accept (mime-type :item)
        :Content-Type (mime-type :collection)}
        {:name i})]
        (:status response) => 415
        (response-mime-type response) => (mime-type :text)
        (response-location response) => nil
        (let [body (body-from-response response)]
          (.contains body "Acceptable media type: application/vnd.fcms.item+json;version=1") => true
          (.contains body "Acceptable charset: utf-8") => true))
      ;; check if collection is still empty
      (collection/item-count e) => 0)
    
    ;; wrong charset - 406 Not Acceptable
    ;; curl -i --header "Accept: application/vnd.fcms.item+json;version=1" --header "Accept-Charset: iso-8859-1" --header "Content-Type: application/vnd.fcms.item+json;version=1" -X POST -d '{"name":"i"}' http://localhost:3000/c/
    (fact "with the wrong Accept-Charset header"
      (let [response (create-item-with-api {
        :Accept-Charset "iso-8859-1"
        :Accept (mime-type :item)
        :Content-Type (mime-type :item)}
        {:name i})]
        (:status response) => 406
        (response-mime-type response) => (mime-type :text)
        (response-location response) => nil
        (let [body (body-from-response response)]
          (.contains body "Acceptable media type: application/vnd.fcms.item+json;version=1") => true
          (.contains body "Acceptable charset: utf-8") => true))
      ;; check if collection is still empty
      (collection/item-count e) => 0)

    ;; no body - 400 Bad Request
    ;; curl -i --header "Accept: application/vnd.fcms.item+json;version=1" --header "Content-Type: application/vnd.fcms.item+json;version=1" --header "Charset: UTF-8" -X POST http://localhost:3000/c/
    (fact "with no body"
      (let [response (api-request :post "/e/" {
        :skip-body true
        :headers {
          :Accept (mime-type :item)
          :Content-Type (mime-type :item)}})]
        (:status response) => 400
        (response-mime-type response) => (mime-type :text)
        (.contains (body-from-response response) "Bad request.") => true)
      ;; check if collection is still empty
      (collection/item-count e) => 0)
    
    ;; body, but not valid JSON - 400 Bad Request
    ;; curl -i --header "Accept: application/vnd.fcms.item+json;version=1" --header "Content-Type: application/vnd.fcms.item+json;version=1" --header "Charset: UTF-8" -X POST -d 'Hi Mom!' http://localhost:3000/c/
    ;; curl -i --header "Accept: application/vnd.fcms.item+json;version=1" --header "Content-Type: application/vnd.fcms.item+json;version=1" --header "Charset: UTF-8" -X POST -d '{"name":"g' http://localhost:3000/c/
    (fact "with an invalid body"
      (doseq [bad-body ["Hi Mom!" "{'name': 'i'"]]
        (let [response (create-item-with-api bad-body)
              body (body-from-response response)]
          (:status response) => 400
          (response-mime-type response) => (mime-type :text)
          (response-location response) => nil
          (.contains body "Bad request.") => true)
        ;; check if collection is still empty
        (collection/item-count e) => 0))
    
    ;; collection doesn't exist - 404 Not Found
    ;; curl -i --header "Accept: application/vnd.fcms.item+json;version=1" --header "Content-Type: application/vnd.fcms.item+json;version=1" --header "Charset: UTF-8" -X POST -d '{"name":"i"}' http://localhost:3000/not-here/
    (fact "in a collection that doesn't exist"
      (let [response (api-request :post "/not-here/" {
          :headers {
            :Accept (mime-type :item)
            :Content-Type (mime-type :item)}
          :body {:name i}})]
        (:status response) => 404
        (response-mime-type response) => (mime-type :text)
        (let [body (body-from-response response)]
          (.contains body "Collection not found.") => true))
      ;; check if collection is still empty
      (collection/item-count e) => 0)

    ;; no "name" in body - 422 Unprocessable Entity
    ;; curl -i --header "Accept: application/vnd.fcms.item+json;version=1" --header "Content-Type: application/vnd.fcms.item+json;version=1" --header "Charset: UTF-8" -X POST -d '{"slug":"i"}' http://localhost:3000/c/
    (fact "without a name"
      (let [response (create-item-with-api {:slug i})
            body (body-from-response response)]
        (:status response) => 422
        (response-mime-type response) => (mime-type :text)
        (.contains body "Name is required.") => true)
      ;; check if collection is still empty
      (collection/item-count e) => 0)
    
    ;; slug specified in body is already used - 422 Unprocessable Entity
    ;; curl -i --header "Accept: application/vnd.fcms.item+json;version=1" --header "Content-Type: application/vnd.fcms.item+json;version=1" --header "Charset: UTF-8" -X POST -d '{"name":"another i", "slug":"i"}' http://localhost:3000/c/
    (fact "with a slug that's already used in the collection"
      ;; Create an item to conflict with
      (item/create-item e i)
      ;; Get the created item and make sure it's right
      (item/get-item e i) => (contains {
        :collection e
        :name i
        :slug i
        :version 1})
      (collection/item-count e) => 1
      (let [response (create-item-with-api {:name "another i" :slug i})
            body (body-from-response response)]
        (:status response) => 422
        (response-mime-type response) => (mime-type :text)
        (response-location response) => nil
        (.contains body "Slug already used in the collection.") => true)
      ;; check if collection still has only the original item
      (collection/item-count e) => 1)

    ;; slug specified in body is invalid - 422 Unprocessable Entity
    ;; curl -i --header "Accept: application/vnd.fcms.item+json;version=1" --header "Content-Type: application/vnd.fcms.item+json;version=1" --header "Charset: UTF-8" -X POST -d '{"name":"i", "slug":"I i"}' http://localhost:3000/c/
    (fact "with a slug that's invalid"
      (let [response (create-item-with-api {:name i :slug "I i"})
            body (body-from-response response)]
        (:status response) => 422
        (response-mime-type response) => (mime-type :text)
        (response-location response) => nil
        (.contains body "Invalid slug.") => true)
      ;; check if collection is still empty
      (collection/item-count e) => 0)))