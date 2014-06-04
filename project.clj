(defproject webdev "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [ring "1.2.1"]
                 [com.datomic/datomic-pro "0.9.4766.16"]
                 [compojure "1.1.8"]
                 [org.clojure/java.jdbc "0.3.3"]
                 [postgresql/postgresql "9.1-901.jdbc4"]
                 [hiccup "1.0.5"]
                 ]
  :main webdev.core
  :min-lein-version "2.0.0"
  :uberjar-name "webdev.jar"
  :profiles {:dev
             {:main webdev.core/-dev-main}})
