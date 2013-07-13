(require '[fcms.lib.body :as body]
         '[fcms.representations.common :refer (GET PUT DELETE)])

(Then #"^the item is \"([^\"]*)\" named \"([^\"]*)\" in collection \"([^\"]*)\"$" [item-slug item-name coll-slug]
  (body/verify :name item-name)
  (body/verify :slug item-slug)
  (body/verify :collection coll-slug)
  ; the created-at of the item is about now
  ; the updated-at of the item is the same as the created at
  (body/verify-link "self" GET (str "/" coll-slug "/" item-slug))
  (body/verify-link "update" PUT (str "/" coll-slug "/" item-slug))
  (body/verify-link "delete" DELETE (str "/" coll-slug "/" item-slug))
  (body/verify-link "collection" GET (str "/" coll-slug)))