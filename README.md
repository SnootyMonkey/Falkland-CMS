Falkland CMS
============

## DESCRIPTION

Falkland CMS is a Curation Management System written in Clojure, ClojureScript and CouchDB.

### What is a CMS?

Traditionally a CMS or [WCMS (web content management system)](http://en.wikipedia.org/wiki/Web_content_management_system) is a system for publishing website content. Key foundational features of a WCMS are:

* separation of content and presentation, managing the data in the system separately from how it looks when presented
* easy editing of content, usually through an administrative web UI
* collaboration of multiple content authors
* templates to take the data in the system and present it as web content, typically HTML

Falkland CMS has these foundational capabilities, but is not as focused on the creation of new content as a traditional CMS.

Alternatives acronyms that are more suited are Collection Management System (CMS), Digital Collection Management System (DCMS), or the acronym used for Falkland CMS, Curation Management System (CMS) or Digital Curation Management System (DCMS).

### Why use Falkland CMS?

Like its inspiration, [Omeka](http://omeka.org/about/), Falkland CMS sits at the intersection between a traditional Web CMS, a Digital Collection Management system, and a Museum Exhibit Management system.

It is ideal for:

* repository and presentation of digital collections
* online museum exihibits
* online presentation of library collections
* repository and presentation of primary and secondary source collections

Falklands CMS is for collecting and curating, not authoring. While Falkland CMS supports authoring of new content, it is to provide context for the presentation and exhibition of collected resources. Falkland CMS is not intended as a WCMS for a huge corpus of brand new content. You wouldn't run a daily newspaper with Falklankds CMS. Its primary role is to point to existing content, either content on the web, or content collected into Falklands CMS.

### Who is this for?

* scholars
* museum and exihibit curators
* librarians
* archivists
* historical organizations
* teachers and professors
* collectors / hobbyists / enthusiasts

### Who is this not for?

* Anyone that needs granular [security](#security)
* Anyone that needs extensive new content authoring
* Anyone that needs extensive, professional-grade offline artifact cataloging

### What are some (hypothetical) example uses of Falkland CMS?

* The Venerable VIC-20 - an online exihibit of everything Commodore VIC-20
* Camus.org - Primary and secondary source collection for the philosopher Albert Camus
* Mudskippers.org - a guide to all the world's knowledge about the amazing amphibian fish
* Jack Freeman's Library - presenting 40 years of one man's books
* nil.org - A complete guide to Nihilism 
* 8-bit '80's - a site to exhibit an extensive retro video game collection
* The Pitiful Pirates - The most losingest team in the history of professional sports, the Tampa Bay Buccaneers from 1976-1995

### What are some (real) example uses of Falkland CMS?

* [Falklandsophile](http://falklandsophile.com)

### Where'd the name came from?

Falkland CMS was built to support the [Falklandsophile](http://falklandsophile.com) website. So the name seemed fitting.

## INSTALLATION

### External Dependencies

Most of Falkland's dependencies are internal, meaning lein will handle getting them for you. There are a few exceptions:

* [Clojure](http://clojure.org/) - Clojure is a Lisp that runs on the Java VM
* [Java](http://www.java.com/) - a Java VM is needed to run Clojure
* [Leiningen](https://github.com/technomancy/leiningen) - Clojure's native build tool
* [CouchDB](http://http://couchdb.apache.org/) - CouchDB is a schema-free, document-oriented database, ideally suited for a CMS
* [elasticsearch](http://www.elasticsearch.org/) - elasticsearch is a schema-free, document-oriented search engine, ideally suited for CouchDB

## QUICK START

## CONCEPTS

### Items

### Taxonomies

### Faceted Search

### Pages

There are 3 types of pages:

* **static** - A static page is a simple CMS page, it may reference named or searched for resources, but it could also be made up of just static content. A home page, about page, or terms of use page is a good example.
* **item** - An item page displays a single particular item of a particular item type
* **taxonomy** - A taxonomy page 

### <a name="security"/> Security Model

Users of Falkland CMS are one of three types:

* **Administrator** - named users that can administer the configuration of the system and all items
* **User** - named users that can create, update, and delete all items in the system, but not administer the configuration of the system
* **The General Public** - everyone accessing the system anonymously with their web browser

## DEVELOPMENT AND CONTRIBUTING

## GETTING HELP

## LICENSE

Copyright © 2013 Snooty Monkey

Falkland CMS is distributed under the [MIT license](http://opensource.org/licenses/MIT).
