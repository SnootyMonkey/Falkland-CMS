---
layout: default
title: Falkland CMS - Clojure API
author: Sean Johnson
---

## Clojure API

### Namespaces

<a href="Clojure/fcms.resources.common.html" target="_clojure_api">common</a> - a common set of functions for everything that's stored in FCMS. You won't often use these functions directly.

<a href="Clojure/fcms.resources.collection.html" target="_clojure_api">collection</a> - a grouping of items to be managed together

<a href="Clojure/fcms.resources.item.html" target="_clojure_api">item</a> - any resource that's been collected in your collection, often organized into many orthogonal taxonomies.

<a href="Clojure/fcms.resources.taxonomy.html" target="_clojure_api">taxonomy</a> - a hierarchical organization of categories, independent of any other taxonomies.

The full set of <a href="Clojure/index.html" target="_clojure_api" title="Falkland CMS Clojure API Reference Documentation">reference documentation</a> is available.

### Quick-Start from the Interactive REPL

[Download the latest CouchDB](http://couchdb.apache.org/), install it (it's a 1-click installer) and run it (it's a 1-click app).

Clone the Falkland CMS repo:

```console
git clone https://github.com/SnootyMonkey/Falkland-CMS.git
```

Then initialize the DB:

```console
cd Falkland-CMS
lein init-db
```

Start the REPL with:

```console
lein repl
```

Require the base resource namespaces you'll be using:

```clojure
(require '[fcms.resources.collection :as collection])
(require '[fcms.resources.item :as item])
(require '[fcms.resources.taxonomy :as taxonomy])
```

Create a couple new collections:

```clojure
(collection/create-collection "Mudskippers")

(collection/create-collection "Fortune 500")
```

List the collections that exist:

```clojure
(pprint (collection/all-collections))
```

Create a very basic item:

```clojure
(item/create-item "mudskippers" "Fish with Legs!")
```

Create some more typical items:

```clojure
(item/create-item "mudskippers" "Amazing animals - Mudskipper" {:creator "BBC Life episode", :url "https://www.youtube.com/watch?v=KurTiX4FDuQ"})

(item/create-item "fortune-500" "Apple" {:creator ["Steve Jobs", "Steve Wozniak"], :url ["http://apple.com", "http://en.wikipedia.org/wiki/Apple_Inc."]})
```

List all the items in each collection:

```clojure
(pprint (item/all-items "mudskippers"))

(pprint (item/all-items "fortune-500"))
```

Create a taxonomy:

```clojure
(taxonomy/create-taxonomy "mudskippers" "media-types")
(taxonomy/create-taxonomy "mudskippers" "Topics")

(taxonomy/create-taxonomy "fortune-500" "Topics")
```

List all the taxonomies in each collection:

```clojure
(pprint (taxonomy/all-taxonomies "mudskippers"))

(pprint (taxonomy/all-taxonomies "fortune-500"))
```

Create some categories in the taxonomies:

```clojure
(taxonomy/create-category "mudskippers" "media-types/web-pages/wikipedia" "Wikipedia Pages")
(taxonomy/create-category "mudskippers" "media-types/videos/youtube" "YouTube Videos")
(taxonomy/create-category "mudskippers" "media-types/videos/vimeo" "Vimeo Videos")

(taxonomy/create-category "mudskippers" "topics/reproduction")
(taxonomy/create-category "mudskippers" "topics/feeding")

(taxonomy/create-category "fortune-500" "topics/companies" "Companies")
(taxonomy/create-category "fortune-500" "topics/ceos" "CEOs")
```

Look at the updated taxonomies with categories:

```clojure
(pprint (taxonomy/all-taxonomies "mudskippers"))

(pprint (taxonomy/get-taxonomy "fortune-500" "topics"))
```

Categorize the items in some categories:

```clojure
(taxonomy/categorize-item "mudskippers" "amazing-animals-mudskipper" "media-types/videos/youtube")
(taxonomy/categorize-item "mudskippers" "amazing-animals-mudskipper" "topics/reproduction")

(taxonomy/categorize-item "fortune-500" "apple" "topics/companies")
```

List all items categorized in a particular taxonomy:

```clojure
(taxonomy/items-for-taxonomy "mudskippers" "media-types")

(taxonomy/items-for-taxonomy "fortune-500" "topics")
```

List all items categorized in a particular category of a taxonomy:

```clojure
(taxonomy/items-for-category "mudskippers" "media-types/videos")
(taxonomy/items-for-category "mudskippers" "media-types/web-pages/wikipedia")

(taxonomy/items-for-category "fortune-500" "topics/companies")
```

Delete an item:

```clojure
(item/delete-item "fortune-500" "apple")
```

Try retrieving a deleted item:

```clojure
(item/get-item "fortune-500" "apple")

(taxonomy/items-for-taxonomy "fortune-500" "topics")
```

Delete a taxonomy:

```clojure
(taxonomy/delete-taxonomy "mudskippers" "topics")
```

Congratulations! You've completed a whirlwind tour of the Falkland CMS Clojure API. You're all done for now:

```clojure
quit
```