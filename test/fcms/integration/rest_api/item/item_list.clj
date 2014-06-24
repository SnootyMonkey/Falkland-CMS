(ns fcms.integration.rest-api.item.item-list
  "Integration tests for listing items with the REST API."
  (:require [midje.sweet :refer :all]
            [clj-time.format :refer (parse)]
            [fcms.lib.resources :refer :all]
            [fcms.lib.rest-api-mock :refer :all]
            [fcms.resources.collection :as collection]
            [fcms.resources.item :as item]))

;; Utils
(defn- items-from-body [body]
  (-> body
      :collection
      :items))

;; Listing items with the REST API

;;   The system should return a summary of the items stored in a collection and handle the following scenarios:

;;   empty collection
;;   all good, 1 item
;;   all good, many items
;;   all good, force pagination
;;   all good, additional page
;;     no accept
;;     wrong accept
;;     no accept charset
;;     wrong accept charset
;;     collection doesn't exist

;; Background:
;;   Given I had an empty collection "empty"
;;   And the collection "empty" had an item count of 0

;;   Given I had a collection "one" with the following item:
;;   |slug				|name        	|description			|
;;   | i 				| i 					| this is an item	|
;;   And the collection "one" had an item count of 1

;;   Given I had a collection "many" with the following items:
;;   |slug		|name        	|description																																																																				|
;;   | i 		| i 					| this is an item																																																																		|
;;   | uni-i	| 私はガラスを食	| er stîget ûf mit grôzer kraft Τη γλώσσα μου έδωσαν ελληνική მივჰხვდე მას ჩემსა الزجاج و هذا لا يؤلمني. मैं काँच खा सकता ฉันกินกระจกได้ לא מזיק Mogę jeść szkło €|
;;   | i-2 	| i 2					| this is an item 2																																																																	|
;;   | i-3 	| i 3					| this is an item	3																																																																	|
;;   | i-4 	| i 4					| this is an item	4																																																																	|
;;   And the collection "many" had an item count of 5

(defn- create-collection-one-items []
  (item/create-item one i {:slug i :description ascii-description}))

(defn- create-collection-many-items []
  (item/create-item many i {:slug i :description ascii-description})
  (item/create-item many unicode-name {:slug "uni-i" :description unicode-description})
  (item/create-item many "i 2" {:slug "i-2" :description (str ascii-description " 2")})
  (item/create-item many "i 3" {:slug "i-3" :description (str ascii-description " 3")})
  (item/create-item many "i 4" {:slug "i-4" :description (str ascii-description " 4")}))

(defn- setup []
  (reset-collection e)
  (reset-collection one)
  (create-collection-one-items)  
  (reset-collection many)
  (create-collection-many-items))

(defn- teardown []
  (collection/delete-collection e)
  (collection/delete-collection one)
  (collection/delete-collection many))

