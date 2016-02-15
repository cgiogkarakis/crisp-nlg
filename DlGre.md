# Introduction #

Add your content here.



# Prerequisites #

  * Apache Ant 1.6.2 or higher
  * Java


# Compiling #

  * Check out the source tree from Google Code (see the "source" tab)
  * Go to the new directory
  * type "ant"

This should produce a file build/lib/DlGre.jar, which you can run with "java -jar" as usual.


# Running #

The program expects the algorithm you want and the name of an XML domain specification as command-line parameters. You can run it on the example data as follows:

```
java -jar build/lib/DlGre.jar bisim examples/dale-haddock.xml
```

If you only want positive concepts, run it as follows:

```
java -jar build/lib/DlGre.jar positive examples/dale-haddock.xml
```