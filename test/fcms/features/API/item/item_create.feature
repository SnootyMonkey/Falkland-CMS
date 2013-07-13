Feature: Creating Items

  The system should store valid items into a collection.

  # all good - no slug
  # all good - with slug
  # all good - unicode in the body
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

  Background:
    Given I have a collection "c" with no items
    Then the collection "c" has an item count of "0"

  # all good, no slug - 201 Created
  # curl -i --header "Accept: application/vnd.fcms.item+json;version=1" --header "Accept-Charset: utf-8" --header "Content-type: application/vnd.fcms.item+json;version=1" -X POST -d '{"name":"i"}' http://localhost:3000/c/
  Scenario: Create a valid item with no slug
    When I have a "item" "POST" request with URL "/c/"
    And I set the "name" to "i"
    Then the status is "201"
    And the "Location" header is "/c/i"
    And the item is "i" named "i" in collection "c"
    And the collection "c" has an item count of "1"
    When I have a "item" "GET" request with URL "/c/i"
    Then the status is "200"
    And the item is "i" named "i" in collection "c"

  # all good, with slug - 201 Created
  # curl -i --header "Accept: application/vnd.fcms.item+json;version=1" --header "Accept-Charset: utf-8" --header "Content-type: application/vnd.fcms.item+json;version=1" -X POST -d '{"name":"i", "slug":"another-i"}' http://localhost:3000/c/
  Scenario: Create a valid item with a slug
    When I have a "item" "POST" request with URL "/c/"
    And I set the "name" to "i"
    And I set the "slug" to "another-i"
    Then the status is "201"
    And the "Location" header is "/c/another-i"
    And the item is "another-i" named "i" in collection "c"
    And the collection "c" has an item count of "1"
    When I have a "item" "GET" request with URL "/c/another-i"
    Then the status is "200"
    And the item is "another-i" named "i" in collection "c"

  # all good, unicode in the body - 201 Created
  # curl -i --header "Accept: application/vnd.fcms.item+json;version=1" --header "Accept-Charset: utf-8" --header "Content-type: application/vnd.fcms.item+json;version=1" -X POST -d '{"name":"私はガラスを食", "slug":"i", "description":"er stîget ûf mit grôzer kraft Τη γλώσσα μου έδωσαν ελληνική მივჰხვდე მას ჩემსა الزجاج و هذا لا يؤلمني. मैं काँच खा सकता ฉันกินกระจกได้ לא מזיק Mogę jeść szkło €"}' http://localhost:3000/c/
  Scenario: Create a valid item containing unicode
    When I have a "item" "POST" request with URL "/c/"
    And I set the "name" to "私はガラスを食"
    And I set the "slug" to "i"
    And I set the "description" to "er stîget ûf mit grôzer kraft Τη γλώσσα μου έδωσαν ελληνική მივჰხვდე მას ჩემსა الزجاج و هذا لا يؤلمني. मैं काँच खा सकता ฉันกินกระจกได้ לא מזיק Mogę jeść szkło €"
    Then the status is "201"
    And the "Location" header is "/c/i"
    And the item is "i" named "私はガラスを食" in collection "c"
    And the "description" is "er stîget ûf mit grôzer kraft Τη γλώσσα μου έδωσαν ελληνική მივჰხვდე მას ჩემსა الزجاج و هذا لا يؤلمني. मैं काँच खा सकता ฉันกินกระจกได้ לא מזיק Mogę jeść szkło €"
    And the collection "c" has an item count of "1"
    When I have a "item" "GET" request with URL "/c/i"
    Then the status is "200"
    And the item is "i" named "私はガラスを食" in collection "c"
    And the "description" is "er stîget ûf mit grôzer kraft Τη γλώσσα μου έδωσαν ελληνική მივჰხვდე მას ჩემსა الزجاج و هذا لا يؤلمني. मैं काँच खा सकता ฉันกินกระจกได้ לא מזיק Mogę jeść szkło €"


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








# no accept type - 201 Created

# curl -i --header "Accept-Charset: utf-8" --header "Content-type: application/vnd.fcms.item+json;version=1" -X POST -d '{"name":"c"}' http://localhost:3000/vic-20/

# Location: /vic-20/c
# {"name":"c","created-at":"","updated-at":"","slug":"c","collection":"vic-20","description":"","links":[{"rel":"self","method":"get","href":"/vic-20/c","type":"application/vnd.fcms.item+json;version=1"},{"rel":"update","method":"put","href":"/vic-20/c","type":"application/vnd.fcms.item+json;version=1"},{"rel":"delete","method":"delete","href":"/vic-20/c"},{"rel":"collection","method":"get","href":"/vic-20","type":"application/vnd.fcms.collection+json"}]}

# wrong accept type - 406 Not Acceptable

# curl -i --header "Accept: application/vnd.fcms.collection+json;version=1" --header "Accept-Charset: utf-8" --header "Content-type: application/vnd.fcms.item+json;version=1" -X POST -d '{"name":"c"}' http://localhost:3000/vic-20/

# Acceptable media type: application/vnd.fcms.item+json
# Acceptable char set: UTF-8

# no accept charset, unicode in body - 201 Created

# curl -i --header "Accept: application/vnd.fcms.item+json;version=1" --header "Content-type: application/vnd.fcms.item+json;version=1" -X POST -d '{"name":"私はガラスを食", "slug":"d"}' http://localhost:3000/vic-20/

# Location: /vic-20/d
# {"name":"私はガラスを食","created-at":"","updated-at":"","slug":"d","collection":"vic-20","description":"","links":[{"rel":"self","method":"get","href":"/vic-20/d","type":"application/vnd.fcms.item+json;version=1"},{"rel":"update","method":"put","href":"/vic-20/d","type":"application/vnd.fcms.item+json;version=1"},{"rel":"delete","method":"delete","href":"/vic-20/d"},{"rel":"collection","method":"get","href":"/vic-20","type":"application/vnd.fcms.collection+json"}]}

# wrong accept charset - 406 Not Acceptable

# curl -i --header "Accept: application/vnd.fcms.item+json;version=1" --header "Accept-Charset: iso-8859-1" --header "Content-type: application/vnd.fcms.item+json;version=1" -X POST -d '{"name":"e"}' http://localhost:3000/vic-20/

# Acceptable media type: application/vnd.fcms.item+json
# Acceptable char set: UTF-8

# no content type - 415 Unsupported Media Type

# curl -i --header "Accept: application/vnd.fcms.item+json;version=1" --header "Accept-Charset: utf-8" -X POST -d '{"name":"f"}' http://localhost:3000/vic-20/

# wrong content type - 415 Unsupported Media Type

# curl -i --header "Accept: application/vnd.fcms.item+json;version=1" --header "Accept-Charset: utf-8" --header "Content-type: application/vnd.fcms.collection+json;version=1" -X POST -d '{"name":"g"}' http://localhost:3000/vic-20/

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
