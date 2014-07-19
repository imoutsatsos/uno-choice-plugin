uno-choice-plugin
=================

A Jenkins **UI plugin for selecting one or multiple options for a job parameter**. It provides a number of capabilities in a single plugin some, but not all, of which can be found amongst several other plugins. 

As of July 2014 the plugin supports:

1. Selecting one or multiple options for a parameter
2. Combo-box, check-box and radio button UI
3. Dynamic generation of option values from a groovy command or Scriptler script
4. Cascading updates when job form parameters change
5. Displaying reference parameters (a new type of Jenkins UI parameter) which are dynamically generated, support cascading updates, and are displayed as a variety of HTML formatted elements on the job form.
 
This plugin is developed in support of the diverse computational requirements of life-science Jenkins applications as proposed by the [BioUno project](http://biouno.org/)

Many parts of the code may be adapted from existing plug-ins. We ask to keep issues 
related to this plug-in in this plug-in's repository in GitHub (and 
not in Jenkins JIRA), and also that any enhancements found in this project 
may be contributed back to the original project (or to a new one).

Visit the [plug-in Wiki](https://github.com/biouno/uno-choice-plugin/wiki) 
for more details on each parameter type.

# License

This plug-in is licensed under the MIT License. Parts of this plug-in 
may have been adapter/rewritten from existing plug-ins with similar 
licens (e.g.: Apache License). In case we missed anything while 
porting code, or if we forgot to give credits, please let us know via 
http://biouno.org. Thanks!
