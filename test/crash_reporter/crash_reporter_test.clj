(ns crash-reporter.crash-reporter-test
  (:require [clojure.test :as test]
            [crash-reporter.crash-reporter :as service]
            [clojure.java.jdbc :as db]
            [crash-reporter.queue-sender :as sender]
            [environ.core :refer [env]]
            [io.pedestal.test :as pedestal-test]
            [org.httpkit.fake :refer [with-fake-http]]
            [io.pedestal.http :as http]
            [clojure.data.json :as json]))

;; The params is taken from docker-compose.dev.yml
(def pg-db {:dbtype "postgresql"
            :dbname "crashes"
            :host "d_db"
            :user "user"
            :password "123456"})

(defn- clear-db [] 
  (db/delete! pg-db :crashes []))

(defn- test-fixture [f]
  (f)
  (clear-db))

(test/use-fixtures :each test-fixture)

(def service
  "Service under test"
  (::http/service-fn (http/create-servlet service/service)))

(test/deftest report-crash-test
  (let [webhook-url (env :slack-webhook)
        body (json/write-str {:message "test" :stack "test.js:12" :additionalData {:device "Mi Note 9" :os "Android 11"}})]
        (print webhook-url)
    (with-fake-http [{:url webhook-url :method :post} {:status 200 :body "ok"}]
      (sender/connect) ;; as we don't run the whole server, we connect here
      (test/is (= 200 (:status (pedestal-test/response-for service
                                        :post "/report-crash"
                                        :headers {"Content-Type" "application/json"}
                                        :body body))))
      (Thread/sleep 1000)))) ;; to complete sending of data to Slack

(test/deftest message-handler-test
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
      (test/is (= "ok 200\n" (with-out-str (service/message-handler  nil nil payload)))))))

(test/deftest message-handler-test-without-additional-data
  (let [webhook-url (env :slack-webhook)
        payload (.getBytes (json/write-str {:message "test" :stack "test.js:12"}))
        expected-body {:text "test" :attachments [{:color "#ff0000" :title "Stack trace" :text "test.js:12"}
                                                  {:color "#ff0000" :title "Additional Info" :text "Not provided"}]}]
    (with-fake-http [{:url webhook-url :method :post}
                     (fn [_ opts _]
                       (if (= (json/read-str (:body opts) :key-fn keyword) expected-body)
                         {:status 200 :body "ok"}
                         {:status 400 :body "incorrect body"}))]
      (test/is (= "ok 200\n" (with-out-str (service/message-handler  nil nil payload)))))))

(test/deftest message-handler-should-write-to-db
  (let [webhook-url (env :slack-webhook)
        deviceInfo {:device "Iphone 11" :os "IOS 11"}
        crashData {:message "test" :stack "test.js:12" :additionalData deviceInfo}
        payload (.getBytes (json/write-str crashData))]
    (with-fake-http [{:url webhook-url :method :post}
                     (fn [_ _ _] {:status 200 :body "ok"})]
      (service/message-handler  nil nil payload)
      (test/is (= crashData (first (map
                                    (comp #(json/read-str % :key-fn keyword) str)
                                    (db/query pg-db  ["select * from crashes"] {:row-fn :data}))))))))
