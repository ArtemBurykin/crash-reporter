(ns crash-reporter.crash-reporter-test
  (:require [clojure.test :refer :all]
            [crash-reporter.crash-reporter :as service]
            [crash-reporter.queue-sender :as sender]
            [clojure.java.io :as io]
            [io.pedestal.test :refer :all]
            [org.httpkit.fake :refer [with-fake-http]]
            [crash-reporter.config :as config]
            [io.pedestal.http :as http]
            [clojure.data.json :as json]))

(def service
  "Service under test"
  (::http/service-fn (http/create-servlet service/service)))

(deftest report-crash-test
  (let [webhook-url (config/get-slack-hook (config/parse-config (-> "config.json" io/resource)))]
    (with-fake-http [{:url webhook-url :method :post} {:status 200 :body "ok"}]
      (sender/connect) ;; as we don't run the whole server, we connect here
      (is (= 200 (:status (response-for service
                                        :post "/report-crash"
                                        :headers {"Content-Type" "application/json"}
                                        :body "{\"message\":\"test\", \"stack\":\"test.js:12\"}"))))
      (Thread/sleep 1000)))) ;; to complete sending of data to Slack

(deftest message-handler-test
  (let [webhook-url "http://hooks.slack.com/services/fake-hook" ;; the value is specified in test/resources/config.json
        payload (.getBytes "{\"message\":\"test\", \"stack\": \"test.js:12\"}")
        expected-body {:text "test" :attachments [{:color "#ff0000" :title "Stack trace" :text "test.js:12"}]}]
    (with-fake-http [{:url webhook-url :method :post}
                     (fn [_ opts _]
                       (if (= (json/read-str (:body opts) :key-fn keyword) expected-body)
                         {:status 200 :body "ok"}
                         {:status 400 :body "incorrect body"}))]
      (is (= "ok 200\n" (with-out-str (service/message-handler  nil nil payload)))))))