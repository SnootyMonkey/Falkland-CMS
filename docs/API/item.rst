:tocdepth: 2
Items
#####

An `item <http://www.wordnik.com/words/item>`_ is a single entry in a collection. An item
is not itself THE member of the collection, but is a pointer TO the member of the collection.
It's not THE article on Faulkner's early history with bedwetting and its effect on his later
writing career, it's a pointer TO the article.

With Falkland CMS, you capture items in a collection and categorize them into multiple orthogonal
taxonomies. Items can point to literally anything in the world: books, articles, web pages,
videos, art, artifacts, quotes, collectables, et cetera. An item is simply anything in a
collection that is big enough to warrant spending the time to organize the collection.
Once created, items can be browsed and searched, and can be displayed in exhibits.

Some examples of items:

* a VIC-20 cartridge for vic20.com
* a secondary source about *The Fall* for Camus.org
* a Youtube video about mudskippers for Mudskippers.org
* a book, *The Fall*, in Jack Freeman's Library
* a wikipedia entry on amoralism for nil.org
* an Atari 7800 game for 8-bit.com
* a John McKay quote for pitifulpirates.com

List Items in a Collection
--------------------------

Get all the items in a collection.

Request
~~~~~~~

.. code-block:: http

   GET /:collection-slug/?num=50&start=200

.. warning::

   The trailing slash after the collection slug is important and must be included in the request.

Parameters
^^^^^^^^^^

 - **collection-slug**: the slug of the collection
 - **num**: how many items to return, optional, defaults to 50
 - **start**: 0-based index of the 1st item to return, optional, defaults to 0

Headers
^^^^^^^

- **Accept**: application/vnd.fcms.item+json;version=1
- **Accept-Charset**: utf-8

Example
^^^^^^^

.. code-block:: bash

   curl -i --header "Accept: application/vnd.fcms.item+json;version=1" --header "Accept-Charset: utf-8" -X GET http://{host:port}/mudskippers/

   curl -i --header "Accept: application/vnd.fcms.item+json;version=1" --header "Accept-Charset: utf-8" -X GET http://{host:port}/mudskippers/?num=100

   curl -i --header "Accept: application/vnd.fcms.item+json;version=1" --header "Accept-Charset: utf-8" -X GET http://{host:port}/mudskippers/?num=10&start=10

Response
~~~~~~~~

The response has a JSON array called items which contains each item in the collection. The response also contains a link for creating new items in the collection and pagination links.

Response Status
^^^^^^^^^^^^^^^

- **200**: OK
- **404**: collection was not found

Example
^^^^^^^

