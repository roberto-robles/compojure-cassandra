(ns cassaforte.docs
  (:require [clojurewerkz.cassaforte.client :as cc]
            [clojurewerkz.cassaforte.cql    :as cql]
            [clojurewerkz.cassaforte.query :refer :all]))


(let [conn (cc/connect ["127.0.0.1"])]
  (cql/use-keyspace conn "clojure_test")
  (cql/create-table conn "user_posts"
                (column-definitions {:username :varchar
                                     :post_id  :varchar
                                     :body     :text
                                     :primary-key [:username :post_id]})))

(let [conn  (cc/connect ["127.0.0.1"])
      table "patient_info"]
  (cql/use-keyspace conn "clojure_test")
  (def result (cql/select conn table (where :id 1)))
  (print result))
