(ns fcms.lib.resource
  (:require [clojure.test :refer :all]
  					[clj-time.format :refer (parse)]
  					[fcms.resources.collection :as collection]))

(def c "c")

(defn empty-collection-c [f]
  (collection/delete-collection c)
  (collection/create-collection c)
  (f)
  (collection/delete-collection c))

(defn verify-new-resource [coll-slug resource]
	(is (= (:collection resource) c))
  (is (= (:version resource) 1))
  (is (instance? String (:id resource)))
  (is (instance? String (:created-at resource)))
  (is (instance? org.joda.time.DateTime (parse (:created-at resource))))
  (is (= (:created-at resource) (:updated-at resource))))