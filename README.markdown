Clojure-DBM is an interface to key/value databases like Tokyo Cabinet.

Usage
-----

To use it, you need to first define a repository map:

    (def cabinet
      {:type     :clojure-dbm.tokyo-cabinet/hdb
       :filename "cabinet.db"})

The type of the cabinet is a fully qualified keyword. The namespace of the
type denotes the library to be used, and will be required automatically. In
this case, we're using a Tokyo Cabinet hash-table database.

Clojure-DBM provides three functions for manipulating the database:

* `(fetch key)`       - Read a Clojure value from the database
* `(store key value)` - Store a Clojure value in the database
* `(delete key)`      - Delete a value from the database

Wrap these commands in `with-db`. This will open and close the database for you.

    (with-db cabinet
      (store :greet ["Hello" "World"])
      (fetch :greet))

Extending
---------

To extend this interface for more database types, you need to implement five
multimethods:

#### (db-open repository)

Takes the repository map as an argument. Returns the map with an arbitrary key
that contains the open database object.

#### (db-close repository)

Takes the repository map as an argument. Closes the database object added to
the repository by `db-open`.

#### (db-fetch repository key)

Takes the repository map and a key string as arguments. Uses the key to find
a value in the database. The return value must a string.

#### (db-store repository key value)

Takes a repository map and a key and value string as arguments. Stores the
value string in the database with the key string.

#### (db-delete repository key)

Takes the repository map and a key string as arguments. Uses the key to find
a value in the database, and then delete it.
