(require '[fcms.resources.collection :as collection])

(Given #"^the system knows about the following items in collection \"([^\"]*)\"$" [coll-name table]
  (let [items (table->rows table)]
    (collection/create-collection coll-name)
    (collection/create-items (str "/" coll-name) items)))