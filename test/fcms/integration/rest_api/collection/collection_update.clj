(ns fcms.integration.rest-api.collection.collection-update
  "Integration tests for updating collections with the REST API."
  (:require [midje.sweet :refer :all]
            [fcms.lib.resources :refer :all]
            [fcms.lib.rest-api-mock :refer :all]
            [fcms.lib.body :refer (verify-collection-links)]
            [fcms.resources.collection :as collection]))

;; The system should update collections stored in the system and handle the following scenarios:
;;
;; all good - no slug
;; all good - with same slug
;; all good - with different slug
;; all good - unicode in the body
;; all good - no name in body
;; no accept
;; no content header
;; no charset
;; conflicting reserved properties
;; wrong accept
;; wrong content header
;; wrong charset
;; no body
;; body, but not valid JSON
;; collection doesn't exist
;; different slug specified in body is already used
;; different slug specified in body is invalid

(with-state-changes [(before :facts (do (delete-all-collections)(reset-collection c)))
                     (after :facts (delete-all-collections))]

  (facts "about using the REST API to update a collection"

    ;; all good - 200 OK
    ;; curl -i --header "Accept: application/vnd.fcms.collection+json;version=1" --header "Accept-Charset: utf-8" --header "Content-Type: application/vnd.fcms.collection+json;version=1" -X PUT -d '{"name":"c-prime", "c":"c"}' http://localhost:3000/c/
    (fact "without providing a new slug"
      (let [response (api-request :put "/c" {
        :headers {
          :Content-Type (mime-type :collection)
          :Accept (mime-type :collection)}
        :body {
          :name "c-prime"
          :c c}})]
        (:status response) => 200
        (response-mime-type response) => (mime-type :collection)
        (json? response) => true
        (verify-collection-links c (:links (body-from-response response))))
      ;; check that the update worked
      (let [coll (collection/get-collection c)]
        coll => (contains {
          :name "c-prime"
          :c c
          :slug c
          :version 2})
        (:description coll) => nil)
      ;; check it didn't create another collection
      (collection/collection-count) => 1)

    ;; all good, with same slug - 200 OK
    ;; curl -i --header "Accept: application/vnd.fcms.collection+json;version=1" --header "Accept-Charset: utf-8" --header "Content-Type: application/vnd.fcms.collection+json;version=1" -X PUT -d '{"name":"c-prime", "slug": "c", c":"c"}' http://localhost:3000/c/
    (fact "with the same slug"
      (let [response (api-request :put "/c" {
        :headers {
          :Content-Type (mime-type :collection)
          :Accept (mime-type :collection)}
        :body {
          :name "c-prime"
          :slug c
          :c c}})]
        (:status response) => 200
        (response-mime-type response) => (mime-type :collection)
        (json? response) => true
        (verify-collection-links c (:links (body-from-response response))))
      ;; check that the update worked
      (let [coll (collection/get-collection c)]
        coll => (contains {
          :name "c-prime"
          :c c
          :slug c
          :version 2})
        (:description coll) => nil)
      ;; check it didn't create another collection
      (collection/collection-count) => 1)

    ;; all good, with different slug - 200 OK
    ;; curl -i --header "Accept: application/vnd.fcms.collection+json;version=1" --header "Accept-Charset: utf-8" --header "Content-Type: application/vnd.fcms.collection+json;version=1" -X PUT -d '{"name":"c-prime", "slug": "c-moved", c":"c"}' http://localhost:3000/c/
    (fact "with a new slug"
      (let [response (api-request :put "/c" {
        :headers {
          :Content-Type (mime-type :collection)
          :Accept (mime-type :collection)}
        :body {
          :name "c-prime"
          :slug "c-moved"
          :c c}})]
        (:status response) => 200
        (response-mime-type response) => (mime-type :collection)
        (json? response) => true
        (verify-collection-links "c-moved" (:links (body-from-response response))))
      ;; check that the update worked
      (let [coll (collection/get-collection "c-moved")]
        coll => (contains {
          :name "c-prime"
          :c c
          :slug "c-moved"
          :version 2})
        (:description coll) => nil)
      ;; check it didn't create another collection
      (collection/collection-count) => 1)

    ;; all good, unicode in the body - 200 OK
    ;; curl -i --header "Accept: application/vnd.fcms.collection+json;version=1" --header "Accept-Charset: utf-8" --header "Content-Type: application/vnd.fcms.collection+json;version=1" -X PUT -d '{"name":"私", "description":"Τη γλώσσα μου έδωσαν ελληνική მივჰხვდე მას ჩემსა الزجاج و هذا لا يؤلمني. मैं काँच खा सकता ฉันกินกระจกได้ לא מזיק Mogę jeść szkło", "c":"c"}' http://localhost:3000/c/
    (fact "with a property containing unicode"
      (let [response (api-request :put "/c" {
        :headers {
          :Content-Type (mime-type :collection)
          :Accept (mime-type :collection)}
        :body {
          :name "私"
          :description "Τη γλώσσα μου έδωσαν ελληνική მივჰხვდე მას ჩემსა الزجاج و هذا لا يؤلمني. मैं काँच खा सकता ฉันกินกระจกได้ לא מזיק Mogę jeść szkło"
          :c c}})]
        (:status response) => 200
        (response-mime-type response) => (mime-type :collection)
        (json? response) => true
        (verify-collection-links c (:links (body-from-response response))))
      ;; check that the update worked
      (collection/get-collection c) => (contains {
        :name "私"
        :c c
        :description "Τη γλώσσα μου έδωσαν ελληνική მივჰხვდე მას ჩემსა الزجاج و هذا لا يؤلمني. मैं काँच खा सकता ฉันกินกระจกได้ לא מזיק Mogę jeść szkło"
        :slug c
        :version 2})
      ;; check it didn't create another collection
      (collection/collection-count) => 1)

    ;; all good, no "name" in body - 200 OK
    ;; curl -i --header "Accept: application/vnd.fcms.collection+json;version=1" --header "Accept-Charset: utf-8" --header "Content-Type: application/vnd.fcms.collection+json;version=1" -X PUT -d '{"description":"a no name c", "c":"c"}' http://localhost:3000/c/
    (fact "without providing a new slug"
      (let [response (api-request :put "/c" {
        :headers {
          :Content-Type (mime-type :collection)
          :Accept (mime-type :collection)}
        :body {
          :description "a no name c"
          :c c}})]
        (:status response) => 200
        (response-mime-type response) => (mime-type :collection)
        (json? response) => true
        (verify-collection-links c (:links (body-from-response response))))
      ;; check that the update worked
      (collection/get-collection c) => (contains {
        :name c
        :c c
        :description "a no name c"
        :slug c
        :version 2})
      ;; check it didn't create another collection
      (collection/collection-count) => 1)

    ;; no Accept header - 200 OK
    ;; curl -i --header "Accept-Charset: utf-8" --header "Content-Type: application/vnd.fcms.collection+json;version=1" -X PUT -d '{"name":"c-prime", "c":"c"}' http://localhost:3000/c/
    (fact "without an accept header"
      (let [response (api-request :put "/c" {
        :headers {
          :Content-Type (mime-type :collection)}
        :body {
          :name "c-prime"
          :c c}})]
        (:status response) => 200
        (response-mime-type response) => (mime-type :collection)
        (json? response) => true
        (verify-collection-links c (:links (body-from-response response))))
      ;; check that the update worked
      (let [coll (collection/get-collection c)]
        coll => (contains {
          :name "c-prime"
          :c c
          :slug c
          :version 2})
        (:description coll) => nil)
      ;; check it didn't create another collection
      (collection/collection-count) => 1)

    ;; no Content-Type header - 200 OK
    ;; curl -i --header "Accept: application/vnd.fcms.collection+json;version=1" --header "Accept-Charset: utf-8" -X PUT -d '{"name":"c-prime", "c":"c"}' http://localhost:3000/c/
    (fact "without a content-type header"
      (let [response (api-request :put "/c" {
        :headers {
          :Accept (mime-type :collection)}
        :body {
          :name "c-prime"
          :c c}})]
        (:status response) => 200
        (response-mime-type response) => (mime-type :collection)
        (json? response) => true
        (verify-collection-links c (:links (body-from-response response))))
      ;; check that the update worked
      (let [coll (collection/get-collection c)]
        coll => (contains {
          :name "c-prime"
          :c c
          :slug c
          :version 2})
        (:description coll) => nil)
      ;; check it didn't create another collection
      (collection/collection-count) => 1)

    ;; no Accept-Charset header - 200 OK
    ;; curl -i --header "Accept: application/vnd.fcms.collection+json;version=1" --header "Content-Type: application/vnd.fcms.collection+json;version=1" -X PUT -d '{"name":"c-prime", "c":"c"}' http://localhost:3000/c/
    (fact "without an Accept-Charset header"
      (let [response (api-request :put "/c" {
        :skip-charset true
        :headers {
          :Content-Type (mime-type :collection)
          :Accept (mime-type :collection)}
        :body {
          :name "c-prime"
          :c c}})]
        (:status response) => 200
        (response-mime-type response) => (mime-type :collection)
        (json? response) => true
        (verify-collection-links c (:links (body-from-response response))))
      ;; check that the update worked
      (let [coll (collection/get-collection c)]
        coll => (contains {
          :name "c-prime"
          :c c
          :slug c
          :version 2})
        (:description coll) => nil)
      ;; check it didn't create another collection
      (collection/collection-count) => 1))

(future-facts "about attempting to use the REST API to update a collection"))