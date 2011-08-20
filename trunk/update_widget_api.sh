#!/bin/sh

# Script to update javadoc deployed in svn

[ ! -d widget-api ] && mkdir widget-api

cp -r widget/target/widget/widget/* widget-api

for i in `find widget-api -type d | grep -v .svn | sed -e 's#widget-api/##g' `
do
   [ ! -d widget/target/widget/widget/$i ] && svn delete widget-api/$i 
done

for i in `find widget-api -type f | grep -v .svn | sed -e 's#widget-api/##g' `
do
   [ ! -f widget/target/widget/widget/$i ] && svn delete widget-api/$i 
done

find widget-api/  | grep -v .svn | xargs svn add

find widget-api/ -type f -name "*html" -exec svn propset svn:mime-type text/html '{}' ';'
find widget-api/ -type f -name "*js" -exec svn propset svn:mime-type application/x-javascript '{}' ';'
find widget-api/ -type f -name "*png" -exec svn propset svn:mime-type image/png '{}' ';'
find widget-api/ -type f -name "*jpg" -exec svn propset svn:mime-type image/jpeg '{}' ';'





