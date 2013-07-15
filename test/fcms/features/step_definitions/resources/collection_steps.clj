(require '[fcms.resources.collection :as collection]
         '[fcms.resources.item :as item]
         '[fcms.lib.check :refer (check)])

(Given #"^I have a collection \"([^\"]*)\" with no items$" [coll-slug]
  (collection/delete-collection coll-slug)
  (collection/create-collection coll-slug))

(Given #"^I have a collection \"([^\"]*)\" with the following items$" [coll-slug table]
  (collection/delete-collection coll-slug)
  (collection/create-collection coll-slug)
  (let [items (table->rows table)]
    (doseq [item items] (item/create-item coll-slug (:name item) {:slug (:slug item)}))))

(Then #"^the collection \"([^\"]*)\" has an item count of \"([^\"]*)\"$" [coll-slug count]
  (check (= (read-string count) (collection/item-count coll-slug))))