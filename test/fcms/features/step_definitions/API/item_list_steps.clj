(require '[fcms.lib.body :as body]
         '[fcms.representations.common :refer (POST)]
         '[fcms.resources.item :refer (item-media-type)]
         '[fcms.lib.checks :refer (check about-now? timestamp?)])

(defn- verify-item-list [coll-slug length]
  (body/verify-length :items length)
  (body/verify-link "create" POST (str "/" coll-slug "/") item-media-type))

(Then #"^there will be no items in \"([^\"]*)\"$" [coll-slug]
  (verify-item-list coll-slug 0))

(Then #"^there will be (\d+) items? in \"([^\"]*)\":$" [length coll-slug table]
  (verify-item-list coll-slug (read-string length))
  (loop [items (table->rows table), i 0]
    (when (seq items)
      ;; TODO verify match of table to JSON item
      (body/verify-item-links coll-slug (:slug (first items)) (:links (nth (body/value-of :items) i)))
      (recur (rest items) (inc i)))))

(Then #"^all the timestamps will be matching parseable dates$" []
  (doseq [item (body/value-of :items)]
    (timestamp? (:created-at item))
    (timestamp? (:updated-at item))
    (check (= (:created-at item) (:updated-at item)))))