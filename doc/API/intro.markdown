Resources for helping create the docs (REMOVE LATER)

* [Markdown Cheat Sheet](http://packetlife.net/media/library/16/Markdown.pdf)
* [Github Flavored Markdown](http://github.github.com/github-flavored-markdown/)
* [Full Markdown Syntax](http://daringfireball.net/projects/markdown/syntax)
* [JSON formatter](http://jsonformatter.curiousconcept.com/)
* [cURL help](http://blogs.plexibus.com/2009/01/15/rest-esting-with-curl/)


# Falkland CMS Hypermedia API

This is the documentation for using the Falkland CMS Hypermedia (REST) API.

Falkland CMS supports a Hypermedia (RESTful) API that can be used from any programming language to access all the capabilities of Falkland CMS.

The intent of this API is to be a true [REST](http://en.wikipedia.org/wiki/Representational_State_Transfer) API in the full-fledged [Roy Fielding](http://roy.gbiv.com/), [HATEOAS](http://en.wikipedia.org/wiki/HATEOAS) style. You can help with that by pointing out anything you perceive as a deviation. 


## Notes on this Documentation

The example API calls are provided in [cURL](http://curl.haxx.se/). When exploring the API feel free to use cURL, the HTTP client of your choice, or a programming language you are most comfortable in. It should be simple to convert the cURL example to your own preferred HTTP client.


## Request Headers

Provide the custom [Internet media type](http://en.wikipedia.org/wiki/Internet_media_type) (formerly MIME type) saying you want Falkland CMS [JSON](http://www.json.org/) data and the version of the API you are using in the header of your API requests.

	Accept: application/json;vnd.falklandcms.{type};version={version}  

	Example:  Accept: application/json;vnd.falklandcms.category;version=1
	
The version of the API resource you want is determined by the Accept header that you pass in the request. If no version is provided, it is assumed you want the latest version.


## Security / Authentication / Sessions

TBD.

## Resources

These resources are available in Falkland CMS:

* [Taxonomy](#taxonomy)
* [Category](#category)
* [Collection](#collection)
* [Item](#item)

NOTES ON SLUGS: 
* Collections and taxonomies can't have the same slugs
* Categories at the same level in the taxonomy can't have the same slugs
* Items and categories can't have the same slugs
* Items in the same collection can't have the same slugs
* Items get provided a slug unless one is provided

/{collection-slug}
/ (default collection if none provided)

	GET - get representation of the collection
	POST - create a new collection
	PUT - update the collection
	DELETE - delete the collection

/{collection-slug}/items
/items (default collection if none provided)

	GET - list of items in the collection 
	POST - create new item in the collection

/{collection-slug}/items/{item-slug}
/items/{item-slug} (default collection if none provided)

	GET - get representation of the item
	PUT - update the representation of the item
	DELETE - delete the item

/{collection-slug}/{taxonomy-slug}/{category-slug}
/{taxonomy-slug}/{category-slug} (default collection if none provided)
/{collection-slug}/{taxonomy-slug}/{category-slug}/{category-slug}
/{taxonomy-slug}/{category-slug}/{category-slug} (default collection if none provided)

	GET - get representation of the category
	GET - get items in the category
	PUT - update the representation of the category
	DELETE - delete the category

/{collection-slug}/{taxonomy-slug}/{category-slug}/{category-slug}/{item-slug}
/{taxonomy-slug}/{category-slug}/{category-slug}/{item-slug} (default collection if none provided)

	GET - get representation of the item
	PUT - update the representation of the item
	DELETE - delete the item

