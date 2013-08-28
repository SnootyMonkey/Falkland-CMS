Feature: Updating Items with the REST API

  The system should return the detail of items stored in a collection and handle the following scenarios:

  all good - no slug
  all good - with same slug
  all good - with different slug
  all good - unicode in the body
  all good - no name in body
  conflicting reserved properties
  no accept
  wrong accept
  no content header
  wrong content header
  no charset
  wrong charset
  no body
  body, but not valid JSON
  collection doesn't exist
  item doesn't exist
  different slug specified in body is already used
  different slug specified in body is invalid

	Background:
	  Given I had a collection "c" with the following items:
	  
	  |slug				|name        	|description																																																																				|
	  | i 				| i 					| this is an item																																																																		|
	  | another-i	| 私はガラスを食	| er stîget ûf mit grôzer kraft Τη γλώσσα μου έδωσαν ελληνική მივჰხვდე მას ჩემსა الزجاج و هذا لا يؤلمني. मैं काँच खा सकता ฉันกินกระจกได้ לא מזיק Mogę jeść szkło €|
	  
	  And the collection "c" had an item count of 2
	  And I delay a moment

	# all good, no slug - 200 OK
	# (get-in ctx [:updated-item :slug])
	Scenario: Update an item without providing a new slug
	  When I have a "PUT" request to URL "/c/i"
	  And I provide an "item"
	  And I accept an "item"
	  And I set the "name" to "i-prime"
	  And I set the "i" to "i"
	  Then the status will be "200"
	  And I will receive an "item"
	  And the body will be JSON
	  And the updated item "i" in collection "c" will be named "i-prime"
	  And the "description" will not exist
	  And the "i" will be "i"
	  And the collection "c" has an item count of 2
	  When I have a "GET" request to URL "/c/i"
	  And I accept an "item"
	  Then the status will be "200"
	  And I will receive an "item"
	  And the body will be JSON
	  And the updated item "i" in collection "c" will be named "i-prime"
	  And the "description" will not exist
	  And the "i" will be "i"

	# all good, with same slug - 200 OK
	# curl -i --header "Accept: application/vnd.fcms.item+json;version=1" --header "Accept-Charset: utf-8" --header "Content-Type: application/vnd.fcms.item+json;version=1" -X PUT -d '{"name":"i-prime", "slug":"i", i":"i"}' http://localhost:3000/c/i
	Scenario: Update an item providing the same slug
	  When I have a "PUT" request to URL "/c/i"
	  And I provide an "item"
	  And I accept an "item"
	  And I set the "name" to "i-prime"
		And I set the "slug" to "i"
	  And I set the "i" to "i"
	  Then the status will be "200"
	  And I will receive an "item"
	  And the body will be JSON
	  And the updated item "i" in collection "c" will be named "i-prime"
	  And the "description" will not exist
	  And the "i" will be "i"
	  And the collection "c" has an item count of 2
	  When I have a "GET" request to URL "/c/i"
	  And I accept an "item"
	  Then the status will be "200"
	  And I will receive an "item"
	  And the body will be JSON
	  And the updated item "i" in collection "c" will be named "i-prime"
	  And the "description" will not exist
	  And the "i" will be "i"

	# all good, with different slug - 200 OK
	# curl -i --header "Accept: application/vnd.fcms.item+json;version=1" --header "Accept-Charset: utf-8" --header "Content-Type: application/vnd.fcms.item+json;version=1" -X PUT -d '{"name":"i", "slug":"i-moved","i":"i"}' http://localhost:3000/c/i
	Scenario: Update an item providing a new slug
	  When I have a "PUT" request to URL "/c/i"
	  And I provide an "item"
	  And I accept an "item"
	  And I set the "name" to "i-prime"
		And I set the "slug" to "i-moved"
	  And I set the "i" to "i"
	  Then the status will be "200"
	  And the "Location" header will be "/c/i-moved"
	  And I will receive an "item"
	  And the body will be JSON
	  And the updated item "i-moved" in collection "c" will be named "i-prime"
	  And the "description" will not exist
	  And the "i" will be "i"
	  And the collection "c" has an item count of 2
	  When I have a "GET" request to URL "/c/i"
	  And I accept an "item"
	  Then the status will be "404"
	  And the body will be empty
	  When I have a "GET" request to URL "/c/i-moved"
	  And I accept an "item"
	  Then the status will be "200"
	  And I will receive an "item"
	  And the body will be JSON
	  And the updated item "i-moved" in collection "c" will be named "i-prime"
	  And the "description" will not exist
	  And the "i" will be "i"

	# all good, unicode in the body - 200 OK
	# curl -i --header "Accept: application/vnd.fcms.item+json;version=1" --header "Accept-Charset: utf-8" --header "Content-Type: application/vnd.fcms.item+json;version=1" -X PUT -d '{"name":"私", "description":"Τη γλώσσα μου έδωσαν ελληνική მივჰხვდე მას ჩემსა الزجاج و هذا لا يؤلمني. मैं काँच खा सकता ฉันกินกระจกได้ לא מזיק Mogę jeść szkło", "i":"i"}' http://localhost:3000/c/another-i
	Scenario: Update an item containing unicode
	  When I have a "PUT" request to URL "/c/another-i"
	  And I provide an "item"
	  And I accept an "item"
	  And I set the "name" to "私"
	  And I set the "description" to "Τη γλώσσα μου έδωσαν ελληνική მივჰხვდე მას ჩემსა الزجاج و هذا لا يؤلمني. मैं काँच खा सकता ฉันกินกระจกได้ לא מזיק Mogę jeść szkło"
	  And I set the "i" to "i"
	  Then the status will be "200"
	  And I will receive an "item"
	  And the body will be JSON
	  And the updated item "another-i" in collection "c" will be named "私"
	  And the "description" will be "Τη γλώσσα μου έδωσαν ελληνική მივჰხვდე მას ჩემსა الزجاج و هذا لا يؤلمني. मैं काँच खा सकता ฉันกินกระจกได้ לא מזיק Mogę jeść szkło"
	  And the "i" will be "i"
	  And the collection "c" has an item count of 2
	  When I have a "GET" request to URL "/c/another-i"
	  And I accept an "item"
	  Then the status will be "200"
	  And I will receive an "item"
	  And the body will be JSON
	  And the updated item "another-i" in collection "c" will be named "私"
	  And the "description" will be "Τη γλώσσα μου έδωσαν ελληνική მივჰხვდე მას ჩემსა الزجاج و هذا لا يؤلمني. मैं काँच खा सकता ฉันกินกระจกได้ לא מזיק Mogę jeść szkło"
	  And the "i" will be "i"

  # all good, no "name" in body - 200 OK
  # curl -i --header "Accept: application/vnd.fcms.item+json;version=1" --header "Accept-Charset: utf-8" --header "Content-Type: application/vnd.fcms.item+json;version=1" -X PUT -d '{description":"a no name i", "i":"i"}' http://localhost:3000/c/i
  Scenario: Attempt to update an item without a name
    When I have a "PUT" request to URL "/c/i"
    And I provide an "item"
    And I accept an "item"
    And I set the "description" to "a no name i"
    And I set the "i" to "i"
    Then the status will be "200"
    And I will receive an "item"
    And the body will be JSON
    And the updated item "i" in collection "c" will be named "i"
    And the "description" will be "a no name i"
    And the "i" will be "i"

  # conflicting reserved properties - 422 Unprocessable Entity
  # curl -i --header "Accept: application/vnd.fcms.item+json;version=1" --header "Accept-Charset: utf-8" --header "Content-Type: application/vnd.fcms.item+json;version=1" -X PUT -d '{"name":"i-1", "version":"foo", "i":"i"}' http://localhost:3000/c/i
  # curl -i --header "Accept: application/vnd.fcms.item+json;version=1" --header "Accept-Charset: utf-8" --header "Content-Type: application/vnd.fcms.item+json;version=1" -X PUT -d '{"name":"i-2", "collection":"foo", "i":"j"}' http://localhost:3000/c/i
  # curl -i --header "Accept: application/vnd.fcms.item+json;version=1" --header "Accept-Charset: utf-8" --header "Content-Type: application/vnd.fcms.item+json;version=1" -X PUT -d '{"name":"i-3", "id":"foo", "i":"k"}' http://localhost:3000/c/i
  # curl -i --header "Accept: application/vnd.fcms.item+json;version=1" --header "Accept-Charset: utf-8" --header "Content-Type: application/vnd.fcms.item+json;version=1" -X PUT -d '{"name":"i-4", "type":"foo", "i":"l"}' http://localhost:3000/c/i
  # curl -i --header "Accept: application/vnd.fcms.item+json;version=1" --header "Accept-Charset: utf-8" --header "Content-Type: application/vnd.fcms.item+json;version=1" -X PUT -d '{"name":"i-5", "created-at":"foo", "i":"m"}' http://localhost:3000/c/i
  # curl -i --header "Accept: application/vnd.fcms.item+json;version=1" --header "Accept-Charset: utf-8" --header "Content-Type: application/vnd.fcms.item+json;version=1" -X PUT -d '{"name":"i-6", "updated-at":"foo", "i":"n"}' http://localhost:3000/c/i
  Scenario: Attempt to update an item with a property that conflict with a reserved property
  	# version
    When I have a "PUT" request to URL "/c/i"
    And I provide an "item"
    And I accept an "item"
    And I set the "name" to "i-1"
    And I set the "i" to "i"
    And I set the "version" to "foo"
    Then the status will be "200"
    And I will receive an "item"
    And the body will be JSON
    And version 2 of the updated item "i" in collection "c" will be named "i-1"
    And the "i" will be "i"
    # collection
    When I have a "PUT" request to URL "/c/i"
    And I provide an "item"
    And I accept an "item"
    And I set the "name" to "i-2"
    And I set the "i" to "j"
    And I set the "collection" to "foo"
    Then the status will be "200"
    And I will receive an "item"
    And the body will be JSON
    And version 3 of the updated item "i" in collection "c" will be named "i-2"
    And the "collection" will be "c"
    And the "i" will be "j"
    # id
    When I have a "PUT" request to URL "/c/i"
    And I provide an "item"
    And I accept an "item"
    And I set the "name" to "i-3"
    And I set the "i" to "k"
    And I set the "id" to "foo"
    Then the status will be "200"
    And I will receive an "item"
    And the body will be JSON
    And version 4 of the updated item "i" in collection "c" will be named "i-3"
    And the "i" will be "k"
    # type
    When I have a "PUT" request to URL "/c/i"
    And I provide an "item"
    And I accept an "item"
    And I set the "name" to "i-4"
    And I set the "i" to "l"
    And I set the "type" to "foo"
    Then the status will be "200"
    And I will receive an "item"
    And the body will be JSON
    And version 5 of the updated item "i" in collection "c" will be named "i-4"
    And the "type" will not exist
    And the "i" will be "l"
    # created-at
    When I have a "PUT" request to URL "/c/i"
    And I provide an "item"
    And I accept an "item"
    And I set the "name" to "i-5"
    And I set the "i" to "m"
    And I set the "created-at" to "foo"
    Then the status will be "200"
    And I will receive an "item"
    And the body will be JSON
    And version 6 of the updated item "i" in collection "c" will be named "i-5"
    And the "i" will be "m"
    # updated-at
    When I have a "PUT" request to URL "/c/i"
    And I provide an "item"
    And I accept an "item"
    And I set the "name" to "i-6"
    And I set the "i" to "n"
    And I set the "updated-at" to "foo"
    Then the status will be "200"
    And I will receive an "item"
    And the body will be JSON
    And version 7 of the updated item "i" in collection "c" will be named "i-6"
    And the "type" will not exist
    And the "i" will be "n"
    # updated?
    And the collection "c" has an item count of 2
    When I have a "GET" request to URL "/c/i"
    And I accept an "item"
    Then the status will be "200"
    And I will receive an "item"
    And the body will be JSON
    And version 7 of the updated item "i" in collection "c" will be named "i-6"
    And the "i" will be "n"

	# no accept type - 200 OK
	# curl -i --header "Accept-Charset: utf-8" --header "Content-Type: application/vnd.fcms.item+json;version=1" -X PUT -d '{"name":"i-prime", "i":"i"}' http://localhost:3000/c/i
	Scenario: Update an item without using an Accept header
	  When I have a "PUT" request to URL "/c/i"
	  And I provide an "item"
	  And I set the "name" to "i-prime"
	  And I set the "i" to "i"
	  Then the status will be "200"
	  And I will receive an "item"
	  And the body will be JSON
	  And the updated item "i" in collection "c" will be named "i-prime"
	  And the "description" will not exist
	  And the "i" will be "i"
	  And the collection "c" has an item count of 2
	  When I have a "GET" request to URL "/c/i"
	  And I accept an "item"
	  Then the status will be "200"
	  And I will receive an "item"
	  And the body will be JSON
	  And the updated item "i" in collection "c" will be named "i-prime"
	  And the "description" will not exist
	  And the "i" will be "i"	

	# wrong accept type - 406 Not Acceptable
	# curl -i --header "Accept: application/vnd.fcms.collection+json;version=1" --header "Accept-Charset: utf-8" --header "Content-Type: application/vnd.fcms.item+json;version=1" -X PUT -d '{"name":"i-prime", "i":"i"}' http://localhost:3000/c/i
	Scenario: Attempt to update an item with the wrong Accept header
	  When I have a "PUT" request to URL "/c/i"
	  And I provide an "item"
	  And I accept a "collection"
	  And I set the "name" to "i-prime"
	  And I set the "i" to "i"
	  Then the status will be "406"
	  And the body will be text
	  And the body will contain "Acceptable media type: application/vnd.fcms.item+json;version=1"
	  And the body will contain "Acceptable charset: utf-8"
	  And the collection "c" has an item count of 2
	  When I have a "GET" request to URL "/c/i"
	  And I accept an "item"
	  Then the status will be "200"
	  And I will receive an "item"
	  And the body will be JSON
	  And the item "i" in collection "c" will be named "i"
	  And the "description" will be "this is an item"
	  And the "i" will not exist

	# no Content-Type header - 200 OK
	# curl -i --header "Accept: application/vnd.fcms.item+json;version=1" --header "Accept-Charset: utf-8" -X PUT -d '{"name":"i-prime", "i":"i"}' http://localhost:3000/c/i
	Scenario: Attempt to update an item without a Content-Type header
	  When I have a "PUT" request to URL "/c/i"
	  And I accept an "item"
	  And I set the "name" to "i-prime"
	  And I set the "i" to "i"
	  Then the status will be "200"
	  And I will receive an "item"
	  And the body will be JSON
	  And the updated item "i" in collection "c" will be named "i-prime"
	  And the "description" will not exist
	  And the "i" will be "i"
	  And the collection "c" has an item count of 2
	  When I have a "GET" request to URL "/c/i"
	  And I accept an "item"
	  Then the status will be "200"
	  And I will receive an "item"
	  And the body will be JSON
	  And the updated item "i" in collection "c" will be named "i-prime"
	  And the "description" will not exist
	  And the "i" will be "i"	

	# wrong Content-Type header - 415 Unsupported Media Type
	# curl -i --header "Accept: application/vnd.fcms.item+json;version=1" --header "Accept-Charset: utf-8" --header "Content-Type: application/vnd.fcms.collection+json;version=1" -X PUT -d '{"name":"i-prime", "i":"i"}' http://localhost:3000/c/i
	Scenario: Attempt to update an item with the wrong Content-Type header
	  When I have a "PUT" request to URL "/c/i"
	  And I provide a "collection"
	  And I accept an "item"
	  And I set the "name" to "i-prime"
	  And I set the "i" to "i"
	  Then the status will be "415"
	  And the body will be text
	  And the body will contain "Acceptable media type: application/vnd.fcms.item+json;version=1"
	  And the body will contain "Acceptable charset: utf-8"
	  And the collection "c" has an item count of 2
	  When I have a "GET" request to URL "/c/i"
	  And I accept an "item"
	  Then the status will be "200"
	  And I will receive an "item"
	  And the body will be JSON
	  And the item "i" in collection "c" will be named "i"
	  And the "description" will be "this is an item"
	  And the "i" will not exist

	# no charset - 200 OK
	# curl -i --header "Accept: application/vnd.fcms.item+json;version=1" --header "Content-Type: application/vnd.fcms.item+json;version=1" -X PUT -d '{"name":"i-prime", "i":"i"}' http://localhost:3000/c/i
	Scenario: Attempt to update an item without an Accept-Charset header
	  When I have a "PUT" request to URL "/c/i"
	  And I remove the header "Accept-Charset"
	  And I provide an "item"
	  And I accept an "item"
	  And I set the "name" to "i-prime"
	  And I set the "i" to "i"
	  Then the status will be "200"
	  And I will receive an "item"
	  And the body will be JSON
	  And the updated item "i" in collection "c" will be named "i-prime"
	  And the "description" will not exist
	  And the "i" will be "i"
	  And the collection "c" has an item count of 2
	  When I have a "GET" request to URL "/c/i"
	  And I accept an "item"
	  Then the status will be "200"
	  And I will receive an "item"
	  And the body will be JSON
	  And the updated item "i" in collection "c" will be named "i-prime"
	  And the "description" will not exist
	  And the "i" will be "i"	

	# wrong charset - 406 Not Acceptable
	# curl -i --header "Accept: application/vnd.fcms.item+json;version=1" --header "Accept-Charset: iso-8859-1" --header "Content-Type: application/vnd.fcms.item+json;version=1" -X PUT -d '{"name":"i-prime", "i":"i"}' http://localhost:3000/c/i
	Scenario: Attempt to update an item with the wrong Accept-Charset header
	  When I have a "PUT" request to URL "/c/i"
	  And I set the "Accept-Charset" header to "iso-8859-1"
	  And I provide an "item"
	  And I accept an "item"
	  And I set the "name" to "i-prime"
	  And I set the "i" to "i"
	  Then the status will be "406"
	  And the body will be text
	  And the body will contain "Acceptable media type: application/vnd.fcms.item+json;version=1"
	  And the body will contain "Acceptable charset: utf-8"
	  And the collection "c" has an item count of 2
	  When I have a "GET" request to URL "/c/i"
	  And I accept an "item"
	  Then the status will be "200"
	  And I will receive an "item"
	  And the body will be JSON
	  And the item "i" in collection "c" will be named "i"
	  And the "description" will be "this is an item"
	  And the "i" will not exist

	# no body - 400 Bad Request
	# curl -i --header "Accept: application/vnd.fcms.item+json;version=1" --header "Accept-Charset: utf-8" --header "Content-Type: application/vnd.fcms.item+json;version=1" -X PUT http://localhost:3000/c/i
	Scenario: Attempt to update an item with no body
	  When I have a "PUT" request to URL "/c/i"
	  And I provide an "item"
	  And I accept an "item"
	  And I provide no body
	  Then the status will be "400"
	  And the body will be text
	  And the body contents will be "Bad request."
	  And the collection "c" has an item count of 2
	  When I have a "GET" request to URL "/c/i"
	  And I accept an "item"
	  Then the status will be "200"
	  And I will receive an "item"
	  And the body will be JSON
	  And the item "i" in collection "c" will be named "i"
	  And the "description" will be "this is an item"
	  And the "i" will not exist

	# body, but not valid JSON - 400 Bad Request
	# curl -i --header "Accept: application/vnd.fcms.item+json;version=1" --header "Accept-Charset: utf-8" --header "Content-Type: application/vnd.fcms.item+json;version=1" -X PUT -d 'Hi Mom!' http://localhost:3000/c/i
	# curl -i --header "Accept: application/vnd.fcms.item+json;version=1" --header "Accept-Charset: utf-8" --header "Content-Type: application/vnd.fcms.item+json;version=1" -X PUT -d '{"name":"i-prime"' http://localhost:3000/c/i
	Scenario: Attempt to update an item with an invalid body
	  When I have a "PUT" request to URL "/c/i"
	  And I provide an "item"
	  And I accept an "item"
	  And I provide the body "Hi Mom!"
	  Then the status will be "400"
	  And the body will be text
	  And the body contents will be "Bad request."
	  And the collection "c" has an item count of 2
	  When I have a "GET" request to URL "/c/i"
	  And I accept an "item"
	  Then the status will be "200"
	  And I will receive an "item"
	  And the body will be JSON
	  And the item "i" in collection "c" will be named "i"
	  And the "description" will be "this is an item"
	  And the "i" will not exist
	  When I have a "PUT" request to URL "/c/i"
	  And I provide an "item"
	  And I accept an "item"
	  And I provide the body "{'name':'i-prime'"
	  Then the status will be "400"
	  And the body will be text
	  And the body contents will be "Bad request."
	  And the collection "c" has an item count of 2
	  When I have a "GET" request to URL "/c/i"
	  And I accept an "item"
	  Then the status will be "200"
	  And I will receive an "item"
	  And the body will be JSON
	  And the item "i" in collection "c" will be named "i"
	  And the "description" will be "this is an item"
	  And the "i" will not exist


	# collection doesn't exist - 404 Not Found
	# curl -i --header "Accept: application/vnd.fcms.item+json;version=1" --header "Accept-Charset: utf-8" --header "Content-Type: application/vnd.fcms.item+json;version=1" -X PUT -d '{"name":"i-prime", "i":"i"}' http://localhost:3000/not-here/i
	Scenario: Attempt to update an item in a collection that doesn't exist
	  When I have a "PUT" request to URL "/not-here/i"
	  And I provide an "item"
	  And I accept an "item"
	  And I set the "name" to "i-prime"
	  And I set the "i" to "i"
	  Then the status will be "404"
	  And the body will be text
	  And the body contents will be "Collection not found."
	  And the collection "c" has an item count of 2
	  When I have a "GET" request to URL "/c/i"
	  And I accept an "item"
	  Then the status will be "200"
	  And I will receive an "item"
	  And the body will be JSON
	  And the item "i" in collection "c" will be named "i"
	  And the "description" will be "this is an item"
	  And the "i" will not exist

	# item doesn't exist - 404 Not Found
	# curl -i --header "Accept: application/vnd.fcms.item+json;version=1" --header "Accept-Charset: utf-8" --header "Content-Type: application/vnd.fcms.item+json;version=1" -X PUT -d '{"name":"i-prime", "i":"i"}' http://localhost:3000/c/not-here
	Scenario: Attempt to update an item that doesn't exist
	  When I have a "PUT" request to URL "/c/not-here"
	  And I provide an "item"
	  And I accept an "item"
	  And I set the "name" to "i-prime"
	  And I set the "i" to "i"
	  Then the status will be "404"
	  And the body will be empty
	  And the collection "c" has an item count of 2
	  When I have a "GET" request to URL "/c/i"
	  And I accept an "item"
	  Then the status will be "200"
	  And I will receive an "item"
	  And the body will be JSON
	  And the item "i" in collection "c" will be named "i"
	  And the "description" will be "this is an item"
	  And the "i" will not exist

	# different slug specified in body is already used - 422 Unprocessable Entity
	# curl -i --header "Accept: application/vnd.fcms.item+json;version=1" --header "Accept-Charset: utf-8" --header "Content-Type: application/vnd.fcms.item+json;version=1" -X PUT -d '{"name":"i-prime", "slug":"another-i", "i":"i"}' http://localhost:3000/c/i
	Scenario: Attempt to update an item with a slug that's already used in the collection
	  When I have a "PUT" request to URL "/c/i"
	  And I provide an "item"
	  And I accept an "item"
	  And I set the "name" to "i-prime"
		And I set the "slug" to "another-i"
	  And I set the "i" to "i"
	  Then the status will be "422"
	  And the body will be text
	  And the body contents will be "Slug already used in collection."
	  And the collection "c" has an item count of 2
	  When I have a "GET" request to URL "/c/i"
	  And I accept an "item"
	  Then the status will be "200"
	  And I will receive an "item"
	  And the body will be JSON
	  And the item "i" in collection "c" will be named "i"
	  And the "description" will be "this is an item"
	  And the "i" will not exist
	  When I have a "GET" request to URL "/c/another-i"
	  And I accept an "item"
	  Then the status will be "200"
	  And I will receive an "item"
	  And the body will be JSON
	  And the item "another-i" in collection "c" will be named "私はガラスを食"
	  And the "description" will be "er stîget ûf mit grôzer kraft Τη γλώσσα μου έδωσαν ελληνική მივჰხვდე მას ჩემსა الزجاج و هذا لا يؤلمني. मैं काँच खा सकता ฉันกินกระจกได้ לא מזיק Mogę jeść szkło €"
	  And the "i" will not exist

	# different slug specified in body is invalid - 422 Unprocessable Entity
	# curl -i --header "Accept: application/vnd.fcms.item+json;version=1" --header "Accept-Charset: utf-8" --header "Content-Type: application/vnd.fcms.item+json;version=1" -X PUT -d '{"name":"i-prime", "slug":"I i", "i":"i"}' http://localhost:3000/c/i
	Scenario: Attempt to update an item with a slug that's invalid
	  When I have a "PUT" request to URL "/c/i"
	  And I provide an "item"
	  And I accept an "item"
	  And I set the "name" to "i-prime"
		And I set the "slug" to "I i"
	  And I set the "i" to "i"
	  Then the status will be "422"
	  And the body will be text
	  And the body contents will be "Invalid slug."
	  And the collection "c" has an item count of 2
	  When I have a "GET" request to URL "/c/i"
	  And I accept an "item"
	  Then the status will be "200"
	  And I will receive an "item"
	  And the body will be JSON
	  And the item "i" in collection "c" will be named "i"
	  And the "description" will be "this is an item"
	  And the "i" will not exist