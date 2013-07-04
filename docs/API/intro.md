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

	Accept: application/json;vnd.fcms.{type};version={version}

	Example:  Accept: application/json;vnd.fcms.category;version=1

The version of the API resource you want is determined by the Accept header that you pass in the request. If no version is provided, it is assumed you want the latest version.


## Security / Authentication / Sessions

TBD.

## Resources

These resources are available in Falkland CMS:

* [Collection](./collection) - a group of curated items, a Falkland CMS system can have one or more collections
* [Taxonomy](./taxonomy) - a way of categorizing items in a collection, collections can have multiple orthogonal taxonomies
* [Category](./category) - a hierarchical unit of a taxonomy, optionally with a parent, children, and metadata definitions for the items it contains
* [Item](./item) - a single entry in a collection. An item is not itself THE member of the collection, but is a pointer TO the member of the collection.

Each of these resources has a slug that's used when referring to the resource in a URL. A slug is just a unique identifier that's suitable for use in a URL.

## API at a Glance

The following is a quick overview of the API calls that make up the Falkland CMS. The details on the representations of each resource and the HTTP actions available on each are linked above.

/

	GET - list of collections, links to resources

/collection-slug
Example: /mudskippers

	GET (accept collection type) - get representation of the collection
	GET (accept item type) - list of item resources in the collection
	PUT - create or update the collection resource
	POST - create a new item resource in the collection
	DELETE - delete the collection

/collection-slug/taxonomy-slug
Example: /mudskippers/media-type

	GET - get representation of the taxonomy
	PUT - create or update the representation of the taxonomy
	DELETE - delete the taxonomy

/collection-slug/taxonomy-slug/category-slug
Example: /mudskippers/media-type/video
/collection-slug/taxonomy-slug/category-slug/category-slug
Example: /mudskippers/media-type/video/online

	GET (accept category type) - get representation of the category
	GET (accept item type) - list of item resources in the category
	PUT - create or update the representation of the category
	DELETE - delete the category

/collection-slug/item-slug
Example: /mudskippers/amazing-animals-mudskipper
/collection-slug/taxonomy-slug/category-slug/category-slug/item-slug
Example: /mudskippers/media-type/video/amazing-animals-mudskipper

	GET - get representation of the item
	PUT - create or update the representation of the item
	DELETE - delete the item

/collection-slug/?
/mudskippers/?creator=BBC
/collection-slug/taxonomy-slug/category-slug/category-slug/?
/mudskippers/media-type/video/online?creator=BBC

	GET - query in the scope