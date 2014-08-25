(ns fcms.integration.rest-api.collection.collection-create
  (:require [midje.sweet :refer :all]
            [fcms.lib.resources :refer :all]
            [fcms.resources.collection :refer :all]
            [fcms.lib.rest-api-mock :refer :all]
            [fcms.resources.common :as common]
            [fcms.resources.collection :as collection]))

;; Creating collections with the REST API

;; The system should store newly created valid collections and handle the following scenarios:

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
;; no name in body
;; slug specified in body is already used
;; slug specified in body is invalid

;; ----- Utilities -----

(defn- create-collection-with-api
  "Makes an API request to create the collection and returns the response."  
  ([body]
     (api-request :post "/" {
      :headers {
        :Accept-Charset "utf-8"
        :Accept (mime-type :collection)
        :Content-Type (mime-type :collection)}
      :body body}))
  ([headers body]
     (api-request :post "/" {
        :headers headers
        :body body})))

;; ----- Tests -----
(with-state-changes [(after :facts (doseq [coll (all-collections)]
                                     (delete-collection (:slug coll))))]

  (facts "about using the REST API to create a valid new collection"

    ;; all good, no slug - 201 Created
    ;; curl -i --header "Accept: application/vnd.fcms.collection+json;version=1" --header "Accept-Charset: utf-8" --header "Content-Type: application/vnd.fcms.collection+json;version=1" -X POST -d '{"name":"c"}' http://localhost:3000/
    (fact "when no slug is specified"
      ;; Create the collection
      (let [response (create-collection-with-api {:name c})]
        (:status response) => 201
        (response-mime-type response) => (mime-type :collection)
        (response-location response) => "/c"
        (json? response) => true)
      ;; Get the created collection and make sure it's right
      (collection/get-collection c) => (contains {
        :slug c
        :name c
        :version 1})
      ;; Collection is empty?
      (collection/item-count c) => 0)

    (fact "when the generated slug is different than the provided name"
      ;; Create the collection
      (let [response (create-collection-with-api {:name long-unicode-name})]
        (:status response) => 201
        (response-mime-type response) => (mime-type :collection)
        (response-location response) => (str "/" generated-slug)
        (json? response) => true)
      ;; Get the created collection and make sure it's right
      (collection/get-collection "this-is-also-a-slug") => (contains {
        :slug generated-slug
        :name long-unicode-name
        :version 1})
      ;; Collection is empty?
      (collection/item-count "this-is-also-a-slug") => 0)

    (fact "when the generated slug is already used"
      ;; Create first collection
      (let [response (create-collection-with-api {:name c})]
        (:status response) => 201
        (response-mime-type response) => (mime-type :collection)
        (response-location response) => "/c"
        (json? response) => true)
      ;; Get the created collection and make sure it's right
      (collection/get-collection c) => (contains {
        :slug c
        :name c
        :version 1})
      ;; Collection is empty?
      (collection/item-count c) => 0
      
      ;; Create second collection
      (let [response (create-collection-with-api {:name c})]
        (:status response) => 201
        (response-mime-type response) => (mime-type :collection)
        (response-location response) => "/c-1"
        (json? response) => true)
      ;; Get the created collection and make sure it's right
      (collection/get-collection "c-1") => (contains {
        :slug "c-1"
        :name c
        :version 1})
      ;; Collection is empty?
      (collection/item-count c) => 0)

    (fact "with a provided slug"
      ;; Create the collection
      (let [response (create-collection-with-api {:name c :slug "another-c"})]
        (:status response) => 201
        (response-mime-type response) => (mime-type :collection)
        (response-location response) => "/another-c"
        (json? response) => true)
      ;; Get the created collection and make sure it's right
      (collection/get-collection "another-c") => (contains {
        :slug "another-c"
        :name c
        :version 1})
      ;; Collection is empty?
      (collection/item-count "another-c") => 0)

    (fact "containing unicode"
      ;; Create the collection
      (let [response (create-collection-with-api {:name unicode-name :slug c :description unicode-description})]
        (:status response) => 201
        (response-mime-type response) => (mime-type :collection)
        (response-location response) => "/c"
        (json? response) => true)
      ;; Get the created collection and make sure it's right
      (collection/get-collection c) => (contains {
        :slug c
        :name unicode-name
        :version 1})
      ;; Collection is empty?
      (collection/item-count c) => 0)

    (fact "without an Accept header"
      (let [response (create-collection-with-api {:Content-Type (mime-type :collection) :Accept-Charset "utf-8"} {:name c})]
        (:status response) => 201
        (response-mime-type response) => (mime-type :collection)
        (response-location response) => "/c"
        (json? response) => true)
      ;; Get the created collection and make sure it's right
      (collection/get-collection c) => (contains {
        :slug c
        :name c
        :version 1})
      ;; Collection is empty?
      (collection/item-count c) => 0)

    (fact "without a Content-Type header"
      (let [response (create-collection-with-api {:Accept (mime-type :collection) :Accept-Charset "utf-8"} {:name c})]
        (:status response) => 201
        (response-mime-type response) => (mime-type :collection)
        (response-location response) => "/c"
        (json? response) => true)
      ;; Get the created collection and make sure it's right
      (collection/get-collection c) => (contains {
        :slug c
        :name c
        :version 1})
      ;; Collection is empty?
      (collection/item-count c) => 0)

    (fact "without an Accept-Charset header"
      (let [response (create-collection-with-api {:Accept (mime-type :collection) :Accept-Charset "utf-8"} {:name c})]
        (:status response) => 201
        (response-mime-type response) => (mime-type :collection)
        (response-location response) => "/c"
        (json? response) => true)
      ;; Get the created collection and make sure it's right
      (collection/get-collection c) => (contains {
        :slug c
        :name c
        :version 1})
      ;; Collection is empty?
      (collection/item-count c) => 0))

  (facts "about attempting to use the REST API to create a collection"
    
    ;; conflicting reserved properties - 422 Unprocessable Entity
    ;; curl -i --header "Accept: application/vnd.fcms.collection+json;version=1" --header "Accept-Charset: utf-8" --header "Content-Type: application/vnd.fcms.collection+json;version=1" -X POST -d '{"name":"c", "id":"foo"}' http://localhost:3000/
    ;; curl -i --header "Accept: application/vnd.fcms.collection+json;version=1" --header "Accept-Charset: utf-8" --header "Content-Type: application/vnd.fcms.collection+json;version=1" -X POST -d '{"name":"c", "version":"foo"}' http://localhost:3000/
    ;; curl -i --header "Accept: application/vnd.fcms.collection+json;version=1" --header "Accept-Charset: utf-8" --header "Content-Type: application/vnd.fcms.collection+json;version=1" -X POST -d '{"name":"c", "links":"foo"}' http://localhost:3000/
    ;; curl -i --header "Accept: application/vnd.fcms.collection+json;version=1" --header "Accept-Charset: utf-8" --header "Content-Type: application/vnd.fcms.collection+json;version=1" -X POST -d '{"name":"c", "type":"foo"}' http://localhost:3000/
    ;; curl -i --header "Accept: application/vnd.fcms.collection+json;version=1" --header "Accept-Charset: utf-8" --header "Content-Type: application/vnd.fcms.collection+json;version=1" -X POST -d '{"name":"c", "created-at":"foo"}' http://localhost:3000/
    ;; curl -i --header "Accept: application/vnd.fcms.collection+json;version=1" --header "Accept-Charset: utf-8" --header "Content-Type: application/vnd.fcms.collection+json;version=1" -X POST -d '{"name":"c", "updated-at":"foo"}' http://localhost:3000/
    (fact "with a property that conflicts with a reserved property"
      ;; conflicts with each reserved property
      (doseq [keyword-name common/reserved-properties]
        (let [response (create-collection-with-api {:name c keyword-name "foo"})
              body (body-from-response response)]
          (:status response) => 422
          (response-mime-type response) => (mime-type :text)
          (.contains body "A reserved property was used.") => true)
        ;; check that the collection does not exist
        (collection/get-collection c) => nil))

    ;; wrong accept type - 406 Not Acceptable
    ;; curl -i --header "Accept: application/vnd.fcms.item+json;version=1" --header "Accept-Charset: utf-8" --header "Content-Type: application/vnd.fcms.collection+json;version=1" -X POST -d '{"name":"c"}' http://localhost:3000/
    (fact "with the wrong Accept header"
      (let [response (create-collection-with-api {
        :Accept (mime-type :item)
        :Content-Type (mime-type :collection)}
        {:name c})]
        (:status response) => 406
        (response-mime-type response) => (mime-type :text)
        (response-location response) => nil
        (let [body (body-from-response response)]
          (.contains body "Acceptable media type: application/vnd.fcms.collection+json;version=1") => true
          (.contains body "Acceptable charset: utf-8") => true))
      ;; check that the collection does not exist
      (collection/get-collection c) => nil)

    ;; wrong content type - 415 Unsupported Media Type
    ;; curl -i --header "Accept: application/vnd.fcms.collection+json;version=1" --header "Accept-Charset: utf-8" --header "Content-Type: application/vnd.fcms.item+json;version=1" -X POST -d '{"name":"c"}' http://localhost:3000/
    (fact "with the wrong Content-Type header"
      (let [response (create-collection-with-api {
        :Accept (mime-type :collection)
        :Content-Type (mime-type :item)}
        {:name c})]
        (:status response) => 415
        (response-mime-type response) => (mime-type :text)
        (response-location response) => nil
        (let [body (body-from-response response)]
          (.contains body "Acceptable media type: application/vnd.fcms.collection+json;version=1") => true
          (.contains body "Acceptable charset: utf-8") => true))
      ;; check that the collection does not exist
      (collection/get-collection c) => nil)

    ;; wrong charset - 406 Not Acceptable
    ;; curl -i --header "Accept: application/vnd.fcms.collection+json;version=1" --header "Accept-Charset: iso-8859-1" --header "Content-Type: application/vnd.fcms.collection+json;version=1" -X POST -d '{"name":"c"}' http://localhost:3000/
    (fact "with the wrong Accept-Charset header"
      (let [response (create-collection-with-api {
        :Accept-Charset "iso-8859-1"
        :Accept (mime-type :collection)
        :Content-Type (mime-type :collection)}
        {:name c})]
        (:status response) => 406
        (response-mime-type response) => (mime-type :text)
        (response-location response) => nil
        (let [body (body-from-response response)]
          (.contains body "Acceptable media type: application/vnd.fcms.collection+json;version=1") => true
          (.contains body "Acceptable charset: utf-8") => true))
      ;; check that the collection does not exist
      (collection/get-collection c) => nil)

    ;; no body - 400 Bad Request
    ;; curl -i --header "Accept: application/vnd.fcms.collection+json;version=1" --header "Content-Type: application/vnd.fcms.collection+json;version=1" --header "Charset: UTF-8" -X POST http://localhost:3000/
    (fact "with no body"
      (let [response (api-request :post "/" {
        :skip-body true
        :headers {
          :Accept (mime-type :collection)
          :Content-Type (mime-type :collection)}})]
        (:status response) => 400
        (response-mime-type response) => (mime-type :text)
        (.contains (body-from-response response) "Bad request.") => true)
      ;; check that the collection does not exist
      (collection/get-collection c) => nil)

    ;; body, but not valid JSON - 400 Bad Request
    ;; curl -i --header "Accept: application/vnd.fcms.collection+json;version=1" --header "Content-Type: application/vnd.fcms.collection+json;version=1" --header "Charset: UTF-8" -X POST -d 'Hi Mom!' http://localhost:3000/
    ;; curl -i --header "Accept: application/vnd.fcms.collection+json;version=1" --header "Content-Type: application/vnd.fcms.collection+json;version=1" --header "Charset: UTF-8" -X POST -d '{"name":"c' http://localhost:3000/
    (fact "with an invalid body"
      (doseq [bad-body ["Hi Mom!" "{'name': 'c'"]]
        (let [response (create-collection-with-api bad-body)
              body (body-from-response response)]
          (:status response) => 400
          (response-mime-type response) => (mime-type :text)
          (response-location response) => nil
          (.contains body "Bad request.") => true)
        ;; check that the collection does not exist
        (collection/get-collection c) => nil))

    ;; no "name" in body - 422 Unprocessable Entity
    ;; curl -i --header "Accept: application/vnd.fcms.collection+json;version=1" --header "Content-Type: application/vnd.fcms.collection+json;version=1" --header "Charset: UTF-8" -X POST -d '{"slug":"c"}' http://localhost:3000/
    (fact "without a name"
      (let [response (create-collection-with-api {:slug c})
            body (body-from-response response)]
        (:status response) => 422
        (response-mime-type response) => (mime-type :text)
        (.contains body "Name is required.") => true)
      ;; check that the collection does not exist
      (collection/get-collection c) => nil)

    ;; slug specified in body is already used - 422 Unprocessable Entity
    ;; curl -i --header "Accept: application/vnd.fcms.collection+json;version=1" --header "Content-Type: application/vnd.fcms.collection+json;version=1" --header "Charset: UTF-8" -X POST -d '{"name":"another c", "slug":"c"}' http://localhost:3000/
    (fact "with a slug that's already used in the collection"
      ;; Create a collection to conflict with
      (collection/create-collection c)
      ;; Get the created collection and make sure it's right
      (collection/get-collection c) => (contains {
        :name c
        :slug c
        :version 1})
      (collection/collection-count) => 1
      (let [response (create-collection-with-api {:name "another c" :slug c})
            body (body-from-response response)]
        (:status response) => 422
        (response-mime-type response) => (mime-type :text)
        (response-location response) => nil
        (.contains body "Slug already used.") => true)
      ;; check if only the original collection still exists
      (collection/get-collection c) => (contains {
        :name c
        :slug c
        :version 1})
      (collection/collection-count) => 1)

    ;; slug specified in body is invalid - 422 Unprocessable Entity
    ;; curl -i --header "Accept: application/vnd.fcms.collection+json;version=1" --header "Content-Type: application/vnd.fcms.collection+json;version=1" --header "Charset: UTF-8" -X POST -d '{"name":"c", "slug":"C c"}' http://localhost:3000/
    (fact "with a slug that's invalid"
      (let [response (create-collection-with-api {:name c :slug "C c"})
            body (body-from-response response)]
        (:status response) => 422
        (response-mime-type response) => (mime-type :text)
        (response-location response) => nil
        (.contains body "Invalid slug.") => true)
      ;; check that the collection does not exist
      (collection/get-collection c) => nil)))