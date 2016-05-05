#!/bin/sh

make clean
make
java -cp lib/json-20160212.jar:lib/hamcrest-core-1.3.jar:lib/junit-4.12.jar:. tests/TestRunner

