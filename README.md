Active Choices Plug-in
=================

A Jenkins **UI plugin for selecting one or multiple options for a job parameter**. It provides a number of capabilities
in a single plugin some, but not all, of which can be found amongst several other plugins. This project was previously
called Uno Choice Plug-in, while under the BioUno project.

As of July 2014 the plugin supports:

1. Selecting one or multiple options for a parameter
2. Combo-box, check-box and radio button UI
3. Dynamic generation of option values from a groovy command or Scriptler script
4. Cascading updates when job form parameters change
5. Displaying reference parameters (a new type of Jenkins UI parameter) which are dynamically generated, support
cascading updates, and are displayed as a variety of HTML formatted elements on the job form.
 
This plugin is developed in support of the diverse computational requirements of life-science Jenkins applications as
proposed by the [BioUno project](http://biouno.org/).

Many parts of the code may be adapted from existing plug-ins. We ask to keep issues related to this plug-in in
Jenkins JIRA.

Visit the [plug-in Wiki](https://wiki.jenkins-ci.org/display/JENKINS/Active+Choices+Plugin) for more details on each
parameter type.

# Building

If you have phantomjs in your $PATH, or if you edit pom.xml, you can run the JavaScript tests too with QUnit. It is executed with the Maven Exec Plug-in, but if you don't have phantomjs installed, the plug-in will simply ignore it and run only the Java tests.

`mvn clean test install`

# License

This plug-in is licensed under the MIT License. Parts of this plug-in have been adapted from existing plug-ins
with compatible licenses (e.g.: Apache License).
