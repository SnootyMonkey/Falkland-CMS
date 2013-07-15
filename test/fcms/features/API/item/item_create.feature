Feature: Creating Items

  The system should store valid items into a collection and handle the following scenarios:

  all good - no slug
  all good - with slug
  all good - unicode in the body
  no accept
  wrong accept
  no content header
  wrong content header
  no charset
  wrong charset
  no body
  body not valid JSON
  collection doesn't exist
  no name in body
  slug specified in body is already used
  slug specified in body is invalid

  Background:
    Given I have a collection "c" with no items
    Then the collection "c" has an item count of "0"

  # all good, no slug - 201 Created
  # curl -i --header "Accept: application/vnd.fcms.item+json;version=1" --header "Accept-Charset: utf-8" --header "Content-type: application/vnd.fcms.item+json;version=1" -X POST -d '{"name":"i"}' http://localhost:3000/c/
  Scenario: Create a valid item with no slug
    When I have a "POST" request to URL "/c/"
    And I provide a "item"
    And I accept a "item"
    And I set the "name" to "i"
    Then the status is "201"
    And the "Location" header is "/c/i"
    And the body is JSON
    And the item is "i" named "i" in collection "c"
    And the collection "c" has an item count of "1"
    When I have a "GET" request to URL "/c/i"
    And I accept a "item"
    Then the status is "200"
    And the body is JSON
    And the item is "i" named "i" in collection "c"

  # all good, with slug - 201 Created
  # curl -i --header "Accept: application/vnd.fcms.item+json;version=1" --header "Accept-Charset: utf-8" --header "Content-type: application/vnd.fcms.item+json;version=1" -X POST -d '{"name":"i", "slug":"another-i"}' http://localhost:3000/c/
  Scenario: Create a valid item with a slug
    When I have a "POST" request to URL "/c/"
    And I provide a "item"
    And I accept a "item"
    And I set the "name" to "i"
    And I set the "slug" to "another-i"
    Then the status is "201"
    And the "Location" header is "/c/another-i"
    And the body is JSON
    And the item is "another-i" named "i" in collection "c"
    And the collection "c" has an item count of "1"
    When I have a "GET" request to URL "/c/another-i"
    And I accept a "item"
    Then the status is "200"
    And the body is JSON
    And the item is "another-i" named "i" in collection "c"

  # all good, unicode in the body - 201 Created
  # curl -i --header "Accept: application/vnd.fcms.item+json;version=1" --header "Accept-Charset: utf-8" --header "Content-type: application/vnd.fcms.item+json;version=1" -X POST -d '{"name":"私はガラスを食", "slug":"i", "description":"er stîget ûf mit grôzer kraft Τη γλώσσα μου έδωσαν ελληνική მივჰხვდე მას ჩემსა الزجاج و هذا لا يؤلمني. मैं काँच खा सकता ฉันกินกระจกได้ לא מזיק Mogę jeść szkło €"}' http://localhost:3000/c/
  Scenario: Create a valid item containing unicode
    When I have a "POST" request to URL "/c/"
    And I provide a "item"
    And I accept a "item"
    And I set the "name" to "私はガラスを食"
    And I set the "slug" to "i"
    And I set the "description" to "er stîget ûf mit grôzer kraft Τη γλώσσα μου έδωσαν ελληνική მივჰხვდე მას ჩემსა الزجاج و هذا لا يؤلمني. मैं काँच खा सकता ฉันกินกระจกได้ לא מזיק Mogę jeść szkło €"
    Then the status is "201"
    And the "Location" header is "/c/i"
    And the body is JSON
    And the item is "i" named "私はガラスを食" in collection "c"
    And the "description" is "er stîget ûf mit grôzer kraft Τη γλώσσα μου έδωσαν ελληνική მივჰხვდე მას ჩემსა الزجاج و هذا لا يؤلمني. मैं काँच खा सकता ฉันกินกระจกได้ לא מזיק Mogę jeść szkło €"
    And the collection "c" has an item count of "1"
    When I have a "GET" request to URL "/c/i"
    And I accept a "item"
    Then the status is "200"
    And the body is JSON
    And the item is "i" named "私はガラスを食" in collection "c"
    And the "description" is "er stîget ûf mit grôzer kraft Τη γλώσσα μου έδωσαν ελληνική მივჰხვდე მას ჩემსა الزجاج و هذا لا يؤلمني. मैं काँच खा सकता ฉันกินกระจกได้ לא מזיק Mogę jeść szkło €"

  # no accept type - 201 Created
  # curl -i --header "Accept-Charset: utf-8" --header "Content-type: application/vnd.fcms.item+json;version=1" -X POST -d '{"name":"i"}' http://localhost:3000/c/
  Scenario: Create a valid item without using an Accept header
    When I have a "POST" request to URL "/c/"
    And I provide a "item"
    And I set the "name" to "i"
    Then the status is "201"
    And the "Location" header is "/c/i"
    And the body is JSON
    And the item is "i" named "i" in collection "c"
    And the collection "c" has an item count of "1"
    When I have a "GET" request to URL "/c/i"
    And I accept a "item"
    Then the status is "200"
    And the body is JSON
    And the item is "i" named "i" in collection "c"

  # wrong accept type - 406 Not Acceptable
  # curl -i --header "Accept: application/vnd.fcms.collection+json;version=1" --header "Accept-Charset: utf-8" --header "Content-type: application/vnd.fcms.item+json;version=1" -X POST -d '{"name":"i"}' http://localhost:3000/c/
  Scenario: Attempt to create an item with the wrong Accept type
    When I have a "POST" request to URL "/c/"
    And I provide a "item"
    And I accept a "collection"
    And I set the "name" to "i"
    Then the status is "406"
    And the "Location" header is not present
    And the body is text
    And the body contains "Acceptable media type: application/vnd.fcms.item+json;version=1"
    And the body contains "Acceptable char set: utf-8"
    And the collection "c" has an item count of "0"
    When I have a "GET" request to URL "/c/i"
    And I accept a "item"
    Then the status is "404"
    And the body is empty

  # no content type - 415 Unsupported Media Type
  # curl -i --header "Accept: application/vnd.fcms.item+json;version=1" --header "Accept-Charset: utf-8" -X POST -d '{"name":"i"}' http://localhost:3000/c/
  Scenario: Create a valid item without using a Content-type header
    When I have a "POST" request to URL "/c/"
    And I accept a "item"
    And I set the "name" to "i"
    Then the status is "415"
    And the "Location" header is not present
    And the body is text
    And the body is "Unsupported media type."
    And the collection "c" has an item count of "0"
    When I have a "GET" request to URL "/c/i"
    And I accept a "item"
    Then the status is "404"
    And the body is empty

  # wrong content type - 415 Unsupported Media Type
  # curl -i --header "Accept: application/vnd.fcms.item+json;version=1" --header "Accept-Charset: utf-8" --header "Content-type: application/vnd.fcms.collection+json;version=1" -X POST -d '{"name":"i"}' http://localhost:3000/c/
  Scenario: Attempt to create an item with the wrong Accept type
    When I have a "POST" request to URL "/c/"
    And I provide a "collection"
    And I accept a "item"
    And I set the "name" to "i"
    Then the status is "415"
    And the "Location" header is not present
    And the body is text
    And the body is "Unsupported media type."
    And the collection "c" has an item count of "0"
    When I have a "GET" request to URL "/c/i"
    And I accept a "item"
    Then the status is "404"
    And the body is empty

