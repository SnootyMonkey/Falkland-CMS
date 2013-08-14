Feature: Creating Taxonomies with the REST API

  The system should store valid items into a collection and handle the following scenarios:

  POST
  all good - no slug
  all good - generated slug is different than the name
  all good - generated slug is already used
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
  category structure in body is not valid
  collection doesn't exist
  no name in body
  slug specified in body is already used
  slug specified in body is invalid