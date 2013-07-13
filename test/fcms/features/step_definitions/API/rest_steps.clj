(require '[clojure.string :refer (lower-case)]
         '[clj-json.core :as json]
         '[ring.mock.request :refer (request body content-type header)]
         '[fcms.features.step-definitions.api.mock :as mock]
         '[fcms.lib.check :refer (check)]
         '[fcms.app :refer (app)]
         '[fcms.resources.collection :as collection]
         '[fcms.resources.item :as item])

(defn- method-keyword [method]
  (keyword (lower-case method)))

(defn- mime-type [res-type]
  (case res-type
    "item" item/item-media-type
    "collection" collection/collection-media-type))

(When #"^I have a \"([^\"]*)\" \"([^\"]*)\" request with URL \"([^\"]*)\"$" [res-type method url]
  (mock/body {})
  (mock/request (request (method-keyword method) url))
  (mock/request (content-type (mock/request) (mime-type res-type)))
  (mock/request (header (mock/request) "Accept" (mime-type res-type)))
  (mock/request (header (mock/request) "Accept-Charset" "utf-8")))

(When #"^I set the \"([^\"]*)\" to \"([^\"]*)\"$" [property value]
  (mock/body (assoc (mock/body) (keyword property) value)))

(Then #"^the status is \"([^\"]*)\"$" [status]
  (mock/response (app (body (mock/request) (json/generate-string (mock/body)))))
  (check (= (read-string status) (:status (mock/response)))))

(Then #"^the \"([^\"]*)\" header is \"([^\"]*)\"$" [header value]
  (check (= value (get-in (mock/response) [:headers header]))))

(Then #"^the \"([^\"]*)\" is \"([^\"]*)\"$" [property value]
  (check (= value ((mock/body) (keyword property)))))