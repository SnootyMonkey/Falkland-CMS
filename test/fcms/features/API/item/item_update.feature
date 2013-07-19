Feature: Updating Items with the REST API

  The system should return the detail of items stored in a collection and handle the following scenarios:

  all good - no slug
  all good - with same slug
  all good - with different slug
  all good - unicode in the body
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
  no name in body
  different slug specified in body is already used
  different slug specified in body is invalid

	Background:
	  Given I had a collection "c" with the following items:
	  
	  |slug				|name        	|description																																																																				|
	  | i 				| i 					| this is an item																																																																		|
	  | another-i	| 私はガラスを食	| er stîget ûf mit grôzer kraft Τη γλώσσα μου έδωσαν ελληνική მივჰხვდე მას ჩემსა الزجاج و هذا لا يؤلمني. मैं काँच खा सकता ฉันกินกระจกได้ לא מזיק Mogę jeść szkło €|
	  
	  And the collection "c" had an item count of 2

	# all good, no slug - 200 OK
	# curl -i --header "Accept: application/vnd.fcms.item+json;version=1" --header "Accept-Charset: utf-8" --header "Content-Type: application/vnd.fcms.item+json;version=1" -X PUT -d '{"name":"i-prime", "i":"i"}' http://localhost:3000/c/i
	Scenario: Update an item without providing a new slug
	  When I have a "PUT" request to URL "/c/i"
	  And I provide an "item"
	  And I accept an "item"
	  And I set the "name" to "i-prime"
	  And I set the "i" to "i"
	  Then the status will be "200"
	  And I will receive an "item"
	  And the body will be JSON
	  And the item "i" in collection "c" will be named "i-prime"
	  And the "description" will not exist
	  And the "i" will be "i"
	  And the collection "c" has an item count of 2
	  When I have a "GET" request to URL "/c/i"
	  And I accept an "item"
	  Then the status will be "200"
	  And I will receive an "item"
	  And the body will be JSON
	  And the item "i" in collection "c" will be named "i-prime"
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
	  And the item "i" in collection "c" will be named "i-prime"
	  And the "description" will not exist
	  And the "i" will be "i"
	  And the collection "c" has an item count of 2
	  When I have a "GET" request to URL "/c/i"
	  And I accept an "item"
	  Then the status will be "200"
	  And I will receive an "item"
	  And the body will be JSON
	  And the item "i" in collection "c" will be named "i-prime"
	  And the "description" will not exist
	  And the "i" will be "i"

# all good - with different slug
#And the "Location" header will be "/c/i-moved"
# curl -i --header "Accept: application/vnd.fcms.item+json;version=1" --header "Accept-Charset: utf-8" --header "Content-Type: application/vnd.fcms.item+json;version=1" -X PUT -d '{"name":"a3", "slug":"a3", description":"a3", "a3":"a3"}' http://localhost:3000/vic-20/a

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
	  And the item "another-i" in collection "c" will be named "私"
	  And the "description" will be "Τη γλώσσα μου έδωσαν ελληνική მივჰხვდე მას ჩემსა الزجاج و هذا لا يؤلمني. मैं काँच खा सकता ฉันกินกระจกได้ לא מזיק Mogę jeść szkło"
	  And the "i" will be "i"
	  And the collection "c" has an item count of 2
	  When I have a "GET" request to URL "/c/another-i"
	  And I accept an "item"
	  Then the status will be "200"
	  And I will receive an "item"
	  And the body will be JSON
	  And the item "another-i" in collection "c" will be named "私"
	  And the "description" will be "Τη γλώσσα μου έδωσαν ελληνική მივჰხვდე მას ჩემსა الزجاج و هذا لا يؤلمني. मैं काँच खा सकता ฉันกินกระจกได้ לא מזיק Mogę jeść szkło"
	  And the "i" will be "i"

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
	  And the item "i" in collection "c" will be named "i-prime"
	  And the "description" will not exist
	  And the "i" will be "i"
	  And the collection "c" has an item count of 2
	  When I have a "GET" request to URL "/c/i"
	  And I accept an "item"
	  Then the status will be "200"
	  And I will receive an "item"
	  And the body will be JSON
	  And the item "i" in collection "c" will be named "i-prime"
	  And the "description" will not exist
	  And the "i" will be "i"	

	# wrong accept type - 406 Not Acceptable
	# curl -i --header "Accept: application/vnd.fcms.collection+json;version=1" --header "Accept-Charset: utf-8" --header "Content-Type: application/vnd.fcms.item+json;version=1" -X PUT -d '{"name":"i-prime", "i":"i"}' http://localhost:3000/c/i
	Scenario: Attempt to update an item with the wrong Accept type
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
	  And the item "i" in collection "c" will be named "i-prime"
	  And the "description" will not exist
	  And the "i" will be "i"
	  And the collection "c" has an item count of 2
	  When I have a "GET" request to URL "/c/i"
	  And I accept an "item"
	  Then the status will be "200"
	  And I will receive an "item"
	  And the body will be JSON
	  And the item "i" in collection "c" will be named "i-prime"
	  And the "description" will not exist
	  And the "i" will be "i"	

