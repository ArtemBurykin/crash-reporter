(ns crash-reporter.crash-reporter 
  (:require [io.pedestal.http :as http]
            [crash-reporter.slack-sender :as slack]
            [crash-reporter.config :as config]
            [io.pedestal.http.route :as route]
            [io.pedestal.http.body-params :as body-params]
            [crash-reporter.queue-sender :as sender]))

(defn message-handler
  "The handler to send crashes to Slack"
  [_ _ payload]
  (let [crash-data (String. payload "UTF-8")]
    (str (slack/send-to-slack crash-data))))

(defn report-crash
  "The end-point to send a crash report"
  [{:keys [json-params]}]
  (sender/send-crash-to-queue json-params message-handler)
  {:status 200})

(def routes
  (route/expand-routes
   #{["/report-crash" :post [(body-params/body-params) report-crash] :route-name :report-crash]}))

(def service {:env                 :prod
              ::http/routes        routes
              ::http/resource-path "/public"
              ::http/type          :jetty
              ::http/port          (config/get-port config/config)})
