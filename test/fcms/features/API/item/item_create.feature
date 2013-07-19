Feature: Creating Items with the REST API

  The system should store valid items into a collection and handle the following scenarios:

  POST
  all good - no slug
  all good - with slug
  all good - unicode in the body
  no accept
  wrong accept
  no content type
  wrong content type
  no charset
  wrong charset
  no body
  body not valid JSON
  collection doesn't exist
  no name in body
  slug specified in body is already used
  slug specified in body is invalid

  PUT
  all good - no slug
  all good - with slug
  all good - unicode in the body
  slug conflicting with URL
  no accept
  wrong accept
  no content header
  wrong content header
  no charset
  wrong charset
  no body
  body, but not valid JSON
  collection doesn't exist
  no name in body
  slug specified in body is already used
  slug specified in body is invalid
  slug specified in URL is invalid

  Background:
    Given I had an empty collection "c"
    And the collection "c" had an item count of 0

  # all good, no slug - 201 Created
  # curl -i --header "Accept: application/vnd.fcms.item+json;version=1" --header "Accept-Charset: utf-8" --header "Content-Type: application/vnd.fcms.item+json;version=1" -X POST -d '{"name":"i"}' http://localhost:3000/c/
  Scenario: Create an item without providing a slug
    When I have a "POST" request to URL "/c/"
    And I provide an "item"
    And I accept an "item"
    And I set the "name" to "i"
    Then the status will be "201"
    And I will receive an "item"
    And the "Location" header will be "/c/i"
    And the body will be JSON
    And the item "i" in collection "c" will be named "i"
    And the collection "c" will have an item count of 1
    When I have a "GET" request to URL "/c/i"
    And I accept an "item"
    Then the status will be "200"
    And I will receive an "item"
    And the body will be JSON
    And the item "i" in collection "c" will be named "i"

  # all good, with slug - 201 Created
  # curl -i --header "Accept: application/vnd.fcms.item+json;version=1" --header "Accept-Charset: utf-8" --header "Content-Type: application/vnd.fcms.item+json;version=1" -X POST -d '{"name":"i", "slug":"another-i"}' http://localhost:3000/c/
  Scenario: Create an item with a provided slug
    When I have a "POST" request to URL "/c/"
    And I provide an "item"
    And I accept an "item"
    And I set the "name" to "i"
    And I set the "slug" to "another-i"
    Then the status will be "201"
    And I will receive an "item"
    And the "Location" header will be "/c/another-i"
    And the body will be JSON
    And the item "another-i" in collection "c" will be named "i"
    And the collection "c" will have an item count of 1
    When I have a "GET" request to URL "/c/another-i"
    And I accept an "item"
    Then the status will be "200"
    And I will receive an "item"
    And the body will be JSON
    And the item "another-i" in collection "c" will be named "i"

  # all good, unicode in the body - 201 Created
  # curl -i --header "Accept: application/vnd.fcms.item+json;version=1" --header "Accept-Charset: utf-8" --header "Content-Type: application/vnd.fcms.item+json;version=1" -X POST -d '{"name":"私はガラスを食", "slug":"i", "description":"er stîget ûf mit grôzer kraft Τη γλώσσα μου έδωσαν ελληνική მივჰხვდე მას ჩემსა الزجاج و هذا لا يؤلمني. मैं काँच खा सकता ฉันกินกระจกได้ לא מזיק Mogę jeść szkło €"}' http://localhost:3000/c/
  Scenario: Create an item containing unicode
    When I have a "POST" request to URL "/c/"
    And I provide an "item"
    And I accept an "item"
    And I set the "name" to "私はガラスを食"
    And I set the "slug" to "i"
    And I set the "description" to "er stîget ûf mit grôzer kraft Τη γλώσσα μου έδωσαν ελληνική მივჰხვდე მას ჩემსა الزجاج و هذا لا يؤلمني. मैं काँच खा सकता ฉันกินกระจกได้ לא מזיק Mogę jeść szkło €"
    Then the status will be "201"
    And I will receive an "item"
    And the "Location" header will be "/c/i"
    And the body will be JSON
    And the item "i" in collection "c" will be named "私はガラスを食"
    And the "description" will be "er stîget ûf mit grôzer kraft Τη γλώσσα μου έδωσαν ελληνική მივჰხვდე მას ჩემსა الزجاج و هذا لا يؤلمني. मैं काँच खा सकता ฉันกินกระจกได้ לא מזיק Mogę jeść szkło €"
    And the collection "c" will have an item count of 1
    When I have a "GET" request to URL "/c/i"
    And I accept an "item"
    Then the status will be "200"
    And I will receive an "item"
    And the body will be JSON
    And the item "i" in collection "c" will be named "私はガラスを食"
    And the "description" will be "er stîget ûf mit grôzer kraft Τη γλώσσα μου έδωσαν ελληνική მივჰხვდე მას ჩემსა الزجاج و هذا لا يؤلمني. मैं काँच खा सकता ฉันกินกระจกได้ לא מזיק Mogę jeść szkło €"

  # no accept type - 201 Created
  # curl -i --header "Accept-Charset: utf-8" --header "Content-Type: application/vnd.fcms.item+json;version=1" -X POST -d '{"name":"i"}' http://localhost:3000/c/
  Scenario: Create an item without an Accept header
    When I have a "POST" request to URL "/c/"
    And I provide an "item"
    And I set the "name" to "i"
    Then the status will be "201"
    And I will receive an "item"
    And the "Location" header will be "/c/i"
    And the body will be JSON
    And the item "i" in collection "c" will be named "i"
    And the collection "c" has an item count of 1
    When I have a "GET" request to URL "/c/i"
    And I accept an "item"
    Then the status will be "200"
    And I will receive an "item"
    And the body will be JSON
    And the item "i" in collection "c" will be named "i"

  # wrong accept type - 406 Not Acceptable
  # curl -i --header "Accept: application/vnd.fcms.collection+json;version=1" --header "Accept-Charset: utf-8" --header "Content-Type: application/vnd.fcms.item+json;version=1" -X POST -d '{"name":"i"}' http://localhost:3000/c/
  Scenario: Attempt to create an item with the wrong Accept type
    When I have a "POST" request to URL "/c/"
    And I provide an "item"
    And I accept a "collection"
    And I set the "name" to "i"
    Then the status will be "406"
    And the "Location" header will not be present
    And the body will be text
    And the body will contain "Acceptable media type: application/vnd.fcms.item+json;version=1"
    And the body will contain "Acceptable charset: utf-8"
    And the collection "c" has an item count of 0
    When I have a "GET" request to URL "/c/i"
    And I accept an "item"
    Then the status will be "404"
    And the body will be empty

  # no content type - 201 Created
  # curl -i --header "Accept: application/vnd.fcms.item+json;version=1" --header "Accept-Charset: utf-8" -X POST -d '{"name":"i"}' http://localhost:3000/c/
  Scenario: Create an item without a Content-Type header
    When I have a "POST" request to URL "/c/"
    And I accept an "item"
    And I set the "name" to "i"
    Then the status will be "201"
    And I will receive an "item"
    And the "Location" header will be "/c/i"
    And the body will be JSON
    And the item "i" in collection "c" will be named "i"
    And the collection "c" has an item count of 1
    When I have a "GET" request to URL "/c/i"
    And I accept an "item"
    Then the status will be "200"
    And I will receive an "item"
    And the body will be JSON
    And the item "i" in collection "c" will be named "i"

  # wrong content type - 415 Unsupported Media Type
  # curl -i --header "Accept: application/vnd.fcms.item+json;version=1" --header "Accept-Charset: utf-8" --header "Content-Type: application/vnd.fcms.collection+json;version=1" -X POST -d '{"name":"i"}' http://localhost:3000/c/
  Scenario: Attempt to create an item with the wrong Content-Type header
    When I have a "POST" request to URL "/c/"
    And I provide a "collection"
    And I accept an "item"
    And I set the "name" to "i"
    Then the status will be "415"
    And the "Location" header will not be present
    And the body will be text
    And the body contents will be "Unsupported media type."
    And the collection "c" has an item count of 0
    When I have a "GET" request to URL "/c/i"
    And I accept an "item"
    Then the status will be "404"
    And the body will be empty

  # no charset - 201 Created
  # curl -i --header "Accept: application/vnd.fcms.item+json;version=1" --header "Content-Type: application/vnd.fcms.item+json;version=1" -X POST -d '{"name":"i"}' http://localhost:3000/c/
  Scenario: Create an item without an Accept-Charset header
    When I have a "POST" request to URL "/c/"
    And I remove the header "Accept-Charset"
    And I provide an "item"
    And I accept an "item"
    And I set the "name" to "i"
    Then the status will be "201"
    And I will receive an "item"
    And the "Location" header will be "/c/i"
    And the body will be JSON
    And the item "i" in collection "c" will be named "i"
    And the collection "c" has an item count of 1
    When I have a "GET" request to URL "/c/i"
    And I accept an "item"
    Then the status will be "200"
    And I will receive an "item"
    And the body will be JSON
    And the item "i" in collection "c" will be named "i"
 
  # wrong charset - 
  # curl -i --header "Accept: application/vnd.fcms.item+json;version=1" --header "Accept-Charset: utf-8" --header "Content-Type: application/vnd.fcms.item+json;version=1" -X POST -d '{"name":"i"}' http://localhost:3000/c/
  Scenario: Attempt to create an item with the wrong Accept-Charset header
    When I have a "POST" request to URL "/c/"
    And I set the "Accept-Charset" header to "iso-8859-1"
    And I provide an "item"
    And I accept an "item"
    And I set the "name" to "i"
    Then the status will be "406"
    And the "Location" header will not be present
    And the body will be text
    And the body will contain "Acceptable media type: application/vnd.fcms.item+json;version=1"
    And the body will contain "Acceptable charset: utf-8"
    And the collection "c" has an item count of 0
    When I have a "GET" request to URL "/c/i"
    And I accept an "item"
    Then the status will be "404"
    And the body will be empty

  # no body - 400 Bad Request
  # curl -i --header "Accept: application/vnd.fcms.item+json;version=1" --header "Content-Type: application/vnd.fcms.item+json;version=1" --header "Charset: UTF-8" -X POST http://localhost:3000/c/
  Scenario: Attempt to create an item with no body
    When I have a "POST" request to URL "/c/"
    And I provide an "item"
    And I accept an "item"
    And I set the "name" to "i"
    And I provide no body
    Then the status will be "400"
    And the body will be text
    And the body contents will be "Bad request."
    And the collection "c" has an item count of 0

  # body, but not valid JSON - 400 Bad Request
  # curl -i --header "Accept: application/vnd.fcms.item+json;version=1" --header "Content-Type: application/vnd.fcms.item+json;version=1" --header "Charset: UTF-8" -X POST -d 'Hi Mom!' http://localhost:3000/c/
  # curl -i --header "Accept: application/vnd.fcms.item+json;version=1" --header "Content-Type: application/vnd.fcms.item+json;version=1" --header "Charset: UTF-8" -X POST -d '{"name":"g' http://localhost:3000/c/
  Scenario: Attempt to create an item with an invalid body
    When I have a "POST" request to URL "/c/"
    And I provide an "item"
    And I accept an "item"
    And I provide the body "Hi Mom!"
    Then the status will be "400"
    And the body will be text
    And the body contents will be "Bad request."
    And the collection "c" has an item count of 0
    When I have a "POST" request to URL "/c/"
    And I provide an "item"
    And I accept an "item"
    And I provide the body "{'name':'i'"
    Then the status will be "400"
    And the body will be text
    And the body contents will be "Bad request."
    And the collection "c" has an item count of 0

  # collection doesn't exist - 404 Not Found
  # curl -i --header "Accept: application/vnd.fcms.item+json;version=1" --header "Content-Type: application/vnd.fcms.item+json;version=1" --header "Charset: UTF-8" -X POST -d '{"name":"i"}' http://localhost:3000/not-here/
  Scenario: Attempt to create an item in a collection that doesn't exist
    When I have a "POST" request to URL "/not-here/"
    And I provide an "item"
    And I accept an "item"
    And I set the "name" to "i"
    Then the status will be "404"
    And the body will be text
    And the body contents will be "Collection not found."
    And the collection "c" has an item count of 0

  # no "name" in body - 422 Unprocessable Entity
  # curl -i --header "Accept: application/vnd.fcms.item+json;version=1" --header "Content-Type: application/vnd.fcms.item+json;version=1" --header "Charset: UTF-8" -X POST -d '{"slug":"i"}' http://localhost:3000/c/
  Scenario: Attempt to create an item without a name
    When I have a "POST" request to URL "/c/"
    And I provide an "item"
    And I accept an "item"
    And I set the "slug" to "i"
    Then the status will be "422"
    And the body will be text
    And the body contents will be "Name is required."
    And the collection "c" has an item count of 0

  # slug specified in body is already used - 422 Unprocessable Entity
  # curl -i --header "Accept: application/vnd.fcms.item+json;version=1" --header "Content-Type: application/vnd.fcms.item+json;version=1" --header "Charset: UTF-8" -X POST -d '{"name":"another-i", "slug":"i"}' http://localhost:3000/c/
  Scenario: Attempt to use a slug that's already used in the collection
    # create an item with slug "i"
    When I have a "POST" request to URL "/c/"
    And I provide an "item"
    And I accept an "item"
    And I set the "name" to "i"
    Then the status will be "201"
    And I will receive an "item"
    And the "Location" header will be "/c/i"
    And the body will be JSON
    And the item "i" in collection "c" will be named "i"
    And the collection "c" has an item count of 1
    When I have a "GET" request to URL "/c/i"
    And I accept an "item"
    Then the status will be "200"
    And I will receive an "item"
    And the body will be JSON
    And the item "i" in collection "c" will be named "i"
    # try to create another item with slug "i"
    When I have a "POST" request to URL "/c/"
    And I provide an "item"
    And I accept an "item"
    And I set the "name" to "another-i"
    And I set the "slug" to "i"
    Then the status will be "422"
    And the body will be text
    And the body contents will be "Slug already used in collection."
    And the collection "c" has an item count of 1

  # slug specified in body is invalid - 422 Unprocessable Entity
  # curl -i --header "Accept: application/vnd.fcms.item+json;version=1" --header "Content-Type: application/vnd.fcms.item+json;version=1" --header "Charset: UTF-8" -X POST -d '{"name":"i", "slug":"I i"}' http://localhost:3000/c/
  Scenario: Attempt to use a slug that's invalid
    When I have a "POST" request to URL "/c/"
    And I provide an "item"
    And I accept an "item"
    And I set the "name" to "i"
    And I set the "slug" to "I i"
    Then the status will be "422"
    And the body will be text
    And the body contents will be "Invalid slug."
    And the collection "c" has an item count of 0