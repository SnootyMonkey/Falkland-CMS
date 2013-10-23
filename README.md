# Falkand CMS Documention

This is the git branch (gh-pages) for the Falkland CMS documentation.

Falkland CMS is a Curation Management System used to collect, organize, curate and present the knowledge that exists in the world about a particular topic.

If you just want to use the documentation, you're better off using them from the [Falkland CMS Website](http://falkland-cms.com/).

## Hosting Your Own Documentation Website

Most users should just use [falkland-cms.com](http://falkland-cms.com/) to access the docs, but if you're contributing to Falkland CMS's documentation, or if you want you own local copy, you'll need to build them.

Falkland CMS's website is hosted by GitHub as GitHUb Pages, which are powered by [jekyll](http://jekyllrb.com/). To build and serve the documentation website, you'll need a few things:

* [Ruby](https://www.ruby-lang.org/) - many Linux or Mac systems will already have Ruby
* [RubyGems](http://rubygems.org/) - many Ruby installs will already have RubyGems

To host the website:
 
```console
gem install redcarpet
gem install jekyll

jekyll serve --baseurl '' --port 3000
open http://localhost:3000
```

## Generating the API Reference Documentation

### Clojure API

Most developers should just use the [online docs](http://falkland-cms.com/API/clojure.html) but if you're contributing to Falkland CMS and working on the API docs, or if you want you own local copy, you'll need to build them.

To build the Clojure API docs you need 2 copies of the repository, one for the code to generate from and one for the documentation to generate to:

```
git clone https://github.com/SnootyMonkey/Falkland-CMS.git
git clone https://github.com/SnootyMonkey/Falkland-CMS.git Falkland-CMS-docs
cd Falkland-CMS-docs
git checkout gh-pages
cd ../Falkland-CMS
lein doc
```

The resulting Clojure API reference documentation will be at: ./Falkland-CMS-docs/API/Clojure/index.html

### REST API

Again, most developers should just use the [online docs](http://falkland-cms-api.readthedocs.org/) but if you're contributing to Falkland CMS and working on the API docs, or if you want you own local copy, you'll need to build them.

The REST API docs are written in [reStructuredText](http://docutils.sourceforge.net/rst.html) and built by [Sphinx](http://sphinx-doc.org). You'll need a few things:

 * [Python](http://www.python.org/) - many Linux or Mac systems will already have Python
 * [Sphinx](http://sphinx-doc.org/) - `easy_install -U Sphinx`
 * [pygments-style-solarized](https://pypi.python.org/pypi/pygments-style-solarized) - `easy_install -U pygments-style-solarized`

To build the docs:

```console
git clone https://github.com/SnootyMonkey/Falkland-CMS.git Falkland-CMS-docs
cd Falkland-CMS-docs
git checkout gh-pages
cd API/REST
make html
```

The resulting REST API reference documentation will be at: ./Falkland-CMS-docs/API/REST/_build/html/index.html

## Contributing to the Docs

If you'd like to enhance the documentation, please fork [Falkland CMS on GitHub](https://github.com/SnootyMonkey/Falkland-CMS). You can then submit your pull requests to the gh-pages branch if you'd like to contribute back your enhancements. 

Alternatively you can use GitHub's online editing features to make edits to the documentation and submit a pull request, all in your web browser.

I promise to look at every pull request and incorporate it, or at least provide feedback on why if I won't.

Details on the structure of the documentation branch can be found in the [contributing section](http://falkland-cms.com/contributing.html) of the Falkland CMS documentation.

### Check Spelling

```console
lein spell
```

## Acknowledgements

Many thanks to the [Omeka project](http://omeka.org/) for showing the way to [beautiful docs](http://omeka.readthedocs.org/en/latest/index.html), and to the fine folks behind [Read the Docs](http://readthedocs.org) for hosting so much of the open source world's best documentation.

Thanks to the contributors to [jekyll](http://jekyllrb.com/) for making such a nice static website generation tool, and to the folks of [GitHub](http://github.com/) for hosting so many open source projects, and their documentation and websites, for free.