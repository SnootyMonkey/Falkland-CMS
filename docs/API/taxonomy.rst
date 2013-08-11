:tocdepth: 2
Taxonomies
##########

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