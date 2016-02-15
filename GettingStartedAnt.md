Notice that this page describes how to build and run an old tag of CRISP using Apache Ant.
The latest version uses the Maven build manager. Information on how to get started with the latest version can be found [here](GettingStarted.md)

# Requirements #

In order to build the CRISP system, you will need the following software:
  * Java SE 5.0 Development Kit or higher (get it [here](http://java.sun.com/javase/downloads/index.jsp), unless you have a Mac, in which case it should come preinstalled on you machine)
  * Apache Ant (get it [here](http://ant.apache.org/bindownload.cgi)). If you install Ant via the Fedora package management system, be sure to also install the ant-contrib package, which defines the javacc task.
  * A Subversion client (if you can't get one through the package management system of your platform, e.g. Cygwin, apt-get, etc., you can get binaries [here](http://subversion.tigris.org/project_packages.html)).


# Building CRISP #

Assuming your Java and Ant are installed properly, build CRISP as follows:
  * Check out the Subversion repository using the following command:
```
   svn checkout http://crisp-nlg.googlecode.com/svn/tags/ant-crisp/Code crisp-nlg
```
> (See also the [instructions for checking out from the Google Code repository](http://code.google.com/p/crisp-nlg/source).)
  * Change to the new `crisp-nlg` subdirectory.
  * Build the system using Ant:
```
   ant
```

The executable Jar file is now in `dist/crisp.jar`.


# Running CRISP #
Run the combined converter and planner on an example input file:
```
java -jar dist/crisp.jar grammars/acl07/modifiers/problem-acl-mod2.xml
```

This should display a plan corresponding to the sentence "Mary likes the foo1 white rabbit" and leave behind the files `/tmp/crisptest-mod2-domain.lisp` and `/tmp/crisptest-mod2-problem.lisp`, which are PDDL input files that can be solved with other off-the-shelf planners.

A new jar runs the new version of the planner, which outputs the derivation tree, derived tree and final sentence after constructing the plan. To use this one, run:
```
java -jar dist/crisp-sentence.jar grammars/acl07/modifiers/problem-acl-mod2.xml
```

The format for the input files should be straightforward; see the examples.