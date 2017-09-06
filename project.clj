(defproject pik-logistic-loader "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [mount "0.1.11"]
                 [org.clojure/core.async "0.3.443"]
                 [org.clojure/tools.logging "0.4.0"]
                 [com.brunobonacci/safely "0.3.0"]
                 [org.slf4j/slf4j-log4j12 "1.7.25"]
                 [log4j/log4j "1.2.17" :exclusions [javax.mail/mail
                                                    javax.jms/jms
                                                    com.sun.jmdk/jmxtools
                                                    com.sun.jmx/jmxri]]
                 [clj-http "3.7.0"]
                 [cheshire "5.8.0"]
                 [com.brunobonacci/safely "0.3.0"]
                 [cprop "0.1.11"]
                 [com.layerware/hugsql "0.4.7"]
                 [org.clojure/java.jdbc "0.7.0"]
                 [net.sourceforge.jtds/jtds "1.3.1"]
                 [clj-time "0.14.0"]
                 [org.clojure/tools.cli "0.3.5"]]
  :main pik-logistic-loader.core
  :profiles {:uberjar {:omit-source true
                       ;:aot [pik-logistic-loader.core]
                       :aot :all
                       :uberjar-name "pik-logistic-loader.jar"}})
                       ;:resource-paths ["resources"]}})
