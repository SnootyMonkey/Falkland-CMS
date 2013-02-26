Feature: Item list
	
	Scenario: List resources
		Given the system knows about the following items
			|name				|creator			|URL									|
			|Amazon.com	|Jeff Bezos		|http://amazon.com/		|
			|Apple 			|Steve Jobs		|http://apple.com/		|
			|iPod 			|Jonathan Ives|http://apple.com/ipod|			
		When an API client requests GET /resources
		Then the response should be JSON:
		"""
		[
			{
				"name": "Amazon.com",
				"creator": "Jeff Bezos",
				"URL": "http://amazon.com"
			},
			{
				"name": "Apple",
				"creator": "Steve Jobs",
				"URL": "http://apple.com/"
			},
			{
				"name": "iPod",
				"creator": "Jonathan Ives",
				"URL": "http://apple.com/ipod"
			}
		]
		"""