(with-state-changes [(before :facts (setup))
                     (after :facts (teardown))]

  (facts "about listing items"
         ;; empty collection - 200 OK
         ;; curl -i --header "Accept: application/vnd.collection+vnd.fcms.item+json;version=1" --header "Accept-Charset: utf-8" -X GET http://localhost:3000/empty/
         (fact "from an empty collection"
               (let [response (api-request :get "/e/"
                                           {:headers
                                            {:Accept (mime-type :item-collection)
                                             :Accept-Charset "utf-8"}})]
                 (:status response) => 200
                 (response-mime-type response) => (mime-type :item-collection)
                 (json? response) => true
                 (collection/item-count e) => 0))

         ;; all good, 1 item - 200 OK
         ;; curl -i --header "Accept: application/vnd.collection+vnd.fcms.item+json;version=1" --header "Accept-Charset: utf-8" -X GET http://localhost:3000/one/
         (fact "from a collection"
               (let [response (api-request :get "/one/"
                                           {:headers
                                            {:Accept (mime-type :item-collection)
                                             :Accept-Charset "utf-8"}})]
                 (:status response) => 200
                 (response-mime-type response) => (mime-type :item-collection)
                 (json? response) => true
                 (collection/item-count one) => 1
                 (let [body (body-from-response response)
                       item (first (items-from-body body))]
                   (:name item) => i
                   (:slug item) => i
                   (:description item) => ascii-description
                   (:version item) => 1
                   (:collection item) => one
                   (instance? timestamp (parse (:created-at item))) => true)))
         ;; all good, many items - 200 OK
         ;; curl -i --header "Accept: application/vnd.collection+vnd.fcms.item+json;version=1" --header "Accept-Charset: utf-8" -X GET http://localhost:3000/one/
         (fact "from a collection with many items"
               (let [response (api-request :get "/many/"
                                           {:headers
                                            {:Accept (mime-type :item-collection)
                                             :Accept-Charset "utf-8"}})]
                 (:status response) => 200
                 (response-mime-type response) => (mime-type :item-collection)
                 (json? response) => true
                 (collection/item-count many) => 5
                 (let [body (body-from-response response)
                       i-1 (first (items-from-body body))
                       uni-i (second (items-from-body body))
                       i-2 (nth (items-from-body body) 2)
                       i-3 (nth (items-from-body body) 3)
                       i-4 (nth (items-from-body body) 4)]
                   ;; i-1
                   i-1 => (contains {
                                     :collection many
                                     :name i
                                     :slug i
                                     :description ascii-description
                                     :version 1})
                   (instance? timestamp (parse (:created-at i-1))) => true
                   ;; uni-i
                   uni-i => (contains {
                                       :collection many
                                       :name unicode-name
                                       :slug "uni-i"
                                       :description unicode-description
                                       :version 1})
                   (instance? timestamp (parse (:created-at uni-i))) => true
                   i-2 => (contains {
                                     :collection many
                                     :name "i 2"
                                     :slug "i-2"
                                     :description (str ascii-description " 2")
                                     :version 1})
                   (instance? timestamp (parse (:created-at i-2))) => true
                   i-3 => (contains {
                                     :collection many
                                     :name "i 3"
                                     :slug "i-3"
                                     :description (str ascii-description " 3")
                                     :version 1})
                   (instance? timestamp (parse (:created-at i-3))) => true
                   i-4 => (contains {
                                     :collection many
                                     :name "i 4"
                                     :slug "i-4"
                                     :description (str ascii-description " 4")
                                     :version 1})
                   (instance? timestamp (parse (:created-at i-4))) => true)))
         
         ;; TODO
         ;; all good, force pagination
         ;; all good, additional page

         ;; no accept - 200 OK
         ;; curl -i --header "Accept-Charset: utf-8" -X GET http://localhost:3000/one/
         (fact "without using an an Accept header"
               (let [response (api-request :get "/one/"
                                           {:headers {:Accept-Charset "utf-8"}})]
                 (:status response) => 200
                 (response-mime-type response) => (mime-type :item-collection)
                 (json? response) => true
                 (let [body (body-from-response response)
                       item (first (items-from-body body))]
                   item => (contains {
                                      :collection one
                                      :name i
                                      :slug i
                                      :description ascii-description
                                      :version 1})
                   (instance? timestamp (parse (:created-at item))) => true)))

         ;; wrong accept - 406 Not Acceptable
         ;; curl -i --header "Accept: application/vnd.fcms.item+json;version=1" --header "Accept-Charset: utf-8" -X GET http://localhost:3000/one/
         (fact "with the wrong Accept header"
               (let [response (api-request :get "/one/"
                                           {:headers {:Accept (mime-type :item)
                                                      :Accept-Charset "utf-8"}})]
                 (:status response) => 406
                 (response-mime-type response) => (mime-type :text)
                 (let [body (body-from-response response)]
                   (.contains body "Acceptable media type: application/vnd.collection+vnd.fcms.item+json;version=1") => true
                   (.contains body "Acceptable charset: utf-8") => true)))


         ;; no accept charset - 200 OK
         ;; curl -i --header "Accept: application/vnd.collection+vnd.fcms.item+json;version=1" -X GET http://localhost:3000/one/
         (fact "without the Accept-Charset header"
               (let [response (api-request :get "/one/"
                                           {:headers {:Accept (mime-type :item-collection)}})]
                 (:status response) => 200
                 (response-mime-type response) => (mime-type :item-collection)
                 (json? response) => true
                 (collection/item-count one) => 1
                 (let [body (body-from-response response)
                       item (first (items-from-body body))]
                   (:name item) => i
                   (:slug item) => i
                   (:description item) => ascii-description
                   (:version item) => 1
                   (:collection item) => one
                   (instance? timestamp (parse (:created-at item))) => true)))

         ;; wrong accept charset - 406 Not Acceptable
         ;; curl -i --header "Accept: application/vnd.collection+vnd.fcms.item+json;version=1" --header "Accept-Charset: iso-8859-1" -X GET http://localhost:3000/one/
         (fact "with the wrong Accept-Charset header"
               (let [response (api-request :get "/one/"
                                           {:headers {:Accept (mime-type :item-collection)
                                                      :Accept-Charset "iso-8859-1"}})]
                 (:status response) => 406
                 (response-mime-type response) => (mime-type :text)
                 (let [body (body-from-response response)]
                   (.contains body "Acceptable media type: application/vnd.collection+vnd.fcms.item+json;version=1") => true
                   (.contains body "Acceptable charset: utf-8") => true)))
         ;; collection doesn't exist
         ;; curl -i --header "Accept: application/vnd.collection+vnd.fcms.item+json;version=1" --header "Accept-Charset: utf-8" -X GET http://localhost:3000/not-here/
         (fact "from a collection that doesn't exist"
               (let [response (api-request :get "/not-here/"
                                           {:headers {:Accept (mime-type :item-collection)
                                                      :Accept-Charset "utf-8"}})]
                 (:status response) => 404
                 (response-mime-type response) => (mime-type :text)
                 (let [body (body-from-response response)]
                   (.contains body "Collection not found.") => true)))))
