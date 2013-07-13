(ns fcms.lib.http-mock)

(def req) ; mock HTTP request
(def bod) ; body of the mock HTTP request or response
(def resp) ; mock HTTP response

(defn request
  ([] req)
  ([new-req] (def req new-req)))

(defn body
  ([] bod)
  ([new-body] (def bod new-body)))

(defn response
  ([] resp)
  ([new-resp] (def resp new-resp)))
