# Requirements #

In order to build the CRISP system, you will need the following software:
  * Java SE 5.0 Development Kit or higher (get it [here](http://java.sun.com/javase/downloads/index.jsp), unless you have a Mac, in which case it should come preinstalled on you machine)
  * Apache Maven (get it [here](http://maven.apache.org/download.html)).
  * A Subversion client (if you can't get one through the package management system of your platform, e.g. Cygwin, apt-get, etc., you can get binaries [here](http://subversion.tigris.org/project_packages.html)).


# Building CRISP #

Assuming your Java and Maven are installed properly, build CRISP as follows:
  * Check out the Subversion repository using the following command:
```
   svn checkout http://crisp-nlg.googlecode.com/svn/trunk/Code/crisp
```
> (See also the [instructions for checking out from the Google Code repository](http://code.google.com/p/crisp-nlg/source).)
  * Change to the new `crisp` subdirectory.
  * Build the system using Maven:
```
   mvn install assembly:assembly
```
> > Maven will automatically download all required dependencies and store them in your local maven repository.
> > It will then compile CRISP and finally assemble an executable jar file that contains CRISP and all required libraries.

The executable Jar file is now in `target/crisp-[version number]-jar-with-dependencies.jar`.


# Preparing to run CRISP #

The current version of CRISP requires the FF planner to run, which is available [here](http://www.loria.fr/~hoffmanj/ff.html). You need to tell CRISP about FF's location on your file system by creating a file called "crisp.properties". This file must specify the full filename of FF like this:
```
FfBinary=/path/to/your/ff
```


# Running CRISP #

_**Note: We have recently made changes to the input file format that CRISP assumes. This means that the (old) ACL files will no longer be processed by the current version. We will update the ACL files as soon as we can.**_

Run the combined converter and planner on an example input file:
```
java -jar target/crisp-[version number]-jar-with-dependencies.jar grammars/acl07/modifiers/problem-acl-mod2.xml
```

This should display a plan corresponding to the sentence "Mary likes the white foo1 rabbit", a TAG derivation tree, a derived trees and the resulting output sentence. It should also leave behind the files `crisptest-mod2-domain.lisp` and `crisptest-mod2-problem.lisp`, which are PDDL input files that can be solved with other off-the-shelf planners.

The format for the input files should be straightforward; see the examples.