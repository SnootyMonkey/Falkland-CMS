(ns fcms.views.common)

(def GET "get")
(def POST "post")
(def PUT "put")
(def DELETE "delete")
(def PATCH "delete")

(def ordered-keys [:name, :created-at, :updated-at, :slug, :description])

(defn link-map [rel method url media-type]
  (array-map :rel rel :method method :href url :type media-type))

(defn self-link [url media-type]
  (link-map "self" GET url media-type))

(defn create-link [url media-type]
  (link-map "create" POST url media-type))

(defn update-link [url media-type]
  (link-map "update" PUT url media-type))

(defn delete-link [url]
  (array-map :rel "delete" :method DELETE :href url))

(defn ordered
  "Make an ordered array-map of the ordered key-values for the property map"
  [prop-map]
  ;; Iterate over the ordered keys collecting the value from the map for each key (or a blank string),
  ;; put these into a vector with the key name, and create an array-map out of it
  (apply array-map (flatten (map #(vec [% (or (prop-map %) "")]) ordered-keys))))

(defn append-sorted
  "Given the an array-map of the ordered keys and a map of the remaining keys, return an array-map
  that has the remaining keys sorted and appended to the ordered keys"
  [prop-map, remaining-map]
  ;; sort the remaining key-value pairs into a sorted map
  (let [sorted-remaining (apply sorted-map (flatten (into [] remaining-map)))]
    ;; create a new ordered array map from our existing ordered map and the newly sorted map
    (apply array-map (concat (flatten (into [] prop-map)) (flatten (into [] sorted-remaining))))))

(defn remaining-keys
  "Remove all the ordered keys leaving just the remaining keys"
  [prop-map]
  (apply dissoc prop-map ordered-keys))