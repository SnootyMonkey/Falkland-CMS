Feature: Listing items with the REST API

  The system should return a summary of the items stored in a collection and handle the following scenarios:

  empty collection
  all good, 1 item
  all good, many items
  all good, force pagination
  all good, additional page
	no accept
	wrong accept
	no accept charset
	wrong accept charset
	collection doesn't exist

	Background:
	  Given I had an empty collection "empty"
	  And the collection "empty" had an item count of 0
	  
	  Given I had a collection "one" with the following item:
	  |slug				|name        	|description			|
	  | i 				| i 					| this is an item	|
	  And the collection "one" had an item count of 1

	  Given I had a collection "many" with the following items:
	  |slug		|name        	|description																																																																				|
	  | i 		| i 					| this is an item																																																																		|
	  | uni-i	| 私はガラスを食	| er stîget ûf mit grôzer kraft Τη γλώσσα μου έδωσαν ελληνική მივჰხვდე მას ჩემსა الزجاج و هذا لا يؤلمني. मैं काँच खा सकता ฉันกินกระจกได้ לא מזיק Mogę jeść szkło €|
	  | i-2 	| i 2					| this is an item 2																																																																	|
	  | i-3 	| i 3					| this is an item	3																																																																	|
	  | i-4 	| i 4					| this is an item	4																																																																	|
	  And the collection "many" had an item count of 5
	  
  # empty collection - 200 OK
  # curl -i --header "Accept: application/vnd.fcms.item+json;version=1" --header "Accept-Charset: utf-8" -X GET http://localhost:3000/empty/
  Scenario: List items from an empty collection
	  When I have a "GET" request to URL "/empty/"
	  And I accept an "item"
	  Then the status will be "200"
	  And I will receive an "item"
	  And the body will be JSON
	  And there will be no items in "empty"

	# all good, 1 item - 200 OK
	# curl -i --header "Accept: application/vnd.fcms.item+json;version=1" --header "Accept-Charset: utf-8" -X GET http://localhost:3000/one/
  Scenario: List an item from a collection
	  When I have a "GET" request to URL "/one/"
	  And I accept an "item"
	  Then the status will be "200"
	  And I will receive an "item"
	  And the body will be JSON
	  And there will be this item in "one":
	  |slug				|name        	|description			|version|collection|
	  | i 				| i 					| this is an item	|1			|one			 |
	  And all the timestamps will be matching parseable dates

	# all good, many items - 200 OK
	# curl -i --header "Accept: application/vnd.fcms.item+json;version=1" --header "Accept-Charset: utf-8" -X GET http://localhost:3000/one/
  Scenario: List many items from a collection
	  When I have a "GET" request to URL "/many/"
	  And I accept an "item"
	  Then the status will be "200"
	  And I will receive an "item"
	  And the body will be JSON
	  And there will be these items in "many":
	  |slug		|name        	|description																																																																				|version|collection|
	  | i 		| i 					| this is an item																																																																		|1			|many			 |
	  | uni-i	| 私はガラスを食	| er stîget ûf mit grôzer kraft Τη γλώσσα μου έδωσαν ελληνική მივჰხვდე მას ჩემსა الزجاج و هذا لا يؤلمني. मैं काँच खा सकता ฉันกินกระจกได้ לא מזיק Mogę jeść szkło €|1			|many			 |
	  | i-2 	| i 2					| this is an item 2																																																																	|1			|many			 |
	  | i-3 	| i 3					| this is an item	3																																																																	|1			|many			 |
	  | i-4 	| i 4					| this is an item	4																																																																	|1			|many			 |

	# TODO
	# all good, force pagination
	# all good, additional page

	# no accept - 200 OK
	# curl -i --header "Accept-Charset: utf-8" -X GET http://localhost:3000/one/
	Scenario: List an item from a collection without using an Accept header
		When I have a "GET" request to URL "/one/"
		Then the status will be "200"
		And I will receive an "item"
		And the body will be JSON
		And there will be this item in "one":
		|slug				|name        	|description			|version|collection|
		| i 				| i 					| this is an item	|1			|one			 |
		And all the timestamps will be matching parseable dates

	# wrong accept - 406 Not Acceptable
	# curl -i --header "Accept: application/vnd.fcms.collection+json;version=1" --header "Accept-Charset: utf-8" -X GET http://localhost:3000/one/
	Scenario: Attempt to list an item from a collection with the wrong Accept header
		When I have a "GET" request to URL "/one/"
		And I accept an "collection"
		Then the status will be "406"
		And the body will be text
		And the body will contain "Acceptable media type: application/vnd.fcms.item+json;version=1"
		And the body will contain "Acceptable charset: utf-8"

	# no accept charset - 200 OK
	# curl -i --header "Accept: application/vnd.fcms.item+json;version=1" -X GET http://localhost:3000/one/
  Scenario: List an item from a collection without using an Accept-Charset header
	  When I have a "GET" request to URL "/one/"
	  And I remove the header "Accept-Charset"
	  And I accept an "item"
	  Then the status will be "200"
	  And I will receive an "item"
	  And the body will be JSON
	  And there will be this item in "one":
	  |slug				|name        	|description			|version|collection|
	  | i 				| i 					| this is an item	|1			|one			 |
	  And all the timestamps will be matching parseable dates

	# wrong accept charset - 406 Not Acceptable
	# curl -i --header "Accept: application/vnd.fcms.item+json;version=1" --header "Accept-Charset: iso-8859-1" -X GET http://localhost:3000/one/
  Scenario: Attempt to list an item with the wrong Accept-Charset header 
	  When I have a "GET" request to URL "/one/"
	  And I set the "Accept-Charset" header to "iso-8859-1"
	  And I accept an "item"
	  Then the status will be "406"
	  And the body will be text
	  And the body will contain "Acceptable media type: application/vnd.fcms.item+json;version=1"
	  And the body will contain "Acceptable charset: utf-8"

	# collection doesn't exist
	# curl -i --header "Accept: application/vnd.fcms.item+json;version=1" --header "Accept-Charset: utf-8" -X GET http://localhost:3000/not-here/
	Scenario: Attempt to list an item from a collection that doesn't exist
		When I have a "GET" request to URL "/not-here/"
		And I accept an "item"
		Then the status will be "404"
		And the body will be text
		And the body contents will be "Collection not found."