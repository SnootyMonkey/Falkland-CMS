<a name='item'/>
## Collection Resource

A [collection](http://www.wordnik.com/words/collection) is a logical grouping of items to be managed together. A collection also has zero or more taxanomies that define hierarchical organizational structures for the items in the collection.

Some examples of collection:

* A curated collection of secondary sources about Albert Camus for Camus.org
* Everything there is on the Internet about mudskippers for Mudskippers.org
* All the books in Jack Freeman's Library
* Everything written about amoralism for nil.org
* All Atari 7800 games for 8-bit.com
* All the collected Buccaneer quotes for pitifulpirates.com

You can:

* [List the collections that exist in the system](#list-collections)
* [Get a specific Collection](#get-collection)
* [Update a Collection](#update-collection)
* [Delete a Collection](#delete-collection)

<a name='list-collections'/>
### List Collections in the sytem

Get all the collections in the system.

#### Request

   URL         /?num=50&start=200
   Method      GET
   Query       num - how many items to return, optional, defaults to 50
   Query       start - 0-based index of the 1st item to return, optional, defaults to 0
   Accept      application/vnd.fcms.collection+json;version=1

#### Request Example

```shell
curl -i --header "Accept: application/vnd.fcms.collection+json;version=1" -X GET http://{host:port}/

curl -i --header "Accept: application/vnd.fcms.collection+json;version=1" -X GET http://{host:port}/?num=100

curl -i --header "Accept: application/vnd.fcms.collection+json;version=1" -X GET http://{host:port}/?num=10&start=10
```

#### Response

The response has a JSON array called collections which contains partial representations of each collection and a link to the full representation of the collection. The response also contains a link for creating new collections and pagination links.

#### Response Status

* 200 - OK

#### Response Example

```json
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
               "href":"/mudskippers"
               "type":"application/vnd.fcms.collection+json;version=1"
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
               "href":"/camus"
               "type":"application/vnd.fcms.collection+json;version=1"
            }
         ]
      }
   ],
   "links":[
      {
         "rel":"new",
         "method":"post",
         "href":"/",
         "type":"application/vnd.fcms.collection+json;version=1"
      },
      {
         "rel":"first",
         "method":"get",
         "href":"/?num=2",
         "type":"application/vnd.fcms.collection+json;version=1"
      },
      {
         "rel":"prev",
         "method":"get",
         "href":"/?num=2&start=2",
         "type":"application/vnd.fcms.collection+json;version=1"
      },
      {
         "rel":"next",
         "method":"get",
         "href":"/?num=2&start=4",
         "type":"application/vnd.fcms.collection+json;version=1"
      }
   ]
}
```

<a name='get-collection'/>
### Get a Collection

Get a particular collection. Note: the lack of a trailing slash is important.

#### Request

   URL         /{collection slug}
   Method      GET
   Accept      application/vnd.fcms.collection+json;version=1

#### Request Example

cURL

```shell
curl -i --header "Accept: application/vnd.fcms.collection+json;version=1" -X GET http://{host:port}/mudskippers
```

#### Response

The response has a complete JSON representation of the collection which contains links to available actions on the collection, and links to any taxonomies associated with the collection.

#### Response Status

* 200 - OK
* 404 - collection was not found

#### Response Example

```json
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
         "href":"/mudskippers"
         "type":"application/vnd.fcms.collection+json;version=1"
      },
      {
         "rel":"contains",
         "method":"get",
         "href":"/mudskippers/"
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
```

<a name='create-collection'/>
### Create a Collection

Create a new collection in the system.

#### Request

   URL            /
   Method         POST
   Accept         application/vnd.fcms.collection+json;version=1
   Content-type   application/vnd.fcms.collection+json;version=1
   Charset        UTF-8

Pass in details for the new collection as a JSON representation. The name is required and will be used to create the slug.

Here is a minimal representation:

```json
{
   "name":"Mudskippers"
}
```

Here is a more complete representation:

```json
{
   "name":"Mudskippers",
   "taxonomy":"/mudskippers/media-types",
   "taxonomy":"/mudskippers/topics",
   "description":"The Internet's best resources on the Mudskipper"
}
```

#### Request Example

cURL

```shell
curl -i --header "Accept: application/vnd.fcms.collection+json;version=1" --header "Content-type: application/vnd.fcms.collection+json;version=1" --header "Charset: UTF-8" -X POST -d '{"name":"Mudskippers","taxonomy":"/mudskippers/media-types","taxonomy":"/mudskippers/topics","description":"The Internet's best resources on the Mudskipper"}' http://{host:port}/
```

#### Response

The new collection is at the location provided in the location in the header. A representation of the new collection is also returned.

#### Response Status

* 201 - created
* 422 - the collection entity you passed in is not valid

#### Response Examples

```json
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
         "href":"/mudskippers"
         "type":"application/vnd.fcms.collection+json;version=1"
      },
      {
         "rel":"contains",
         "method":"get",
         "href":"/mudskippers/"
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
```

Create a new collection in the system with a specified slug.

#### Request

URL            /{collection-slug}
Method         PUT
Accept         application/vnd.fcms.collection+json;version=1
Content-type   application/vnd.fcms.collection+json;version=1
Charset        UTF-8

Pass in details for the new item as a JSON representation.

Here is a minimal representation:

```json
{
   "name":"Mudskippers"
}
```

Here is a more complete representation:

```json
{
   "name":"Mudskippers",
   "taxonomy":"/mudskippers/media-types",
   "taxonomy":"/mudskippers/topics",
   "description":"The Internet's best resources on the Mudskipper"
}
```

#### Request Example

cURL

```shell
curl -i --header "Accept: application/vnd.fcms.collection+json;version=1" --header "Content-type: application/vnd.fcms.collection+json;version=1" --header "Charset: UTF-8" -X PUT -d '{"name":"Mudskippers","taxonomy":"/mudskippers/media-types","taxonomy":"/mudskippers/topics","description":"The Internet's best resources on the Mudskipper"}' http://{host:port}/mudskippers
```

#### Response

The representation of the new item is at the specified location, which is echoed in the location in the header. A representation of the new item is also returned.

#### Response Status

* 200 - successful
* 422 - the item entity you passed in is not valid

#### Response Examples

```json
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
         "href":"/mudskippers"
         "type":"application/vnd.fcms.collection+json;version=1"
      },
      {
         "rel":"contains",
         "method":"get",
         "href":"/mudskippers/"
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
```

<a name='update-collection'/>
### Update a Collection

Update an existing collection.

#### Request

   URL            /{collection-slug}
   Method         PUT
   Accept         application/vnd.fcms.item+json;version=1
   Content-type   application/vnd.fcms.item+json;version=1
   Charset        UTF-8

Pass in details for the updated collection as a JSON representation.

```json
{
   "name":"Mudskipper",
   "slug":"mudskipper-info",
   "taxonomy":"/mudskippers/topics",
   "description":"The world's best resources on the Mudskipper"
}
```

Note: provide a new slug in the JSON body to move a collection to a new slug.

#### Request Example

cURL

```shell
curl -i --header "Accept: application/vnd.fcms.collection+json;version=1" --header "Content-type: application/vnd.fcms.item+json;version=1" --header "Charset: UTF-8" -X PUT -d '{"name":"Mudskipper","slug":"mudskipper-info","taxonomy":"/mudskippers/topics","description":"The world's best resources on the Mudskipper"}' http://{host:port}/mudskippers
```

#### Response

The representation of the updated collection is at the specified location, which is echoed in the location in the header. A representation of the updated collection is also returned.

#### Response Status

* 200 - update successful
* 404 - the collection is not found
* 422 - the item entity you passed in is not valid

#### Response Examples

```json
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
         "href":"/mudskipper-info"
         "type":"application/vnd.fcms.collection+json;version=1"
      },
      {
         "rel":"contains",
         "method":"get",
         "href":"/mudskippers/"
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
```

<a name='delete-collection'/>
### Delete a Collection

Delete an existing collection.

#### Request

   URL            /{collection-slug}
   Method         DELETE

#### Request Example

cURL

```shell
curl -i -X DELETE http://{host:port}/mudskippers
```

#### Response

There is no response body, just a status.

#### Response Status

* 204 - deleted
* 404 - collection was not found