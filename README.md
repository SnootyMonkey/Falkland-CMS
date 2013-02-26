falkland-cms
============

Falkland CMS is a Content/Collection Management System written in Clojure, ClojureScript and CouchDB.

## What is a CMS?

## Why Falkland CMS?

### Who is this for?

### Who is this not for?

* Anyone that needs granular [security][#security]

### Where'd the name came from?

Falkland CMS was built to support the [Falklandsophile](http://falklandsophile.com) website. So the name seemed fitting.

## Installation

### External Dependencies

Most of Falkland's dependencies are internal, meaning lein will handle getting them for you. There are a few exceptions:

* [Clojure](http://clojure.org/) - Clojure is a Lisp that runs on the Java VM
* [Java](http://www.java.com/) - a Java VM is needed to run Clojure
* [Leiningen](https://github.com/technomancy/leiningen) - Clojure's native build tool
* [CouchDB](http://http://couchdb.apache.org/) - CouchDB is a document-oriented database, ideally suited for a CMS

## Quick Start Guide

## Concepts

### Taxonomies

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