(defproject crash-reporter "0.2.1"
  :description "A crash reporter"
  :url "https://avesystems.ru"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.10.0"]
                 [io.pedestal/pedestal.service "0.5.8"]
                 [io.pedestal/pedestal.jetty "0.5.8"]
                 [http-kit "2.4.0-alpha5"]
                 [org.slf4j/slf4j-simple "1.7.28"]
                 [com.novemberain/langohr "5.1.0"]
                 [org.postgresql/postgresql "42.3.1"]
                 [org.clojure/java.jdbc "0.7.12"]
                 [environ "1.2.0"]
                 [org.clojure/data.json "1.0.0"]]
  :repl-options {:init-ns crash-reporter.server}
  :main crash-reporter.server
  :profiles {:uberjar {:aot [crash-reporter.server] :uberjar-name "crash-reporter.jar"}
             :test     {:dependencies   [[http-kit.fake "0.2.1"]]
                       :resource-paths ["test/resources"]}})
