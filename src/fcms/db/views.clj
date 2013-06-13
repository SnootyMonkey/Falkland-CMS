;; CouchDB Views
(ns fcms.db.views
  (:require [com.ashafa.clutch :as clutch]
            [fcms.models.base :as base]))

(println "FCMS: Initializing CouchDB views.")

;; http://localhost:5984/falklandcms/_design/collection/_view/all
;; http://localhost:5984/falklandcms/_design/collection/_view/all?include_docs=true
(clutch/with-db (base/db)
  (clutch/save-view "collection"
    (clutch/view-server-fns :cljs {:all 
      {:map (fn [doc]
        (if (and (aget doc "data") (= (aget (aget doc "data") "type") "collection"))
          (js/emit (aget (aget doc "data") "slug") nil)))}})))

;; http://localhost:5984/falklandcms/_design/item/_view/all
;; http://localhost:5984/falklandcms/_design/collection/_view/all?include_docs=true
(clutch/with-db (base/db)
  (clutch/save-view "item"
    (clutch/view-server-fns :cljs {:all {:map (fn [doc]
      (if (and (aget doc "data") (= (aget (aget doc "data") "type") "item"))
        (js/emit (aget (aget doc "data") "slug") nil)))}})))

;; http://localhost:5984/falklandcms/_design/taxonomy/_view/all
;; http://localhost:5984/falklandcms/_design/collection/_view/all?include_docs=true
(clutch/with-db (base/db)
  (clutch/save-view "taxonomy"
    (clutch/view-server-fns :cljs {:all {:map (fn [doc]
      (if (and (aget doc "data") (= (aget (aget doc "data") "type") "taxonomy"))
        (js/emit (aget (aget doc "data") "slug") nil)))}})))

(println "FCMS: CouchDB view initialization complete.")

;; TODO want something more like below, so with less repition, but type needs to be a string literal by the time it makes it into the generated JS
;; at a minimum, need a macro that takes care of the with-db save-view view-server-fns boilerplate.

; (defn- type-of [doc type]
;   (and (aget doc "data") (= (aget (aget doc "data") "type") type)))

; (defn all [type]
;   (clutch/with-db (base/db)
;     (clutch/save-view type-name
;       (clutch/view-server-fns :cljs {:all {:map (fn [doc]
;         (if (type-of doc type)
;           (js/emit (str (aget (aget doc "data") "slug") " | " (aget doc "_id") " | " (aget doc "_rev")) nil))}}))))

; (println "FCMS: Initializing CouchDB views.")
; (doseq [type ["collection", "item", "taxonomy"]] (all type))
; (println "FCMS: CouchDB view initialization complete.")