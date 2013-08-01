(require '[fcms.lib.body :as body]
         '[fcms.representations.common :refer (GET PUT DELETE)])

(defn verify-collection-list [slug]
  ; (body/verify :name item-name)
  ; (body/verify :slug item-slug)
  ; (body/verify :collection coll-slug)
  (body/verify-link "self" GET (str "/" slug "/"))
  (body/verify-link "update" PUT (str "/" slug "/"))
  (body/verify-link "delete" DELETE (str "/" slug "/")))

(Then #"^there will be no items in \"([^\"]*)\"$" [slug]
  (verify-collection-list slug))