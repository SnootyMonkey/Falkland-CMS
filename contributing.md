---
layout: default
title: Falkland CMS - Contributing
description: Falkland CMS is a Curation Management System used to collect, organize, curate and present the knowledge that exists in the world about a particular topic.
author: Sean Johnson
---

## Development and Contributing

Please fork [Falkland CMS on GitHub](https://github.com/SnootyMonkey/Falkland-CMS) if you'd like to enhance it. Submit your pull requests if you'd like to contribute back your enhancements. I promise to look at every pull request and incorporate it, or at least provide feedback on why if I won't.

### Branches

There are 3 long lived branches in the repository:

[master](https://github.com/SnootyMonkey/Falkland-CMS/tree/master) - mainline, picked up by [continual integration on Travis-CI](https://travis-ci.org/SnootyMonkey/Falkland-CMS), named releases are tagged with: vX.X.X


[dev](https://github.com/SnootyMonkey/Falkland-CMS/tree/dev) - development mainline, picked up by [continual integration on Travis-CI](https://travis-ci.org/SnootyMonkey/Falkland-CMS)

[gh-pages](https://github.com/SnootyMonkey/Falkland-CMS/tree/gh-pages) - documentation branch, [jekyll](http://jekyllrb.com/) powered GitHub Pages website (you're reading it now) and [Codox](https://github.com/weavejester/codox) and [Sphinx](http://sphinx-doc.org/) powered API reference documentation

Additional short lived feature branches will come and go.


### Repository Structure - Development

[/project.clj](https://github.com/SnootyMonkey/Falkland-CMS/blob/master/project.clj) - [Leiningen](http://leiningen.org/) project config file

[/.travis.yml](https://github.com/SnootyMonkey/Falkland-CMS/blob/master/.travis.yml) - [Travis-CI](https://travis-ci.org/SnootyMonkey/Falkland-CMS) continual integration config file

[/LICENSE.txt](https://github.com/SnootyMonkey/Falkland-CMS/blob/master/LICENSE.txt) - Mozilla Public License 2.0

[/src/fcms/app.clj](https://github.com/SnootyMonkey/Falkland-CMS/blob/master/src/fcms/app.clj) - bootstrap the Falkland CMS server

[/src/fcms/config.clj](https://github.com/SnootyMonkey/Falkland-CMS/blob/master/src/fcms/config.clj) - configuration for the Falkland CMS server instance

[/src/fcms/api/](https://github.com/SnootyMonkey/Falkland-CMS/blob/master/src/fcms/api/) - [Liberator](http://clojure-liberator.github.io/liberator/) request handlers implementing the REST API

[/src/fcms/resources/](https://github.com/SnootyMonkey/Falkland-CMS/blob/master/src/fcms/resources/) - Clojure API, a light business logic wrapper around [CouchDB](http://couchdb.apache.org/)

[/src/fcms/representations/](https://github.com/SnootyMonkey/Falkland-CMS/blob/master/src/fcms/representations/) - JSON rendering for the REST API

[/src/fcms/lib/](https://github.com/SnootyMonkey/Falkland-CMS/blob/master/src/fcms/lib/) - helper files

[/src/fcms/db/](https://github.com/SnootyMonkey/Falkland-CMS/blob/master/src/fcms/db/) - initialization of the [CouchDB](http://couchdb.apache.org/) views

[/src/fcms/cljs/](https://github.com/SnootyMonkey/Falkland-CMS/blob/master/src/fcms/cljs/) - [ClojureScript](https://github.com/clojure/clojurescript) for the Web UI

[/src/brepl/fcms/connect.cljs](https://github.com/SnootyMonkey/Falkland-CMS/blob/master/src/brepl/fcms/connect.clj) - [ClojureScript](https://github.com/clojure/clojurescript) browser REPL

[/resources/](https://github.com/SnootyMonkey/Falkland-CMS/blob/master/resources/) - CSS, ClojureScript, images

[/test/fcms/features/](https://github.com/SnootyMonkey/Falkland-CMS/blob/master/test/fcms/features/) - [Cucumber](http://cukes.info/) integration tests

[/test/fcms/unit/](https://github.com/SnootyMonkey/Falkland-CMS/blob/master/test/fcms/unit/) - [Midje](https://github.com/marick/Midje) and [core.test](http://richhickey.github.io/clojure/clojure.test-api.html) unit tests

[/test/fcms/lib/](https://github.com/SnootyMonkey/Falkland-CMS/blob/master/test/fcms/lib/) - helper files for tests

[/design](https://github.com/SnootyMonkey/Falkland-CMS/blob/master/design/) - temporary design documents created before the final documentation exists 

### Repository Structure - Documentation (gh-pages branch)



