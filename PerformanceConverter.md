On the ACL example "easy", the converter takes 990ms for the whole thing.  678ms of this time are spent in computeDomain, and about 622ms of this are spent in xpath.evaluate and TermParser.parse.  This means there is enormous potential for speeding the converter up, but this would require
  * constructing terms by hand
  * using no XPath expressions.

Both of these would make the code so ugly that I wouldn't want to see, let alone maintain, it.  Thus it stays as it is for now.
