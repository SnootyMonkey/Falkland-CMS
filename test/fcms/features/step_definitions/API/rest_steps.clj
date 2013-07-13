(require '[clojure.string :refer (lower-case)]
         '[clojure.walk :refer (keywordize-keys)]
         '[clj-json.core :as json]
         '[clojure.core.incubator :refer (dissoc-in)]
         '[print.foo :refer (print->)]
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

(defn- body-from-response [resp-map]
  (try
    ;; treat the body as JSON
    (keywordize-keys (json/parse-string (:body resp-map)))
    (catch Exception e
      ;; must not be valid JSON
      (:body resp-map))))

;; mock a request

(When #"^I have a \"([^\"]*)\" \"([^\"]*)\" request with URL \"([^\"]*)\"$" [res-type method url]
  (mock/body {})
  (mock/request (request (method-keyword method) url))
  (mock/request (content-type (mock/request) (mime-type res-type)))
  (mock/request (header (mock/request) "Accept" (mime-type res-type)))
  (mock/request (header (mock/request) "Accept-Charset" "utf-8")))

(When #"^I set the \"([^\"]*)\" header to \"([^\"]*)\"$" [header value]
  (mock/request (header (mock/request) header value)))

(When #"^I accept a \"([^\"]*)\"$" [res-type]
  (mock/request (header (mock/request) "Accept" (mime-type res-type))))

(When #"^I remove the header \"([^\"]*)\"$" [header]
  (mock/request (dissoc-in (mock/request) [:headers (lower-case header)])))

(When #"^I set the \"([^\"]*)\" to \"([^\"]*)\"$" [property value]
  (mock/body (assoc (mock/body) (keyword property) value)))

;; pretends to execute the request, then checks the HTTP status code
(Then #"^the status is \"([^\"]*)\"$" [status]
  (mock/response (app (body (mock/request) (json/generate-string (mock/body)))))
  (mock/body (body-from-response (mock/response)))
  (check (= (read-string status) (:status (mock/response)))))

;; check on the response

(Then #"^the \"([^\"]*)\" header is \"([^\"]*)\"$" [header value]
  (check (= value (get-in (mock/response) [:headers header]))))

(Then #"^the \"([^\"]*)\" header is not present$" [header]
  (check (nil? (get-in (mock/response) [:headers header]))))

(Then #"^the \"([^\"]*)\" is \"([^\"]*)\"$" [property value]
  (check (map? (mock/body)))
  (check (= value ((mock/body) (keyword property)))))

(Then #"^the body contains \"([^\"]*)\"$" [contents]
  (check (string? (mock/body)))
  (check (.contains (mock/body) contents)))