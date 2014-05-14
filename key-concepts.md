---
layout: default
title: Falkland CMS - Key Concepts
description: The key concepts of Falkland CMS, a Curation Management System used to collect, curate, organize, and present the knowledge that exists in the world about a particular topic.
author: Sean Johnson
---

## Resources

There are 4 main types of resources in Falkland CMS, collections, items, taxonomies and pages.

### Collections

A Falkland CMS instance has one or more collections of items. A collection is all the items that you'd like to be organized and searched together.

Taxonomical organization and searches don't span multiple collections, so you'll often have just one collection per website or app. There may be cases where you have multiple websites or apps supported by the same collection or one website or app accessing multiple collections, but these would be less typical.

### Items

Items are referential in nature. They point to something that exists, and act as a catalog entry for it. Items can reference a web page, a book, a product, a person, an animal, a building... the possibilities are endless and simply depend on what it is you are trying to curate.

### Taxonomies

A taxonomy is a particular classification scheme for organizing items into hierarchical categories. Central to Falkland CMS is supporting multiple independent taxonomies. Most collections will have many different taxonomies to categorize their items by different criteria. 

### <a id="pages"></a> Pages

There are 3 types of pages in Falkland CMS:

* **item pages** - displays a single item from the collection.
* **category pages** - displays the items in a particular category of a particular taxonomy.
* **exhibit pages** - may reference many named or searched for resources, but could also be made up of just static content. A home page is a good example of a dynamic exhibit page, and an about page, or terms of use page is a good example of a completely static exhibit page.

![FCMS Key Concepts]({{site.url}}/assets/img/FCMS-High-level.png "FCMS Key Concepts")

---

## <a id="search"></a> Faceted Search / Navigation

[Faceted search](http://en.wikipedia.org/wiki/Faceted_search) or faceted navigation is full-text search blended with navigation by multiple, orthogonal filters. It's possible with faceted search for example to do a full text search on "Camus BBC" and then drill down into the search results limiting them to the "online video" in the media taxonomy, and the "United States" in the geography taxonomy. Faceted search and navigation is a powerful technique for quickly exploring a large catalog of items and is a key capability of Falkland CMS.

---

## <a id="security"></a> Security Model

Users of Falkland CMS are one of three types:

* **Administrator** - named users that can administer the configuration of the system and all items.
* **User** - named users that can create, update, and delete all items in the system, but not administer the configuration of the system.
* **The Public** - everyone accessing the system anonymously with their web browser.