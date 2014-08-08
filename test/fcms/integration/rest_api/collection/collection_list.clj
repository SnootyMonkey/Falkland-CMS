(ns fcms.integration.rest-api.collection.collection-list
  "Integration tests for listing collections with the REST API."
  (:require [midje.sweet :refer :all]
            [clj-time.format :refer (parse)]
            [fcms.lib.resources :refer :all]
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
;; wrong accept
;; no accept charset
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

    (fact "no collections"
      (let [response (api-request :get "/" {})]
        (:status response) => 200
        (response-mime-type response) => (mime-type :collection)
        (json? response) => true
        (collections-from-body (body-from-response response)) => [])))
  
  (with-state-changes [(before :facts (create-collection c))
                        (after :facts (delete-collection c))]
    (fact "one collection"
      (let [response (api-request :get "/" {})]
        (:status response) => 200
        (response-mime-type response) => (mime-type :collection)
        (json? response) => true 
        (first (collections-from-body (body-from-response response))) => (contains {:name c
                                                                                     :slug c
                                                                                     :version 1}))))
  
  (with-state-changes [(before :facts (do
                                        (create-collection c)
                                        (create-collection "c-1")
                                        (create-collection "c-2")))
                        (after :facts (delete-collection c))]
    (fact "many collections"
      (let [response (api-request :get "/" {})]
        (:status response) => 200
        (response-mime-type response) => (mime-type :collection)
        (json? response) => true
        (let [collections (collections-from-body (body-from-response response))]
          (first collections) => (contains {:name c
                                             :slug c
                                             :version 1})
          (second collections) => (contains {:name "c-1"
                                              :slug "c-1"
                                              :version 1})
          (last collections) => (contains {:name "c-2"
                                            :slug "c-2"
                                            :version 1})))))
  ;; all good, force pagination - 200 OK
  (future-fact "when forcing pagination")

  ;; all good, force pagination and using a subsequent page - 200 OK
  (future-fact "when forcing pagination and using a subsequent page"))

(with-state-changes [(before :facts (doseq [coll (all-collections)]
                         (delete-collection (:slug coll))))]
  (facts "about attempting to use the REST API to list collections"
    
    (fact "without using an Accept header"
      (let [response (api-request :get "/" {})]
        (:status response) => 200
        (response-mime-type response) => (mime-type :collection)
        (json? response) => true
        (collections-from-body (body-from-response response)) => []))
    
    (fact "wrong Accept header"
      (let [response (api-request :get "/" {:headers {:Accept (mime-type :item)}})]
        (:status response) => 406
        (response-mime-type response) => (mime-type :text)
        (let [body (body-from-response response)]
          (.contains body "Acceptable media type: application/vnd.fcms.collection+json") => true
          (.contains body "Acceptable charset: utf-8") => true)))

    (fact "no Accept-Charset"
      (let [response (api-request :get "/" {:headers {}})]
        (:status response) => 200
        (response-mime-type response) => (mime-type :collection)
        (json? response) => true
        (collections-from-body (body-from-response response)) => []))

    (fact "wrong Accept-Charset"
      (let [response (api-request :get "/" {:headers {:Accept-Charset "iso-8859-1"}})]
        (:status response) => 406
        (response-mime-type response) => (mime-type :text)
        (let [body (body-from-response response)]
          (.contains body "Acceptable media type: application/vnd.fcms.collection+json") => true
          (.contains body "Acceptable charset: utf-8") => true)))
    
    (future-fact "using an invalid page")))