.. code-block:: json

   {
      "items":[
         {
            "name":"Amazing animals - Mudskipper",
            "URL":"http://www.youtube.com/watch?v=mJhUKzEq47U",
            "created_at":"2013-04-23T14:30:50Z",
            "updated_at":"2013-04-23T14:30:50Z",
            "slug":"amazing-animals-mudskipper",
            "collection":"mudskippers",
            "description":"Excerpt from David Attenborough's BBC Life series episode 04",
            "links":[
               {
                  "rel":"self",
                  "method":"get",
                  "href":"/mudskippers/amazing-animals-mudskipper",
                  "type":"application/vnd.fcms.item+json;version=1"
               }
               {
                  "rel":"update",
                  "method":"put",
                  "href":"/mudskippers/amazing-animals-mudskipper",
                  "type":"application/vnd.fcms.item+json;version=1"
               },
               {
                  "rel":"delete",
                  "method":"delete",
                  "href":"/mudskippers/amazing-animals-mudskipper",
               },
               {
                  "rel":"category",
                  "method":"get",
                  "href":"/mudskippers/media-types/videos/online",
                  "type":"application/vnd.fcms.category+json;version=1"
               },
               {
                  "rel":"category",
                  "method":"get",
                  "href":"/mudskippers/topics/reproduction",
                  "type":"application/vnd.fcms.category+json;version=1"
               },
               {
                  "rev":"collection",
                  "method":"get",
                  "href":"/mudskippers",
                  "type":"application/vnd.fcms.collection+json;version=1"
               }
            ]
         },
         {
            "name":"Mudskipper's Habitat",
            "URL": "http://animal.discovery.com/tv-shows/animal-planet-presents/videos/whats-to-love-mudskippers-habitat.htm",
            "created_at":"2011-04-23T14:32:17Z",
            "updated_at":"2011-04-23T14:32:17Z",
            "slug":"mudskippers-habitat",
            "collection":"mudskippers",
            "description":"Animal Planet Presents Mudskipper's Habitat",
            "links":[
               {
                  "rel":"self",
                  "method":"get",
                  "href":"/mudskippers/mudskippers-habitat"
                  "type":"application/vnd.fcms.item+json;version=1"
               }
               {
                  "rel":"update",
                  "method":"put",
                  "href":"/mudskippers/mudskippers-habitat",
                  "type":"application/vnd.fcms.item+json;version=1"
               },
               {
                  "rel":"delete",
                  "method":"delete",
                  "href":"/mudskippers/mudskippers-habitat",
               },
               {
                  "rel":"category",
                  "method":"get",
                  "href":"/mudskippers/media-types/videos/online",
                  "type":"application/vnd.fcms.category+json;version=1"
               },
               {
                  "rel":"category",
                  "method":"get",
                  "href":"/mudskippers/topics/habitat",
                  "type":"application/vnd.fcms.category+json;version=1"
               },
               {
                  "rev":"collection",
                  "method":"get",
                  "href":"/mudskippers",
                  "type":"application/vnd.fcms.collection+json;version=1"
               }
            ]
         }
      ],
      "links":[
         {
            "rel":"create",
            "method":"post",
            "href":"/mudskippers",
            "type":"application/vnd.fcms.item+json;version=1"
         },
         {
            "rel":"first",
            "method":"get",
            "href":"/mudskippers?num=2",
            "type":"application/vnd.fcms.item+json;version=1"
         },
         {
            "rel":"prev",
            "method":"get",
            "href":"/mudskippers?num=2&start=2",
            "type":"application/vnd.fcms.item+json;version=1"
         },
         {
            "rel":"next",
            "method":"get",
            "href":"/mudskippers?num=2&start=4",
            "type":"application/vnd.fcms.item+json;version=1"
         }
      ]
   }


Get an Item
-----------

Get a particular item.

Request
~~~~~~~

.. code-block:: http

   GET /:collection-slug/:item-slug

Parameters
^^^^^^^^^^

- **collection-slug**: the slug of the collection
- **item-slug**: the slug of the item

Headers
^^^^^^^

- **Accept**: application/vnd.fcms.item+json;version=1
- **Accept-Charset**: utf-8

Example
^^^^^^^

.. code-block:: bash

   curl -i --header "Accept: application/vnd.fcms.item+json;version=1" --header "Accept-Charset: utf-8" -X GET http://{host:port}/mudskippers/amazing-animals-mudskipper

Response
~~~~~~~~

The response has a complete JSON representation of the item which contains links to available actions on the item, a reverse link to the collection containing the item, and links to any categories the item is a member of.

Status
^^^^^^

- **200**: OK
- **404**: collection or item was not found

Example
^^^^^^^

