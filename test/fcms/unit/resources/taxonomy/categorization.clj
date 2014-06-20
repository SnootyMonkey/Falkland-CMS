(ns fcms.unit.resources.taxonomy.categorization
  (:require [midje.sweet :refer :all]
            [fcms.lib.resources :refer :all]
            [fcms.resources.collection :as collection]
            [fcms.resources.taxonomy :as taxonomy]
						[fcms.resources.item :as item]))

;; ----- Fixtures -----

(def x "x")
(def m "m")

(defn existing-item-x []
  (item/create-item e x)
  (doseq [category ["t/foo" "t/bar" "t/fubar/a" "t/fubar/b"]]
  	(taxonomy/categorize-item e x category)))

(defn existing-item-m []
  (item/create-item e m)
  (doseq [category ["t/foo" "t/fubar/a" "food/fruit" "food/vegetable/carrot"]]
	  (taxonomy/categorize-item e m category)))

;; ----- Tests -----

(with-state-changes [(before :facts (do (reset-collection e) (existing-taxonomy-t)))
										 (after :facts (collection/delete-collection e))]

	(facts "about item categorization failures"

  	(with-state-changes [(before :facts (item/create-item e i))]

			(fact "with a non-existent collection"
				(doseq [coll-slug (conj bad-strings "not-here")]
					(taxonomy/categorize-item coll-slug i "/t/foo") => :bad-collection))

	  	(fact "with a non-existent taxonomy"
	  		(doseq [category-path (concat bad-strings
	  													["not-here/foo" "not-here/foo/"
	  													 "/not-here/foo" "/not-here/foo/"])]
	  			(taxonomy/categorize-item e i category-path) => :bad-taxonomy))

	  	(fact "with a non-existent item"
	  		(doseq [item-slug (conj bad-strings "not-here")]
	  			(taxonomy/categorize-item e item-slug "/t/foo") => :bad-item))

	  	(fact "with no category"
	  		(doseq [category-path ["t" "t/" "/t" "/t/"]]
	  			(taxonomy/categorize-item e i category-path) => :bad-category))

	  	(fact "with a non-existent root category"
	  		(doseq [category-path ["t/not-here" "t/not-here/" "/t/not-here" "/t/not-here/"]]
	  			(taxonomy/categorize-item e i category-path) => :bad-category))

	  	(fact "with a non-existent leaf category"
	  		(doseq [category-path ["t/foo/not-here" "t/foo/not-here/" "/t/foo/not-here" "/t/foo/not-here/"]]
	  			(taxonomy/categorize-item e i category-path) => :bad-category)
	  		(doseq [category-path ["t/fubar/a/not-here" "t/fubar/a/not-here/" "/t/fubar/a/not-here" "/t/fubar/a/not-here/"]]
	  			(taxonomy/categorize-item e i category-path) => :bad-category)))

  	(with-state-changes [(before :facts (existing-item-x))]

	  	(fact "with a duplicate root category"
	  		(doseq [category-path ["t/foo" "t/foo/" "/t/foo" "/t/foo/"]]
  				(taxonomy/categorize-item e x category-path) => :duplicate-category))

	  	(fact "with a duplicate parent category"
	  		(doseq [category-path ["t/fubar" "t/fubar/" "/t/fubar" "/t/fubar/"]]
  				(taxonomy/categorize-item e x category-path) => :duplicate-category))

	  	(fact "with a duplicate leaf category"
	  		(doseq [category-path ["t/fubar/a" "t/fubar/a/" "/t/fubar/a" "/t/fubar/a/"]]
  				(taxonomy/categorize-item e x category-path) => :duplicate-category))))

	(facts "about item categorization sucesses"

		(fact "creating a new version"
			(item/create-item e i)
			(:version (item/get-item e i)) => 1
			(taxonomy/categorize-item e i "t/foo")
			(:version (item/get-item e i)) => 2)

		(fact "with a single root category"
			(doseq [category-path ["t/bar" "t/bar/" "/t/bar" "/t/bar/"]]
				(item/create-item e i)
				(:categories (taxonomy/categorize-item e i category-path)) => ["t/bar"]
				(:categories (item/get-item e i)) => ["t/bar"]
				(item/delete-item e i)))

  	(fact "with multiple root categories"
			(doseq [category-paths [["t/bar" "t/foo"]
															["t/bar/" "t/foo/"]
															["/t/bar" "/t/foo"]
															["/t/bar/" "/t/foo/"]]]
				(item/create-item e i)
				(doseq [category-path category-paths]
					(taxonomy/categorize-item e i category-path))
				(:categories (item/get-item e i)) => ["t/bar" "t/foo"]
				(item/delete-item e i)))

		(fact "with a single leaf category"
			(doseq [category-path ["t/fubar/a" "t/fubar/a/" "/t/fubar/a" "/t/fubar/a/"]]
				(item/create-item e i)
				(:categories (taxonomy/categorize-item e i category-path)) => ["t/fubar/a"]
				(:categories (item/get-item e i)) => ["t/fubar/a"]
				(item/delete-item e i)))

  	(fact "with multiple leaf categories"
			(doseq [category-paths [["t/fubar/a" "t/fubar/b"]
															["t/fubar/a/" "t/fubar/b/"]
															["/t/fubar/a" "/t/fubar/b"]
															["/t/fubar/a/" "/t/fubar/b/"]]]
				(item/create-item e i)
				(doseq [category-path category-paths]
					(taxonomy/categorize-item e i category-path))
				(:categories (item/get-item e i)) => ["t/fubar/a" "t/fubar/b"]
				(item/delete-item e i)))

  	(fact "with a single root category and a single leaf category"
			(doseq [category-paths [["t/bar" "t/fubar/b"]
															["t/bar/" "t/fubar/b/"]
															["/t/bar" "/t/fubar/b"]
															["/t/bar/" "/t/fubar/b/"]]]
				(item/create-item e i)
				(doseq [category-path category-paths]
					(taxonomy/categorize-item e i category-path))
				(:categories (item/get-item e i)) => ["t/bar" "t/fubar/b"]
				(item/delete-item e i)))

  	(with-state-changes [(before :facts (existing-taxonomy-t2))]

	  	(fact "with a single root category and a single leaf category in multiple taxonomies"
				(doseq [category-paths [["t/bar" "t/fubar/b" "food/fruit" "food/vegetable/carrot"]
																["t/bar/" "t/fubar/b/" "food/fruit/" "food/vegetable/carrot/"]
																["/t/bar" "/t/fubar/b" "/food/fruit" "/food/vegetable/carrot"]
																["/t/bar/" "/t/fubar/b/" "/food/fruit/" "/food/vegetable/carrot/"]]]
					(item/create-item e i)
					(doseq [category-path category-paths]
						(taxonomy/categorize-item e i category-path))
					(:categories (item/get-item e i)) => ["t/bar" "t/fubar/b" "food/fruit" "food/vegetable/carrot"]
					(item/delete-item e i)))))

	(facts "about item uncategorization failures"

  	(with-state-changes [(before :facts (existing-item-x))]

			(fact "with a non-existent collection"
				(doseq [coll-slug (conj bad-strings "not-here")]
					(taxonomy/uncategorize-item coll-slug x "/t/foo") => :bad-collection))

	  	(fact "with a non-existent taxonomy"
	  		(doseq [category-path (concat bad-strings
	  													 ["not-here/foo" "not-here/foo/"
	  													  "/not-here/foo" "/not-here/foo/"])]
	  			(taxonomy/uncategorize-item e x category-path) => :bad-taxonomy))

	  	(fact "with a non-existent item"
	  		(taxonomy/uncategorize-item e nil "/t/foo") => :bad-item
	  		(taxonomy/uncategorize-item e "" "/t/foo") => :bad-item
	  		(taxonomy/uncategorize-item e "not-here" "/t/foo") => :bad-item)

	  	(fact "with no category"
	  		(doseq [category-path ["t" "t/" "/t" "/t/"]]
	  			(taxonomy/uncategorize-item e x category-path) => :bad-category))

	  	(fact "with a non-existent root category"
	  		(doseq [category-path ["t/not-here" "t/not-here/" "/t/not-here" "/t/not-here/"]]
	  			(taxonomy/uncategorize-item e x category-path) => :bad-category))

	  	(fact "with a non-existent leaf category"
	  		(doseq [category-path ["t/foo/not-here" "t/foo/not-here/" "/t/foo/not-here" "/t/foo/not-here/"]]
	  			(taxonomy/uncategorize-item e x category-path) => :bad-category)
	  		(doseq [category-path ["t/fubar/a/not-here" "t/fubar/a/not-here/" "/t/fubar/a/not-here" "/t/fubar/a/not-here/"]]
	  			(taxonomy/uncategorize-item e x category-path) => :bad-category))

	    (fact "with a partial category path"
	      (doseq [category-path ["t/fubar" "t/fubar/" "/t/fubar" "/t/fubar/"]]
	  			(taxonomy/uncategorize-item e x category-path) => :bad-category))))

  (facts "about item uncategorization successes"

		(fact "creating a new version"
    	(existing-item-x)
			(:version (item/get-item e x)) => 5
			(taxonomy/uncategorize-item e x "t/foo")
			(:version (item/get-item e x)) => 6)

    (fact "with a single root category"
    	(doseq [category-path ["t/foo" "t/foo/" "/t/foo" "/t/foo/"]]
    		(existing-item-x)
				(:categories (taxonomy/uncategorize-item e x category-path)) => ["t/bar" "t/fubar/a" "t/fubar/b"]
				(:categories (item/get-item e x)) => ["t/bar" "t/fubar/a" "t/fubar/b"]
				(item/delete-item e x)))

    (fact "with multiple root categories"
    	(doseq [category-paths [["t/foo" "t/bar"]
    													["t/foo/" "t/bar/"]
    													["/t/foo" "/t/bar"]
    													["/t/foo/" "/t/bar/"]]]
    		(existing-item-x)
    		(doseq [category-path category-paths]
					(taxonomy/uncategorize-item e x category-path))
				(:categories (item/get-item e x)) => ["t/fubar/a" "t/fubar/b"]
				(item/delete-item e x)))

    (fact "with a single leaf category"
    	(doseq [category-path ["t/fubar/a" "t/fubar/a/" "/t/fubar/a" "/t/fubar/a/"]]
    		(existing-item-x)
				(:categories (taxonomy/uncategorize-item e x category-path)) => ["t/foo" "t/bar" "t/fubar/b"]
				(:categories (item/get-item e x)) => ["t/foo" "t/bar" "t/fubar/b"]
				(item/delete-item e x)))

    (fact "with multiple leaf categories"
    	(doseq [category-paths [["t/fubar/a" "t/fubar/b"]
    													["t/fubar/a/" "t/fubar/b/"]
    													["/t/fubar/a" "/t/fubar/b"]
    													["/t/fubar/a/" "/t/fubar/b/"]]]
    		(existing-item-x)
    		(doseq [category-path category-paths]
					(taxonomy/uncategorize-item e x category-path))
				(:categories (item/get-item e x)) => ["t/foo" "t/bar"]
				(item/delete-item e x)))

    (fact "with a single root category and a single leaf category"
    	(doseq [category-paths [["t/foo" "t/fubar/a"]
    													["t/foo/" "t/fubar/a/"]
    													["/t/foo" "/t/fubar/a"]
    													["/t/foo/" "/t/fubar/a/"]]]
    		(existing-item-x)
    		(doseq [category-path category-paths]
					(taxonomy/uncategorize-item e x category-path))
				(:categories (item/get-item e x)) => ["t/bar" "t/fubar/b"]
				(item/delete-item e x)))

    (fact "with a single root category and a single leaf category in multiple taxonomies"
      (existing-taxonomy-t2)
      (existing-item-m)
      (:categories (taxonomy/uncategorize-item e m "t/foo")) => ["t/fubar/a" "food/fruit" "food/vegetable/carrot"]
      (:categories (item/get-item e m)) => ["t/fubar/a" "food/fruit" "food/vegetable/carrot"]
			(:categories (taxonomy/uncategorize-item e m "t/fubar/a/")) => ["food/fruit" "food/vegetable/carrot"]
      (:categories (item/get-item e m)) => ["food/fruit" "food/vegetable/carrot"]
      (:categories (taxonomy/uncategorize-item e m "/food/fruit")) => ["food/vegetable/carrot"]
      (:categories (item/get-item e m)) => ["food/vegetable/carrot"]
      (:categories (taxonomy/uncategorize-item e m "/food/vegetable/carrot/")) => []
      (:categories (item/get-item e m)) => [])))
