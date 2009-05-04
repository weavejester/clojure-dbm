;; Copyright (c) James Reeves. All rights reserved.
;; The use and distribution terms for this software are covered by the Eclipse
;; Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php) which
;; can be found in the file epl-v10.html at the root of this distribution. By
;; using this software in any fashion, you are agreeing to be bound by the
;; terms of this license. You must not remove this notice, or any other, from
;; this software.

(ns clojure-dbm.tokyo-cabinet
  "Implementation of the clojure-dbm interface for Tokyo Cabinet."
  (:use clojure-dbm)
  (:use clojure.contrib.fcase)
  (:use clojure.contrib.def)
  (:use clojure.contrib.except)
  (:import (tokyocabinet HDB FDB BDB)))

(derive ::bdb ::db)
(derive ::fdb ::db)
(derive ::hdb ::db)

(defvar- write+create
  (bit-or HDB/OWRITER HDB/OCREAT))

(defn- error-message [db]
  (.errmsg db (.ecode db)))

(defn- open-cabinet
  "Open a new Tokyo Cabinet database given a database class like HDB."
  [db-class repository]
  (let [db       (.newInstance db-class)
        filename (:filename repository)
        success? (.open db filename write+create)]
    (if success?
      (assoc repository :db db)
      (throwf (str "Could not open file: " (error-message db))))))

(defmethod db-open ::bdb [repository]
  (open-cabinet BDB repository))

(defmethod db-open ::fdb [repository]
  (open-cabinet FDB repository))

(defmethod db-open ::hdb [repository]
  (open-cabinet HDB repository))

(defmethod db-close ::db
  [repository]
  (if-let [db (:db repository)]
    (when-not (.close db)
      (throwf (str "Could not close file: " (error-message db))))))

(defmethod db-fetch ::db
  [repository key]
  (.get (:db repository) key))

(defmethod db-store ::db
  [repository key value]
  (.put (:db repository) key value))

(defmethod db-delete ::db
  [repository key]
  (.out (:db repository) key))
