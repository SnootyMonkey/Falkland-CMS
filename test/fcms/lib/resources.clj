(ns fcms.lib.resources
  (:require [clojure.test :refer :all]
  					[clj-time.format :refer (parse)]
            [fcms.resources.collection-resource :as resource]
  					[fcms.resources.collection :as collection]
            [fcms.resources.taxonomy :as taxonomy]
            [fcms.resources.item :as item]))

;; ----- Names / slugs -----

(def c "c")
(def e "e")
(def t "t")
(def t2 "food")
(def foo "foo")

;; ----- Collections -----

(defn empty-collection-e []
  (collection/delete-collection e)
  (collection/create-collection e))

(defn empty-collection-c [f]
  (collection/delete-collection c)
  (collection/create-collection c)
  (f)
  (collection/delete-collection c))

;; ----- Category maps -----

(def existing-categories-c [
  {:slug "foo" :name "Foo"}
  {:slug "bar" :name "Bar"}
  {:slug "fubar" :name "FUBAR" :categories [
    {:slug "a" :name "A"}
    {:slug "b" :name "B"}
  ]}])

(def existing-categories-c2 [
  {:slug "fruit" :name "Fruit" :categories [
    {:slug "apple" :name "Apple"}
    {:slug "pear" :name "Pear"}
  ]}
  {:slug "vegetable" :name "Vegetable" :categories [
    {:slug "carrot" :name "Carrot"}
    {:slug "brocolli" :name "Brocolli"}
  ]}])

;; ----- Taxonomies -----

(defn empty-collection-e []
  (collection/delete-collection e)
  (collection/create-collection e))


(defn empty-taxonomy-et []
  (taxonomy/create-taxonomy e "Empty Taxonomy" 
     {:slug "et"
      :description "Categorize it."}))

(defn existing-taxonomy-t []
  (resource/create-resource e "Taxonomy" :taxonomy [] 
    {:slug "t"
     :description "Categorize it."
     :categories existing-categories-c}))

(defn existing-taxonomy-t2 []
  (resource/create-resource e "Food" :taxonomy [] 
    {:slug t2
     :description "Yummy."
     :categories existing-categories-c2}))

;; ----- Items -----

(defn existing-item-e [f]
  (item/create-item c e)
  (taxonomy/categorize-item c e "t/foo")
  (taxonomy/categorize-item c e "t/fubar/a")
  (f)
  (item/delete-item c e))