# wrong Content-Type header - 406 Not Acceptable

# curl -i --header "Accept: application/vnd.fcms.item+json;version=1" --header "Accept-Charset: utf-8" --header "Content-Type: application/vnd.fcms.collection+json;version=1" -X PUT -d '{"name":"a9", "description":"a9", "a9":"a9"}' http://localhost:3000/vic-20/a

# no charset

# curl -i --header "Accept: application/vnd.fcms.item+json;version=1" --header "Content-Type: application/vnd.fcms.item+json;version=1" -X PUT -d '{"name":"a10", "description":"a10", "a10":"a10"}' http://localhost:3000/vic-20/a

# wrong charset - 406 Not Acceptable

# curl -i --header "Accept: application/vnd.fcms.item+json;version=1" --header "Accept-Charset: iso-8859-1" --header "Content-Type: application/vnd.fcms.item+json;version=1" -X PUT -d '{"name":"a11", "description":"a11", "a11":"a11"}' http://localhost:3000/vic-20/a

# no body - 400 Bad Request

# curl -i --header "Accept: application/vnd.fcms.item+json;version=1" --header "Accept-Charset: utf-8" --header "Content-Type: application/vnd.fcms.item+json;version=1" -X PUT http://localhost:3000/vic-20/a

# body, but not valid JSON - 400 Bad Request

# curl -i --header "Accept: application/vnd.fcms.item+json;version=1" --header "Accept-Charset: utf-8" --header "Content-Type: application/vnd.fcms.item+json;version=1" -X PUT -d 'Hi Mom!' http://localhost:3000/vic-20/a

# curl -i --header "Accept: application/vnd.fcms.item+json;version=1" --header "Accept-Charset: utf-8" --header "Content-Type: application/vnd.fcms.item+json;version=1" -X PUT -d '{"name":"a12"' http://localhost:3000/vic-20/a

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

# no "name" in body

# curl -i --header "Accept: application/vnd.fcms.item+json;version=1" --header "Accept-Charset: utf-8" --header "Content-Type: application/vnd.fcms.item+json;version=1" -X PUT -d '{description":"a15", "a15":"a15"}' http://localhost:3000/vic-202/a

# different slug specified in body is already used

# curl -i --header "Accept: application/vnd.fcms.item+json;version=1" --header "Accept-Charset: utf-8" --header "Content-Type: application/vnd.fcms.item+json;version=1" -X PUT -d '{"name":"a16", "slug":"a3", description":"a16", "a16":"a16"}' http://localhost:3000/vic-20/a

# different slug specified in body is invalid

# curl -i --header "Accept: application/vnd.fcms.item+json;version=1" --header "Accept-Charset: utf-8" --header "Content-Type: application/vnd.fcms.item+json;version=1" -X PUT -d '{"name":"a17", "slug":"a a", description":"a17", "a17":"a17"}' http://localhost:3000/vic-20/a