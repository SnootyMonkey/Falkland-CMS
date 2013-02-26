Falkland-CMS
============

Falkland CMS is a Content/Collection Management System written in Clojure, ClojureScript and CouchDB.

## What is a CMS?

## Why Falkland CMS?

### Who is this for?

### Who is this not for?

* Anyone that needs granular [security](#security)

### Where'd the name came from?

Falkland CMS was built to support the [Falklandsophile](http://falklandsophile.com) website. So the name seemed fitting.

## Installation

### External Dependencies

Most of Falkland's dependencies are internal, meaning lein will handle getting them for you. There are a few exceptions:

* [Clojure](http://clojure.org/) - Clojure is a Lisp that runs on the Java VM
* [Java](http://www.java.com/) - a Java VM is needed to run Clojure
* [Leiningen](https://github.com/technomancy/leiningen) - Clojure's native build tool
* [CouchDB](http://http://couchdb.apache.org/) - CouchDB is a schema-free, document-oriented database, ideally suited for a CMS
* [elasticsearch](http://www.elasticsearch.org/) - elasticsearch is a schema-free, document-oriented search engine, ideally suited for CouchDB

## Quick Start Guide

## Concepts

### Items

### Taxonomies

### Faceted Search

### Pages

There are 3 types of pages

* **static** - A static page is a simple CMS page, it may reference named or searched for resources, but it could also be made up of just static content. A home page, about page, or terms of use page is a good example.
* **item** - An item page displays a single particular item of a particular item type
* **taxonomy** - A taxonomy page 

### <a id="security"/> Security Model

Users of Falkland CMS are one of three types:

* **Administrator** - named users that can administer the configuration of the system and all items
* **User** - named users that can create, update, and delete all items in the system, but not administer the configuration of the system
* **The General Public** - everyone accessing the system anonymously with their web browser

## Example Sites

* [Falklandsophile](http://falklandsophile.com)

## Contributing

## Getting Help

## License

Copyright © 2013 Snooty Monkey

Falkland CMS is distributed under the [MIT license](http://opensource.org/licenses/MIT).
