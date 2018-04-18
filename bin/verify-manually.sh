#!/bin/bash
abort()
{
    echo >&2 '
*************************
***      ABORTED      ***
*************************
'
    echo "An error occurred. Exiting..." >&2
    exit 1
}
trap 'abort' 0

set -e
echo --------------------------------------------------------------------------------
echo -- This verification needs manual user interaction since it starts
echo -- The different configured runtimes one by one
echo --------------------------------------------------------------------------------

# verify main pom
./mvnw -B clean verify install

# test execution on google app engine test server
./mvnw -B -f runtime-gae-standard/pom.xml appengine:run
