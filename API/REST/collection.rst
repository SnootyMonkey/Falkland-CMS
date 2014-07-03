:tocdepth: 2

***********
Collections
***********

A `collection <http://www.wordnik.com/words/collection>`_ is a logical grouping of items to be
managed together. A collection also has zero or more taxonomies that define hierarchical
organizational structures for the items in the collection.

Some examples of collection:

- Secondary sources about Albert Camus' works for Camus.org
- Everything there is on the Internet about mudskippers for Mudskippers.org
- All the books in Jack Freeman's Library
- Everything published about amoralism for nil.org
- All Atari 7800 games for 8-bit.com
- Collected Buccaneer quotes for pitifulpirates.com

List Collections
================

List all the collections in the system.

Request
-------

.. code-block:: http

   GET /


Headers
~~~~~~~

- **Accept**: application/vnd.fcms.collection+json;version=1
- **Accept-Charset**: utf-8

Example
~~~~~~~

.. code-block:: bash

   curl -i --header "Accept: application/vnd.fcms.collection+json;version=1" --header "Accept-Charset: utf-8" -X GET http://{host:port}/

Response
--------

The response has a JSON array called collections which contains each collection in the system.
The response also contains a link for creating new collections.

Status
~~~~~~

- **200**: OK

Example
~~~~~~~

.. code-block:: json

   {
      "collections":[
         {
            "name":"Mudskippers",
            "created_at":"2013-04-23T14:30:50Z",
            "updated_at":"2013-04-23T14:30:50Z",
            "links":[
               {
                  "rel":"self",
                  "method":"get",
                  "href":"/mudskippers",
                  "type":"application/vnd.fcms.collection+json;version=1"
               },
               {
                  "rel":"contains",
                  "method":"get",
                  "href":"/mudskippers/",
                  "type":"application/vnd.fcms.item+json;version=1"
               },
               {
                  "rel":"create",
                  "method":"post",
                  "href":"/mudskippers/",
                  "type":"application/vnd.fcms.item+json;version=1"
               },
               {
                  "rel":"update",
                  "method":"put",
                  "href":"/mudskippers",
                  "type":"application/vnd.fcms.collection+json;version=1"
               },
               {
                  "rel":"delete",
                  "method":"delete",
                  "href":"/mudskippers",
               },
               {
                  "rel":"taxonomy",
                  "method":"get",
                  "href":"/mudskippers/media-types",
                  "type":"application/vnd.fcms.taxonomy+json;version=1"
               },
               {
                  "rel":"taxonomy",
                  "method":"get",
                  "href":"/mudskippers/topics",
                  "type":"application/vnd.fcms.taxonomy+json;version=1"
               }
            ]
         },
         {
            "name":"Secondary Sources on Albert Camus",
            "created_at":"2011-04-23T14:32:17Z",
            "updated_at":"2011-04-23T14:32:17Z",
            "links":[
               {
                  "rel":"self",
                  "method":"get",
                  "href":"/camus",
                  "type":"application/vnd.fcms.collection+json;version=1"
               },
               {
                  "rel":"contains",
                  "method":"get",
                  "href":"/camus/",
                  "type":"application/vnd.fcms.item+json;version=1"
               },
               {
                  "rel":"create",
                  "method":"post",
                  "href":"/camus/",
                  "type":"application/vnd.fcms.item+json;version=1"
               },
               {
                  "rel":"update",
                  "method":"put",
                  "href":"/camus",
                  "type":"application/vnd.fcms.collection+json;version=1"
               },
               {
                  "rel":"delete",
                  "method":"delete",
                  "href":"/camus",
               },
               {
                  "rel":"taxonomy",
                  "method":"get",
                  "href":"/camus/media-types",
                  "type":"application/vnd.fcms.taxonomy+json;version=1"
               },
               {
                  "rel":"taxonomy",
                  "method":"get",
                  "href":"/camus/issues",
                  "type":"application/vnd.fcms.taxonomy+json;version=1"
               },
               {
                  "rel":"taxonomy",
                  "method":"get",
                  "href":"/camus/geography",
                  "type":"application/vnd.fcms.taxonomy+json;version=1"
               }
            ]
         }
      ],
      "links":[
         {
            "rel":"create",
            "method":"post",
            "href":"/",
            "type":"application/vnd.fcms.collection+json;version=1"
         }
      ]
   }

