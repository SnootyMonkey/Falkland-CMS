(require '[clj-time.core :refer (before?)]
         '[clj-time.format :refer (parse)]
         '[fcms.lib.body :as body]
         '[fcms.representations.common :refer (GET PUT DELETE)]
         '[fcms.resources.item :refer (item-media-type)]
         '[fcms.resources.collection :refer (collection-media-type)]
         '[fcms.lib.checks :refer (check about-now? timestamp?)])

(defn verify-item [coll-slug item-slug item-name]
  (body/verify :name item-name)
  (body/verify :slug item-slug)
  (body/verify :collection coll-slug)
  (body/verify-link "self" GET (str "/" coll-slug "/" item-slug) item-media-type)
  (body/verify-link "update" PUT (str "/" coll-slug "/" item-slug) item-media-type)
  (body/verify-link "delete" DELETE (str "/" coll-slug "/" item-slug))
  (body/verify-link "collection" GET (str "/" coll-slug) collection-media-type))

(Then #"^the item \"([^\"]*)\" in collection \"([^\"]*)\" will be named \"([^\"]*)\"$" [item-slug coll-slug item-name]
  (verify-item coll-slug item-slug item-name))

(Then #"^the new item \"([^\"]*)\" in collection \"([^\"]*)\" will be named \"([^\"]*)\"$" [item-slug coll-slug item-name]
  (verify-item coll-slug item-slug item-name)
  (body/verify :version 1)
  (timestamp? (body/value-of :created-at))
  (timestamp? (body/value-of :updated-at))
  (about-now? (body/value-of :created-at))
  (body/verify-relation = :created-at :updated-at))

(Then #"^the updated item \"([^\"]*)\" in collection \"([^\"]*)\" will be named \"([^\"]*)\"$" [item-slug coll-slug item-name]
  (verify-item coll-slug item-slug item-name)
  (body/verify :version 2)
  (timestamp? (body/value-of :created-at))
  (timestamp? (body/value-of :updated-at))
  (about-now? (body/value-of :updated-at))
  (check (before? (parse (body/value-of :created-at)) (parse (body/value-of :updated-at)))))

(Then #"^the timestamps will be matching parseable dates$" []
  (timestamp? (body/value-of :created-at))
  (timestamp? (body/value-of :updated-at))
  (body/verify-relation = :created-at :updated-at))