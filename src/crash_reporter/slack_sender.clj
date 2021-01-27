(ns crash-reporter.slack-sender
  (:require [org.httpkit.client :as http-client]
            [crash-reporter.config :as config]
            [clojure.data.json :as json]))

(def slack-webhook (config/get-slack-hook config/config))

(defn send-to-slack
  "Sends the log of pings to the url of a slack web hook, returns a status of the request"
  [crash]
  (let [crash-obj (json/read-str crash :key-fn keyword)
        message {:text (:message crash-obj) :attachments [{:color "#ff0000" :title "Stack trace" :text (:stack crash-obj)}]}
        {:keys [status body]} @(http-client/post slack-webhook {:body (json/write-str message)})]
    (println body status)))