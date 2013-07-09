# all good
# no accept
# wrong accept
# no accept charset
# wrong accept charset
# collection doesn't exist
# item doesn't exist

# all good - 200 OK

# curl -i --header "Accept: application/vnd.fcms.item+json;version=1" --header "Accept-Charset: utf-8" -X GET http://localhost:3000/vic-20/gorf

# {"name":"gorf","created-at":"","updated-at":"","slug":"gorf","collection":"vic-20","description":"","links":[{"rel":"self","method":"get","href":"/vic-20/gorf","type":"application/vnd.fcms.item+json;charset=utf-8;version=1"},{"rel":"update","method":"put","href":"/vic-20/gorf","type":"application/vnd.fcms.item+json;charset=utf-8;version=1"},{"rel":"delete","method":"delete","href":"/vic-20/gorf"},{"rel":"collection","method":"get","href":"/vic-20","type":"application/vnd.fcms.collection+json"}]}

# no accept - 200 OK

# curl -i --header "Accept-Charset: utf-8" -X GET http://localhost:3000/vic-20/gorf

# {"name":"gorf","created-at":"","updated-at":"","slug":"gorf","collection":"vic-20","description":"","links":[{"rel":"self","method":"get","href":"/vic-20/gorf","type":"application/vnd.fcms.item+json;charset=utf-8;version=1"},{"rel":"update","method":"put","href":"/vic-20/gorf","type":"application/vnd.fcms.item+json;charset=utf-8;version=1"},{"rel":"delete","method":"delete","href":"/vic-20/gorf"},{"rel":"collection","method":"get","href":"/vic-20","type":"application/vnd.fcms.collection+json"}]}

# wrong accept - 406 Not Acceptable

# curl -i --header "Accept: application/vnd.fcms.collection+json;version=1" curl -i --header "Accept-Charset: utf-8" -X GET http://localhost:3000/vic-20/gorf

# Acceptable media type: application/vnd.fcms.item+json;version=1
# Acceptable charset: utf-8

# no accept charset - 200 OK

# curl -i --header "Accept: application/vnd.fcms.item+json;version=1" -X GET http://localhost:3000/vic-20/gorf

# {"name":"gorf","created-at":"","updated-at":"","slug":"gorf","collection":"vic-20","description":"","links":[{"rel":"self","method":"get","href":"/vic-20/gorf","type":"application/vnd.fcms.item+json;charset=utf-8;version=1"},{"rel":"update","method":"put","href":"/vic-20/gorf","type":"application/vnd.fcms.item+json;charset=utf-8;version=1"},{"rel":"delete","method":"delete","href":"/vic-20/gorf"},{"rel":"collection","method":"get","href":"/vic-20","type":"application/vnd.fcms.collection+json"}]}

# wrong accept charset - 406 Not Acceptable

# curl -i --header "Accept: application/vnd.fcms.item+json;version=1" --header "Accept-Charset: iso-8859-1" -X GET http://localhost:3000/vic-20/gorf

# Acceptable media type: application/vnd.fcms.item+json;version=1
# Acceptable charset: utf-8

# collection doesn't exist - 404 Not Found

# curl -i --header "Accept: application/vnd.fcms.item+json;version=1" --header "Accept-Charset: utf-8" -X GET http://localhost:3000/vic-202/gorf

# Collection not found.

# item doesn't exist - 404 Not Found

# curl -i --header "Accept: application/vnd.fcms.item+json;version=1" --header "Accept-Charset: utf-8" -X GET http://localhost:3000/vic-20/gorf2