[![Stories in Ready](https://badge.waffle.io/SnootyMonkey/falkland-cms.png)](https://waffle.io/SnootyMonkey/falkland-cms)  
Falkland CMS [![Build Status](https://travis-ci.org/SnootyMonkey/Falkland-CMS.png?branch=master)](https://travis-ci.org/SnootyMonkey/Falkland-CMS)
============

Falkland CMS is a Curation Management System written in Clojure, ClojureScript and CouchDB.

### What's a Curation Management Sysytem?

Traditionally a CMS or [WCMS (web content management system)](http://en.wikipedia.org/wiki/Web_content_management_system) is a system for publishing website content. Key foundational features of a WCMS are:

* separation of content and presentation, managing the data in the system separately from how it looks when presented
* easy editing of content, usually through an administrative web UI
* collaboration of multiple content authors
* templates to take the data in the system and present it as web content, typically HTML

Falkland CMS has these foundational capabilities, but is not nearly as focused on the creation of *new* content as is a traditional WCMS. Instead, Falkland CMS is used for collecting, curating and exhibiting existing content.

Alternatives acronyms that are more suited are Collection Management System (CMS), Digital Collection Management System (DCMS), or the acronym that is used for Falkland CMS, Curation Management System (CMS) or Digital Curation Management System (DCMS).

### Why use Falkland CMS?

Like its inspiration, [Omeka](http://omeka.org/about/), Falkland CMS sits at the intersection between a traditional Web CMS, a Digital Collection Management system, and a Museum Exhibit Management system.

Falklands CMS is for collecting and curating, not authoring. While Falkland CMS supports authoring of new content, this new content is intended to provide context for the presentation and exhibition of collected resources. Falkland CMS is not intended as a WCMS for a huge corpus of brand new content. You wouldn't run a daily newspaper with Falklankds CMS. Its primary role is to point to existing content, either content on the web, or content collected into Falklands CMS.

Ideal uses:

* a repository of digital collections
* online presentation of library collections
* online museum exhibits

### Who is this for?

* scholars
* museum and exhibit curators
* librarians
* archivists
* historical organizations
* teachers and professors
* collectors / hobbyists / enthusiasts

### Who is this not for?

* Anyone that needs extensive new content authoring
* Anyone that needs extensive, professional-grade offline artifact cataloging
* Anyone that needs granular [security](#security)

### What are some (hypothetical) example uses of Falkland CMS?

* The Venerable VIC-20 - an online exhibit of everything Commodore VIC-20
* Camus.org - a primary and secondary source collection for the philosopher Albert Camus
* Mudskippers.org - a guide to all the world's knowledge about the amazing amphibious fish
* Jack Freeman's Library - presenting 40 years of one man's books
* nil.org - a complete guide to Nihilism 
* 8-bit '80's - a site to exhibit an extensive retro video game collection
* The Pitiful Pirates - a history of the Tampa Bay Buccaneers from 1976-1995

### What are some (real) example uses of Falkland CMS?

* [Falklandsophile](http://falklandsophile.com) - the complete guide to resources about the Falkland Islands

### Where'd the name came from?

Falkland CMS was built to support the [Falklandsophile](http://falklandsophile.com) website. So the name seemed fitting.

## STATUS

Falkland CMS is pre-alpha and not yet usable.

## INSTALLATION

## Quick Start - Heroku

## Local Installation

Most of Falkland's dependencies are internal, meaning Leiningen will handle getting them for you. There are a few exceptions:

* [Java 7](http://www.oracle.com/technetwork/java/javase/downloads/index.html) - a Java 7 JRE is needed to run Clojure
* [Leiningen](https://github.com/technomancy/leiningen) - Clojure's build and package management tool
* [CouchDB](http://http://couchdb.apache.org/) - CouchDB is a schema-free, document-oriented database, ideally suited for a CMS
* [elasticsearch](http://www.elasticsearch.org/) - elasticsearch is a schema-free, document-oriented search engine, ideally suited for CouchDB

## KEY CONCEPTS

### Collections

### Items

### Taxonomies

### Faceted Search

### Pages

There are 3 types of pages:

* **exhibit** - An exhibit page is a CMS page, it may reference many named or searched for resources, but it could also be made up of just static content. A home page, about page, or terms of use page is a good example of the latter.
* **item** - An item page displays a single particular item of a particular item type
* **taxonomy** - A taxonomy page ...

### <a name="security"/> Security Model

Users of Falkland CMS are one of three types:

* **Administrator** - named users that can administer the configuration of the system and all items
* **User** - named users that can create, update, and delete all items in the system, but not administer the configuration of the system
* **The General Public** - everyone accessing the system anonymously with their web browser

## DEVELOPMENT AND CONTRIBUTING

## GETTING HELP

## LICENSE

Copyright B) 2013 Snooty Monkey

Falkland CMS is distributed under the [Mozilla Public License v2.0](http://www.mozilla.org/MPL/2.0/).
