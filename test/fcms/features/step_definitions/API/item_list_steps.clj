(require '[fcms.lib.body :as body]
         '[fcms.representations.common :refer (POST)]
         '[fcms.resources.item :refer (item-media-type)])

(defn verify-item-list [slug length]
  (body/verify-length :items length)
  (body/verify-link "create" POST (str "/" slug "/") item-media-type))

(Then #"^there will be no items in \"([^\"]*)\"$" [slug]
  (verify-item-list slug 0))