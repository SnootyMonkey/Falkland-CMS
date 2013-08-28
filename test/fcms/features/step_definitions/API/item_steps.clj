(require '[clj-time.core :refer (before?)]
         '[clj-time.format :refer (parse)]
         '[fcms.lib.body :as body]
         '[fcms.lib.checks :refer (check about-now? timestamp?)])

(defn- verify-item [coll-slug item-slug item-name]
  (body/verify :name item-name)
  (body/verify :slug item-slug)
  (body/verify :collection coll-slug)
  (timestamp? (body/value-of :created-at))
  (timestamp? (body/value-of :updated-at))
  (about-now? (body/value-of :created-at))
  (body/verify-item-links coll-slug item-slug (body/value-of :links)))

(defn- verify-updated-item [coll-slug item-slug item-name version]
  (verify-item coll-slug item-slug item-name)
  (body/verify :version version)
  (check (before? (parse (body/value-of :created-at)) (parse (body/value-of :updated-at)))))

(Then #"^the item \"([^\"]*)\" in collection \"([^\"]*)\" will be named \"([^\"]*)\"$" [item-slug coll-slug item-name]
  (verify-item coll-slug item-slug item-name))

(Then #"^the new item \"([^\"]*)\" in collection \"([^\"]*)\" will be named \"([^\"]*)\"$" [item-slug coll-slug item-name]
  (verify-item coll-slug item-slug item-name)
  (body/verify :version 1)
  (body/verify-relation = :created-at :updated-at))

(Then #"^the updated item \"([^\"]*)\" in collection \"([^\"]*)\" will be named \"([^\"]*)\"$" [item-slug coll-slug item-name]
  (verify-updated-item coll-slug item-slug item-name 2))

(Then #"^version (\d+) of the updated item \"([^\"]*)\" in collection \"([^\"]*)\" will be named \"([^\"]*)\"$" [version item-slug coll-slug item-name]
  (verify-updated-item coll-slug item-slug item-name (read-string version)))

(Then #"^the timestamps will be matching parseable dates$" []
  (timestamp? (body/value-of :created-at))
  (timestamp? (body/value-of :updated-at))
  (body/verify-relation = :created-at :updated-at))