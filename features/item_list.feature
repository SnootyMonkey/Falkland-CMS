Feature: Item list
	
	Scenario: List resources
		Given the system knows about the following items
			|taxonomy				|name				|creator			|URL									|
			|media-type:URL	|Amazon.com	|Jeff Bezos		|http://amazon.com/		|
			|media-type:URL	|Apple 			|Steve Jobs		|http://apple.com/		|
			|media-type:URL	|iPod 			|Jonathan Ives|http://apple.com/ipod|			
		When an API client requests GET /resources
		Then the response should be JSON:
		"""
		[
			{
				"id": any,
				"version": any,
				"name": "Amazon.com",
				"creator": "Jeff Bezos",
				"URL": "http://amazon.com"
			},
			{
				"id": any,
				"version": any,
				"name": "Apple",
				"creator": "Steve Jobs",
				"URL": "http://apple.com/"
			},
			{
				"id": any,
				"version": any,
				"name": "iPod",
				"creator": "Jonathan Ives",
				"URL": "http://apple.com/ipod"
			}
		]
		"""