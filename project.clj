(defproject mywebapp "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :dependencies [[org.clojure/clojure "1.4.0"]
                 [compojure "1.1.1"]
                 [hiccup "1.0.1"]
                 [ring/ring-json "0.3.1"]
                 [clojurewerkz/cassaforte "2.0.0-beta1"]
                 [org.clojure/data.json "0.2.5"]
                 [liberator "0.10.0"]]
  :plugins [[lein-ring "0.7.3"]]
  :ring {:handler mywebapp.routes/app}
  :profiles
  {:dev {:dependencies [[ring-mock "0.1.3"]]}})
