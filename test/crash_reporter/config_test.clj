(ns crash-reporter.config-test
  (:require [clojure.test :refer :all]
            [crash-reporter.config :as config]))

(deftest get-connection-settings-empty-test
  (is (= {:host "localhost", :port 5672, :username "guest", :vhost "/", :password "guest"}
         (config/get-connection-settings {:slackHook "some-hook"}))))

(deftest get-connection-settings-config-map-test
  (is (= {:host "host", :port 5677, :username "someuser", :vhost "/vhost", :password "userpass"}
         (config/get-connection-settings 
          {:slackHook "some-hook" 
           :rmqConf {:host "host" :username "someuser" :port 5677 :vhost "/vhost" :password "userpass"}}))))
