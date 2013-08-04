(require '[fcms.lib.body :as body]
         '[fcms.representations.common :refer (POST)]
         '[fcms.resources.item :refer (item-media-type)]
         '[fcms.lib.checks :refer (check about-now? timestamp?)])

(defn- verify-item-list [slug length]
  (body/verify-length :items length)
  (body/verify-link "create" POST (str "/" slug "/") item-media-type))

(Then #"^there will be no items in \"([^\"]*)\"$" [slug]
  (verify-item-list slug 0))

(Then #"^there will be (\d+) items? in \"([^\"]*)\":$" [length slug table]
  (verify-item-list slug (read-string length)))
  ;;(doseq [item (table->rows table)]
    ;; TODO verify match of table to JSON item
    ;; TODO verify JSON item's links

(Then #"^all the timestamps will be matching parseable dates$" []
  (doseq [item (body/value-of :items)]
    (timestamp? (:created-at item))
    (timestamp? (:updated-at item))
    (check (= (:created-at item) (:updated-at item)))))