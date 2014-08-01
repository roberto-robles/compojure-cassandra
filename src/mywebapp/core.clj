(ns mywebapp.core
  (:use ring.middleware.params)
  (:require [clojurewerkz.cassaforte.client :as cc]
            [clojurewerkz.cassaforte.cql    :as cql]
            [clojurewerkz.cassaforte.query :refer :all]
            [clojure.data.json :as json]))

(use '[ring.middleware.json :only [wrap-json-response]]
     '[ring.util.response :only [response]])

(cc/connect ["127.0.0.1"])

;; Check for Id and query Cassandra Database to retrieve patient information
(defn patient-handler [request]
  (def patient-id (get (read-string request) :id))
  (let [conn  (cc/connect ["127.0.0.1"])
        table "patient_info"]
    (cql/use-keyspace conn "clojure_test")
    (def result (cql/select conn table (where :id (read-string patient-id)))))
    (json/write-str result))

;; Add or edit an existing patient_info record
(defn patient-add-handler [request]
  (def post-data (read-string request))
  (println post-data)
  (let [conn (cc/connect ["127.0.0.1"])]
    (cql/use-keyspace conn "clojure_test")
    (cql/insert conn "patient_info" {:name (post-data :name) :id (read-string (post-data :id)) :info (post-data :info)})
    (println "Patient Added"))

  (println "patient-add-handler")
  (json/write-str {:success true :message "Completed succesfully"}))

(defn json-example-handler [request]
  (json/write-str {:success true :message "This is a JSON"}))

(def patient-app
  (wrap-json-response patient-handler))