Get a Collection
================

Get a particular collection. 

Request
-------

.. code-block:: http

   GET         /:collection-slug

.. warning::

   The lack of a trailing slash after the slug is important.

Headers
~~~~~~~

- **Accept**: application/vnd.fcms.collection+json;version=1
- **Accept-Charset**: utf-8

Example
~~~~~~~

.. code-block:: bash

   curl -i --header "Accept: application/vnd.fcms.collection+json;version=1" --header "Accept-Charset: utf-8" -X GET http://{host:port}/mudskippers

Response
--------

The response has a complete JSON representation of the collection which contains links to available actions on the collection, and links to any taxonomies associated with the collection.

Status
~~~~~~

- **200**: OK
- **404**: the collection was not found

Example
~~~~~~~

.. code-block:: json

   {
      "name":"Mudskippers",
      "created_at":"2013-04-23T14:30:50Z",
      "updated_at":"2013-04-23T14:30:50Z",
      "slug":"mudskippers",
      "description":"The Internet's best resources on the Mudskipper",
      "links":[
         {
            "rel":"self",
            "method":"get",
            "href":"/mudskippers",
            "type":"application/vnd.fcms.collection+json;version=1"
         },
         {
            "rel":"contains",
            "method":"get",
            "href":"/mudskippers/",
            "type":"application/vnd.fcms.item+json;version=1"
         },
         {
            "rel":"create",
            "method":"post",
            "href":"/mudskippers/",
            "type":"application/vnd.fcms.item+json;version=1"
         },
         {
            "rel":"update",
            "method":"put",
            "href":"/mudskippers",
            "type":"application/vnd.fcms.collection+json;version=1"
         },
         {
            "rel":"delete",
            "method":"delete",
            "href":"/mudskippers",
         },
         {
            "rel":"taxonomy",
            "method":"get",
            "href":"/mudskippers/media-types",
            "type":"application/vnd.fcms.taxonomy+json;version=1"
         },
         {
            "rel":"taxonomy",
            "method":"get",
            "href":"/mudskippers/topics",
            "type":"application/vnd.fcms.taxonomy+json;version=1"
         }
      ]
   }

Create a Collection
===================

Create a new collection in the system.

Request
-------

.. code-block:: http

   POST /

Parameters
~~~~~~~~~~

Pass in details for the new collection as a JSON representation. The name is required and will
be used to create the slug if no slug is provided.

Here is a minimal representation of a JSON body:

.. code-block:: json

   {
      "name":"Mudskippers"
   }

Here is a more complete representation of a JSON body:

.. code-block:: json

   {
      "name":"Mudskippers",
      "taxonomy":"/mudskippers/media-types",
      "taxonomy":"/mudskippers/topics",
      "description":"The Internet's best resources on the Mudskipper"
   }

Headers
~~~~~~~

- **Accept**: application/vnd.fcms.collection+json;version=1
- **Accept-Charset**: utf-8
- **Content-type**: application/vnd.fcms.collection+json;version=1

Example
~~~~~~~

.. code-block:: bash

   curl -i --header "Accept: application/vnd.fcms.collection+json;version=1" --header "Accept-Charset: utf-8" --header "Content-type: application/vnd.fcms.collection+json;version=1" -X POST -d '{"name":"Mudskippers","taxonomy":"/mudskippers/media-types","taxonomy":"/mudskippers/topics","description":"The Internet's best resources on the Mudskipper"}' http://{host:port}/

Response
--------

The new collection is at the location provided in the location in the header. A representation of
the new collection is also returned.

Status
~~~~~~

- **201**: created
- **422**: the collection entity you passed in is not valid

Headers
~~~~~~~

- **Location**: the URL of the newly created collection

Example
~~~~~~~

