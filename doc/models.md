# Falkland CMS Data Models

## Models

*base* -

*item* -

*taxonomy* -

## From the REPL

Start the REPL with:

	lein repl

Include the models that you are going to use:

	(require '[fcms.models.base :as base])
	(require '[fcms.models.item :as item])

	-or, if you are doing devolpment on the code, use the reload flag-

	(require '[fcms.models.base :as base] :reload-all)
	(require '[fcms.models.item :as item] :reload-all)

List everything in the CMS (don't do this on a very full CMS!):

	(base/all)

Create an item:

	(item/create-item {:taxonomy ["media-type:web page" "topic:company"] :name "Apple" :creator ["Steve Jobs", ""], :url ["http://apple.com"]})

List all the items the CMS (don't do this on a very full CMS!):

	(item/all)

List all items of a particular taxonomy:

	(item/all ["media-type:web page"])

	(item/all ["media-type:video" "topic:mudskippers"])

Retrieve an item:

	(base/retrieve "ec5d4c2028bdcd46d95affe6db038172")

Delete an item:

	(base/delete (base/retrieve "ec5d4c2028bdcd46d95affe6db038172"))

	(base/delete-by-id "ec5d4c2028bdcd46d95affe6db038172")