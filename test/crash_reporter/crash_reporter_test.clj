(ns crash-reporter.crash-reporter-test
  (:require [clojure.test :refer :all]
            [crash-reporter.crash-reporter :as service]
            [crash-reporter.queue-sender :as sender]
            [environ.core :refer [env]]
            [io.pedestal.test :refer :all]
            [org.httpkit.fake :refer [with-fake-http]]
            [io.pedestal.http :as http]
            [clojure.data.json :as json]))

(def service
  "Service under test"
  (::http/service-fn (http/create-servlet service/service)))

(deftest report-crash-test
  (let [webhook-url (env :slack-webhook)
        body (json/write-str {:message "test" :stack "test.js:12" :additionalData {:device "Mi Note 9" :os "Android 11"}})]
        (print webhook-url)
    (with-fake-http [{:url webhook-url :method :post} {:status 200 :body "ok"}]
      (sender/connect) ;; as we don't run the whole server, we connect here
      (is (= 200 (:status (response-for service
                                        :post "/report-crash"
                                        :headers {"Content-Type" "application/json"}
                                        :body body))))
      (Thread/sleep 1000)))) ;; to complete sending of data to Slack

(deftest message-handler-test
  (let [webhook-url (env :slack-webhook)
        deviceInfo {:device "Iphone 11" :os "IOS 11"}
        payload (.getBytes (json/write-str {:message "test" :stack "test.js:12" :additionalData deviceInfo}))
        expected-body {:text "test" :attachments [{:color "#ff0000" :title "Stack trace" :text "test.js:12"}
                                                  {:color "#ff0000" :title "Additional Info" :text (json/write-str deviceInfo)}]}]
    (with-fake-http [{:url webhook-url :method :post}
                     (fn [_ opts _]
                       (if (= (json/read-str (:body opts) :key-fn keyword) expected-body)
                         {:status 200 :body "ok"}
                         {:status 400 :body "incorrect body"}))]
      (is (= "ok 200\n" (with-out-str (service/message-handler  nil nil payload)))))))

(deftest message-handler-test-without-additional-data
  (let [webhook-url (env :slack-webhook)
        payload (.getBytes (json/write-str {:message "test" :stack "test.js:12"}))
        expected-body {:text "test" :attachments [{:color "#ff0000" :title "Stack trace" :text "test.js:12"}
                                                  {:color "#ff0000" :title "Additional Info" :text "Not provided"}]}]
    (with-fake-http [{:url webhook-url :method :post}
                     (fn [_ opts _]
                       (if (= (json/read-str (:body opts) :key-fn keyword) expected-body)
                         {:status 200 :body "ok"}
                         {:status 400 :body "incorrect body"}))]
      (is (= "ok 200\n" (with-out-str (service/message-handler  nil nil payload)))))))