(require '[clojure.string :refer (lower-case)]
         '[ring.mock.request :refer (request content-type)]
         '[fcms.resources.item :as item])

(def req)

(defn method-keyword [method]
  (keyword (lower-case method)))

(When #"^I have a \"([^\"]*)\" \"([^\"]*)\" request with URL \"([^\"]*)\"$" [res-type method url]
  (def req (content-type (request (method-keyword method) url) res-type)))