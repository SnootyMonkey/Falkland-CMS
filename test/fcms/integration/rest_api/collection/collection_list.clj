(ns fcms.integration.rest-api.collection.collection-list
  "Integration tests for listing collections with the REST API."
  (:require [midje.sweet :refer :all]
            [clj-time.format :refer (parse)]
            [fcms.lib.resources :refer :all]
            [fcms.lib.body :refer (verify-collection-links)]
            [fcms.lib.rest-api-mock :refer :all]
            [fcms.resources.collection :refer :all]))

;; The system should return a summary list of the collections stored in the system and handle the following scenarios:
;;
;; GET
;; all good, no collections
;; all good, 1 collection
;; all good, many collections
;; all good, force pagination
;; all good, additional page
;; no accept
;; no accept charset
;; wrong accept
;; wrong accept charset
;; using an invalid page

;; ----- Utilities -----

(defn- collections-from-body [body]
  (-> body
      :collection
      :collections))

(facts "about using the REST API to list collections"
  
  (with-state-changes [(before :facts (do
                                        (doseq [coll (all-collections)]
                                          (delete-collection (:slug coll)))))]

    ;; all good, no collections - 200 OK
    ;; curl -i --header "Accept: application/vnd.collection+vnd.fcms.collection+json;version=1" --header "Accept-Charset: utf-8" -X GET http://localhost:3000/
    (fact "when there are no collections"
      (let [response (api-request :get "/" {})]
        (:status response) => 200
        (response-mime-type response) => (mime-type :collection-list)
        (json? response) => true
        (collections-from-body (body-from-response response)) => [])))
  
  (with-state-changes [(before :facts (create-collection c))
                        (after :facts (delete-collection c))]

    ;; all good, 1 collection - 200 OK
    ;; curl -i --header "Accept: application/vnd.collection+vnd.fcms.collection+json;version=1" --header "Accept-Charset: utf-8" -X GET http://localhost:3000/
    (fact "when there is one collection"
      (let [response (api-request :get "/" {})]
        (:status response) => 200
        (response-mime-type response) => (mime-type :collection-list)
        (json? response) => true
        (let [coll (first (collections-from-body (body-from-response response)))]
          coll => (contains {:name c
                             :slug c
                             :version 1})
          (verify-collection-links c (:links coll))))))
  
  (with-state-changes [(before :facts (do
                                        (create-collection c)
                                        (create-collection "c-1")
                                        (create-collection "c-2")))
                        (after :facts (delete-collection c))]
    
    ;; all good, many collections - 200 OK
    ;; curl -i --header "Accept: application/vnd.collection+vnd.fcms.collection+json;version=1" --header "Accept-Charset: utf-8" -X GET http://localhost:3000/
    (fact "when there are many collections"
      (let [response (api-request :get "/" {})]
        (:status response) => 200
        (response-mime-type response) => (mime-type :collection-list)
        (json? response) => true
        (let [collections (collections-from-body (body-from-response response))]
          (first collections) => (contains {:name c
                                             :slug c
                                             :version 1})
          (verify-collection-links c (:links (first collections)))
          (second collections) => (contains {:name "c-1"
                                              :slug "c-1"
                                              :version 1})
          (verify-collection-links "c-1" (:links (second collections)))
          (last collections) => (contains {:name "c-2"
                                            :slug "c-2"
                                            :version 1})
          (verify-collection-links "c-2" (:links (last collections)))))))

    ;; all good, force pagination - 200 OK
    (future-fact "when forcing pagination")

    ;; all good, force pagination and using a subsequent page - 200 OK
    (future-fact "when forcing pagination and using a subsequent page"))

  (with-state-changes [(before :facts (do
                                        (doseq [coll (all-collections)]
                                          (delete-collection (:slug coll)))))]

    ;; no accept - 200 OK
    ;; curl -i --header --header "Accept-Charset: utf-8" -X GET http://localhost:3000/
    (fact "without the Accept header"
      (let [response (api-request :get "/" {})]
        (:status response) => 200
        (response-mime-type response) => (mime-type :collection-list)
        (json? response) => true
        (collections-from-body (body-from-response response)) => []))

    ;; no accept charset - 200 OK
    ;; curl -i --header "Accept: application/vnd.collection+vnd.fcms.collection+json;version=1" -X GET http://localhost:3000/
    (fact "without the Accept-Charset"
      (let [response (api-request :get "/" {:headers {}})]
        (:status response) => 200
        (response-mime-type response) => (mime-type :collection-list)
        (json? response) => true
        (collections-from-body (body-from-response response)) => [])))


(with-state-changes [(before :facts (doseq [coll (all-collections)]
                         (delete-collection (:slug coll))))]
  (facts "about attempting to use the REST API to list collections"
        
    ;; wrong accept - 406 Not Acceptable
    ;; curl -i --header "Accept: application/vnd.collection+vnd.fcms.item+json;version=1" --header "Accept-Charset: utf-8" -X GET http://localhost:3000/
    (fact "with the wrong Accept header"
      (let [response (api-request :get "/" {:headers {:Accept (mime-type :item-list)}})]
        (:status response) => 406
        (response-mime-type response) => (mime-type :text)
        (let [body (body-from-response response)]
          (.contains body "Acceptable media type: application/vnd.collection+vnd.fcms.collection+json") => true
          (.contains body "Acceptable charset: utf-8") => true)))


    ;; wrong accept charset - 406 Not Acceptable
    ;; curl -i --header "Accept: application/vnd.collection+vnd.fcms.collection+json;version=1" --header "Accept-Charset: iso-8859-1" -X GET http://localhost:3000/
    (fact "with the wrong Accept-Charset"
      (let [response (api-request :get "/" {:headers {:Accept-Charset "iso-8859-1"}})]
        (:status response) => 406
        (response-mime-type response) => (mime-type :text)
        (let [body (body-from-response response)]
          (.contains body "Acceptable media type: application/vnd.collection+vnd.fcms.collection+json") => true
          (.contains body "Acceptable charset: utf-8") => true)))
    
    (future-fact "using an invalid page")))