.. code-block:: json

   {
      "name":"Mudskippers",
      "created_at":"2013-04-23T14:30:50Z",
      "updated_at":"2013-04-23T14:30:50Z",
      "slug":"mudskippers",
      "description":"The Internet's best resources on the Mudskipper",
      "links":[
         {
            "rel":"self",
            "method":"get",
            "href":"/mudskippers",
            "type":"application/vnd.fcms.collection+json;version=1"
         },
         {
            "rel":"contains",
            "method":"get",
            "href":"/mudskippers/",
            "type":"application/vnd.fcms.item+json;version=1"
         },
         {
            "rel":"create",
            "method":"post",
            "href":"/mudskippers/",
            "type":"application/vnd.fcms.item+json;version=1"
         },
         {
            "rel":"update",
            "method":"put",
            "href":"/mudskippers",
            "type":"application/vnd.fcms.collection+json;version=1"
         },
         {
            "rel":"delete",
            "method":"delete",
            "href":"/mudskippers",
         },
         {
            "rel":"taxonomy",
            "method":"get",
            "href":"/mudskippers/media-types",
            "type":"application/vnd.fcms.taxonomy+json;version=1"
         },
         {
            "rel":"taxonomy",
            "method":"get",
            "href":"/mudskippers/topics",
            "type":"application/vnd.fcms.taxonomy+json;version=1"
         }
      ]
   }

Update a Collection
===================

Update an existing collection.

Request
-------

.. code-block:: http

   PUT /:collection-slug

Parameters
~~~~~~~~~~

Pass in details for the updated collection as a JSON representation. The name is required.

If no slug is provided in the JSON representation, the existing slug will be used.

.. code-block:: json

   {
      "name":"Mudskipper",
      "slug":"mudskipper-info",
      "taxonomy":"/mudskippers/topics",
      "description":"The world's best resources on the Mudskipper"
   }

.. note::

   Provide a new slug in the JSON body to move a collection.

Headers
~~~~~~~

- **Accept**: application/vnd.fcms.collection+json;version=1
- **Accept-Charset**: utf-8
- **Content-type**: application/vnd.fcms.collection+json;version=1

Example
~~~~~~~

.. code-block:: bash

   curl -i --header "Accept: application/vnd.fcms.collection+json;version=1" --header "Accept-Charset: utf-8" --header "Content-type: application/vnd.fcms.item+json;version=1" -X PUT -d '{"name":"Mudskipper","slug":"mudskipper-info","taxonomy":"/mudskippers/topics","description":"The world's best resources on the Mudskipper"}' http://{host:port}/mudskippers

Response
--------

The representation of the updated collection is at the specified location, which is echoed in the
location in the header. A representation of the updated collection is also returned.

Status
~~~~~~

- **200**: update successful
- **404**: the collection is not found
- **422**: the item entity you passed in is not valid

Example
~~~~~~~

.. code-block:: json

   {
      "name":"Mudskippers",
      "created_at":"2013-04-23T14:30:50Z",
      "updated_at":"2013-04-23T14:30:50Z",
      "slug":"mudskipper-info",
      "description":"The world's best resources on the Mudskipper",
      "links":[
         {
            "rel":"self",
            "method":"get",
            "href":"/mudskipper-info",
            "type":"application/vnd.fcms.collection+json;version=1"
         },
         {
            "rel":"contains",
            "method":"get",
            "href":"/mudskippers/",
            "type":"application/vnd.fcms.item+json;version=1"
         },
         {
            "rel":"create",
            "method":"post",
            "href":"/mudskippers/",
            "type":"application/vnd.fcms.item+json;version=1"
         },
         {
            "rel":"update",
            "method":"put",
            "href":"/mudskipper-info",
            "type":"application/vnd.fcms.collection+json;version=1"
         },
         {
            "rel":"delete",
            "method":"delete",
            "href":"/mudskipper-info",
         },
         {
            "rel":"taxonomy",
            "method":"get",
            "href":"/mudskipper-info/media-types",
            "type":"application/vnd.fcms.taxonomy+json;version=1"
         },
         {
            "rel":"taxonomy",
            "method":"get",
            "href":"/mudskipper-info/topics",
            "type":"application/vnd.fcms.taxonomy+json;version=1"
         }
      ]
   }

Delete a Collection
===================

Delete an existing collection.

Request
-------

.. code-block:: http

   DELETE /:collection-slug

Example
~~~~~~~

.. code-block:: http

   curl -i -X DELETE http://{host:port}/mudskippers

Response
--------

There is no response body, just a status.

Status
~~~~~~

- **204**: deleted
- **404**: collection was not found