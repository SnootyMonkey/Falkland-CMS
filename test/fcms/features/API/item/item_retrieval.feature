Feature: Retrieving Items

  The system should return the detail of items stored in a collection and handle the following scenarios:

  all good
	all good with unicode
	no accept
	wrong accept
	no accept charset
	wrong accept charset
	collection doesn't exist
	item doesn't exist

	Background:
	  Given I have a collection "c" with the following items
	  
	  |slug			|name        	|description																																																																				|
	  |i 				|i 					 	|this is an item																																																																		|
	  |another-i|私はガラスを食	|er stîget ûf mit grôzer kraft Τη γλώσσα μου έδωσαν ελληνική მივჰხვდე მას ჩემსა الزجاج و هذا لا يؤلمني. मैं काँच खा सकता ฉันกินกระจกได้ לא מזיק Mogę jeść szkło €	|
	  
	  Then the collection "c" has an item count of "2"

  # all good - 200 OK
  # curl -i --header "Accept: application/vnd.fcms.item+json;version=1" --header "Accept-Charset: utf-8" -X GET http://localhost:3000/c/another-i
  Scenario: Retrieve an item
	  When I have a "GET" request to URL "/c/i"
	  And I accept a "item"
	  Then the status is "200"
	  And I receive an "item"
	  And the body is JSON
	  And the item is "i" named "i" in collection "c"
	  And the "description" is "this is an item"

	# all good with unicode - 200 OK
	# curl -i --header "Accept: application/vnd.fcms.item+json;version=1" --header "Accept-Charset: utf-8" -X GET http://localhost:3000/c/another-i
	Scenario: Retrieve an item containing unicode
	When I have a "GET" request to URL "/c/another-i"
		And I accept a "item"
		Then the status is "200"
		And I receive an "item"
		And the body is JSON
		And the item is "another-i" named "私はガラスを食" in collection "c"
		And the "description" is "er stîget ûf mit grôzer kraft Τη γλώσσα μου έδωσαν ελληνική მივჰხვდე მას ჩემსა الزجاج و هذا لا يؤلمني. मैं काँच खा सकता ฉันกินกระจกได้ לא מזיק Mogę jeść szkło €"

	# no accept - 200 OK
	# curl -i --header "Accept-Charset: utf-8" -X GET http://localhost:3000/c/i
  Scenario: Retrieve an item without using an Accept header
	  When I have a "GET" request to URL "/c/i"
	  Then the status is "200"
	  And I receive an "item"
	  And the body is JSON
	  And the item is "i" named "i" in collection "c"
	  And the "description" is "this is an item"

	# wrong accept - 406 Not Acceptable
	# curl -i --header "Accept: application/vnd.fcms.collection+json;version=1" curl -i --header "Accept-Charset: utf-8" -X GET http://localhost:3000/c/i
	Scenario: Attempt to retrieve an item with the wrong Accept type
		When I have a "GET" request to URL "/c/i"
		And I accept a "collection"
		Then the status is "406"
		And the body is text
		And the body contains "Acceptable media type: application/vnd.fcms.item+json;version=1"
		And the body contains "Acceptable charset: utf-8"

	# no accept charset - 200 OK
	# curl -i --header "Accept: application/vnd.fcms.item+json;version=1" -X GET http://localhost:3000/c/i
  Scenario: Retrieve an item without using an Accept-Charset header
	  When I have a "GET" request to URL "/c/i"
	  And I remove the header "Accept-Charset"
	  And I accept a "item"
	  Then the status is "200"
	  And I receive an "item"
	  And the body is JSON
	  And the item is "i" named "i" in collection "c"
	  And the "description" is "this is an item"

	# wrong accept charset - 406 Not Acceptable
	# curl -i --header "Accept: application/vnd.fcms.item+json;version=1" --header "Accept-Charset: iso-8859-1" -X GET http://localhost:3000/c/i
  Scenario: Retrieve an item without using an Accept-Charset header
	  When I have a "GET" request to URL "/c/i"
	  And I set the "Accept-Charset" header to "iso-8859-1"
	  And I accept a "item"
	  Then the status is "406"
	  And the body is text
	  And the body contains "Acceptable media type: application/vnd.fcms.item+json;version=1"
	  And the body contains "Acceptable charset: utf-8"

	# collection doesn't exist - 404 Not Found
	# curl -i --header "Accept: application/vnd.fcms.item+json;version=1" --header "Accept-Charset: utf-8" -X GET http://localhost:3000/not-here/i
	Scenario: Attempt to retrieve an item from a collection that doesn't exist
		When I have a "GET" request to URL "/not-here/i"
		And I accept a "item"
		Then the status is "404"
		And the body is text
		And the body is "Collection not found."

	# item doesn't exist - 404 Not Found
	# curl -i --header "Accept: application/vnd.fcms.item+json;version=1" --header "Accept-Charset: utf-8" -X GET http://localhost:3000/c/not-here
	Scenario: Attempt to retrieve an item that doesn't exist
		When I have a "GET" request to URL "/c/not-here"
		And I accept a "item"
		Then the status is "404"
		And the body is empty