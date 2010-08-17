#!/bin/sh

# Script to update javadoc deployed in svn

[ ! -d apidocs ] && mkdir apidocs

cp -r docs/target/apidocs/* apidocs
rm -rf apidocs/WEB-INF apidocs/META-INF

for i in `find apidocs -type d | grep -v .svn | sed -e 's#apidocs/##g' `
do
   [ ! -d docs/target/apidocs/$i ] && svn delete apidocs/$i 
done

for i in `find apidocs -type f | grep -v .svn | sed -e 's#apidocs/##g' `
do
   [ ! -f docs/target/apidocs/$i ] && svn delete apidocs/$i 
done

find apidocs/  | grep -v .svn | xargs svn add

find apidocs/ -type f -name "*html" -exec svn propset svn:mime-type text/html '{}' ';'





