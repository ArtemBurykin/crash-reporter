(ns crash-reporter.server
  (:gen-class) 
  (:require [io.pedestal.http :as http]
            [crash-reporter.queue-sender :as sender]
            [crash-reporter.crash-reporter :as service]))

(defonce runnable-service (http/create-server service/service))

(defn -main
  "The entry-point for 'lein run'"
  []
  (println "\nStarting the crash report server..")
  (sender/connect)
  (http/start runnable-service)
  (sender/disconnect))