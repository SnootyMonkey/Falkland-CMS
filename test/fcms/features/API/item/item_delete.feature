Feature: Deleting Items with the REST API

  The system should delete items from a collection and handle the following scenarios:

	all good
	item doesn't exist
	collection doesn't exist

	Background:
	  Given I have a collection "c" with the following items

	  |slug				|name			|description					|
	  |i  				|i   			|this is an item 			|
	  |another-i 	|another-i|this is another item |

	  Then the collection "c" has an item count of "2"

	# all good - 204 No Content
	# curl -i -X DELETE http://localhost:3000/c/i
	Scenario: Delete an item
  	When I have a "DELETE" request to URL "/c/i"
  	Then the status is "204"
  	And the body is empty
  	And the collection "c" has an item count of "1"
  	When I have a "GET" request to URL "/c/i"
  	And I accept a "item"
  	Then the status is "404"
  	When I have a "GET" request to URL "/c/another-i"
  	And I accept a "item"
  	Then the status is "200"
  	And I receive an "item"
  	And the body is JSON
  	And the item "another-i" is named "another-i" in collection "c"

	# collection doesn't exist - 404 Not Found
	# curl -i -X DELETE http://localhost:3000/not-here/i
	Scenario: Delete an item from a collection that does not exist
	 	When I have a "DELETE" request to URL "/not-here/i"
	 	Then the status is "404"
	 	And the body is text
	 	And the body is "Collection not found."
	 	And the collection "c" has an item count of "2"
	 	When I have a "GET" request to URL "/c/i"
	 	And I accept a "item"
	 	Then the status is "200"
	 	And I receive an "item"
	 	And the body is JSON
	 	And the item "i" is named "i" in collection "c"
	 	When I have a "GET" request to URL "/c/another-i"
	 	And I accept a "item"
	 	Then the status is "200"
	 	And the body is JSON
	 	And the item "another-i" is named "another-i" in collection "c"

	# item doesn't exist - 404 Not Found
	# curl -i -X DELETE http://localhost:3000/c/not-here
	Scenario: Delete an item does not exist
	 	When I have a "DELETE" request to URL "/c/not-here"
	 	Then the status is "404"
	 	And the body is empty
	 	And the collection "c" has an item count of "2"
	 	When I have a "GET" request to URL "/c/i"
	 	And I accept a "item"
	 	Then the status is "200"
	 	And I receive an "item"
	 	And the body is JSON
	 	And the item "i" is named "i" in collection "c"
	 	When I have a "GET" request to URL "/c/another-i"
	 	And I accept a "item"
	 	Then the status is "200"
	 	And I receive an "item"
	 	And the body is JSON
	 	And the item "another-i" is named "another-i" in collection "c"