.. code-block:: json

   {
      "name":"Amazing animals - Mudskipper",
      "URL":"http://www.youtube.com/watch?v=mJhUKzEq47U",
      "created_at":"2013-04-23T14:30:50Z",
      "updated_at":"2013-04-23T14:30:50Z",
      "slug":"amazing-animals-mudskipper",
      "collection":"mudskippers",
      "description":"Excerpt from David Attenborough's BBC Life series episode 04",
      "links":[
         {
            "rel":"self",
            "method":"get",
            "href":"/mudskippers/amazing-animals-mudskipper",
            "type":"application/vnd.fcms.item+json;version=1"
         },
         {
            "rel":"update",
            "method":"put",
            "href":"/mudskippers/amazing-animals-mudskipper",
            "type":"application/vnd.fcms.item+json;version=1"
         },
         {
            "rel":"delete",
            "method":"delete",
            "href":"/mudskippers/amazing-animals-mudskipper",
         },
         {
            "rel":"category",
            "method":"get",
            "href":"/mudskippers/media-types/videos/online",
            "type":"application/vnd.fcms.category+json;version=1"
         },
         {
            "rel":"category",
            "method":"get",
            "href":"/mudskippers/topics/reproduction",
            "type":"application/vnd.fcms.category+json;version=1"
         },
         {
            "rev":"collection",
            "method":"get",
            "href":"/mudskippers",
            "type":"application/vnd.fcms.collection+json;version=1"
         }
      ]
   }

Create an Item
--------------

Create a new item in a collection.

Request
~~~~~~~

.. code-block:: http

   POST /:collection-slug

Pass in details for the new item as a JSON representation. The name is required and will be used to create the slug.


Parameters
^^^^^^^^^^

- **collection-slug**: the slug of the collection

Here is a minimal representation of a JSON body:

.. code-block:: json

   {
      "name":"Mudskipper",
      "URL":"http://en.wikipedia.org/wiki/Mudskipper"
   }

Here is a more complete representation of a JSON body:

.. code-block:: json

   {
      "name":"Mudskipper",
      "URL":"http://en.wikipedia.org/wiki/Mudskipper",
      "category":"/mudskippers/media-types/articles/online",
      "category":"/mudskippers/topics/general",
      "description":"Mudskipper entry from Wikipedia, the free encyclopedia"
   }

Headers
^^^^^^^

- **Accept**: application/vnd.fcms.item+json;version=1
- **Accept-Charset**: utf-8
- **Content-type**: application/vnd.fcms.item+json;version=1

Example
^^^^^^^

.. code-block:: bash

   curl -i --header "Accept: application/vnd.fcms.item+json;version=1" --header "Accept-Charset: utf-8" --header "Content-type: application/vnd.fcms.item+json;version=1" -X POST -d '{"name":"Mudskipper","URL":"http://en.wikipedia.org/wiki/Mudskipper","category":"/mudskippers/media-types/articles/online","category":"/mudskippers/topics/general","description":"Mudskipper entry from Wikipedia, the free encyclopedia"}' http://{host:port}/mudskippers/

Response
~~~~~~~~

The new item is at the location provided in the location in the header. A representation of the new item is also returned.

Status
^^^^^^

- **201**: created
- **404**: the collection is not found
- **422**: the item entity you passed in is not valid

Headers
^^^^^^^

- **Location**: the URL of the newly created item

Example
^^^^^^^

.. code-block:: json

   {
      "name":"Mudskipper",
      "URL":"http://en.wikipedia.org/wiki/Mudskipper",
      "created_at":"2013-04-23T14:30:50Z",
      "updated_at":"2013-04-23T14:30:50Z",
      "slug":"wiki-mudskipper",
      "collection":"mudskippers",
      "description":"Mudskipper entry from Wikipedia, the free encyclopedia",
      "links":[
         {
            "rel":"self",
            "method":"get",
            "href":"/mudskippers/wikipedia-mudskipper",
            "type":"application/vnd.fcms.item+json;version=1"
         },
         {
            "rel":"update",
            "method":"put",
            "href":"/mudskippers/wikipedia-mudskipper",
            "type":"application/vnd.fcms.item+json;version=1"
         },
         {
            "rel":"delete",
            "method":"delete",
            "href":"/mudskippers/wikipedia-mudskipper",
         },
         {
            "rel":"category",
            "method":"get",
            "href":"/mudskippers/media-types/articles/online",
            "type":"application/vnd.fcms.category+json;version=1"
         },
         {
            "rel":"category",
            "method":"get",
            "href":"/mudskippers/topics/general",
            "type":"application/vnd.fcms.category+json;version=1"
         },
         {
            "rev":"collection",
            "method":"get",
            "href":"/mudskippers",
            "type":"application/vnd.fcms.collection+json;version=1"
         }
      ]
   }

