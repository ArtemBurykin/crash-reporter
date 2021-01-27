(ns crash-reporter.service-test
  (:require [clojure.test :refer :all]
            [crash-reporter.crash-reporter :as service]
            [io.pedestal.test :refer :all]
            [org.httpkit.fake :refer [with-fake-http]]
            [io.pedestal.http :as http]
            [clojure.data.json :as json]))

(def service
  "Service under test"
  (::http/service-fn (http/create-servlet service/service)))

(deftest report-crash-test
  (let [webhook-url "http://hooks.slack.com/services/fake-hook"] ;; the value is specified in test/resources/config.json
    (with-fake-http [{:url webhook-url :method :post} {:status 200 :body "ok"}] 
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