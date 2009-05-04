;; Copyright (c) James Reeves. All rights reserved.
;; The use and distribution terms for this software are covered by the Eclipse
;; Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php) which
;; can be found in the file epl-v10.html at the root of this distribution. By
;; using this software in any fashion, you are agreeing to be bound by the
;; terms of this license. You must not remove this notice, or any other, from
;; this software.

(ns clojure-dbm.memory
  "Implementation of the clojure-dbm to an in-memory map. Primarily for
  testing purposes."
  (:use clojure-dbm))

(def memory-stores (ref {}))

(defn- get-store
  "Find or create an in-memory map."
  [make-store name]
  (dosync
    (if (contains? @memory-stores name)
      (@memory-stores name)
      (let [store (ref (make-store))]
        (alter memory-stores assoc name store)
        store))))

(defmethod db-open ::hash-map
  [repository]
  (assoc repository :db (get-store hash-map (:name repository))))

(defmethod db-close ::hash-map [repository])

(defmethod db-fetch ::hash-map
  [repository key]
  (@(:db repository) key))

(defmethod db-store ::hash-map
  [repository key value]
  (dosync
    (alter (:db repository) assoc key value)))

(defmethod db-delete ::hash-map
  [repository key]
  (dosync
    (alter (:db repository) dissoc key)))
