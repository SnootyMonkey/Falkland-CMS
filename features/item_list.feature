Feature: Item list

	Background:
		Given the system knows about the following items in collection "business"
			|taxonomy				|name				|creator			|URL									|
			|media-type:URL	|Amazon.com	|Jeff Bezos		|http://amazon.com/		|
			|media-type:URL	|Apple 			|Steve Jobs		|http://apple.com/		|
			|media-type:URL	|iPod 			|Jonathan Ives|http://apple.com/ipod|			
	
	Scenario: List resources
		When an API client requests GET "/business/" accepting "application/vnd.fcms.item+json;version=1"
		Then the response should be JSON:
		"""
		{
		   "items":[
		      {
		         "name":"Amazon.com",
		         "URL":"http://amazon.com/",
		         "created_at":any,
		         "updated_at":any,
		         "links":[
		            {
		               "rel":"self",
		               "method":"get",
		               "href":"/business/amazon-com"
		               "type":"application/vnd.fcms.item+json;version=1"
		            }
		         ]
		      },
		      {
		         "name":"Apple",
		         "URL": "http://apple.com/"
		         "created_at":any,
		         "updated_at":any,
		         "links":[
		            {
		               "rel":"self",
		               "method":"get",
		               "href":"/business/apple"
		               "type":"application/vnd.fcms.item+json;version=1"
		            }
		         ]
		      },
		      {
		         "name":"iPod",
		         "URL":"http://apple.com/ipod",
		         "created_at":any,
		         "updated_at":any,
		         "links":[
		            {
		               "rel":"self",
		               "method":"get",
		               "href":"/business/ipod"
		               "type":"application/vnd.fcms.item+json;version=1"
		            }
		         ]
		      }
		   ],
		   "links":[
		      {
		         "rel":"new",
		         "method":"post",
		         "href":"/business/",
		         "type":"application/vnd.fcms.item+json;version=1"
		      }
		   ]
		}
		"""