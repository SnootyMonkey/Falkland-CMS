(ns fcms.integration.api.item.item-list
  "Integration tests for listing items with the REST API."
  (:require [midje.sweet :refer :all]
            [fcms.lib.resources :refer :all]
            [fcms.lib.rest-api-mock :refer :all]
            [fcms.resources.collection :as collection]
            [fcms.resources.item :as item]))

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

(defn- create-collection-one []
  (collection/create-collection "one")
  (item/create-item "one" i {:slug i :description unicode-name}))

(defn- setup []
  (reset-collection e)
  ())

(defn- teardown []
  (collection/delete-collection e))

(with-state-changes [(before :facts (setup))
                     (after :facts (teardown))]

  (facts "about listing items"
         (fact "from an empty collection"
               (let [response (api-request :get "/e/"
                                           {:headers
                                            {:Accept (mime-type :item-collection)}})]
                 (:status response) => 200))))
