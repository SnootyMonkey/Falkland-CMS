(ns fcms.views.collections
  (:require [clj-json.core :as json]
            [fcms.views.common :as common]))

(defn ordered 
  "Make an ordered array-map of the ordered key-values for the collection"
  [coll]
  ;; Iterate over the ordered keys collecting the value from the map for each key (or a blank string),
  ;; put these into a vector with the key name, and create an array-map out of it
  (apply array-map (flatten (map #(vec [% (or (coll %) "")]) common/ordered-keys))))

(defn sorted 
  "Given the an array-map of the ordered keys and a map of the remaining keys, return an array-map
  that has the remaining keys sorted and appended to the ordered keys"
  [ordered, remaining]
  ;; sort the remaining key-value pairs into a sorted map
  (let [sorted-remaining (apply sorted-map (flatten (into [] remaining)))]
    ;; create a new ordered array map from our existing ordered map and the newly sorted map
    (apply array-map (concat (flatten (into [] ordered)) (flatten (into [] sorted-remaining))))))

(defn remaining 
  "Remove all the ordered keys leaving just the remaining keys"
  [coll]
  (apply dissoc coll common/ordered-keys))

(defn links [coll]
  "Add the HATEAOS links to the collection"
  (apply array-map (concat (flatten (into [] coll)) [:links []])))

(defn render-collection 
  "Create a JSON representation of a collection for the REST API"
  [coll]
  ;; Generate JSON from the sorted array map that results from:
  ;; 1) removing unneeded :id key
  ;; 2) making an ordered array hash of the known ordered keys
  ;; 3) adding a sorted hash of any remaining keys
  ;; 3) adding the HATEAOS links to the array hash
  (let [coll-props (dissoc coll :id)]
    (json/generate-string (-> coll-props ordered (sorted ,,, (remaining coll-props)) links))))