#!/bin/sh

# Script to update javadoc deployed in svn

[ ! -d gviz-api ] && mkdir gviz-api

cp -r gviz/gviz-api-export/target/gvizapi/gvizapi/* gviz-api

for i in `find gviz-api -type d | grep -v .svn | sed -e 's#gviz-api/##g' `
do
   [ ! -d gviz/gviz-api-export/target/gvizapi/gvizapi/$i ] && svn delete gviz-api/$i 
done

for i in `find gviz-api -type f | grep -v .svn | sed -e 's#gviz-api/##g' `
do
   [ ! -f gviz/gviz-api-export/target/gvizapi/gvizapi/$i ] && svn delete gviz-api/$i 
done

find gviz-api/  | grep -v .svn | xargs svn add

find gviz-api/ -type f -name "*html" -exec svn propset svn:mime-type text/html '{}' ';'
find gviz-api/ -type f -name "*js" -exec svn propset svn:mime-type application/x-javascript '{}' ';'
find gviz-api/ -type f -name "*png" -exec svn propset svn:mime-type image/png '{}' ';'
find gviz-api/ -type f -name "*jpg" -exec svn propset svn:mime-type image/jpeg '{}' ';'

