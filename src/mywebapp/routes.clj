(ns mywebapp.routes
  (:use compojure.core
        mywebapp.views
        mywebapp.core)
  (:require [compojure.handler :as handler]
            [compojure.route :as route]))


(defroutes app-routes
  (GET "/" [] (index-page))
  (GET "/about" [] (about-page))
  (GET "/bluebutton" [] (bluebutton-page))
  (GET "/bluebutton/patient" [] (patient-page))
  (POST "/bluebutton/patient" {params :params} (patient-handler (pr-str params)))
  (POST "/bluebutton/patient/add" {params :params} (patient-add-handler (pr-str params)))
  (route/resources "/")
  (route/not-found "No page"))


(def app
  (handler/site app-routes))
