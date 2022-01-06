(ns crash-reporter.db-repository
  (:gen-class)
  (require '[clojure.java.jdbc :as db]))

;; TODO: write it, create logic of writing crashes to the DB. To test it write a test for the message handler with the DB.
(defn write-crash-to-db
  "Saves the crash data to the DB"
  [data])
