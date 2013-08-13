:tocdepth: 2

Taxonomies
##########

A `taxonomy <http://www.wordnik.com/words/taxonomy>`_ is a hierarchical classification of items
in a collection. A taxonomy is made up of a tree of categories from the more general at the root
of the taxonomy to the more specific at the leaves of the taxonomy. Here is a simple abbreviated example
of a taxonomy of US locations:

![US Locations Taxonomy](./docs/images/US-Location-Taxonomy.png "Sample US Locations Taxonomy")

In the example the taxonomy is called "Location" and it consists of categories such as "Continental US" and
"Florida".

An item classified in a category in a taxonomy is a member of that category and all of that category's
parents. As an illustrative example, imagine an item classified as "Nort Carolina". It's also a member of
"South East", "Continental US" and the "Location" taxonomy as a whole.

![US Locations Taxonomy](./docs/images/Sample-Item-US-Location-Taxonomy.png "Sample Item in US Locations Taxonomy")

With Falkland CMS, it is typical to have multiple orthogonal taxonomies categorizing the items in a collection. 

Items can be categorized in more than 1 category in the same taxonomy. In fact, as has been pointed out above,
it is rare for items to be categorized in just one category in a taxonomy since they are members of their categories' parent
categories as well.

Categories have a slug and a path. The simple slug must follow all the standard rules for slugs in Falkland-CMS and
must be unique to all other categories *at the same level and location in the taxonomy*. The path to a category is
made up of the collection, the taxonomy slug, all the parent slugs and finally the category slug. In our sample taxonomy
for example, the slug for the "North Carolina" category is "north-carolina" and the path is:

	/founding-documents/location/continental-us/south-east/north-carolina/

Listing items at the path above means the "Mecklenburg Declaration of Independence" will be listed since it is classified
in the "North Carolina" category, and listing the items in the following paths would also include the "Mecklenburg Declaration of Independence":

- /founding-documents/location/continental-us/south-east/
- /founding-documents/location/continental-us/
- /founding-documents/location/
- /founding-documents/

Some examples of taxonomies:

* type of media
* type of document
* geographic location
* topic
* time period
* biological classification
* chemical classification
* dewey decimal classification
* folk taxonomies

