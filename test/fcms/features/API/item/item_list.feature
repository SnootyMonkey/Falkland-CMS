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