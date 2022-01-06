(ns crash-reporter.slack-sender
  (:gen-class)
  (:require [org.httpkit.client :as http-client]
            [environ.core :refer [env]]
            [clojure.data.json :as json]))

(def slack-webhook (env :slack-webhook))

(defn send-to-slack
  "Sends the log of pings to the url of a slack web hook, returns a status of the request"
  [crash]
  (let [crash-obj (json/read-str crash :key-fn keyword)
        additionalInfo (if (nil? (:additionalData crash-obj)) "Not provided" (json/write-str (:additionalData crash-obj)))
        message {:text (:message crash-obj) :attachments [{:color "#ff0000" :title "Stack trace" :text (:stack crash-obj)}
                                                          {:color "#ff0000" :title "Additional Info" :text additionalInfo}]}
        {:keys [status body]} @(http-client/post slack-webhook {:body (json/write-str message)})]
    (println body status)))