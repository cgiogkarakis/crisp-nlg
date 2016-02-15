# Prerequisites #

  * g++, make, etc.
  * MySQL-dev
  * [MySQL++](http://tangentsoft.net/mysql++/)


# Downloading #

```
svn checkout https://crisp-nlg.googlecode.com/svn/trunk/Code/mysql-csp
```

(see also the instructions in the "source" tab above)


# Compiling #

Set the following environment variables:
  * MYSQL\_LIB\_DIR to the directory that contains libmysqlclient
  * MYSQLPP\_LIB\_DIR to the directory that contains libmysqlpp
  * MYSQL\_INCL\_DIR to the directory that contains mysql.h

Then type "make". This should build libMysqlCSP.{so,dylib} and mysqlcsp.



# Running #

Set the environment variable that controls where the linker looks for dynamic libraries to .:$MYSQL\_LIB\_DIR:$MYSQLPP\_LIB\_DIR.  This is LD\_LIBRARY\_PATH on Linux and DYLD\_LIBRARY\_PATH on MacOS.

Then type ./mysqlcsp.
