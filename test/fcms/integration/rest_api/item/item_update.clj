(ns fcms.integration.rest-api.item.item-update
  "Integration tests for updating items with the REST API."
  (:require [midje.sweet :refer :all]
            [fcms.lib.resources :refer :all]
            [fcms.lib.rest-api-mock :refer :all]
            [fcms.resources.collection :as collection]
            [fcms.resources.collection-resource :as resource]
            [fcms.resources.item :as item]))

;; The system should return the detail of items stored in a collection and handle the following scenarios:
;;
;; all good - no slug
;; all good - with same slug
;; all good - with different slug
;; all good - unicode in the body
;; all good - no name in body
;; conflicting reserved properties
;; no accept
;; wrong accept
;; no content header
;; wrong content header
;; no charset
;; wrong charset
;; no body
;; body, but not valid JSON
;; collection doesn't exist
;; item doesn't exist
;; different slug specified in body is already used
;; different slug specified in body is invalid

;; ----- Utilities -----

(defn- create-collection-c-items []
  (item/create-item c i {:slug i :description ascii-description})
  (item/create-item c unicode-name {:slug "another-i" :description unicode-description}))

;; ----- Tests -----

(with-state-changes [(before :facts (do (reset-collection c) (create-collection-c-items)))
                     (after :facts (collection/delete-collection c))]

  (facts "about updating an item with the REST API"

    ;; all good, no slug - 200 OK
    ;; curl -i --header "Accept: application/vnd.fcms.item+json;version=1" --header "Accept-Charset: utf-8" --header "Content-Type: application/vnd.fcms.item+json;version=1" -X PUT -d '{"name":"i-prime", "i":"i"}' http://localhost:3000/c/i
    (fact "without providing a new slug"
      (let [response (api-request :put "/c/i" {
        :headers {
          :Content-Type (mime-type :item)
          :Accept (mime-type :item)}
        :body {
          :name "i-prime"
          :i i}})]
        (:status response) => 200
        (response-mime-type response) => (mime-type :item)
        (json? response) => true)
      ;; check that the update worked
      (let [item (item/get-item c i)]
        item => (contains {
          :collection c
          :name "i-prime"
          :i i
          :slug i
          :version 2})
        (:description item) => nil)
      ;; check it didn't create another item
      (collection/item-count c) => 2)

    ;; all good, with same slug - 200 OK
    ;; curl -i --header "Accept: application/vnd.fcms.item+json;version=1" --header "Accept-Charset: utf-8" --header "Content-Type: application/vnd.fcms.item+json;version=1" -X PUT -d '{"name":"i-prime", "slug":"i", "i":"i"}' http://localhost:3000/c/i
    (fact "with the same slug"
      (let [response (api-request :put "/c/i" {
        :headers {
          :Content-Type (mime-type :item)
          :Accept (mime-type :item)}
        :body {
          :name "i-prime"
          :slug "i"
          :i "i"}})]
        (:status response) => 200
        (response-mime-type response) => (mime-type :item)
        (json? response) => true)
      ;; check that the update worked
      (let [item (item/get-item c i)]
        item => (contains {
          :collection c
          :name "i-prime"
          :i i
          :slug i
          :version 2})
        (:description item) => nil)
      ;; check it didn't create another item
      (collection/item-count c) => 2)

    ;; all good, with different slug - 200 OK
    ;; curl -i --header "Accept: application/vnd.fcms.item+json;version=1" --header "Accept-Charset: utf-8" --header "Content-Type: application/vnd.fcms.item+json;version=1" -X PUT -d '{"name":"i", "slug":"i-moved","i":"i"}' http://localhost:3000/c/i
    (fact "with a new slug"
      (let [response (api-request :put "/c/i" {
        :headers {
          :Content-Type (mime-type :item)
          :Accept (mime-type :item)}
        :body {
          :name "i-prime"
          :slug "i-moved"
          :i "i"}})]
        (:status response) => 200
        (response-mime-type response) => (mime-type :item)
        (response-location response) => "/c/i-moved"
        (json? response) => true)
      ;; check it's no longer at the old location
      (item/get-item c "i") => nil
      ;; check that the update worked
      (let [item (item/get-item c "i-moved")]
        item => (contains {
          :collection c
          :name "i-prime"
          :i i
          :slug "i-moved"
          :version 2})
        (:description item) => nil)
      ;; check it didn't create another item
      (collection/item-count c) => 2)

    ;; all good, unicode in the body - 200 OK
    ;; curl -i --header "Accept: application/vnd.fcms.item+json;version=1" --header "Accept-Charset: utf-8" --header "Content-Type: application/vnd.fcms.item+json;version=1" -X PUT -d '{"name":"私", "description":"Τη γλώσσα μου έδωσαν ελληνική მივჰხვდე მას ჩემსა الزجاج و هذا لا يؤلمني. मैं काँच खा सकता ฉันกินกระจกได้ לא מזיק Mogę jeść szkło", "i":"i"}' http://localhost:3000/c/another-i
    (fact "with a property containing unicode"
      (let [response (api-request :put "/c/another-i" {
        :headers {
          :Content-Type (mime-type :item)
          :Accept (mime-type :item)}
        :body {
          :name "私"
          :description "Τη γλώσσα μου έδωσαν ελληνική მივჰხვდე მას ჩემსა الزجاج و هذا لا يؤلمني. मैं काँच खा सकता ฉันกินกระจกได้ לא מזיק Mogę jeść szkło"
          :i i}})]
        (:status response) => 200
        (response-mime-type response) => (mime-type :item)
        (json? response) => true)
      ;; check that the update worked
      (item/get-item c "another-i") => (contains {
        :collection c
        :name "私"
        :i i
        :description "Τη γλώσσα μου έδωσαν ελληνική მივჰხვდე მას ჩემსა الزجاج و هذا لا يؤلمني. मैं काँच खा सकता ฉันกินกระจกได้ לא מזיק Mogę jeść szkło"
        :slug "another-i"
        :version 2})
      ;; check it didn't create another item
      (collection/item-count c) => 2)

    ;; all good, no "name" in body - 200 OK
    ;; curl -i --header "Accept: application/vnd.fcms.item+json;version=1" --header "Accept-Charset: utf-8" --header "Content-Type: application/vnd.fcms.item+json;version=1" -X PUT -d '{description":"a no name i", "i":"i"}' http://localhost:3000/c/i
    (fact "without a name"
      (let [response (api-request :put "/c/i" {
        :headers {
          :Content-Type (mime-type :item)
          :Accept (mime-type :item)}
        :body {
          :description "a no name i"
          :i i}})]
        (:status response) => 200
        (response-mime-type response) => (mime-type :item)
        (json? response) => true)
      ;; check that the update worked and the name is still there
      (item/get-item c i) => (contains {
        :collection c
        :name i
        :i i
        :description "a no name i"
        :slug i
        :version 2})
      ;; check it didn't create another item
      (collection/item-count c) => 2)

    ;; no accept type - 200 OK
    ;; curl -i --header "Accept-Charset: utf-8" --header "Content-Type: application/vnd.fcms.item+json;version=1" -X PUT -d '{"name":"i-prime", "i":"i"}' http://localhost:3000/c/i         
    (fact "without an accept header"
      (let [response (api-request :put "/c/i" {
        :headers {
          :Content-Type (mime-type :item)}
        :body {
          :name "i-prime"
          :i i}})]
        (:status response) => 200
        (json? response) => true
        (response-mime-type response) => (mime-type :item))
      ;; check that the update worked
      (let [item (item/get-item c i)]
        item => (contains {
          :collection c
          :name "i-prime"
          :i i
          :slug i
          :version 2})
        (:description item) => nil)
      ;; check it didn't create another item
      (collection/item-count c) => 2)

    ;; no Content-Type header - 200 OK
    ;; curl -i --header "Accept: application/vnd.fcms.item+json;version=1" --header "Accept-Charset: utf-8" -X PUT -d '{"name":"i-prime", "i":"i"}' http://localhost:3000/c/i
    (fact "without a content-type header"
      (let [response (api-request :put "/c/i" {
        :headers {
          :Accept (mime-type :item)}
        :body {
          :name "i-prime"
          :i i}})]
        (:status response) => 200
        (response-mime-type response) => (mime-type :item)
        (json? response) => true)
      ;; check that the update worked
      (let [item (item/get-item c i)]
        item => (contains {
          :collection c
          :name "i-prime"
          :i i
          :slug i
          :version 2})
        (:description item) => nil)
      ;; check it didn't create another item
      (collection/item-count c) => 2)

    ;; no charset - 200 OK
    ;; curl -i --header "Accept: application/vnd.fcms.item+json;version=1" --header "Content-Type: application/vnd.fcms.item+json;version=1" -X PUT -d '{"name":"i-prime", "i":"i"}' http://localhost:3000/c/i         
    (fact "without an Accept-Charset header"
      (let [response (api-request :put "/c/i" {
        :skip-charset true
        :headers {
          :Content-Type (mime-type :item)
          :Accept (mime-type :item)}
        :body {
          :name "i-prime"
          :i i}})]
        (:status response) => 200
        (response-mime-type response) => (mime-type :item)
        (json? response) => true)
      ;; check that the update worked
      (let [item (item/get-item c i)]
        item => (contains {
          :collection c
          :name "i-prime"
          :i i
          :slug i
          :version 2})
        (:description item) => nil)
      ;; check it didn't create another item
      (collection/item-count c) => 2)))

