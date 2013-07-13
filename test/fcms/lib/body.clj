(ns fcms.lib.body
  (:require [fcms.lib.http-mock :as http-mock]
            [fcms.lib.check :refer (check)]))

(defn verify [prop value]
  (check (= value (prop (http-mock/body)))))

(defn- find-link [rel]
  (some (fn [link] (if (= rel (:rel link)) link nil)) (:links (http-mock/body))))

(defn verify-link [rel method href]
  (if-let [link (find-link rel)]
    (do
      (check (= method (:method link)))
      (check (= href (:href link))))
    (check (= rel :link_not_present))))