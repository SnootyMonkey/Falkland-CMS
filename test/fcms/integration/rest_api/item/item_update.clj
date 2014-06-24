(ns fcms.integration.rest-api.item.item-update
  (:require [midje.sweet :refer :all]
            [fcms.lib.resources :refer :all]
            [fcms.lib.rest-api-mock :refer :all]
            [fcms.resources.collection :as collection]
            [fcms.resources.collection-resource :as resource]
            [fcms.resources.item :as item]))

(defn- create-collection-c-items []
  (item/create-item c i {:slug i :description ascii-description})
  (item/create-item c unicode-name {:slug "another-i" :description unicode-description}))


(defn- setup []
  (reset-collection c)
  (create-collection-c-items))

(defn- teardown []
  (collection/delete-collection c))

(with-state-changes [(before :facts (setup))
                     (after :facts (teardown))]
  (facts "about updating items with the REST API"
         ;; all good, no slug - 200 OK
         ;; (get-in ctx [:updated-item :slug])
         (fact "updating an item without providing a new slug"
               (let [response (api-request :put "/c/i" {:headers
                                                        {:Accept-Charset "utf-8"
                                                         :Content-Type (mime-type :item)
                                                         :Accept (mime-type :item)}
                                                        :body
                                                        {:name "i-prime"
                                                         :i i}})]
                 (:status response) => 200
                 (response-mime-type response) => (mime-type :item)
                 (json? response) => true
                 (let [item (item/get-item c i)]
                   item => (contains
                            {:collection c
                             :name "i-prime"
                             :i i
                             :slug i
                             :version 2})
                   (:description item) => nil)
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
                   (:name body) => "i-prime"
                   (:description body) => nil
                   (:i body) => i)))
         ;; all good, with same slug - 200 OK
         ;; curl -i --header "Accept: application/vnd.fcms.item+json;version=1" --header "Accept-Charset: utf-8" --header "Content-Type: application/vnd.fcms.item+json;version=1" -X PUT -d '{"name":"i-prime", "slug":"i", i":"i"}' http://localhost:3000/c/i
         (fact "update an item providing the same slug"
               (let [response (api-request :put "/c/i" {:headers {:Accept-Charset "utf-8"
                                                                  :Content-Type (mime-type :item)
                                                                  :Accept (mime-type :item)}
                                                        :body {:name "i-prime"
                                                               :slug "i"
                                                               :i "i"}})]
                 (:status response) => 200
                 (response-mime-type response) => (mime-type :item)
                 (json? response) => true
                 (let [item (item/get-item c i)]
                   item => (contains
                            {:collection c
                             :name "i-prime"
                             :i i
                             :slug i
                             :version 2})
                   (:description item) => nil)
                 (collection/item-count c) => 2)
               (let [response (api-request :get "/c/i" {:headers
                                                        {:Accept-Charset "utf-8"
                                                         :Accept (mime-type :item)}})]
                 (:status response) => 200
                 (response-mime-type response) => (mime-type :item)
                 (json? response) => true)
               (let [response (api-request :get "/c/i" {:headers
                                                        {:Accept-Charset "utf-8"
                                                         :Accept (mime-type :item)}})]
                 (:status response) => 200
                 (response-mime-type response) => (mime-type :item)
                 (json? response) => true
                 (let [body (body-from-response response)]
                   (:collection body) => c
                   (:slug body) => i
                   (:name body) => "i-prime"
                   (:description body) => nil
                   (:i body) => i)))
         ;; all good, with different slug - 200 OK
         ;; curl -i --header "Accept: application/vnd.fcms.item+json;version=1" --header "Accept-Charset: utf-8" --header "Content-Type: application/vnd.fcms.item+json;version=1" -X PUT -d '{"name":"i", "slug":"i-moved","i":"i"}' http://localhost:3000/c/i
         (fact "update an item providing a new slug"
               (let [response (api-request :put "/c/i" {:headers
                                                        {:Accept-Charset "utf-8"
                                                         :Content-Type (mime-type :item)
                                                         :Accept (mime-type :item)}
                                                        :body
                                                        {:name "i-prime"
                                                         :slug "i-moved"
                                                         :i "i"}})]
                 (:status response) => 200
                 (response-mime-type response) => (mime-type :item)
                 (json? response) => true
                 (let [item (item/get-item c "i-moved")]
                   item => (contains
                            {:collection c
                             :name "i-prime"
                             :i i
                             :slug "i-moved"
                             :version 2})
                   (:description item) => nil)
                 (collection/item-count c) => 2)
               (let [response (api-request :get "/c/i" {:headers
                                                        {:Accept-Charset "utf-8"
                                                         :Accept (mime-type :item)}})]
                 (:status response) => 404
                 (body-from-response response) => nil)
               (let [response (api-request :get "/c/i-moved" {:headers
                                                              {:Accept-Charset "utf-8"
                                                               :Accept (mime-type :item)}})]
                 (:status response) => 200
                 (response-mime-type response) => (mime-type :item)
                 (json? response) => true
                 (let [body (body-from-response response)]
                   (:collection body) => c
                   (:slug body) => "i-moved"
                   (:name body) => "i-prime"
                   (:description body) => nil
                   (:i body) => i)))
         ;; all good, unicode in the body - 200 OK
         ;; curl -i --header "Accept: application/vnd.fcms.item+json;version=1" --header "Accept-Charset: utf-8" --header "Content-Type: application/vnd.fcms.item+json;version=1" -X PUT -d '{"name":"私", "description":"Τη γλώσσα μου έδωσαν ελληνική მივჰხვდე მას ჩემსა الزجاج و هذا لا يؤلمني. मैं काँच खा सकता ฉันกินกระจกได้ לא מזיק Mogę jeść szkło", "i":"i"}' http://localhost:3000/c/another-i
         (fact "update an item containing unicode"
               (let [response (api-request :put "/c/another-i" {:headers
                                                                {:Accept-Charset "utf-8"
                                                                 :Content-Type (mime-type :item)
                                                                 :Accept (mime-type :item)}
                                                                :body {:name "私"
                                                                       :description "Τη γλώσσα μου έδωσαν ελληνική მივჰხვდე მას ჩემსა الزجاج و هذا لا يؤلمني. मैं काँच खा सकता ฉันกินกระจกได้ לא מזיק Mogę jeść szkło"
                                                                       :i i}})]
                 (:status response) => 200
                 (response-mime-type response) => (mime-type :item)
                 (json? response) => true
                 (let [item (item/get-item c "another-i")]
                   item => (contains
                            {:collection c
                             :name "私"
                             :i i
                             :description "Τη γλώσσα μου έδωσαν ελληνική მივჰხვდე მას ჩემსა الزجاج و هذا لا يؤلمني. मैं काँच खा सकता ฉันกินกระจกได้ לא מזיק Mogę jeść szkło"
                             :slug "another-i"
                             :version 2}))
                 (collection/item-count c) => 2)
               (let [response (api-request :get "/c/another-i" {:headers
                                                                {:Accept-Charset "utf-8"
                                                                 :Accept (mime-type :item)}})]
                 (:status response) => 200
                 (response-mime-type response) => (mime-type :item)
                 (json? response) => true
                 (let [body (body-from-response response)]
                   (:collection body) => c
                   (:slug body) => "another-i"
                   (:name body) => "私"
                   (:description body) => "Τη γλώσσα μου έδωσαν ελληνική მივჰხვდე მას ჩემსა الزجاج و هذا لا يؤلمني. मैं काँच खा सकता ฉันกินกระจกได้ לא מזיק Mogę jeść szkło"
                   (:i body) => i)))
         ;; all good, no "name" in body - 200 OK
         ;; curl -i --header "Accept: application/vnd.fcms.item+json;version=1" --header "Accept-Charset: utf-8" --header "Content-Type: application/vnd.fcms.item+json;version=1" -X PUT -d '{description":"a no name i", "i":"i"}' http://localhost:3000/c/i
         (fact "attempt to update an item without a name"
               (let [response (api-request :put "/c/i" {:headers
                                                        {:Accept-Charset "utf-8"
                                                         :Content-Type (mime-type :item)
                                                         :Accept (mime-type :item)}
                                                        :body
                                                        {:description "a no name i"
                                                         :i i}})]
                 (:status response) => 200
                 (response-mime-type response) => (mime-type :item)
                 (json? response) => true
                 (let [item (item/get-item c i)]
                   item => (contains
                            {:collection c
                             :name i
                             :i i
                             :description "a no name i"
                             :slug i
                             :version 2}))
                 (collection/item-count c) => 2))
         ;; conflicting reserved properties - 422 Unprocessable Entity
         ;; curl -i --header "Accept: application/vnd.fcms.item+json;version=1" --header "Accept-Charset: utf-8" --header "Content-Type: application/vnd.fcms.item+json;version=1" -X PUT -d '{"name":"i-1", "version":"foo", "i":"i"}' http://localhost:3000/c/i
         ;; curl -i --header "Accept: application/vnd.fcms.item+json;version=1" --header "Accept-Charset: utf-8" --header "Content-Type: application/vnd.fcms.item+json;version=1" -X PUT -d '{"name":"i-2", "collection":"foo", "i":"j"}' http://localhost:3000/c/i
         ;; curl -i --header "Accept: application/vnd.fcms.item+json;version=1" --header "Accept-Charset: utf-8" --header "Content-Type: application/vnd.fcms.item+json;version=1" -X PUT -d '{"name":"i-3", "id":"foo", "i":"k"}' http://localhost:3000/c/i
         ;; curl -i --header "Accept: application/vnd.fcms.item+json;version=1" --header "Accept-Charset: utf-8" --header "Content-Type: application/vnd.fcms.item+json;version=1" -X PUT -d '{"name":"i-4", "type":"foo", "i":"l"}' http://localhost:3000/c/i
         ;; curl -i --header "Accept: application/vnd.fcms.item+json;version=1" --header "Accept-Charset: utf-8" --header "Content-Type: application/vnd.fcms.item+json;version=1" -X PUT -d '{"name":"i-5", "created-at":"foo", "i":"m"}' http://localhost:3000/c/i
         ;; curl -i --header "Accept: application/vnd.fcms.item+json;version=1" --header "Accept-Charset: utf-8" --header "Content-Type: application/vnd.fcms.item+json;version=1" -X PUT -d '{"name":"i-6", "updated-at":"foo", "i":"n"}' http://localhost:3000/c/i
         (fact "attempt to update an item with a property that conflicts with a reserved property"
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
         ;; no accept type - 200 OK
         ;; curl -i --header "Accept-Charset: utf-8" --header "Content-Type: application/vnd.fcms.item+json;version=1" -X PUT -d '{"name":"i-prime", "i":"i"}' http://localhost:3000/c/i         
         (fact "update an item without using an accept header"
               (let [response (api-request :put "/c/i" {:headers
                                                          {:Accept-Charset "utf-8"
                                                           :Content-Type (mime-type :item)}
                                                          :body
                                                          {:name "i-prime"
                                                           :i i}})]
                 (:status response) => 200
                 (json? response) => true
                 (response-mime-type response) => (mime-type :item))
               (let [item (item/get-item c i)]
                 item => (contains
                          {:collection c
                           :name "i-prime"
                           :i i
                           :slug i
                           :version 2})
                 (:description item) => nil)               
               (let [response (api-request :get "/c/i" {:headers
                                                        {:Accept-Charset "utf-8"
                                                         :Accept (mime-type :item)}})]
                 (:status response) => 200
                 (response-mime-type response) => (mime-type :item)
                 (json? response) => true
                 (let [body (body-from-response response)]
                   (:collection body) => c
                   (:slug body) => i
                   (:name body) => "i-prime"
                   (:description body) => nil
                   (:i body) => i)))
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
         ;; no Content-Type header - 200 OK
         ;; curl -i --header "Accept: application/vnd.fcms.item+json;version=1" --header "Accept-Charset: utf-8" -X PUT -d '{"name":"i-prime", "i":"i"}' http://localhost:3000/c/i
         (fact "attempt to update an item without a content-type header"
               (let [response (api-request :put "/c/i" {:headers
                                                        {:Accept-Charset "utf-8"
                                                         :Accept (mime-type :item)}
                                                        :body
                                                        {:name "i-prime"
                                                         :i i}})]
                 (:status response) => 200
                 (response-mime-type response) => (mime-type :item)
                 (json? response) => true)
               (let [item (item/get-item c i)]
                 item => (contains
                          {:collection c
                           :name "i-prime"
                           :i i
                           :slug i
                           :version 2})
                 (:description item) => nil)
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
                   (:name body) => "i-prime"
                   (:description body) => nil
                   (:i body) => i)))
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
         ;; no charset - 200 OK
         ;; curl -i --header "Accept: application/vnd.fcms.item+json;version=1" --header "Content-Type: application/vnd.fcms.item+json;version=1" -X PUT -d '{"name":"i-prime", "i":"i"}' http://localhost:3000/c/i         
         (fact "attempt to update an item without an Accept-Charset header"
               (let [response (api-request :put "/c/i" {:headers
                                                        {:Content-Type (mime-type :item)
                                                         :Accept (mime-type :item)}
                                                        :body
                                                        {:name "i-prime"
                                                         :i i}})]
                 (:status response) => 200
                 (response-mime-type response) => (mime-type :item)
                 (json? response) => true)
               (let [item (item/get-item c i)]
                 item => (contains
                          {:collection c
                           :name "i-prime"
                           :i i
                           :slug i
                           :version 2})
                 (:description item) => nil)
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
                     (:name body) => "i-prime"
                     (:description body) => nil
                     (:i body) => i)))))

;; Clojure throws a "method code too large!" exception so the tests
;; are separated

(with-state-changes [(before :facts (setup))
                     (after :facts (teardown))]
  (facts "about updating items with the REST API"
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
         (fact "attempt to update an item with a slug that's invalid"
               (let [response (api-request :put "/c/i" {:headers
                                                        {:Accept-Charset "utf-8"
                                                         :Content-Type (mime-type :item)
                                                         :Accept (mime-type :item)}
                                                        :body
                                                        {:name "i-prime"
                                                         :slug "I i"
                                                         :i i}})]
                 (:status response) => 422
                 (response-mime-type response) => (mime-type :text)
                 (body-from-response response) => "Invalid slug."
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
                   (:i body) => nil)))))
