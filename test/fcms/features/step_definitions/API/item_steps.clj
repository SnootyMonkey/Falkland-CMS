(require '[fcms.features.step-definitions.api.mock :as mock]
         '[fcms.lib.check :refer (check)])

  ; location of the item is "/coll-slug/item-slug"
  ;(check (= (str "/" coll-slug "/" item-slug) (get-in (mock/response) [:headers "Location"]))))

(Then #"^the item is \"([^\"]*)\" named \"([^\"]*)\" in collection \"([^\"]*)\"$" [item-slug item-name coll-slug]
  ; the name of the item is item-name
  (check (= item-name (:name (mock/body))))
  ; the slug of the item is item-slug
  (check (= item-slug (:slug (mock/body)))))
  ; the collection of the item is coll-slug
  ; the created-at of the item is about now
  ; the updated-at of the item is the same as the created at
  ; the self link of the item is a GET on "/coll-slug/item-slug"
  ; the update link of the item is PUT on "/coll-slug/item-slug"
  ; the delete link of the item is DELETE on "/coll-slug/item-slug"
  ; the collection link of the item is GET on "/coll-slug"
