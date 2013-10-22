
./Falkand-CMS
./Falkland-CMS-docs (gh-pages branch)


To build the Clojure API docs

cd ./Falkland-CMS
lein doc

To build the REST API docs

...

To build and serve the documentation site

gem install redcarpet
gem install jekyll

jekyll serve --baseurl '' --port 3000
open http://localhost:3000