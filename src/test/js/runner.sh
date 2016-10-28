#!/bin/bash

# This bash-shell script is a wrapper for phantomjs
# It first checks if the program exists and is executable, if so
# runs phantomjs normally. Otherwise just pretend nothing happened
# and keeps with the rest of the project build.

PHANTOMJS_EXEC=$(which phantomjs)

if [[ -x $PHANTOMJS_EXEC ]];
	then
	phantomjs "$1" "$2"
else
	exit 0
fi
