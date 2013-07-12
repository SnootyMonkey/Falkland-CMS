(require '[clojure.string :refer (lower-case)]
         '[clj-json.core :as json]
         '[fcms.lib.check :refer (check)]
         '[ring.mock.request :as mock]
         '[fcms.app :refer (app)]
         '[fcms.resources.collection :as collection]
         '[fcms.resources.item :as item])

(def req) ; mock HTTP request
(def body) ; body of the mock HTTP request or response
(def resp) ; mock HTTP response

(defn method-keyword [method]
  (keyword (lower-case method)))

(defn mime-type [res-type]
  (case res-type
    "item" item/item-media-type
    "collection" collection/collection-media-type))

(When #"^I have a \"([^\"]*)\" \"([^\"]*)\" request with URL \"([^\"]*)\"$" [res-type method url]
  (def body {})
  (def req (mock/request (method-keyword method) url))
  (def req (mock/content-type req (mime-type res-type)))
  (def req (mock/header req "Accept" (mime-type res-type)))
  (def req (mock/header req "Accept-Charset" "utf-8")))

(When #"^I set the \"([^\"]*)\" to \"([^\"]*)\"$" [property value]
  (def body (assoc body (keyword property) value)))

(Then #"^the status is \"([^\"]*)\"$" [status]
  (def resp (app (mock/body req (json/generate-string body))))
  (check (= (read-string status) (:status resp))))

(Then #"^the item is \"([^\"]*)\" named \"([^\"]*)\" in \"([^\"]*)\"$" [item-slug item-name coll-slug]
  (prn resp)
  ; location of the item is "/coll-slug/item-slug"
  (check (= (str "/" coll-slug "/" item-slug) ((resp :headers) "Location"))))
  ; the name of the item is item-name
  ; the slug of the item is item-slug
  ; the collection of the item is coll-slug
  ; the created-at of the item is about now
  ; the updated-at of the item is the same as the created at
  ; the self link of the item is a GET on "/coll-slug/item-slug"
  ; the update link of the item is PUT on "/coll-slug/item-slug"
  ; the delete link of the item is DELETE on "/coll-slug/item-slug"
  ; the collection link of the item is GET on "/coll-slug"
