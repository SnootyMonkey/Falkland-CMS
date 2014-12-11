---
layout: default
title: Falkland CMS - Command-line UI
author: Sean Johnson
---

## Falkland CMS Command-line Interface

A usable initial version of the Falkland CMS command-line UI is expected sometime in 2015.

## Usage

You can invoke the command-line interface with no arguments to use the interactive mode.

```console
falkland
```

Falkland CMS will respond with the menu of options.

```console
Welcome to Falkland CMS v0.2-SNAPSHOT!

Please choose an option:
1) Change current collection: Mudskippers
2) Add a new item
3) Search for items
4) Configure Falkland CMS
q) Quit

>
```

Chose an option from the resulting menu by typing in the option's number.

### Changing the current collection

```console
Choose a collection:
  1) Camus
* 2) Mudskippers
  3) Pitiful Pirates

  a) Add a new collection
  q) Return to the main menu

> 1

Choose a collection:
* 1) Camus
  2) Mudskippers
  3) Pitiful Pirates

  a) Add a new collection
  q) Return to the main menu

> 
```

### Adding a collection

When you add a collection you're presented with a menu to fill in the collection's attributes.

```console
Choose a collection:
* 1) Camus
  2) Mudskippers
  3) Pitiful Pirates

  a) Add a new collection
  q) Return to the main menu

> a

Add a collection. Please choose an option:
1) Name*:
2) Slug*:

a) Add the collection
q) Cancel and return to the collection menu

> 1
Name: Jack Freeman's Library

Add a collection. Please choose an option:
1) Name*: Jack Freeman's Library
2) Slug*:

a) Add the collection
q) Cancel and return to the main menu

> 2
Slug: jack

Add a collection. Please choose an option:
1) Name*: Jack Freeman's Library
2) Slug*: jack

a) Add the collection
q) Cancel and return to the collection menu

> a

Choose a collection:
* 1) Camus
  2) Mudskippers
  3) Pitiful Pirates
  4) Jack Freeman's Library

  a) Add a new collection
  q) Return to the main menu

>
```

### Adding an item

When you add an item you're presented with a menu to fill in the item's attributes.

```console
Welcome to Falkland CMS v0.1!

Please choose an option:
1) Change current collection: Mudskippers
2) Add a new item
3) Search for items
4) Configure Falkland CMS
q) Quit

> 2

Add an item. Please choose an option:
1) Name*:
2) Slug*:
3) URL*:
4) Creator:
5) Taxonomy - media-type:
6) Taxonomy - topic:

a) Add the item
q) Cancel and return to the main menu

>
```

Select the number of the attribute you wish to populate.

```console
> 1
Name: Amazing animals - Mudskipper

Add an item. Please choose an option:
1) Name*: Amazing animals - Mudskipper
2) Slug*: amazing-animals-mudskipper
3) URL*:
4) Creator:
5) Taxonomy - media-type:
6) Taxonomy - topic:

a) Add the item
q) Cancel and return to the main menu

>
```

You can select one or more categories in each taxonomy you've defined in the system. Select an already selected category to remove the categorization. You can select items at the same time with commas (e.g. 2,5,11) or with ranges (2-5, 11).

```console
Taxonomy - media-type

  1) Web Page
  2) -> PDF
  3) Video
  4) -> Online
  5) -> DVD
  6) -> VHS
  7) Book
  8) -> Hardcover
  9) -> Paperback
  10) -> eBook
  11) --> DRM protected
  12) --> DRM free

  a) Add the categorization
  q) Cancel and return to the main menu

> 10

Taxonomy - media-type

  1) Web Page
  2) -> PDF
  3) Video
  4) -> Online
  5) -> DVD
  6) -> VHS
* 7) Book
  8) -> Hardcover
  9) -> Paperback
* 10) -> eBook
* 11) --> DRM protected
  12) --> DRM free

  a) Add the categorization
  q) Cancel and return to the main menu

> 10,11

  1) Web Page
  2) -> PDF
  3) Video
  4) -> Online
  5) -> DVD
  6) -> VHS
* 7) Book
  8) -> Hardcover
  9) -> Paperback
  10) -> eBook
  11) --> DRM protected
  12) --> DRM free

  a) Add the categorization
  q) Cancel and return to the main menu

> a
```

