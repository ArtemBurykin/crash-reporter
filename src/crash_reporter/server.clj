(ns crash-reporter.server
  (:gen-class) 
  (:require [io.pedestal.http :as http]
            [langohr.core      :as rmq]
            [crash-reporter.queue-sender :as sender]
            [crash-reporter.crash-reporter :as service]))

(defonce runnable-service (http/create-server service/service))

(defn -main
  "The entry-point for 'lein run'"
  [& args]
  (println "\nStarting the crash report server..")
  (http/start runnable-service)
  (rmq/close sender/ch)
  (rmq/close sender/conn))