;; Clojure throws a "method code too large!" exception so the tests are separated
(with-state-changes [(before :facts (do (reset-collection c) (create-collection-c-items)))
                     (after :facts (collection/delete-collection c))]

  (facts "about failing to update an item with the REST API"

    ;; conflicting reserved properties - 422 Unprocessable Entity
    ;; curl -i --header "Accept: application/vnd.fcms.item+json;version=1" --header "Accept-Charset: utf-8" --header "Content-Type: application/vnd.fcms.item+json;version=1" -X PUT -d '{"name":"i-15", "version":"foo", "i":"i"}' http://localhost:3000/c/i
    ;; curl -i --header "Accept: application/vnd.fcms.item+json;version=1" --header "Accept-Charset: utf-8" --header "Content-Type: application/vnd.fcms.item+json;version=1" -X PUT -d '{"name":"i-2", "collection":"foo", "i":"j"}' http://localhost:3000/c/i
    ;; curl -i --header "Accept: application/vnd.fcms.item+json;version=1" --header "Accept-Charset: utf-8" --header "Content-Type: application/vnd.fcms.item+json;version=1" -X PUT -d '{"name":"i-3", "id":"foo", "i":"k"}' http://localhost:3000/c/i
    ;; curl -i --header "Accept: application/vnd.fcms.item+json;version=1" --header "Accept-Charset: utf-8" --header "Content-Type: application/vnd.fcms.item+json;version=1" -X PUT -d '{"name":"i-4", "type":"foo", "i":"l"}' http://localhost:3000/c/i
    ;; curl -i --header "Accept: application/vnd.fcms.item+json;version=1" --header "Accept-Charset: utf-8" --header "Content-Type: application/vnd.fcms.item+json;version=1" -X PUT -d '{"name":"i-5", "created-at":"foo", "i":"m"}' http://localhost:3000/c/i
    ;; curl -i --header "Accept: application/vnd.fcms.item+json;version=1" --header "Accept-Charset: utf-8" --header "Content-Type: application/vnd.fcms.item+json;version=1" -X PUT -d '{"name":"i-6", "updated-at":"foo", "i":"n"}' http://localhost:3000/c/i
    (fact "with a property that conflicts with a reserved property"
         (doseq [[reserved-property name] (map list
                                               [:id :collection :version :type :created-at :updated-at :links :categories]
                                               ["i-1" "i-2" "i-3" "i-4" "i-5" "i-6" "i-7" "i-8"])]
           (let [response (api-request :put "/c/i" {:headers
                                                    {:Accept-Charset "utf-8"
                                                     :Content-Type (mime-type :item)
                                                     :Accept (mime-type :item)}
                                                    :body
                                                    {:name name
                                                     reserved-property "foo"
                                                     :i i}})]
             (:status response) => 422
             (response-mime-type response) => (mime-type :text)
             (body-from-response response) => "A reserved property was used."))
         (collection/item-count c) => 2
         (let [response (api-request :get "/c/i" {:headers
                                                  {:Accept-Charset "utf-8"
                                                   :Accept (mime-type :item)}})]
           (:status response) => 200
           (response-mime-type response) => (mime-type :item)
           (json? response) => true
           (let [body (body-from-response response)]
             (:collection body) => c
             (:slug body) => i
             (:name body) => i
             (:description body) => "this is an item"
             (:i body) => nil)))

         ;; wrong accept type - 406 Not Acceptable
         ;; curl -i --header "Accept: application/vnd.fcms.collection+json;version=1" --header "Accept-Charset: utf-8" --header "Content-Type: application/vnd.fcms.item+json;version=1" -X PUT -d '{"name":"i-prime", "i":"i"}' http://localhost:3000/c/i
         (fact "attempt to update an item with the wrong accept-header"
               (let [response (api-request :put "/c/i" {:headers
                                                        {:Accept-Charset "utf-8"
                                                         :Accept (mime-type :collection)
                                                         :Content-Type (mime-type :item)}
                                                        :body
                                                        {:name "i-prime"
                                                         :i i}})]
                 (:status response) => 406
                 (response-mime-type response) => (mime-type :text)
                 (let [body (body-from-response response)]
                   (.contains body "Acceptable media type: application/vnd.fcms.item+json;version=1") => true
                   (.contains body "Acceptable charset: utf-8") => true)
                 (collection/item-count c) => 2)
               (let [response (api-request :get "/c/i" {:headers
                                                        {:Accept-Charset "utf-8"
                                                         :Accept (mime-type :item)}})]
                 (:status response) => 200
                 (response-mime-type response) => (mime-type :item)
                 (json? response) => true
                 (let [body (body-from-response response)]
                   (:collection body) => c
                   (:slug body) => i
                   (:name body) => i
                   (:description body) => "this is an item"
                   (:i body) => nil)))


         ;; wrong Content-Type header - 415 Unsupported Media Type
         ;; curl -i --header "Accept: application/vnd.fcms.item+json;version=1" --header "Accept-Charset: utf-8" --header "Content-Type: application/vnd.fcms.collection+json;version=1" -X PUT -d '{"name":"i-prime", "i":"i"}' http://localhost:3000/c/i
         (fact "attempt to update an item with the wrong Content-Type header"
               (let [response (api-request :put "/c/i" {:headers
                                                        {:Accept-Charset "utf-8"
                                                         :Content-Type (mime-type :collection)
                                                         :Accept (mime-type :item)}
                                                        :body
                                                        {:name "i-prime"
                                                         :i i}})]
                 (:status response) => 415
                 (response-mime-type response) => (mime-type :text)
                 (let [body (body-from-response response)]
                   (.contains body "Acceptable media type: application/vnd.fcms.item+json;version=1") => true
                   (.contains body "Acceptable charset: utf-8") => true))
                 (collection/item-count c) => 2
                 (let [response (api-request :get "/c/i" {:headers
                                                          {:Accept-Charset "utf-8"
                                                           :Accept (mime-type :item)}})]
                   (:status response) => 200
                   (response-mime-type response) => (mime-type :item)
                   (json? response) => true
                   (let [body (body-from-response response)]
                     (:collection body) => c
                     (:slug body) => i
                     (:name body) => "i"
                     (:description body) => "this is an item"
                     (:i body) => nil)))

         ;; wrong charset - 406 Not Acceptable
         ;; curl -i --header "Accept: application/vnd.fcms.item+json;version=1" --header "Accept-Charset: iso-8859-1" --header "Content-Type: application/vnd.fcms.item+json;version=1" -X PUT -d '{"name":"i-prime", "i":"i"}' http://localhost:3000/c/i
         (fact "attempt to update an item with the wrong Accept-Charset header"
               (let [response (api-request :put "/c/i" {:headers
                                                        {:Accept-Charset "iso-8859-1"
                                                         :Content-Type (mime-type :item)
                                                         :Accept (mime-type :item)}
                                                        :body
                                                        {:name "i-prime"
                                                         :i i}})]
                 (:status response) => 406
                 (response-mime-type response) => (mime-type :text)
                 (let [body (body-from-response response)]
                   (.contains body "Acceptable media type: application/vnd.fcms.item+json;version=1") => true
                   (.contains body "Acceptable charset: utf-8") => true)
                 (collection/item-count c) => 2)
               (let [response (api-request :get "/c/i" {:headers
                                                        {:Accept-Charset "utf-8"
                                                         :Accept (mime-type :item)}})]
                 (:status response) => 200
                 (response-mime-type response) => (mime-type :item)
                 (json? response) => true
                 (let [body (body-from-response response)]
                   (:collection body) => c
                   (:slug body) => i
                   (:name body) => "i"
                   (:description body) => "this is an item"
                   (:i body) => nil)))

         ;; no body - 400 Bad Request
         ;; curl -i --header "Accept: application/vnd.fcms.item+json;version=1" --header "Accept-Charset: utf-8" --header "Content-Type: application/vnd.fcms.item+json;version=1" -X PUT http://localhost:3000/c/i
         (fact "attempt to update an item with no body"
               (let [response (api-request :put "/c/i" {:headers
                                                        {:Accept-Charset "utf-8"
                                                         :Content-Type (mime-type :item)
                                                         :Accept (mime-type :item)}
                                                        :body nil})]
                 (:status response) => 400
                 (response-mime-type response) => (mime-type :text)
                 (body-from-response response) => "Bad request."
                 (collection/item-count c) => 2)
               (let [response (api-request :get "/c/i" {:headers
                                                        {:Accept-Charset "utf-8"
                                                         :Accept (mime-type :item)}})]
                 (:status response) => 200
                 (response-mime-type response) => (mime-type :item)
                 (json? response) => true
                 (let [body (body-from-response response)]
                   (:collection body) => c
                   (:slug body) => i
                   (:name body) => "i"
                   (:description body) => "this is an item"
                   (:i body) => nil)))

         ;; body, but not valid JSON - 400 Bad Request
         ;; curl -i --header "Accept: application/vnd.fcms.item+json;version=1" --header "Accept-Charset: utf-8" --header "Content-Type: application/vnd.fcms.item+json;version=1" -X PUT -d 'Hi Mom!' http://localhost:3000/c/i
         ;; curl -i --header "Accept: application/vnd.fcms.item+json;version=1" --header "Accept-Charset: utf-8" --header "Content-Type: application/vnd.fcms.item+json;version=1" -X PUT -d '{"name":"i-prime"' http://localhost:3000/c/i
         (fact "attempt to update an item with an invalid body"
               (doseq [body ["Hi Mom!" "{'name': 'i-prime'"]]
                 (let [response (api-request :put "/c/i" {:headers
                                                          {:Accept-Charset "utf-8"
                                                           :Content-Type (mime-type :item)
                                                           :Accept (mime-type :item)}
                                                          :body body})]
                   (:status response) => 400
                   (response-mime-type response) => (mime-type :text)
                   (body-from-response response) => "Bad request."
                   (collection/item-count c) => 2)
                 (let [response (api-request :get "/c/i" {:headers
                                                          {:Accept-Charset "utf-8"
                                                           :Accept (mime-type :item)}})]
                   (:status response) => 200
                   (response-mime-type response) => (mime-type :item)
                   (json? response) => true
                   (let [body (body-from-response response)]
                     (:collection body) => c
                     (:slug body) => i
                     (:name body) => "i"
                     (:description body) => "this is an item"
                     (:i body) => nil))))

         ;; collection doesn't exist - 404 Not Found
         ;; curl -i --header "Accept: application/vnd.fcms.item+json;version=1" --header "Accept-Charset: utf-8" --header "Content-Type: application/vnd.fcms.item+json;version=1" -X PUT -d '{"name":"i-prime", "i":"i"}' http://localhost:3000/not-here/i
         (fact "attempt to update an item in a collection that doesn't exist"
               (let [response (api-request :put "/not-here/i" {:headers
                                                               {:Accept-Charset "utf-8"
                                                                :Content-Type (mime-type :item)
                                                                :Accept (mime-type :item)}
                                                               :body
                                                               {:name "i-prime"
                                                                :i i}})]
                 (:status response) => 404
                 (response-mime-type response) => (mime-type :text)
                 (body-from-response response) => "Collection not found."
                 (collection/item-count c) => 2)
               (let [response (api-request :get "/c/i" {:headers
                                                        {:Accept-Charset "utf-8"
                                                         :Accept (mime-type :item)}})]
                 (:status response) => 200
                 (response-mime-type response) => (mime-type :item)
                 (json? response) => true
                 (let [body (body-from-response response)]
                   (:collection body) => c
                   (:slug body) => i
                   (:name body) => "i"
                   (:description body) => "this is an item"
                   (:i body) => nil)))

         ;; item doesn't exist - 404 Not Found
         ;; curl -i --header "Accept: application/vnd.fcms.item+json;version=1" --header "Accept-Charset: utf-8" --header "Content-Type: application/vnd.fcms.item+json;version=1" -X PUT -d '{"name":"i-prime", "i":"i"}' http://localhost:3000/c/not-here
         (fact "attempt to update an item that doesn't exist"
               (let [response (api-request :put "/c/not-here" {:headers
                                                               {:Accept-Charset "utf-8"
                                                                :Content-Type (mime-type :item)
                                                                :Accept (mime-type :item)}
                                                               :body
                                                               {:name "i-prime"
                                                                :i i}})]
                 (:status response) => 404
                 (response-mime-type response) => (mime-type :text)
                 (body-from-response response) => nil
                 (collection/item-count c) => 2)
               (let [response (api-request :get "/c/i" {:headers
                                                        {:Accept-Charset "utf-8"
                                                         :Accept (mime-type :item)}})]
                 (:status response) => 200
                 (response-mime-type response) => (mime-type :item)
                 (json? response) => true
                 (let [body (body-from-response response)]
                   (:collection body) => c
                   (:slug body) => i
                   (:name body) => "i"
                   (:description body) => "this is an item"
                   (:i body) => nil)))

         ;; different slug specified in body is already used - 422 Unprocessable Entity
         ;; curl -i --header "Accept: application/vnd.fcms.item+json;version=1" --header "Accept-Charset: utf-8" --header "Content-Type: application/vnd.fcms.item+json;version=1" -X PUT -d '{"name":"i-prime", "slug":"another-i", "i":"i"}' http://localhost:3000/c/i
         (fact "attempt to update an item with a slug that's already used in the collection"
               (let [response (api-request :put "/c/i" {:headers
                                                        {:Accept-Charset "utf-8"
                                                         :Content-Type (mime-type :item)
                                                         :Accept (mime-type :item)}
                                                        :body
                                                        {:name "i-prime"
                                                         :slug "another-i"
                                                         :i i}})]
                 (:status response) => 422
                 (response-mime-type response) => (mime-type :text)
                 (body-from-response response) => "Slug already used in collection."
                 (collection/item-count c) => 2)
               (let [response (api-request :get "/c/i" {:headers
                                                        {:Accept-Charset "utf-8"
                                                         :Accept (mime-type :item)}})]
                 (:status response) => 200
                 (response-mime-type response) => (mime-type :item)
                 (json? response) => true
                 (let [body (body-from-response response)]
                   (:collection body) => c
                   (:slug body) => i
                   (:name body) => "i"
                   (:description body) => "this is an item"
                   (:i body) => nil))
               (let [response (api-request :get "/c/another-i" {:headers
                                                                 {:Accept-Charset "utf-8"
                                                                  :Accept (mime-type :item)}})]
                 (:status response) => 200
                 (response-mime-type response) => (mime-type :item)
                 (json? response) => true
                 (let [body (body-from-response response)]
                   (:collection body) => c
                   (:slug body) => "another-i"
                   (:name body) => unicode-name
                   (:description body) => unicode-description
                   (:i body) => nil)))

    ;; different slug specified in body is invalid - 422 Unprocessable Entity
    ;; curl -i --header "Accept: application/vnd.fcms.item+json;version=1" --header "Accept-Charset: utf-8" --header "Content-Type: application/vnd.fcms.item+json;version=1" -X PUT -d '{"name":"i-prime", "slug":"I i", "i":"i"}' http://localhost:3000/c/i
    (fact "with a slug that's invalid"
      (let [response (api-request :put "/c/i" {
        :headers {
          :Content-Type (mime-type :item)
          :Accept (mime-type :item)}
        :body {
          :name "i-prime"
          :slug "I i"
          :i i}})]
        (:status response) => 422
        (response-mime-type response) => (mime-type :text)
        (body-from-response response) => "Invalid slug.")
      ;; check that the update failed
      (item/get-item c i) => (contains {
        :collection c
        :slug i
        :name i
        :description "this is an item"})
      ;; check it didn't create another item
      (collection/item-count c) => 2)))