### Searching for items

You can search for items in the system by any of the attributes of the time. The interface is very similar to how you add an item, except you are specifying the partial information by which to search.

```console
Welcome to Falkland CMS v0.1!

Please choose an option:
1) Change current collection: Mudskippers
2) Add a new item
3) Search for items
4) Configure Falkland CMS
q) Quit

> 3

Find items. Please choose an option:
1) Name:
2) URL:
3) Creator:
4) Taxonomy - media-type:
5) Taxonomy - topic:

s) Search for matching items
q) Cancel and return to the main menu

> 1
Name: mud

Find items. Please choose an option:
1) Name: mud
2) URL:
3) Creator:
4) Taxonomy - media-type:
5) Taxonomy - topic:

s) Search for matching items
q) Cancel and return to the main menu

> s

2 items found.

1) Name: Amazing animals - Mudskipper
	 URL: https://www.youtube.com/watch?v=KurTiX4FDuQ
	 Creator: BBC Life episode

2) Name: High and Dry
	 URL: http://video.nationalgeographic.com/video/kids/animals-pets-kids/fish-kids/mudskipper-kids/
	 Creater: National Geographic

q) Return to the search menu

>
```

Once an item is found, you can select it to update it. Again, the interface to update is similar to adding a new item.

```console
2 items found.

1) Name: Amazing animals - Mudskipper
	 URL: https://www.youtube.com/watch?v=KurTiX4FDuQ
	 Creator: BBC Life episode

2) Name: High and Dry
	 URL: http://video.nationalgeographic.com/video/kids/animals-pets-kids/fish-kids/mudskipper-kids/
	 Creater: National Geographic

q) Return to the search menu

> 1

Update an item. Please choose an option:
1) Name*: Amazing animals - Mudskipper
2) URL*: https://www.youtube.com/watch?v=KurTiX4FDuQ
3) Creator: BBC Life episode
4) Taxonomy - media-type: video, video:online
5) Taxonomy - topic: fish, fish:mudskipper

u) Update the item
q) Cancel and return to the search results

>
```

### Listing items

You can list all the items in the system with a blank search.

```console
Find items. Please choose an option:
1) Name:
2) URL:
3) Creator:
4) Taxonomy - media-type:
5) Taxonomy - topic:

s) Search for matching items
q) Cancel and return to the main menu

> s
```

You can list all the items in a particular category by selecting only that category.

```console
Find items. Please choose an option:
1) Name:
2) URL:
3) Creator:
4) Taxonomy - media-type:
5) Taxonomy - topic:

s) Search for matching items
q) Cancel and return to the main menu

> 4

Taxonomy - media-type

  1) Web Page
  2) -> PDF
  3) Video
  4) -> Online
  5) -> DVD
  6) -> VHS
  7) Book
  8) -> Hardcover
  9) -> Paperback
  10) -> eBook
  11) --> DRM protected
  12) --> DRM free

  s) Select these categories and return to the main menu
  q) Cancel and return to the main menu

> 4

Taxonomy - media-type

  1) Web Page
  2) -> PDF
* 3) Video
* 4) -> Online
  5) -> DVD
  6) -> VHS
  7) Book
  8) -> Hardcover
  9) -> Paperback
  10) -> eBook
  11) --> DRM protected
  12) --> DRM free

  s) Select these categories and return to the main menu
  q) Cancel and return to the main menu

> s

Find items. Please choose an option:
1) Name:
2) URL:
3) Creator:
4) Taxonomy - media-type: video, video:online
5) Taxonomy - topic:

s) Search for matching items
q) Cancel and return to the main menu

> s
```

### Configure the taxonomies

You can add a new taxonomy to the system.

