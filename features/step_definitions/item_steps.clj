(require '[fcms.models.collection :as collection]
         '[fcms.models.item :as item])

;; Move this to collection or item as a helper function of the model
(defn create-items [collection items]
  (when-not (empty? items)
    (item/create-item collection (first items))
    (recur collection (rest items))))

(Given #"^the system knows about the following items in collection \"([^\"]*)\"$" [coll-name table]
  (let [items (table->rows table)]
    (collection/create-collection coll-name)
    (create-items (str "/" coll-name) items)))