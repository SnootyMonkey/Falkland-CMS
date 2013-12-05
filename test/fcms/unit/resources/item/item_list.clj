(ns fcms.unit.resources.item.item-list
  (:require [midje.sweet :refer :all]
            [fcms.lib.resources :refer :all]
            [fcms.resources.collection :as collection]
            [fcms.resources.item :refer :all]))

(with-state-changes [(before :facts (empty-collection-e))
                     (after :facts (collection/delete-collection e))]

  (future-facts "about item listing failures")
  (future-facts "about listing items"))