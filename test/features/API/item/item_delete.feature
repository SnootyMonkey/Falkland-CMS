# all good
# item doesn't exist
# collection doesn't exist

# all good - 204 No Content

curl -i -X DELETE http://localhost:3000/vic-20/a

# collection doesn't exist - 404 Not Found

curl -X DELETE http://localhost:3000/vic-202/a

Collection not found.

# item doesn't exist - 404 Not Found

curl -X DELETE http://localhost:3000/vic-20/not-me