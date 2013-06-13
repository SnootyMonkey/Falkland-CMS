(ns fcms.models.base
  (:require [clojure.string :as str]
            [com.ashafa.clutch :as clutch])
  (:import (java.net URI)))

(def db-resource (assoc (cemerick.url/url "http://localhost:5984/" "falklandcms")
                    :username nil
                    :password nil))

(defn db []
  (clutch/get-database db-resource))

(defn all-meta []
  (clutch/with-db (db)
    (clutch/all-documents)))

(defn all []
  (clutch/with-db (db)
    (clutch/all-documents {:include_docs true})))

;; Slugify
;; TODO
;; Leave 0-9 and -
;; replace A-Z with a-z
;; replace non-alpha-numeric with -
;; replace -- with -
;; replace - at the end with nothing
;; if not unique, add -1 to the end and increment 1 until it is unique for that type
(defn slugify [doc-name type-name]
  doc-name)

;; TODO set timestamps
;; TODO base version
(defn create [{doc-name :name provided-slug :slug :as props} provided-type]
  (clutch/with-db (db)
    (let [type-name (name provided-type)]
      (let [slug (or provided-slug (slugify doc-name type-name))]
        (clutch/put-document {:_id (str type-name "|" slug) 
          :data (merge props {:slug slug :type type-name})})))))

(defn retrieve [id]
  (clutch/with-db (db)
    (clutch/get-document id)))

(defn delete [item]
  (clutch/with-db (db)
    (clutch/delete-document item)))

(defn delete-by-id [id]
  (let [item (retrieve id)]
    (if-not (nil? item)
      (delete item))))