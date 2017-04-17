#!/bin/sh
#CLASSPATH=.:gluegen-rt-natives-macosx-universal.jar:gluegen-rt.jar:jogl-all-natives-macosx-universal.jar:jogl-all.jar
CLASSPATH=":/Users/okadakaya/Downloads/joglpath/gluegen-rt.jar:/Users/okadakaya/Downloads/joglpath/jogl-all.jar:/Users/okadakaya/Downloads/joglpath/jogl-all-natives-macosx-universal.jar.jar:/Users/okadakaya/Downloads/joglpath/gluegen-rt-natives-macosx-universal.jar"
export CLASSPATH
javac ocha/itolab/hutch/applet/pathviewer/*.java
java ocha/itolab/hutch/applet/pathviewer/ViewerMain
