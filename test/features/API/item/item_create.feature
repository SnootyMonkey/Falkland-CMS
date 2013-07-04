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

# all good, no slug - 201 Created

curl -i --header "Accept: application/vnd.fcms.item+json;version=1" --header "Accept-Charset: utf-8" --header "Content-type: application/vnd.fcms.item+json;version=1" -X POST -d '{"name":"a"}' http://localhost:3000/vic-20/

Location: /vic-20/a
{"name":"a","created-at":"","updated-at":"","slug":"a","collection":"vic-20","description":"","links":[{"rel":"self","method":"get","href":"/vic-20/a","type":"application/vnd.fcms.item+json"},{"rel":"update","method":"put","href":"/vic-20/a","type":"application/vnd.fcms.item+json"},{"rel":"delete","method":"delete","href":"/vic-20/a"},{"rel":"collection","method":"get","href":"/vic-20","type":"application/vnd.fcms.collection+json"}]}

# all good, with slug - 201 Created

curl -i --header "Accept: application/vnd.fcms.item+json;version=1" --header "Accept-Charset: utf-8" --header "Content-type: application/vnd.fcms.item+json;version=1" -X POST -d '{"name":"a", "slug":"another-a"}' http://localhost:3000/vic-20/

Location: /vic-20/another-a
{"name":"a","created-at":"","updated-at":"","slug":"another-a","collection":"vic-20","description":"","links":[{"rel":"self","method":"get","href":"/vic-20/another-a","type":"application/vnd.fcms.item+json"},{"rel":"update","method":"put","href":"/vic-20/another-a","type":"application/vnd.fcms.item+json"},{"rel":"delete","method":"delete","href":"/vic-20/another-a"},{"rel":"collection","method":"get","href":"/vic-20","type":"application/vnd.fcms.collection+json"}]}

# all good, unicode in the body - 201 Created

curl -i --header "Accept: application/vnd.fcms.item+json;version=1" --header "Accept-Charset: utf-8" --header "Content-type: application/vnd.fcms.item+json;version=1" -X POST -d '{"name":"私はガラスを食", "slug":"b", "description":"er stîget ûf mit grôzer kraft Τη γλώσσα μου έδωσαν ελληνική მივჰხვდე მას ჩემსა الزجاج و هذا لا يؤلمني. मैं काँच खा सकता ฉันกินกระจกได้ לא מזיק Mogę jeść szkło €"}' http://localhost:3000/vic-20/

Location: /vic-20/b
{"name":"私はガラスを食","created-at":"","updated-at":"","slug":"b","collection":"vic-20","description":"er stîget ûf mit grôzer kraft Τη γλώσσα μου έδωσαν ελληνική მივჰხვდე მას ჩემსა الزجاج و هذا لا يؤلمني. मैं काँच खा सकता ฉันกินกระจกได้ לא מזיק Mogę jeść szkło €","links":[{"rel":"self","method":"get","href":"/vic-20/b","type":"application/vnd.fcms.item+json;version=1"},{"rel":"update","method":"put","href":"/vic-20/b","type":"application/vnd.fcms.item+json;version=1"},{"rel":"delete","method":"delete","href":"/vic-20/b"},{"rel":"collection","method":"get","href":"/vic-20","type":"application/vnd.fcms.collection+json"}]}

# no accept type - 201 Created

curl -i --header "Accept-Charset: utf-8" --header "Content-type: application/vnd.fcms.item+json;version=1" -X POST -d '{"name":"c"}' http://localhost:3000/vic-20/

Location: /vic-20/c
{"name":"c","created-at":"","updated-at":"","slug":"c","collection":"vic-20","description":"","links":[{"rel":"self","method":"get","href":"/vic-20/c","type":"application/vnd.fcms.item+json;version=1"},{"rel":"update","method":"put","href":"/vic-20/c","type":"application/vnd.fcms.item+json;version=1"},{"rel":"delete","method":"delete","href":"/vic-20/c"},{"rel":"collection","method":"get","href":"/vic-20","type":"application/vnd.fcms.collection+json"}]}

# wrong accept type - 406 Not Acceptable

