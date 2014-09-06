Schema
======

User defined schema of what's required: items, collections

Representation
==============

How to represent user defined item or collection schema?
	Prismatic native?
	Role of Standards?

How to represent instance of FCMS taxonomy?
	Current nested JSON scheme?
	Flattened RDF triples?
	Role of Standards?

How to represent schema of FCMS taxonomy?
	Current nested JSON scheme?
	Flattened RDF triples?
	Role of Standards?

Validation
==========

Create / Update: collection, item
Clojure API / REST API
	Does it have the required items for FCMS?
	Does it not have prohibited items for FCMS?

Create / Update: collection, item
Clojure API / REST API
	Does it have the required items for user schema?

Prismatic schema?
Validateur?

Create / Update: taxonomy
Clojure API / REST API
	Does it conform to the FCMS taxonomy schema?




CouchDB
========

Inside a taxonomy...

:categories []

:categories [{"slug":"fruit", "name":"Fruit"} {"slug":veggies","name":Vegetables"}]

:categories [{"slug":"fruit", "name":"Fruit", "categories" [{"slug":apple","name":"Apple"},{"slug":pear","name","Pear"}]} {"slug":"veggies","name":"Vegetables"}]


Inside an item...


Clojure API
========

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

REST API
========

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


Actions
========

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