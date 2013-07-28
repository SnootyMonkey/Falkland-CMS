(ns fcms.lib.body
  (:require [fcms.lib.http-mock :as http-mock]
            [fcms.lib.checks :refer (check)]))

(defn value-of [prop]
  (prop (http-mock/body)))

(defn verify [prop value]
  (check (= value (value-of prop))))

(defn verify-relation [func prop1 prop2]
  (check (func (value-of prop1) (value-of prop2))))

(defn- find-link [rel]
  (some (fn [link] (if (= rel (:rel link)) link nil)) (value-of :links)))

(defn verify-link [rel method href]
  (if-let [link (find-link rel)]
    (do
      (check (= method (:method link)))
      (check (= href (:href link))))
    (check (= rel :link_not_present))))