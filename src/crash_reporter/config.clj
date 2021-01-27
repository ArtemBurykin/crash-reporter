(ns crash-reporter.config
  (:require [clojure.data.json :as json]
            [langohr.core :as rmq]
            [clojure.java.io :as io])
  (:gen-class))

(defn parse-config
  "Parses the config file to a list of its values"
  [filename]
  (json/read-str (slurp filename) :key-fn keyword))

(def config (parse-config (-> "config.json" io/resource io/file)))

(defn get-slack-hook
  "Retrieves a URL of a slack hook to send a message"
  [config]
  (:slackHook config))

(defn get-connection-settings
  "Retrieves the connection settings for RabbitMQ from the config, if they're empty returns the default settings"
  [config]
  (if (contains? config :rmqConf)
    (merge rmq/*default-config* (:rmqConf config))
    rmq/*default-config*))

(defn get-port
  "Gets the port where the server will perform"
  [config]
  (if (contains? config :port)
    (:port config)
    8080))
