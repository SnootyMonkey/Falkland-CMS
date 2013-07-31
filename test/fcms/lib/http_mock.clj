(ns fcms.lib.http-mock)

(def mock-data (atom {:request {} :body "" :response {}}))

(defn request
  ([] (:request @mock-data))
  ([new-request] (swap! mock-data assoc :request new-request)))

(defn body
  ([] (:body @mock-data))
  ([new-body] (swap! mock-data assoc :body new-body)))

(defn response
  ([] (:response @mock-data))
  ([new-response] (swap! mock-data assoc :response new-response)))