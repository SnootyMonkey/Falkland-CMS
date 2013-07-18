Feature: Deleting Items with the REST API

  The system should delete items from a collection and handle the following scenarios:

	all good
	item doesn't exist
	collection doesn't exist

	Background:
	  Given I had a collection "c" with the following items:

	  |slug 			|name				|description					|
	  | i  				| i   			| this is an item 		|
	  | another-i | another-i	| this is another item|

	  And the collection "c" had an item count of 2

	# all good - 204 No Content
	# curl -i -X DELETE http://localhost:3000/c/i
	Scenario: Delete an item
  	When I have a "DELETE" request to URL "/c/i"
  	Then the status will be "204"
  	And the body will be empty
  	And the collection "c" has an item count of 1
  	When I have a "GET" request to URL "/c/i"
  	And I accept an "item"
  	Then the status will be "404"
  	When I have a "GET" request to URL "/c/another-i"
  	And I accept an "item"
  	Then the status will be "200"
  	And I will receive an "item"
  	And the body will be JSON
  	And the item "another-i" in collection "c" will be named "another-i"

	# collection doesn't exist - 404 Not Found
	# curl -i -X DELETE http://localhost:3000/not-here/i
	Scenario: Delete an item from a collection that does not exist
	 	When I have a "DELETE" request to URL "/not-here/i"
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
	 	When I have a "GET" request to URL "/c/another-i"
	 	And I accept an "item"
	 	Then the status will be "200"
	 	And the body will be JSON
	 	And the item "another-i" in collection "c" will be named "another-i"

	# item doesn't exist - 404 Not Found
	# curl -i -X DELETE http://localhost:3000/c/not-here
	Scenario: Delete an item does not exist
	 	When I have a "DELETE" request to URL "/c/not-here"
	 	Then the status will be "404"
	 	And the body will be empty
	 	And the collection "c" has an item count of 2
	 	When I have a "GET" request to URL "/c/i"
	 	And I accept an "item"
	 	Then the status will be "200"
	 	And I will receive an "item"
	 	And the body will be JSON
	 	And the item "i" in collection "c" will be named "i"
	 	When I have a "GET" request to URL "/c/another-i"
	 	And I accept an "item"
	 	Then the status will be "200"
	 	And I will receive an "item"
	 	And the body will be JSON
	 	And the item "another-i" in collection "c" will be named "another-i"