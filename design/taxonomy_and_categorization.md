Open Questions on Metadata / Schemas / Representations / Validation
===================================================================

## User Defined Metadata Schemas

User defined schema of what's required: items, collections

There is some schema the user wants expressed and enforced.

Should the user's expression and view of the schema be the same as FCMS's internal representation? Ideally, for simplicity, it should, otherwise there is mapping required. But is the mapping more or less work/complexity then validating with the schema as expressed by the user? And is there only one expression/view for the user? Or will FCMS support multiple different ways of representing schemas to/from the user?

User will want to use "off-the-shelf" metadata schemas by just selecting them without having to express them. Casual users will want an "off-the-shelf" schema selected for them.

Relevant standards:
	[Dublin Core](http://dublincore.org/) - "core metadata" for simple and generic resource descriptions
	[Schema.org](http://schema.org/) - an initiative from the big search engines (Google, Bing, Yahoo, Yandex) to index web data

http://www.k4all.ca/book/export/html/189

### Metadata Schema Representation

Technical representation will be different than UI representation which should be incredibly simple (without being simplistic). But more technical users will care about the technical representation too.

There is the representation TO the tool. Where the user expresses this is the schema I want enforced.

There is the representation FROM the tool. Where the user sees the data values of the schema elements for a particular item they are "looking" at. This could be in an HTML editing page (a web form/UI), an HTML presentation page (RDFa, Microdata, Microformats), a metadata presentation page (RDF) or in an API (JSON, RDF, XML...). [Background](http://stackoverflow.com/questions/14307792/what-is-the-relationship-between-rdf-rdfa-microformats-and-microdata).

How to externally represent user defined item or collection schema?
	Role of standards?
	Just one or multiple representations?

How to internally represent user defined item or collection schema?
	Same as external? Harder when external is multiple.

Relevant representations:
	Prismatic schema "native"
	XML Schema - [Dublin Core example](http://dublincore.org/schemas/xmls/)
	RDF Schema - [Dublin Core example](http://dublincore.org/schemas/rdfs/)
	OWL
	JSON?

### Metadata Schema Validation

Create / Update: collection, item
Clojure API / REST API
	Does it have the required items for FCMS?
	Does it not have prohibited items for FCMS?
	Does it have the required items for user schema?

Prismatic schema? https://github.com/prismatic/schema
Validateur? http://clojurevalidations.info/
Manners? https://github.com/RyanMcG/manners

## Taxonomy Schema

The taxonomy schema defines what is and isn't a valid taxonomy. It's not strictly internal as it'll be exposed to API users to define what is and isn't a valid taxonomy. May benefit in terms of cognitive load from using the same technology as item/collection schema.

### Taxonomy Schema Representation

How to represent instance of FCMS taxonomy?
	Current nested JSON scheme?
	Flattened RDF triples?
	Role of standards?

How to represent schema of FCMS taxonomy?
	Current nested JSON scheme?
	Flattened RDF triples?
	Role of standards?

### Taxonomy Validation

Create / Update: taxonomy
Clojure API / REST API
	Does it conform to the FCMS taxonomy schema?



Existing Taxonomy Design / Implementation
=========================================

## CouchDB

Inside a taxonomy...

:categories []

:categories [{"slug":"fruit", "name":"Fruit"} {"slug":veggies","name":Vegetables"}]

:categories [{"slug":"fruit", "name":"Fruit", "categories" [{"slug":apple","name":"Apple"},{"slug":pear","name","Pear"}]} {"slug":"veggies","name":"Vegetables"}]


Inside an item...


## Clojure API

create-taxonomy coll-slug, name
create-taxonomy coll-slug, name, properties

get-taxonomy coll-slug, tax-slug

update-taxonomy coll-slug, tax-slug, properties

delete-taxonomy coll-slug, tax-slug



create-category coll-slug, category-path
create-category coll-slug, category-path, name

category-exists? coll-slug, category-path

move-category coll-slug, old-category-path, new-category-path

delete-category coll-slug, category-path


categorize-item coll-slug category-path item-slug

uncategorize-item coll-slug category-path item-slug

	Item categorized with A/B/C

	uncategorize A/B/C

	Means it is categorized as B still?

	Or it's not categorized in A at all?

items-for-taxonomy coll-slug, taxonomy-slug
item-count-for-taxonomy coll-slug, taxonomy-slug
	from DB reduce	

items-for-category coll-slug, category-path
item-count-for-category coll-slug, category-path
	from DB reduce

categories-for-category coll-slug, category-path
category-count-for-category coll-slug, category-path

## REST API

# Taxonomy operations

POST /coll-slug/
GET /coll-slug/
*PUT /coll-slug/taxonomy-slug
PUT /coll-slug/taxonomy-slug
DELETE /coll-slug/taxonomy-slug
*PATCH /coll-slug/taxonomy-slug

# Category operations

PUT /coll-slug/taxonomy-slug/category-slug/new-category-slug/new-category-slug
DELETE /coll-slug/taxonomy-slug/category-slug/category-slug
POST /category-move/ ( https://www.dropbox.com/developers/core/docs#fileops-move )

# Item operations


## Actions

Create a taxonomy
	new taxonomy document
Update a taxonomy
	updated taxonomy document
Change the slug of a taxonomy
	updated taxonomy document
	update every category document of the taxonomy
	update every item document of the taxonomy
Delete a taxonomy
	delete every category document of the taxonomy
	update every item document of the taxonomy

Add a new category
	new category document
Add multiple new categories
	new category documents
Delete a leaf category
	delete category document
	update every item document of the category

Delete a non-leaf category
	delete every category document of the category
	update every item document of the category
Change the slug of a leaf category
Change the slug of a non-leaf category

Reorder a category at the same level
Move a category

Categorize an item
Uncategorize an item

Count items for a taxonomy
Count items for a leaf category
Count items for a non-leaf category

Get items for a taxonomy
Get items for a leaf category
Get items for a non-leaf category