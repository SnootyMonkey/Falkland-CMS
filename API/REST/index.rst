:tocdepth: 3

***************************
Falkland CMS Hypermedia API
***************************

This is the documentation for using the Falkland CMS Hypermedia (REST) API.

Falkland CMS supports a Hypermedia (RESTful) API that can be used from any programming
language to access all the capabilities of Falkland CMS.

The intent of this API is to be a true `REST <http://en.wikipedia.org/wiki/Representational_State_Transfer>`_
API in the full-fledged `Roy Fielding <http://roy.gbiv.com/>`_, `HATEOAS <http://en.wikipedia.org/wiki/HATEOAS>`_
style. You can help with that by pointing out anything you perceive as a deviation.

.. note::

	The example API calls in this documentation are provided in `cURL <http://curl.haxx.se/>`_. 
	When exploring the API feel free to use cURL, the HTTP client of your choice, or the programming
	language you're most comfortable in. It should be simple to convert the cURL example to your
	own preferred HTTP client.

Request Headers
===============

Provide the custom `Internet media type <http://en.wikipedia.org/wiki/Internet_media_type>`_ 
(formerly MIME type) saying you want Falkland CMS `JSON <http://www.json.org/>`_ data and the
version of the API you are using in the header of your API requests.

		Accept: application/vnd.fcms.\ **{type}** +json;version=\ **version**

		Example:  Accept: aapplication/vnd.fcms.item+json;version=1

The version of the API resource you want is determined by the Accept header that you pass
in the request. If no version is provided, it is assumed you want the latest version.

API Security
------------

Authentication
~~~~~~~~~~~~~~

TBD.

Sessions
~~~~~~~~

Access Control
~~~~~~~~~~~~~~

TBD.

Resources
---------

These resources are available in Falkland CMS:

.. toctree::
	:hidden:

	collection
	item
	taxonomy

-	`Collection <collection.html>`_: a group of curated items, a Falkland CMS system can have one or more collections
- `Item <item.html>`_: a single entry in a collection. An item is not itself THE member of the collection, but is a pointer TO the member of the collection.
- `Taxonomy <taxonomy.html>`_: - a hierarchical categorization of items in a collection, collections typically have multiple independent taxonomies

Slugs
~~~~~

Each of these resources has a slug that's used when referring to the resource in a URL.
A slug is just a unique identifier that's suitable for use in a URL. Slugs in Falkland CMS are:

- alphanumeric (no white space, unicode or special characters)
- lower case
- internally separated by a single dash
- without a prefixed or trailing dash
- 256 characters or less

An example of a slug is: **the-amazing-amphibious-fish**

You don't need to be overly concerned about these rules as Falkland CMS always gives you the
option of having the slug generated from the resource's name.

All items must have unique slugs that do not conflict with other items in the same collection
since they operate in the same namespace: /:collection-slug/:item-slug

Similarly all taxonomies must have unique slugs that do not conflict with other taxonomies in the
same collection since they operate in the same namespace: /:collection-slug/:taxonomy-slug

As you would expect, all collections must have unique slugs as well that do not conflict with
other collections.

Category slugs are more flexible and only need to be unique with other categories at their same
level in the same taxonomy. So the following examples where different categories share the same
slug ("online" and "physical-media") in the same taxonomy are perfectly fine:

- /mudskippers/type/video/physical-media
- /mudskippers/type/video/online
- /mudskippers/type/book/physical-media
- /mudskippers/type/book/online