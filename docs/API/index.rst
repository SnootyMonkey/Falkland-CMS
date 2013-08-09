:tocdepth: 3

Falkland CMS Hypermedia API
===========================

This is the documentation for using the Falkland CMS Hypermedia (REST) API.

Falkland CMS supports a Hypermedia (RESTful) API that can be used from any programming
language to access all the capabilities of Falkland CMS.

The intent of this API is to be a true `REST <http://en.wikipedia.org/wiki/Representational_State_Transfer>`_
API in the full-fledged `Roy Fielding <http://roy.gbiv.com/>`_, `HATEOAS <http://en.wikipedia.org/wiki/HATEOAS>`_
style. You can help with that by pointing out anything you perceive as a deviation.

.. note::

	The example API calls in this documentation are provided in `cURL <http://curl.haxx.se/>`_. 
	When exploring the API feel free to use cURL, the HTTP client of your choice, or a programming
	language you are most comfortable in. It should be simple to convert the cURL example to your
	own preferred HTTP client.

Request Headers
---------------

Provide the custom `Internet media type <http://en.wikipedia.org/wiki/Internet_media_type>`_ 
(formerly MIME type) saying you want Falkland CMS `JSON <http://www.json.org/>`_ data and the
version of the API you are using in the header of your API requests.

		Accept: application/json;vnd.fcms.\ **{type}**;version=\ **version**

		Example:  Accept: application/json;vnd.fcms.category;version=1

The version of the API resource you want is determined by the Accept header that you pass
in the request. If no version is provided, it is assumed you want the latest version.

API Security
------------

Authentication
~~~~~~~~~~~~~~

TBD.

Sessions
~~~~~~~~

TBD.

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
- `Taxonomy <taxonomy.html>`_: - a hierarchical categorization of items in a collection, collections can have multiple orthogonal taxonomies

Slugs
~~~~~

Each of these resources has a slug that's used when referring to the resource in a URL.
A slug is just a unique identifier that's suitable for use in a URL. Slugs in Falkland CMS are:

- alphanumeric (no white space, unicode or special characters)
- lower case
- internally seperated by a single dash
- without a prefixed or trailing dash
- 256 characters or less

An example of a slug is: **the-amazing-amphibious-fish**

You don't need to be overly concerned about these rules as Falkland CMS always gives you the
option of having the slug generated from the resource's name.