There is an extensive [list of taxonomies](http://www.taxonomywarehouse.com/headword_list_new.aspx?vObject=10076&stype=ab) at the Taxonomy Warehouse.

List Taxonomies
---------------

List all the taxonomies in a collection.




How to distinguish between?

GET /:collection-slug/:taxonomy-slug - with item mime-type, get an item list
GET /:collection-slug/:item-slug - with item mime-type, get an item

Do we need an array-of indicator on the mime-type?


POST /:collection-slug - create a taxonomy
GET /:collection-slug - list taxonomies

GET /:collection-slug/:taxonomy-slug - with taxonomy mime-type, get a taxonomy
PUT /:collection-slug/:taxonomy-slug - update a taxonomy
DELETE /:collection-slug/:taxonomy-slug - delete a taxonomy
GET /:collection-slug/:taxonomy-slug - with item mime-type, get an item list
GET /:collection-slug/:taxonomy-slug/:category-slug/:category-slug - get an item list


Here is an example of an extensive topic taxonomy:

.. code-block:: json

	{
	  "name":"Topics",
	  "created_at":"2013-04-23T14:30:50Z",
	  "updated_at":"2013-04-23T14:30:50Z",
	  "slug":"topics",
	  "collection":"falkland-islands",
	  "description":"",
	  "categories": [
			{"natural-history" : "Natural History", "categories": [
	  		{"flora": "Flora"},
				{"insects": "Insects"},
				{"fishes": "Fishes"},
				{"birds": "Birds", "categories": [
					{"penguins": "Penguins"}]},
				{"mammals": "Mammals"}]},
			{"pre-20th-century-history": "Pre-20th Century History", "categories": [
	  		{"naval": "Naval"},
				{"legal-political": "Legal / Political"},
				{"darwin": "Charles Darwin"}]},
			{"modern-history": "Modern History", "categories": [
	 			{"naval": "Naval"},
	 			{"legal-political": "Legal / Political"}]},
			{"military-history": "Military History", "categories": [
	 			{"ww1": "World War I"},
	 			{"ww2": "WW II History"},
	 			{"1982": "1982 Falklands War", "categories": [
	 				{"political": "Political", "categories": [
	 					{"thatcher": "Margaret Thatcher"}]},
	 				{"naval": "Naval", "categories": [
	 					{"uk": "UK", "categories": [
	 						{"sheffield": "HMS Sheffield"},
	 						{"invincible": "HMS Invincible"},
	 						{"hermes": "HMS Hermes"},
	 						{"conqueror": "HMS Conqueror"},
	 						{"canberra": "SS Canberra"},
	 						{"qe2": "HMS Queen Elizabeth 2"},
	 						{"galahad-tristram": "RFA Sir Galahad & RFA Sir Tristram"}]},
	 					{"argentina": "Argentina", "categories": [
	 						{"belgrano": "ARA General Belgrano"}]}]},
	 				{"air": "Air", "categories": [
	 					{"uk": "UK", "categories": [
	 						{"harrier": "Harrier"},
	 						{"Vulcan": "Vulcan"}]},
	 					{"argentina": "Argentina"}]},
	 				{"ground": "Ground", "categories": [
	 					{"uk": "UK", "categories": [
	 						{"3-commando": "3 Commando Brigade", "categories": [
	   						{"sas": "SAS"},
	 							{"40-commando": "40 Commando, Royal Marines"},
	 							{"42-commando": "42 Commando, Royal Marines"},
	 							{"45-commando": "45 Commando, Royal Marines"},
	 							{"2-para": "2nd Battalion, Parachute Regiment"},
	 							{"3-para": "3rd Battalion, Parachute Regiment"}]},
	 						{"5-infantry": "5th Infantry Brigade",  "categories": [
	 							{"welsh-guards": "Welsh Guards"},
	 							{"scots-guards": "Scots Guards"},
	 							{"gurkhas": "Gurkta Rifles"}]}]},
	 					{"argentina": "Argentian"}]},
	 				{"Civilian": "civilian"}]}]},
	   	{"society": "Society", "categories": [
	  		{"people": "People"},
	  		{"sports": "Sports"},
	  		{"food": "Food"},
	  		{"industry": "Industry", "categories": [
		  		{"agriculture": "Agriculture"},
				  {"fisheries": "Fisheries"},
				  {"hydrocarbons": "Oil & Gas"}]},
	  		{"government": "Government", "categories": [
	  			{"constitution": "Constitution"},
	  			{"la": "Legislative Assembly", "categories": [
	  				{"ec": "Executive Council"}]},
	  			{"judiciary": "Judiciary"},
	  			{"police": "Police"},
	  			{"governor": "Governor"}]},
	  		{"tourism": "Tourism", "categories": [
	  			{"lodging": "Lodging"},
	  			{"tours": "Tours"},
		 			{"guides": "Guides"}]}]},
	  	{"fiction": "Fiction"}
	  ],
	  "links":[
	      {
	         "rel":"self",
	         "method":"get",
	         "href":"/falkland-islands/topics",
	         "type":"application/vnd.fcms.taxonomy+json;version=1"
	      },
	      {
	         "rel":"update",
	         "method":"put",
	         "href":"/falkland-islands/topics",
	         "type":"application/vnd.fcms.taxonomy+json;version=1"
	      },
	      {
	         "rel":"delete",
	         "method":"delete",
	         "href":"/falkland-islands/topics",
	      },
	      {
	         "rev":"collection",
	         "method":"get",
	         "href":"/falkland-islands",
	         "type":"application/vnd.fcms.collection+json;version=1",
	      },
	      {
	         "rev":"browse",
	         "method":"get",
	         "href":"/falkland-islands/natural-history",
	         "type":"application/vnd.fcms.item+json;version=1",
	         "name": "Natural History"
	      },
	      {
	         "rev":"browse",
	         "method":"get",
	         "href":"/falkland-islands/modern-history",
	         "type":"application/vnd.fcms.item+json;version=1",
	         "name": "Modern History"
	      },
	      {
	         "rev":"browse",
	         "method":"get",
	         "href":"/falkland-islands/military-history",
	         "type":"application/vnd.fcms.item+json;version=1",
	         "name": "Militiary History"
	      },
	      {
	         "rev":"browse",
	         "method":"get",
	         "href":"/falkland-islands/society",
	         "type":"application/vnd.fcms.item+json;version=1",
	         "name": "Society"
	      },
	      {
	         "rev":"browse",
	         "method":"get",
	         "href":"/falkland-islands/fiction",
	         "type":"application/vnd.fcms.item+json;version=1",
	         "name": "Fiction"
	      }
	   ]
	}