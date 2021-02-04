(ns crash-reporter.queue-sender
  (:gen-class)
  (:require [langohr.core      :as rmq]
            [clojure.data.json :as json]
            [langohr.queue     :as lq]
            [crash-reporter.config :as config]
            [langohr.basic     :as lb]
            [langohr.consumers :as lc]
            [langohr.channel   :as lch]))

(def ^{:const true} default-exchange-name "")

(def conn (atom nil))
(def ch (atom nil))

(defn connect
  "Connects the server to the RabbitMQ server"
  []
  (reset! conn (rmq/connect (config/get-connection-settings config/config)))
  (reset! ch (lch/open @conn)))

(defn disconnect
  "Disconnects from the RabbitMQ server"
  []
  (rmq/close ch)
  (rmq/close conn))

(defn send-crash-to-queue
  "Sends a crash to the queue of RabbitMQ, attaches the message handler to the crash"
  [crash message-handler]
  (let [qname "school.crashes"
        content (json/write-str crash)]
    (lq/declare @ch qname {:exclusive false :auto-delete true})
    (lc/subscribe @ch qname message-handler {:auto-ack true :auto-delete false})
    (lb/publish @ch default-exchange-name qname content {:content-type "text/plain" :type "crashes"})))
