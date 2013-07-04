# Falkland CMS Resources

## Resources

*common* - a common set of functions for everything that's stored in FCMS. You won't often use these functions directly.

*collection* - a grouping of items to be managed together

*item* - any resource that's been collected in your repository, often organized into many orthogonal taxonomies.

*taxonomy* - a means of organizing items into hierarchical categories.
??? needed, or just use a category with no parent?

*category* - a hierarchical organization structure in a taxonomy.

## From the REPL

Start the REPL with:

	lein repl

Require the models that you are going to use:

	(require '[fcms.resources.collection :as collection])
	(require '[fcms.resources.item :as item])
	(require '[fcms.resources.taxonomy :as taxonomy])

Or, if you are doing development on the code, require them using the reload flag:

	(require '[fcms.resources.collection :as collection] :reload-all)
	(require '[fcms.resources.item :as item] :reload-all)
	(require '[fcms.resources.taxonomy :as item] :reload-all)

Create a collection:

	(collection/create-collection "Mudskippers")

Create a basic item:

	(item/create-item "mudskippers" "Fish with Legs!")

Create a taxonomy:

	(taxonomy/create-taxonomy "media-types")

Create a category:

	(item/create-category "Wikipedia Pages" {:parent "media-types/web-pages"})

Create a more complete item:

  (item/create-item "mudskippers" {:categories ["media-types/videos" "topics/fish"] :name "Amazing animals - Mudskipper" :creator "BBC Life episode", :url "https://www.youtube.com/watch?v=KurTiX4FDuQ"})

	(item/create-item "fortune-500" {:categories ["media-types/web-pages" "topics/companies"] :name "Apple" :creator ["Steve Jobs", "Steve Wozniak"], :url ["http://apple.com", "http://en.wikipedia.org/wiki/Apple_Inc."]})
  
List all the items in a collection (don't do this on a very full collection!):

	(item/all "mudskippers")

List all items of a particular taxonomy:

	(item/all "mudskippers" {:categories ["media-types/web-pages"]})

List all items of an intersection of taxonomies:

	(item/all "mudskippers" {:categories ["media-types/videos" "topics/fish"]})

Delete an item:

	(item/delete-item "mudskippers" "amazing-animals-mudskipper")