(require '[fcms.features.step-definitions.api.mock :as mock]
         '[fcms.lib.check :refer (check)])

(defn- find-link [rel]
  (some (fn [link] (if (= rel (:rel link)) link nil)) (:links (mock/body))))

(defn- verify-in-body [prop value]
  (check (= value (prop (mock/body)))))

(defn- verify-link [rel method href]
  (if-let [link (find-link rel)]
    (do
      (check (= method (:method link)))
      (check (= href (:href link))))
    (check (= rel :link_not_present))))

(Then #"^the item is \"([^\"]*)\" named \"([^\"]*)\" in collection \"([^\"]*)\"$" [item-slug item-name coll-slug]
  (verify-in-body :name item-name)
  (verify-in-body :slug item-slug)
  (verify-in-body :collection coll-slug)
  ; the created-at of the item is about now
  ; the updated-at of the item is the same as the created at
  (verify-link "self" "GET" (str "/" coll-slug "/" item-slug))
  (verify-link "update" "PUT" (str "/" coll-slug "/" item-slug))
  (verify-link "delete" "DELETE" (str "/" coll-slug "/" item-slug))
  (verify-link "collection" "GET" (str "/" coll-slug)))