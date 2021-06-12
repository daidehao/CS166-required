#! /bin/bash
echo "creating db named ... "$USER"_DB"
createdb -h localhost -p $PGPORT $USER"_DB"
pg_ctl status

echo "Copying csv files ... "
sleep 1
cp ../data/*.csv /tmp/$USER/myDB/data/.

echo "Initializing tables .. "
sleep 1
sql -h localhost -p $PGPORT $USER"_DB" < ../sql/create.sql
sql -h localhost -p $PGPORT $USER"_DB" < ../sql/create_index.sql
