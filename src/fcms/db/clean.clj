(ns fcms.db.clean
  "Remove all data from a Falkland CMS CouchDB database. USE WITH CAUTION!"
  (:require [com.ashafa.clutch :as clutch]
            [fcms.resources.common :as common]))

(defn- bulk-delete-record [doc]
  {:_id (:id doc) :_rev (:value doc) :_deleted true})

(defn clean []
  (println "FCMS: Cleaning database...")
  (let [doc-revs (clutch/get-view (common/db) "fcms" :all-by-rev {:include_docs false})]
    (clutch/bulk-update (common/db) (map bulk-delete-record doc-revs)))
  (println "FCMS: Database cleaning complete."))

(defn -main []
  (clean))