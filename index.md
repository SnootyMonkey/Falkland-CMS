---
layout: default
title: Falkland CMS - collect, curate, organize, and present knowledge
description: Falkland CMS is a Curation Management System used to collect, curate, organize, and present the knowledge that exists in the world about a particular topic.
author: Sean Johnson
---

## Introduction

Falkland CMS is a Curation Management System used to collect, curate, organize and present the knowledge that exists in the world about a particular topic.

![FCMS Key Concepts]({{site.url}}/assets/img/FCMS-High-level.png "FCMS Key Concepts")

Anything that can be unambiguously referenced with a unique identifier such as an [ISBN/ISSN](http://en.wikipedia.org/wiki/International_Standard_Book_Number), DOI ([Digital Object Identifier](http://en.wikipedia.org/wiki/Digital_object_identifier)), PURL ([Persistent URL](http://en.wikipedia.org/wiki/Persistent_uniform_resource_locator)) or URL ([Uniform Resource Locator](http://en.wikipedia.org/wiki/Uniform_resource_locator)) can be curated with Falkland CMS. The curation process consists of adding metadata information about an item that makes it easier to collect, organize, search, present and exhibit.

Items can be physical things: books, magazines, newspapers, documents, papers, consumer products, artifacts, animals, buildings, people, etc. Or items can be digital or abstract: web pages, articles, chapters, images, audio files, video files, web sites, databases, directories, words, concepts, topics, etc.

Falkland CMS can be used to describe relationships among items and can assist in the archiving and preservation of digital items.

### Who is this for?

Falkland CMS is for anyone who wants to collect, curate, organize and present the knowledge that exists in the world about a particular topic:

* bibliographers and librarians
* digital curators and archivists
* museum and exhibit curators
* scholars and professors
* teachers and students
* historical organizations
* collectors, hobbyists and enthusiasts

### Why use Falkland CMS?

Like its inspiration, [Omeka](http://omeka.org/about/), Falkland CMS sits at the intersection between a traditional Web CMS, a Digital Collection Management system, and a Museum Exhibit Management system. Unlike Omeka, Falkland CMS is written in Clojure, ClojureScript and CouchDB, and so it has a certain elegance and panache. 

A lot of the software in this domain is produced by people with rich domain expertise, but untraditional software engineering backgrounds. This often equates to mountains of poorly constructed PHP or Perl code that is difficult to use and maintain.

Falkland CMS is a deliberate experiment in going the other direction and having experienced software professionals learn the library science needed to create better software.

Ideal uses:

* repository of digital collections
* on-line presentation of collections
* on-line museum exhibits
* comprehensive bibliographies

These are perfect fits for Falkland CMS:

* The Venerable VIC-20 - an on-line exhibit of everything related to the Commodore VIC-20 computer
* Camus.org - a primary and secondary source bibliography for the philosopher Albert Camus
* Mudskippers.org - a guide to all the world's knowledge about the amazing amphibious fish
* Jack Freeman's Library - presenting 40 years of one man's books
* nil.org - a complete guide to nihilism 
* 8-bit '80's - a site to exhibit an extensive retro video game collection
* The Pitiful Pirates - a history of the Tampa Bay Buccaneers from 1976-1995

### Who is this not for?

Falkland CMS is *not* for everyone, if these describe you, you should probably look elsewhere:

* You need the extensive new content authoring and publishing capabilities of a web content management system
* You are doing extensive off-line artifact cataloging
* You need fine granular security
* You are trying to organize, curate and present a very small collection (just dozens of items)

### A word about standards.

If there is one thing the world of digital cataloging is not short of, its acronyms and standards. It seems there is enough for everyone to have a standard of their own!

Falkland CMS embraces standards, and takes a flexible, but opinionated and pragmatic approach to them. Standards are very important... and very plentiful. Falkland CMS remains as flexible as possible so you can use the metadata and interoperability standards most relevant to you. At the same time, Falkland CMS takes an informed, pragmatic and opinionated approach, using widely supported general standards as defaults. If you aren't a card carrying metadata geek, you won't need to make confusing choices from the dizzying amount of potentially relevant standards to utilize Falkland CMS.

### Who is using Falkland CMS?

Falkland CMS was created to support [Falklandsophile](http://falklandsophile.com), a complete guide to resources about the Falkland Islands. This is where the name for Falkland CMS came from.

If you're using Falkland CMS, please drop me a note and I'll link to you here.

### How is Falkland CMS Different than a Content Management System?

A traditional CMS or [WCMS (web content management system)](http://en.wikipedia.org/wiki/Web_content_management_system) is a system for publishing website content. Key features of a WCMS are:

* separation of content and presentation
* easy editing of content, usually through an administrative web UI
* collaboration of multiple content authors
* templates to take the data in the system and present it as web content, typically HTML

Falkland CMS has these foundational capabilities, but is not focused on the creation of *new* content. Instead, Falkland CMS is used for collecting, organizing, curating and presenting existing content.

Falkland CMS supports authoring content, but this content is [metadata](http://en.wikibooks.org/wiki/Open_Metadata_Handbook/Introduction) about existing resources, intended to provide context for the collection, organization, curation, presentation and exhibition of collected resources. You wouldn't run a daily on-line news site with Falkland CMS, like you would with a content management system, but you would use Falkland CMS to collect, curate and present historical or current news reports about a particular topic.

Because of this difference of focus, the acronym that is used for Falkland CMS is Curation Management System. It's not a name and acronym we use, but you could also consider Falkland CMS to be a metadata management system (MMS).

---

## <a id="status"></a> Status &amp; Roadmap

Falkland CMS is pre-alpha and not yet usable. A usable version is expected sometime in 2014.

You can see the roadmap and check on the development progress on [Trello](https://trello.com/b/UgzPjFAX/falkland-cms).

---

## <a id="help"></a> Getting Help

Falkland CMS is pre-alpha and not yet usable. A usable version is expected sometime in 2014 and instructions for getting help will be available then.

You can open an issue or feature request in the [GitHub issue tracker](https://github.com/SnootyMonkey/Falkland-CMS/issues).

---

## <a id="license"></a> License

Falkland CMS is distributed under the [Mozilla Public License v2.0](http://www.mozilla.org/MPL/2.0/).

Copyright © 2013-2014 [Snooty Monkey, LLC](http://snootymonkey.com/)