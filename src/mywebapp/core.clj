(ns mywebapp.core
  (:use ring.middleware.params)
  (:require [clojurewerkz.cassaforte.client :as cc]
            [clojurewerkz.cassaforte.cql    :as cql]
            [clojurewerkz.cassaforte.query :refer :all]
            [clojure.data.json :as json]))

(use '[ring.middleware.json :only [wrap-json-response]]
     '[ring.util.response :only [response]])

(cc/connect ["127.0.0.1"])

;; Helper function for serializing JSON
; (defn json-response [data & [status]]
;   {:status (or status 200)
;    :headers {"Content-Type" "application/json"}
;    :body (json/generate-string data)})

;; Check for Id and query Cassandra Database to retrieve patient information
(defn patient-handler [request]
  (def patient-id (get (read-string request) :id))
  (let [conn  (cc/connect ["127.0.0.1"])
        table "patient_info"]
    (cql/use-keyspace conn "clojure_test")
    (def result (cql/select conn table (where :id (read-string patient-id)))))
    ; (println result))

  ;(wrap-json-response (response {:body {:foo "body"}}))
  ;(response "{\"success\": true}"))
  (json/write-str result))

(def patient-app
  (wrap-json-response patient-handler))
