# Falkland CMS Data Models

## Models

*base* - a common set of functions common to everything that's stored in FCMS. You won't often use functions of base directly.

*item* - any resource that's been collected in your repository, often organized into many orthogonal taxonomies.

*taxonomy* - a means of organizing items into different categories.

## From the REPL

Start the REPL with:

	lein repl

Require the models that you are going to use:

	(require '[fcms.models.base :as base])
	(require '[fcms.models.item :as item])
	(require '[fcms.models.category :as category])

Or, if you are doing devolpment on the code, require them using the reload flag:

	(require '[fcms.models.base :as base] :reload-all)
	(require '[fcms.models.item :as item] :reload-all)
	(require '[fcms.models.item :as category] :reload-all)

List everything in the CMS (don't do this on a very full CMS!):

	(base/all)

Create a category:

	(item/create-category {:name "media-type:wikipedia page" :parent "media-type:web page"})

Create an item:

  (item/create-item {:taxonomy ["media-type:video" "topic:fish"] :name "Amazing animals - Mudskipper" :creator "BBC Life episode", :url "https://www.youtube.com/watch?v=KurTiX4FDuQ"})

	(item/create-item {:taxonomy ["media-type:web page" "topic:company"] :name "Apple" :creator ["Steve Jobs", "Steve Wozniak"], :url ["http://apple.com", "http://en.wikipedia.org/wiki/Apple_Inc."]})
  
List all the items the CMS (don't do this on a very full CMS!):

	(item/all)

List all items of a particular taxonomy:

	(item/all [:taxonomy "media-type:web page"])

List all items of an intersection of taxonomies:

	(item/all [:taxonomy ("media-type:video" "topic:mudskippers")])

Retrieve an item by its ID:

	(base/retrieve "ec5d4c2028bdcd46d95affe6db038172")

Delete an item:

	(base/delete (base/retrieve "ec5d4c2028bdcd46d95affe6db038172"))

	(base/delete-by-id "ec5d4c2028bdcd46d95affe6db038172")