# no body - 400 Bad Request

# curl -i --header "Accept: application/vnd.fcms.item+json;version=1" --header "Content-type: application/vnd.fcms.item+json;version=1" --header "Charset: UTF-8" -X POST http://localhost:3000/vic-20/

# body, but not valid JSON - 400 Bad Request

# curl -i --header "Accept: application/vnd.fcms.item+json;version=1" --header "Content-type: application/vnd.fcms.item+json;version=1" --header "Charset: UTF-8" -X POST -d 'Hi Mom!' http://localhost:3000/vic-20/

# curl -i --header "Accept: application/vnd.fcms.item+json;version=1" --header "Content-type: application/vnd.fcms.item+json;version=1" --header "Charset: UTF-8" -X POST -d '{"name":"g' http://localhost:3000/vic-20/

# collection doesn't exist - 404 Not Found

# curl -i --header "Accept: application/vnd.fcms.item+json;version=1" --header "Content-type: application/vnd.fcms.item+json;version=1" --header "Charset: UTF-8" -X POST -d '{"name":"h"}' http://localhost:3000/vic-202/

# Collection not found.

# no "name" in body - 422 Unprocessable Entity

# curl -i --header "Accept: application/vnd.fcms.item+json;version=1" --header "Content-type: application/vnd.fcms.item+json;version=1" --header "Charset: UTF-8" -X POST -d '{"foo":"h"}' http://localhost:3000/vic-20/

# Name is required.

# slug specified in body is already used - 422 Unprocessable Entity

# curl -i --header "Accept: application/vnd.fcms.item+json;version=1" --header "Content-type: application/vnd.fcms.item+json;version=1" --header "Charset: UTF-8" -X POST -d '{"name":"a", "slug":"a"}' http://localhost:3000/vic-20/

# Slug already used in collection.

# slug specified in body is invalid - 422 Unprocessable Entity

# curl -i --header "Accept: application/vnd.fcms.item+json;version=1" --header "Content-type: application/vnd.fcms.item+json;version=1" --header "Charset: UTF-8" -X POST -d '{"name":"a", "slug":"a a"}' http://localhost:3000/vic-20/

# Invalid slug.

# PUT
# all good - no slug
# all good - with slug
# all good - unicode in the body
# slug conflicting with URL
# no accept
# wrong accept
# no content header
# wrong content header
# no charset
# wrong charset
# no body
# body, but not valid JSON
# collection doesn't exist
# no "name" in body
# slug specified in body is already used
# slug specified in body is invalid
# slug specified in URL is invalid