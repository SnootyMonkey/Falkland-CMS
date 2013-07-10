(require '[fcms.resources.collection :as collection])

(Given #"^I have a collection \"([^\"]*)\" with no items$" [coll-slug]
  (collection/delete-collection coll-slug)
  (collection/create-collection coll-slug))

(Then #"^the collection \"([^\"]*)\" has an item count of \"([^\"]*)\"$" [coll-slug count]
  (assert (= (read-string count) (collection/item-count coll-slug))))