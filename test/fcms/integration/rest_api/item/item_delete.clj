(ns fcms.integration.rest-api.item.item-create
  (:require [midje.sweet :refer :all]
            [fcms.lib.resources :refer :all]
            [fcms.lib.rest-api-mock :refer :all]
            [fcms.resources.collection :as collection]
            [fcms.resources.collection-resource :as resource]
            [fcms.resources.item :as item]))

(defn- create-collection-c-items []
  (item/create-item c i {:slug i :description ascii-description})
  (item/create-item c "another-i" {:slug "another-i" :description "this is another item!"}))


(defn- setup []
  (reset-collection c)
  (create-collection-c-items)  )

(defn- teardown []
  (collection/delete-collection c))

(with-state-changes [(before :facts (setup))
                     (after :facts (teardown))]
  (facts "about deleting items with the REST API"
         ;; all good - 204 No Content
         ;; curl -i -X DELETE http://localhost:3000/c/i
         (fact "delete an item"
               (let [response (api-request :delete "/c/i" {:headers {}})]
                 (:status response) => 204
                 (body-from-response response) => nil
                 (collection/item-count c) => 1)
               (let [response (api-request :get "/c/i" {:headers {:Accept-Charset "utf-8"}})]
                 (:status response) => 404)
               (let [response (api-request :get "/c/another-i" {:headers {:Accept-Charset "utf-8"}})]
                 (:status response) => 200
                 (response-mime-type response) => (mime-type :item)
                 (json? response) => true)
               (item/get-item c "another-i") => (contains
                                                 {:collection c
                                                  :name "another-i"
                                                  :slug "another-i"
                                                  :version 1}))
         ;; collection doesn't exist - 404 Not Found
         ;; curl -i -X DELETE http://localhost:3000/not-here/i
         (fact "delete an item from a collection that does not exist"
               (let [response (api-request :delete "/not-here/i" {:headers {}})]
                 (:status response) => 404
                 (response-mime-type response) => (mime-type :text)
                 (body-from-response response) => "Collection not found."
                 (collection/item-count c) => 2)
               (doseq [item-name ["i" "another-i"]]
                 (let [response (api-request :get (str "/c/" item-name) {:headers {:Accept (mime-type :item)
                                                                                   :Accept-Charset "utf-8"}})]
                   (:status response) => 200
                   (response-mime-type response) => (mime-type :item)
                   (json? response) => true
                   (item/get-item c item-name) => (contains
                                                   {:collection c
                                                    :name item-name
                                                    :slug item-name
                                                    :version 1}))))
         ;; item doesn't exist - 404 Not Found
         ;; curl -i -X DELETE http://localhost:3000/c/not-here
         (fact "deleting an item that doesn't exist"
               (let [response (api-request :delete "/c/not-here" {:headers {}})]
                 (:status response) => 404
                 (body-from-response response) => nil
                 (collection/item-count c) => 2)
               (doseq [item-name ["i" "another-i"]]
                 (let [response (api-request :get (str "/c/" item-name) {:headers {:Accept (mime-type :item)
                                                                                   :Accept-Charset "utf-8"}})]
                   (:status response) => 200
                   (response-mime-type response) => (mime-type :item)
                   (json? response) => true
                   (item/get-item c item-name) => (contains
                                                   {:collection c
                                                    :name item-name
                                                    :slug item-name
                                                    :version 1}))))))
