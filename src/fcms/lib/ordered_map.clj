(ns fcms.lib.ordered-map
  (:require [flatland.ordered.map :refer (ordered-map)]))

(defn zip-ordered-map
  "Returns an ordered map with the keys mapped to the corresponding vals. Adopted from clojure.core's zipmap"
  [keys vals]
    (loop [map (ordered-map)
           ks (seq keys)
           vs (seq vals)]
      (if (and ks vs)
        (recur (assoc map (first ks) (first vs))
               (next ks)
               (next vs))
        map)))