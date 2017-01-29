@echo off
SET CLASSPATH=./build/
start rmiregistry.exe 15000
java.exe Server