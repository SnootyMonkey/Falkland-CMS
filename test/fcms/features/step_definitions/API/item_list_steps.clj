; (require '[fcms.lib.http-mock :as http-mock]
;          '[fcms.lib.body :as body]
;          '[fcms.representations.common :refer (POST)]
;          '[fcms.resources.item :refer (item-media-type)]
;          '[fcms.lib.checks :refer (check check-timestamp?)])

; (defn- verify-item-list [coll-slug length]
;   (body/verify-length [:collection :items] length)
;   (body/verify-link "create" POST (str "/" coll-slug "/") item-media-type (get-in (http-mock/body) [:collection :links])))

; (Then #"^there will be no items in \"([^\"]*)\"$" [coll-slug]
;   (verify-item-list coll-slug 0))

; ;; Compare the items in the response to the items in the provided table
; (Then #"^there will be (this|these) items? in \"([^\"]*)\":$" [_ coll-slug table]
;   ;; the right # of items?
;   (verify-item-list coll-slug (count (table->rows table)))
;   ;; check each item
;   (loop [items (table->rows table), i 0]
;     (when (seq items)
;       ;; check that all the properties match
;       (doseq [property (first items)]
;         (check (= ((first property) (nth (body/value-of [:collection :items]) i)) (last property))))
;       ;; check that all the item links are correct
;       (body/verify-item-links coll-slug (:slug (first items)) (:links (nth (body/value-of [:collection :items]) i)))
;       (recur (rest items) (inc i)))))

; (Then #"^all the timestamps will be matching parseable dates$" []
;   (doseq [item (body/value-of :items)]
;     (check-timestamp? (:created-at item))
;     (check-timestamp? (:updated-at item))
;     (check (= (:created-at item) (:updated-at item)))))