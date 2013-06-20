<a name='item'/>
## Item Resource

An [item](http://www.wordnik.com/words/item) is a single entry in a collection. An item is not itself THE member of the collection, but is a pointer TO the member of the collection. It's not THE article on Faulkner's early history with bedwetting and its effect on his later writing career, it's a pointer TO the article.

With Falkland CMS, you capture items in a collection and catgorize them into multiple orthogonal taxonomies. Items can point to literally anything in the world: books, articles, web pages, videos, art, artifacts, quotes, collectables, et cetera. An item is simply anything in a collection that is big enough to warrant spending the time to organize the collection. Once created, items can be browsed and searched, and can be displayed in exhibits.

Some examples of items:

* a VIC-20 cartridge for vic20.com
* a secondary source about *The Fall* for Camus.org
* a Youtube video about mudskippers for Mudskippers.org
* a book, *The Fall*, in Jack Freeman's Library
* a wikipedia entry on amoralism for nil.org
* an Atari 7800 game for 8-bit.com
* a John McKay quote for pitifulpirates.com

You can:

* [List Iems in a collection](#list-items)
* [Search for Iems in a collection](#search-items)
* [Get a specific Item](#get-item)
* [Create an Item in a Collection](#create-item)
* [Update an Item](#update-item)
* [Delete an Item](#delete-item)

<a name='list-items'/>
### List Items in a Collection

Get all the items in a collection. Note: the trailing slash after the collection slug is important.

#### Request

   URL         /{collection slug}/?num=50&start=200
   Method      GET
   Query       num - how many items to return, optional, defaults to 50
   Query       start - 0-based index of the 1st item to return, optional, defaults to 0
   Accept      application/vnd.fcms.item+json;version=1

#### Request Example

```shell
curl -i --header "Accept: application/vnd.fcms.item+json;version=1" -X GET http://{host:port}/mudskippers/

curl -i --header "Accept: application/vnd.fcms.item+json;version=1" -X GET http://{host:port}/mudskippers/?num=100

curl -i --header "Accept: application/vnd.fcms.item+json;version=1" -X GET http://{host:port}/mudskippers/?num=10&start=10
```

#### Response

The response has a JSON array called items which contains partial representations of each item and a link to the full representation of the item. The response also contains a link for creating new items and pagination links.

#### Response Status

* 200 - OK
* 404 - collection was not found

#### Response Example

```json
{
   "items":[
      {
         "name":"Amazing animals - Mudskipper",
         "URL":"http://www.youtube.com/watch?v=mJhUKzEq47U",
         "created_at":"2013-04-23T14:30:50Z",
         "updated_at":"2013-04-23T14:30:50Z",
         "links":[
            {
               "rel":"self",
               "method":"get",
               "href":"/mudskippers/amazing-animals-mudskipper"
               "type":"application/vnd.fcms.item+json;version=1"
            }
         ]
      },
      {
         "name":"Mudskipper's Habitat",
         "URL": "http://animal.discovery.com/tv-shows/animal-planet-presents/videos/whats-to-love-mudskippers-habitat.htm"
         "created_at":"2011-04-23T14:32:17Z",
         "updated_at":"2011-04-23T14:32:17Z",
         "links":[
            {
               "rel":"self",
               "method":"get",
               "href":"/mudskippers/mudskippers-habitat"
               "type":"application/vnd.fcms.item+json;version=1"
            }
         ]
      }
   ],
   "links":[
      {
         "rel":"new",
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
```

<a name='get-item'/>
### Get an Item

Get a particular item.

#### Request

   URL         /{collection slug}/{item-slug}
   Method      GET
   Accept      application/vnd.fcms.item+json;version=1

   URL         /{collection slug}/{taxonomy-slug}/{category-slug}/{item-slug}
   Method      GET
   Accept      application/vnd.fcms.item+json;version=1

#### Request Example

cURL

```shell
curl -i --header "Accept: application/vnd.fcms.item+json;version=1" -X GET http://{host:port}/mudskippers/amazing-animals-mudskipper

curl -i --header "Accept: application/vnd.fcms.item+json;version=1" -X GET http://{host:port}/mudskippers/media-types/videos/online/amazing-animals-mudskipper
```

#### Response

The response has a complete JSON representation of the item which contains links to available actions on the item, a reverse link to the collection containing the item, and links to any categories the item is a member of.

#### Response Status

* 200 - OK
* 404 - collection, taxonomy, category or item was not found

#### Response Example

```json
{
   "name":"Amazing animals - Mudskipper",
   "URL":"http://www.youtube.com/watch?v=mJhUKzEq47U",
   "created_at":"2013-04-23T14:30:50Z",
   "updated_at":"2013-04-23T14:30:50Z",
   "slug":"amazing-animals-mudskipper",
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
```

<a name='create-item'/>
### Create an Item

Create a new item in a collection.

#### Request

   URL            /{collection-slug}
   Method         POST
   Accept         application/vnd.fcms.item+json;version=1
   Content-type   application/vnd.fcms.item+json;version=1
   Charset        UTF-8

Pass in details for the new item as a JSON representation. The name is required and will be used to create the slug.

Here is a minimal representation:

```json
{
   "name":"Mudskipper",
   "URL":"http://en.wikipedia.org/wiki/Mudskipper"
}
```

Here is a more complete representation:

```json
{
   "name":"Mudskipper",
   "URL":"http://en.wikipedia.org/wiki/Mudskipper",
   "category":"/mudskippers/media-types/articles/online",
   "category":"/mudskippers/topics/general",
   "description":"Mudskipper entry from Wikipedia, the free encyclopedia"
}
```

#### Request Example

cURL

```shell
curl -i --header "Accept: application/vnd.fcms.item+json;version=1" --header "Content-type: application/vnd.fcms.item+json;version=1" --header "Charset: UTF-8" -X POST -d '{"name":"Mudskipper","URL":"http://en.wikipedia.org/wiki/Mudskipper","category":"/mudskippers/media-types/articles/online","category":"/mudskippers/topics/general","description":"Mudskipper entry from Wikipedia, the free encyclopedia"}' http://{host:port}/mudskippers
```

#### Response

The new item is at the location provided in the location in the header. A representation of the new item is also returned.

#### Response Status

* 201 - created
* 404 - the collection is not found
* 422 - the item entity you passed in is not valid

#### Response Examples

```json
{
   "name":"Mudskipper",
   "URL":"http://en.wikipedia.org/wiki/Mudskipper",
   "created_at":"2013-04-23T14:30:50Z",
   "updated_at":"2013-04-23T14:30:50Z",
   "slug":"wiki-mudskipper",
   "description":"Mudskipper entry from Wikipedia, the free encyclopedia",
   "links":[
      {
         "rel":"self",
         "method":"get",
         "href":"/mudskippers/wikipedia-mudskipper"
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
```

Create a new item in a collection with a specified slug and/or category.

#### Request

   URL            /{collection-slug}/{item-slug}
   Method         PUT
   Accept         application/vnd.fcms.item+json;version=1
   Content-type   application/vnd.fcms.item+json;version=1
   Charset        UTF-8

   URL            /{collection-slug}/{taxonomy-slug}/{category-slug}/{category-slug}/{item-slug}
   Method         PUT
   Accept         application/vnd.fcms.item+json;version=1
   Content-type   application/vnd.fcms.item+json;version=1
   Charset        UTF-8

Pass in details for the new item as a JSON representation.

Here is a minimal representation:

```json
{
   "name":"Mudskipper",
   "URL":"http://en.wikipedia.org/wiki/Mudskipper"
}
```

Here is a more complete representation:

```json
{
   "name":"Mudskipper",
   "URL":"http://en.wikipedia.org/wiki/Mudskipper",
   "category":"/mudskippers/topics/general",
   "description":"Mudskipper entry from Wikipedia, the free encyclopedia"
}
```

#### Request Example

cURL

```shell
curl -i --header "Accept: application/vnd.fcms.item+json;version=1" --header "Content-type: application/vnd.fcms.item+json;version=1" --header "Charset: UTF-8" -X PUT -d '{"name":"Mudskipper","URL":"http://en.wikipedia.org/wiki/Mudskipper","category":"/mudskippers/topics/general","description":"Mudskipper entry from Wikipedia, the free encyclopedia"}' http://{host:port}/mudskippers/media-types/articles/online/wikipedia-mudskipper
```

#### Response

The representation of the new item is at the specified location, which is echoed in the location in the header. A representation of the new item is also returned.

#### Response Status

* 200 - successful
* 404 - the collection, taxonomy or category is not found
* 422 - the item entity you passed in is not valid

#### Response Examples

```json
{
   "name":"Mudskipper",
   "URL":"http://en.wikipedia.org/wiki/Mudskipper",
   "created_at":"2013-04-23T14:30:50Z",
   "updated_at":"2013-04-23T14:30:50Z",
   "slug":"wiki-mudskipper",
   "description":"Mudskipper entry from Wikipedia, the free encyclopedia",
   "links":[
      {
         "rel":"self",
         "method":"get",
         "href":"/mudskippers/wikipedia-mudskipper"
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
```

<a name='update-item'/>
### Update an Item

Update an existing item.

#### Request

   URL            /{collection-slug}/{item-slug}
   Method         PUT
   Accept         application/vnd.fcms.item+json;version=1
   Content-type   application/vnd.fcms.item+json;version=1
   Charset        UTF-8

   URL            /{collection-slug}/{taxonomy-slug}/{category-slug}/{category-slug}/{item-slug}
   Method         PUT
   Accept         application/vnd.fcms.item+json;version=1
   Content-type   application/vnd.fcms.item+json;version=1
   Charset        UTF-8

Pass in details for the updated item as a JSON representation.

```json
{
   "name":"Mudskipper",
   "slug":"wiki-mud"
   "URL":"http://en.wikipedia.org/wiki/Mudskipper",
   "category":"/mudskippers/topics/general",
   "description":"Mudskipper entry from Wikipedia, the free encyclopedia"
}
```

Note: provide a new slug in the JSON body to move an item to a new slug.

#### Request Example

cURL

```shell
curl -i --header "Accept: application/vnd.fcms.item+json;version=1" --header "Content-type: application/vnd.fcms.item+json;version=1" --header "Charset: UTF-8" -X PUT -d '{"name":"Mudskipper","slug":"wiki-mud","URL":"http://en.wikipedia.org/wiki/Mudskipper","category":"/mudskippers/topics/general","description":"Mudskipper entry from Wikipedia, the free encyclopedia"}' http://{host:port}/mudskippers/media-types/articles/online/wikipedia-mudskipper
```

#### Response

The representation of the updated item is at the specified location, which is echoed in the location in the header. A representation of the updated item is also returned.

#### Response Status

* 200 - update successful
* 404 - the collection, taxonomy or category is not found
* 422 - the item entity you passed in is not valid

#### Response Examples

```json
{
   "name":"Amazing animals - Mudskipper",
   "URL":"http://www.youtube.com/watch?v=mJhUKzEq47U",
   "created_at":"2013-04-23T14:30:50Z",
   "updated_at":"2013-04-23T14:30:50Z",
   "slug":"amazing-animals-mudskipper",
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
```

<a name='delete-item'/>
### Delete an Item

Delete an existing item.

#### Request

   URL            /{collection-slug}/{item-slug}
   Method         DELETE

   URL            /{collection-slug}/{taxonomy-slug}/{category-slug}/{category-slug}/{item-slug}
   Method         DELETE

#### Request Example

cURL

```shell
curl -i -X DELETE http://{host:port}/mudskippers/amazing-animals-mudskipper

curl -i -X DELETE http://{host:port}/mudskippers/media-types/videos/online/amazing-animals-mudskipper
```

#### Response

There is no response body, just a status.

#### Response Status

* 204 - deleted
* 404 - collection, item, taxonomy or category was not found