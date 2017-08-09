# file-format-streaming-converter [![Build Status](https://travis-ci.org/kenshoo/file-format-streaming-converter.svg?branch=master)](https://travis-ci.org/kenshoo/file-format-streaming-converter)

This is a file format converter from xlsx to csv. The main use of this repository is to convert 
large xlsx files to csv files using streams instead of reading the entire contents of a file.
This converter is able to read strings, texts, booleans and dates.


Example:

An xlsx file that looks like this:

|Header 1   | Header 4	|Header 5	|Header 6	  |
|-----------|-----------|-----------|-------------|
|Value 11   | Value 14  |411	    |Value 16	  |
|Value 21   | 411	    |Value 17	|¥a®	      |
|Value 31   | 411	    |Value 18	|ßÐ ÆÄÁ  ®Æ	  |
|Value 51   | 411	    |Value 20	|ウェブの国際化 |
|Value 71   | 411	    |30/07/17	|Æ ç Ü ß à ô  | 
|Value 91   | 411	    |Value 24	|Φ ψ α έ Ω    |
|Value 101  | 411	    |Value 25	|й ф Ы Щ Д	  |


Would translate to a same looking csv file, including the special characters and the dates.

Licensing
=========
file-format-streaming-converter is licensed under the Apache License, Version 2.0. See
[LICENSE](https://github.com/kenshoo/file-format-streaming-converter/blob/master/LICENSE) for the full
license text.
