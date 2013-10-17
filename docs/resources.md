# Falkland CMS Resources

## Resources

*common* - a common set of functions for everything that's stored in FCMS. You won't often use these functions directly.

*collection* - a grouping of items to be managed together

*collection-resource* - a common set of functions for resources contained in a collection. You won't often use these functions directly.

*item* - any resource that's been collected in your collection, often organized into many orthogonal taxonomies.

*taxonomy* - a hierarchical organization of categories, independent of any other taxonomies.

## From the REPL

Start the REPL with:

	lein repl

Require the models that you are going to use:

	(require '[fcms.resources.collection :as collection])
	(require '[fcms.resources.item :as item])
	(require '[fcms.resources.taxonomy :as taxonomy])

Or, if you are doing development on the code, require them using the reload flag:

	(require '[fcms.resources.collection :as collection] :reload)
	(require '[fcms.resources.item :as item] :reload)
	(require '[fcms.resources.taxonomy :as item] :reload)

Create a collection:

	(collection/create-collection "Mudskippers")

Create a basic item:

	(item/create-item "mudskippers" "Fish with Legs!")

Create more complete items:

  (item/create-item "mudskippers" {:name "Amazing animals - Mudskipper" :creator "BBC Life episode", :url "https://www.youtube.com/watch?v=KurTiX4FDuQ"})

	(item/create-item "fortune-500" {:name "Apple" :creator ["Steve Jobs", "Steve Wozniak"], :url ["http://apple.com", "http://en.wikipedia.org/wiki/Apple_Inc."]})

List all the items in a collection (don't do this on a very full collection!):

	(item/all-items "mudskippers")

	(item/all-items "fortune-500")

Create a taxonomy:

	(taxonomy/create-taxonomy "mudskippers" "media-types")

	(taxonomy/create-taxonomy "fortune-500" "topics" "Topics")

Create categories in a taxonomy:

	(taxonomy/create-category "mudskippers" "media-types/web-pages" "Web Pages")
	(taxonomy/create-category "mudskippers" "media-types/web-pages/wikipedia" "Wikipedia Pages")

	(taxonomy/create-category "fortune-500" "topics/companies")

Categorize items in categories:

	(taxonomy/categorize-item "mudskippers" "amazing-animals-mudskipper" "media-types/videos")
	(taxonomy/categorize-item "mudskippers" "amazing-animals-mudskipper" "topics/reproduction")

	(taxonomy/categorize-item "fortune-500" "apple" "media-types/web-pages")
	(taxonomy/categorize-item "fortune-500" "apple" "topics/companies")

List all items categorized in a particular taxonomy (don't do this on a very full taxonomy!):

	(taxonomy/items-for-taxonomy "mudskippers" "media-types")

List all items categorized in a particular category of a taxonomy:

	(taxonomy/items-for-category "mudskippers" "media-types/web-pages/wikipedia")

	(taxonomy/items-for-category "fortune-500" "topics/companies")

List all items in an intersection of categories:

	(taxonomy/items-for-categories "mudskippers" ["media-types/videos" "topics/reproduction"])

Delete an item:

	(item/delete-item "mudskippers" "amazing-animals-mudskipper")