Update an Item
--------------

Update an existing item.

Request
~~~~~~~

.. code-block:: http

   PUT /:collection-slug/:item-slug

Parameters
^^^^^^^^^^

- **collection-slug**: the slug of the collection
- **item-slug**: the slug of the item

Pass in details for the updated item as a JSON representation.

.. code-block:: json

   {
      "name":"Mudskipper",
      "slug":"wiki-mud",
      "URL":"http://en.wikipedia.org/wiki/Mudskipper",
      "category":"/mudskippers/topics/general",
      "description":"Mudskipper entry from Wikipedia, the free encyclopedia"
   }

.. note::

   Provide a new slug in the JSON body to move an item.

Headers
^^^^^^^

- **Accept**: application/vnd.fcms.item+json;version=1
- **Accept-Charset**: utf-8
- **Content-type**: application/vnd.fcms.item+json;version=1

Example
^^^^^^^

.. code-block:: bash

   curl -i --header "Accept: application/vnd.fcms.item+json;version=1" --header "Accept-Charset: utf-8" --header "Content-type: application/vnd.fcms.item+json;version=1" -X PUT -d '{"name":"Mudskipper","slug":"wiki-mud","URL":"http://en.wikipedia.org/wiki/Mudskipper","category":"/mudskippers/topics/general","description":"Mudskipper entry from Wikipedia, the free encyclopedia"}' http://{host:port}/mudskippers/media-types/articles/online/wikipedia-mudskipper

Response
~~~~~~~~

The representation of the updated item is at the specified location, which is echoed in the location in the header. A representation of the updated item is also returned.

Status
^^^^^^

- **200**: update successful
- **404**: the collection, taxonomy or category is not found
- **422**: the item entity you passed in is not valid

Headers
^^^^^^^

- **Location**: the URL of the newly created item

Examples
^^^^^^^^

.. code-block:: json

   {
      "name":"Amazing animals - Mudskipper",
      "URL":"http://www.youtube.com/watch?v=mJhUKzEq47U",
      "created_at":"2013-04-23T14:30:50Z",
      "updated_at":"2013-04-23T14:30:50Z",
      "slug":"amazing-animals-mudskipper",
      "collection":"mudskippers",
      "description":"Excerpt from David Attenborough's BBC Life series episode 04",
      "links":[
         {
            "rel":"self",
            "method":"get",
            "href":"/mudskippers/amazing-animals-mudskipper"
            "type":"application/vnd.fcms.item+json;version=1"
         },
         {
            "rel":"update",
            "method":"put",
            "href":"/mudskippers/amazing-animals-mudskipper",
            "type":"application/vnd.fcms.item+json;version=1"
         },
         {
            "rel":"delete",
            "method":"delete",
            "href":"/mudskippers/amazing-animals-mudskipper",
         },
         {
            "rel":"category",
            "method":"get",
            "href":"/mudskippers/media-types/videos/online",
            "type":"application/vnd.fcms.category+json;version=1"
         },
         {
            "rel":"category",
            "method":"get",
            "href":"/mudskippers/topics/reproduction",
            "type":"application/vnd.fcms.category+json;version=1"
         },
         {
            "rev":"collection",
            "method":"get",
            "href":"/mudskippers",
            "type":"application/vnd.fcms.collection+json;version=1"
         }
      ]
   }

Delete an Item
--------------

Delete an existing item.

Request
~~~~~~~

.. code-block:: http

   DELETE /:collection-slug/:item-slug

Parameters
^^^^^^^^^^

- **collection-slug**: the slug of the collection
- **item-slug**: the slug of the item

Example
^^^^^^^

.. code-block:: bash

   curl -i -X DELETE http://{host:port}/mudskippers/amazing-animals-mudskipper

Response
~~~~~~~~

There is no response body, just a status.

Status
^^^^^^

- **204**: deleted
- **404**: collection or item was not found