#!/bin/sh

# Script to update javadoc deployed in svn

[ ! -d gadget-api ] && mkdir gadget-api

cp -r gviz/gviz-gadget/target/gadget/gadget/* gadget-api

for i in `find gadget-api -type d | grep -v .svn | sed -e 's#gadget-api/##g' `
do
   [ ! -d gviz/gviz-gadget/target/gadget/gadget/$i ] && svn delete gadget-api/$i 
done

for i in `find gadget-api -type f | grep -v .svn | sed -e 's#gadget-api/##g' `
do
   [ ! -f gviz/gviz-gadget/target/gadget/gadget/$i ] && svn delete gadget-api/$i 
done

find gadget-api/  | grep -v .svn | xargs svn add

find gadget-api/ -type f -name "*html" -exec svn propset svn:mime-type text/html '{}' ';'
find gadget-api/ -type f -name "*js" -exec svn propset svn:mime-type application/x-javascript '{}' ';'
find gadget-api/ -type f -name "*png" -exec svn propset svn:mime-type image/png '{}' ';'
find gadget-api/ -type f -name "*jpg" -exec svn propset svn:mime-type image/jpeg '{}' ';'