```console
Welcome to Falkland CMS v0.1!

Please choose an option:
1) Change current collection: Mudskippers
2) Add a new item
3) Search for items
4) Configure Falkland CMS
q) Quit

> 4

Please choose an option:
1) Taxonomy - media-type
2) Taxonomy - topic

a) Add a new taxonomy
q) Return to the main menu

> 1

Add an taxonomy. Please choose an option:
1) Name:
2) Slug*:

	 Categories:

c) Add a category to the taxonomy
a) Add the taxonomy
q) Cancel and return to the main menu

> 1
Name: Collection Status

Add an taxonomy. Please choose an option:
1) Name: Collection Status
2) Slug*:

	 Categories:

c) Add a category to the taxonomy
a) Add the taxonomy
q) Cancel and return to the main menu

> 2
Slug: collection-status

Add an taxonomy. Please choose an option:
1) Name: Collection Status
2) Slug*: collection-status

	 Categories:

c) Add a category to the taxonomy
a) Add the taxonomy
q) Cancel and return to the main menu

> a

Add a category. Please choose an option:
  1) Name: 
  2) Slug*:

  a) Add the category
  q) Cancel and return to the taxonomy menu

> 2
Slug: owned

Add a category. Please choose an option:
  1) Name: 
  2) Slug*: owned

  a) Add the category
  q) Cancel and return to the taxonomy menu

  > a

Add an taxonomy. Please choose an option:
1) Name: Collection Status
2) Slug*:

	 Categories:
3) owned

c) Add a category to the taxonomy
a) Add the taxonomy
q) Cancel and return to the main menu

> c

Add a category. Please choose an option:
  1) Name: 
  2) Slug*:

     Parent:
  3) owned

  a) Add the category
  q) Cancel and return to the taxonomy menu

> 2
Slug: on-display

Add a category. Please choose an option:
  1) Name: 
  2) Slug*: on-display

     Parent:
  3) owned

  a) Add the category
  q) Cancel and return to the taxonomy menu

> 3

Add a category. Please choose an option:
  1) Name: 
  2) Slug*: on-display

     Parent:
* 3) owned

  a) Add the category
  q) Cancel and return to the taxonomy menu

> a

Add an taxonomy. Please choose an option:
1) Name: Collection Status
2) Slug*:

	 Categories:
3) owned
4) -> on-display

c) Add a category to the taxonomy
a) Add the taxonomy
q) Cancel and return to the main menu

> a
```

Select an existing taxonomy to edit it or delete it.

```console
Please choose an option:
1) Add a taxonomy
2) Taxonomy - media-type
3) Taxonomy - topic
q) Return to the main menu

> 2

Update a taxonomy. Please choose an option:
1) Name:
2) Slug*: media-type

	 Categories:
3) Web Page
4) -> PDF
5) Video
6) -> Online
7) -> DVD
8) -> VHS
9) Book
10) -> Hardcover
11) -> Paperback
12) -> eBook
13) --> DRM protected
14) --> DRM free

c) Add a category to the taxonomy
u) Update the taxonomy
d) Delete the taxonomy
q) Cancel and return to the main menu

>
```

Select an existing category to edit it or delete it.

```console
Update a taxonomy. Please choose an option:
1) Name:
2) Slug*: media-type

	 Categories:
3) Web Page
4) -> PDF
5) Video
6) -> Online
7) -> DVD
8) -> VHS
9) Book
10) -> Hardcover
11) -> Paperback
12) -> eBook
13) --> DRM protected
14) --> DRM free

c) Add a category to the taxonomy
u) Update the taxonomy
d) Delete the taxonomy
q) Cancel and return to the main menu

> 11

Update a category. Please choose an option:
  1) Name: Paperback
  2) Slug*: paperback

   Parent:
   3) Web Page
   4) -> PDF
   5) Video
   6) -> Online
   7) -> DVD
   8) -> VHS
*  9) Book
   10) -> Hardcover
   11) -> Paperback
   12) -> eBook
   13) --> DRM protected
   14) --> DRM free


  a) Update the category
  q) Cancel and return to the taxonomy menu

>
```