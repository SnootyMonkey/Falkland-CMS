# all good - no slug
# all good - with same slug
# all good - with different slug
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
# item doesn't exist
# no "name" in body
# different slug specified in body is already used
# different slug specified in body is invalid

# all good, no slug

curl -i --header "Accept: application/vnd.fcms.item+json;version=1" --header "Accept-Charset: utf-8" --header "Content-type: application/vnd.fcms.item+json;version=1" -X PUT -d '{"name":"a1", "description":"a1", "a1":"a1"}' http://localhost:3000/vic-20/a

# all good - with same slug

curl -i --header "Accept: application/vnd.fcms.item+json;version=1" --header "Accept-Charset: utf-8" --header "Content-type: application/vnd.fcms.item+json;version=1" -X PUT -d '{"name":"a2", "slug":"a", description":"a2", "a2":"a2"}' http://localhost:3000/vic-20/a

# all good - with different slug

curl -i --header "Accept: application/vnd.fcms.item+json;version=1" --header "Accept-Charset: utf-8" --header "Content-type: application/vnd.fcms.item+json;version=1" -X PUT -d '{"name":"a3", "slug":"a3", description":"a3", "a3":"a3"}' http://localhost:3000/vic-20/a

# all good - unicode in the body

curl -i --header "Accept: application/vnd.fcms.item+json;version=1" --header "Accept-Charset: utf-8" --header "Content-type: application/vnd.fcms.item+json;version=1" -X PUT -d '{"name":"私はガラスを食", "description":"er stîget ûf mit grôzer kraft Τη γλώσσα μου έδωσαν ελληνική მივჰხვდე მას ჩემსა الزجاج و هذا لا يؤلمني. मैं काँच खा सकता ฉันกินกระจกได้ לא מזיק Mogę jeść szkło €", "a5":"a5"}' http://localhost:3000/vic-20/a

# no accept type

curl -i --header "Accept-Charset: utf-8" --header "Content-type: application/vnd.fcms.item+json;version=1" -X PUT -d '{"name":"a6", "description":"a6", "a6":"a6"}' http://localhost:3000/vic-20/a

# wrong accept type - 406 Not Acceptable

curl -i --header "Accept: application/vnd.fcms.collection+json;version=1" --header "Accept-Charset: utf-8" --header "Content-type: application/vnd.fcms.item+json;version=1" -X PUT -d '{"name":"a7", "description":"a7", "a7":"a7"}' http://localhost:3000/vic-20/a

# no content-type header

curl -i --header "Accept: application/vnd.fcms.item+json;version=1" --header "Accept-Charset: utf-8" -X PUT -d '{"name":"a8", "description":"a8", "a8":"a8"}' http://localhost:3000/vic-20/a

# wrong content-type header - 406 Not Acceptable

curl -i --header "Accept: application/vnd.fcms.item+json;version=1" --header "Accept-Charset: utf-8" --header "Content-type: application/vnd.fcms.collection+json;version=1" -X PUT -d '{"name":"a9", "description":"a9", "a9":"a9"}' http://localhost:3000/vic-20/a

# no charset

curl -i --header "Accept: application/vnd.fcms.item+json;version=1" --header "Content-type: application/vnd.fcms.item+json;version=1" -X PUT -d '{"name":"a10", "description":"a10", "a10":"a10"}' http://localhost:3000/vic-20/a

# wrong charset - 406 Not Acceptable

curl -i --header "Accept: application/vnd.fcms.item+json;version=1" --header "Accept-Charset: iso-8859-1" --header "Content-type: application/vnd.fcms.item+json;version=1" -X PUT -d '{"name":"a11", "description":"a11", "a11":"a11"}' http://localhost:3000/vic-20/a

# no body - 400 Bad Request

curl -i --header "Accept: application/vnd.fcms.item+json;version=1" --header "Accept-Charset: utf-8" --header "Content-type: application/vnd.fcms.item+json;version=1" -X PUT http://localhost:3000/vic-20/a

# body, but not valid JSON - 400 Bad Request

curl -i --header "Accept: application/vnd.fcms.item+json;version=1" --header "Accept-Charset: utf-8" --header "Content-type: application/vnd.fcms.item+json;version=1" -X PUT -d 'Hi Mom!' http://localhost:3000/vic-20/a

curl -i --header "Accept: application/vnd.fcms.item+json;version=1" --header "Accept-Charset: utf-8" --header "Content-type: application/vnd.fcms.item+json;version=1" -X PUT -d '{"name":"a12"' http://localhost:3000/vic-20/a

# collection doesn't exist - 404 Not Found

curl -i --header "Accept: application/vnd.fcms.item+json;version=1" --header "Accept-Charset: utf-8" --header "Content-type: application/vnd.fcms.item+json;version=1" -X PUT -d '{"name":"a13", "description":"a13", "a13":"a13"}' http://localhost:3000/vic-202/a

Collection not found.

# item doesn't exist

curl -i --header "Accept: application/vnd.fcms.item+json;version=1" --header "Accept-Charset: utf-8" --header "Content-type: application/vnd.fcms.item+json;version=1" -X PUT -d '{"name":"a14", "description":"a14", "a14":"a14"}' http://localhost:3000/vic-202/not-me

# no "name" in body

curl -i --header "Accept: application/vnd.fcms.item+json;version=1" --header "Accept-Charset: utf-8" --header "Content-type: application/vnd.fcms.item+json;version=1" -X PUT -d '{description":"a15", "a15":"a15"}' http://localhost:3000/vic-202/a

# different slug specified in body is already used

curl -i --header "Accept: application/vnd.fcms.item+json;version=1" --header "Accept-Charset: utf-8" --header "Content-type: application/vnd.fcms.item+json;version=1" -X PUT -d '{"name":"a16", "slug":"a3", description":"a16", "a16":"a16"}' http://localhost:3000/vic-20/a

# different slug specified in body is invalid

curl -i --header "Accept: application/vnd.fcms.item+json;version=1" --header "Accept-Charset: utf-8" --header "Content-type: application/vnd.fcms.item+json;version=1" -X PUT -d '{"name":"a17", "slug":"a a", description":"a17", "a17":"a17"}' http://localhost:3000/vic-20/a