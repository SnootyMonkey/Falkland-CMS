# Developer Resources

This is an ever evolving list of resources useful for development of Falkland CMS. Some are long standing links to reference documents and the like, while others will come and go as decision support items during development of new features or designs.

## CMS

* The inspiration for Falkland CMS, [omeka](http://omeka.org/codex/Documentation)

## CouchDB

* The [Clutch](https://github.com/clojure-clutch/clutch) Clojure API for CouchDB
* [Using CouchDB with Clojure](http://www.ibm.com/developerworks/java/library/j-couchdb-clojure/) is somewhat out of date (Feb. 2011), but still has some OK info
* [A Simple Web Application with Clojure and CouchDB](http://www.vijaykiran.com/2011/07/18/a-simple-web-application-with-clojure-and-couchdb/) is somewhat out of date (July 2011), but still has some OK info
* [Simple example CouchDB schema](http://wiki.apache.org/couchdb/ApplicationSchema)

### Views

They won't be in JavaScript because a perverse goal of Falkland CMS is to be JS'less, so the question is Erlang or ClojureScript for CouchDB map/reduce views?

* [Writing CouchDB Views using ClojureScript](http://cemerick.com/2011/10/11/writing-couchdb-views-using-clojurescript/)
* [How to Enable Erlang Views](http://wiki.apache.org/couchdb/EnableErlangViews)
* [CouchDB, Writing views in Erlang](http://newtonius.blogspot.com/2012/08/couchdb-writing-views-in-erlang-part-1.html)
* [CouchDB, Custom Erlang Map Functions](http://blog.echolibre.com/2010/02/couchdb-custom-erlang-map-functions/)
* [CouchDB Woes and Writing Erlang Map/Reduce](http://www.the-eleven.com/tlegg/blog/2012/07/06/couchdb-woes-and-writing-erlang-mapreduce/)
* [Handling JSON Objects in CouchDB Native Erlang Views](http://jamietalbot.com/2010/03/18/handling-json-objects-in-couchdb-native-erlang-views/)

### Schema issues

CouchDB is schema-less, but there are implicitly 2 levels of schemas in Falkland CMS. The CMS' schema that will change slowly and irregularly as Falkland CMS evolves and gets new features and the scheme defined by the taxonomies each administrator defines for their instance. These will be different for everyone and will often change regularly and rapidly.

Given that, we have to deal with 2 issues, a document in CouchDB that is out of date with the current CMS schema, and an item in CouchDB that is out of data with the currently configured taxonomies. Further things can be easily out of date, such as a new field missing. There are more challenging scenarios, such as a field being renamed or changing types, and finally there are radical changes, such as the taxonomy category no longer existing at all.

* http://java.dzone.com/articles/schemas-couchdb
* http://stackoverflow.com/questions/130092/couchdb-document-model-changes
* http://grokbase.com/t/couchdb/user/12b2s3y6n8/how-do-u-handle-schema-changes

## ClojureScript

* [ClojureOne tutorial](http://clojurescriptone.com/)