(ns fcms.unit.resources.item.item-delete
  (:require [midje.sweet :refer :all]
            [fcms.lib.resources :refer :all]
            [fcms.resources.collection :as collection]
            [fcms.resources.item :refer :all]))

;; ----- Tests -----

(with-state-changes [(before :facts (empty-collection-e))
                     (after :facts (collection/delete-collection e))]

  (future-facts "about item deletion failures")
  (future-facts "about deleting items"))