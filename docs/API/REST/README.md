Falkland CMS API Documentation
==============================

This is the developer documentation, but if you just want to use the documentation,
you're better off using them from [Read the Docs](http://falkland-cms-api.readthedocs.org/).

Building the API documentation
------------------------------

Again, most developers should just use the [online docs](http://falkland-cms-api.readthedocs.org/) but
if you're contributing to Falkland CMS and working on the API docs, or if you
want you own local copy, you'll need to build them.

Falkland CMS API docs are written in [reStructuredText](http://docutils.sourceforge.net/rst.html)
and built by [Sphinx](http://sphinx-doc.org). You'll need a few things:

 * [Python](http://www.python.org/) - many Linux or Mac systems will already have Python
 * [Sphinx](http://sphinx-doc.org/) - `easy_install -U Sphinx`
 * [pygments-style-solarized](https://pypi.python.org/pypi/pygments-style-solarized) - `easy_install -U pygments-style-solarized`

To build the docs:

```console
cd docs/API
make html
```

This will result in a directory called docs/API/_build that contains the generated HTML for the docs.

### Acknowledgements

Many thanks to the [Omeka project](http://omeka.org/) for showing the way to [beautiful docs](http://omeka.readthedocs.org/en/latest/index.html), and to the fine folks behind [Read the Docs](http://readthedocs.org) for hosting so much of the open source world's best documentation.