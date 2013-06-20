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