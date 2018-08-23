# Burrows-Wheeler-Scott transform

The bijective variant found by Scott, D. A. of the Burrows-Wheeler transform written in Java. Also known as the 'Scottified' version of the transform.

Version history:
* v0.1.2 LCM is found more easily now in comparator 23-08-2018
* v0.1.1 no calculation LCM anymore 22-08-2018
* v0.1.0 first working version 19-08-2018
* v0.0.3 improved sorting 10-08-2018
* v0.0.2 merged and ported methods from other code 05-08-2018
* v0.0.1 initial version 25-07-2018

The pretty thing about this transform is that the input is of
the same length as the output.

Functionality:
* It basically tries to reduce entropy.
* Works best if the input has some repetition, like text written in any language.

The text used in the example in the main function is Lorem Ipsum.

With great help from https://github.com/zephyrtronium/bwst for understanding BWST in plain English and http://www.allisons.org/ll/AlgDS/Strings/Factors/ for understanding Duval and giving a JavaScript example