curl -i --header "Accept: application/vnd.fcms.collection+json;version=1" --header "Accept-Charset: utf-8" --header "Content-type: application/vnd.fcms.item+json;version=1" -X POST -d '{"name":"c"}' http://localhost:3000/vic-20/

Acceptable media type: application/vnd.fcms.item+json
Acceptable char set: UTF-8

# no accept charset, unicode in body - 201 Created

curl -i --header "Accept: application/vnd.fcms.item+json;version=1" --header "Content-type: application/vnd.fcms.item+json;version=1" -X POST -d '{"name":"私はガラスを食", "slug":"d"}' http://localhost:3000/vic-20/

Location: /vic-20/d
{"name":"私はガラスを食","created-at":"","updated-at":"","slug":"d","collection":"vic-20","description":"","links":[{"rel":"self","method":"get","href":"/vic-20/d","type":"application/vnd.fcms.item+json;version=1"},{"rel":"update","method":"put","href":"/vic-20/d","type":"application/vnd.fcms.item+json;version=1"},{"rel":"delete","method":"delete","href":"/vic-20/d"},{"rel":"collection","method":"get","href":"/vic-20","type":"application/vnd.fcms.collection+json"}]}

# wrong accept charset - 406 Not Acceptable

curl -i --header "Accept: application/vnd.fcms.item+json;version=1" --header "Accept-Charset: iso-8859-1" --header "Content-type: application/vnd.fcms.item+json;version=1" -X POST -d '{"name":"e"}' http://localhost:3000/vic-20/

Acceptable media type: application/vnd.fcms.item+json
Acceptable char set: UTF-8

# no content type - 415 Unsupported Media Type

curl -i --header "Accept: application/vnd.fcms.item+json;version=1" --header "Accept-Charset: utf-8" -X POST -d '{"name":"f"}' http://localhost:3000/vic-20/

# wrong content type - 415 Unsupported Media Type

curl -i --header "Accept: application/vnd.fcms.item+json;version=1" --header "Accept-Charset: utf-8" --header "Content-type: application/vnd.fcms.collection+json;version=1" -X POST -d '{"name":"g"}' http://localhost:3000/vic-20/

# no body - 400 Bad Request

curl -i --header "Accept: application/vnd.fcms.item+json;version=1" --header "Content-type: application/vnd.fcms.item+json;version=1" --header "Charset: UTF-8" -X POST http://localhost:3000/vic-20/

# body, but not valid JSON - 400 Bad Request

curl -i --header "Accept: application/vnd.fcms.item+json;version=1" --header "Content-type: application/vnd.fcms.item+json;version=1" --header "Charset: UTF-8" -X POST -d 'Hi Mom!' http://localhost:3000/vic-20/

curl -i --header "Accept: application/vnd.fcms.item+json;version=1" --header "Content-type: application/vnd.fcms.item+json;version=1" --header "Charset: UTF-8" -X POST -d '{"name":"g' http://localhost:3000/vic-20/

# collection doesn't exist - 404 Not Found

curl -i --header "Accept: application/vnd.fcms.item+json;version=1" --header "Content-type: application/vnd.fcms.item+json;version=1" --header "Charset: UTF-8" -X POST -d '{"name":"h"}' http://localhost:3000/vic-202/

Collection not found.

# no "name" in body - 422 Unprocessable Entity

curl -i --header "Accept: application/vnd.fcms.item+json;version=1" --header "Content-type: application/vnd.fcms.item+json;version=1" --header "Charset: UTF-8" -X POST -d '{"foo":"h"}' http://localhost:3000/vic-20/

Name is required.

# slug specified in body is already used - 422 Unprocessable Entity

curl -i --header "Accept: application/vnd.fcms.item+json;version=1" --header "Content-type: application/vnd.fcms.item+json;version=1" --header "Charset: UTF-8" -X POST -d '{"name":"a", "slug":"a"}' http://localhost:3000/vic-20/

Slug already used in collection.

# slug specified in body is invalid - 422 Unprocessable Entity

curl -i --header "Accept: application/vnd.fcms.item+json;version=1" --header "Content-type: application/vnd.fcms.item+json;version=1" --header "Charset: UTF-8" -X POST -d '{"name":"a", "slug":"a a"}' http://localhost:3000/vic-20/

Invalid slug.
