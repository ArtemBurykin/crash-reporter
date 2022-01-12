(ns crash-reporter.db-repository
  (:gen-class)
  (:import  [org.postgresql.util PGobject])
  (:require [clojure.java.jdbc :as db]
            [environ.core :refer [env]]))

(defn- to-pg-json
  "Transforms the data into the postgresql object, to save it later to the DB"
  [data]
  (doto (PGobject.)
    (.setType (name :json))
    (.setValue data)))

(defn- uuid
  "Generates a random UUID"
  [] (java.util.UUID/randomUUID))

(defn write-crash-to-db
  "Saves the crash data to the DB"
  [data]
  (let [pg-connection {:dbtype "postgresql" 
                    :dbname (env :postgres-db)
                    :host (env :postgres-host)
                    :user (env :postgres-user)
                    :password (env :postgres-password)}] 
    (db/insert! pg-connection :crashes {:data (to-pg-json data) 
                                        :stamp (java.time.LocalDateTime/now)
                                        :id (uuid)})))
