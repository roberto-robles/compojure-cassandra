(ns mywebapp.routes
  (:use compojure.core
        mywebapp.views
        mywebapp.core)
  (:require [compojure.handler :as handler]
            [compojure.route :as route]
            [liberator.core :refer [resource defresource]]
            [ring.middleware.params :refer [wrap-params]]
            [liberator.dev :refer [wrap-trace]]))


(def counter (ref 0))

(defresource parameter [txt]
  :available-media-types ["text/plain"]
  :handle-ok (fn [_] (format "The text is %s" txt)))

(def posts (ref []))

(defroutes app-routes
  (GET "/" [] (index-page))
  (GET "/about" [] (about-page))
  (GET "/bluebutton" [] (bluebutton-page))
  (GET "/bluebutton/patient" [] (patient-page))
  (ANY "/json" {params :params} (json-example-handler (pr-str params)))
  (POST "/bluebutton/patient" {params :params} (patient-handler (pr-str params)))
  (POST "/bluebutton/patient/add" {params :params} (patient-add-handler (pr-str params)))

  ;; Here is a sample of how to return a resource
  (ANY "/api" [] (resource :available-media-types ["text/html"]
                           :handle-ok (fn [ctx]
                                        (format "<html>It's %d milliseconds since the beginning of the epoch."
                                                (System/currentTimeMillis)))))

  ;; This is the correct way to call an API endpoint reusing functions
  (ANY "/foo" [] (resource :available-media-types ["text/html"]
                           :handle-ok (fn [_] (format "The counter is %d" @counter))))

  ;; Here we use a previous defined resource
  (ANY "/bar/:txt" [txt] (parameter txt))

  ;; Resource that checks for parameter to return 200 or not-found (Decision feature)
  ;; This example checks if parameter "word" has value "tiger"
  (ANY "/secret" []
       (resource :available-media-types ["text/html"]
                 :exists? (fn [ctx]
                            ; (println ctx)
                            ; (println "\n" (get-in ctx [:request :params :word]))
                            (= "tiger" (get-in ctx [:request :params :word])))
                 :handle-ok "You found the secret word!"
                 :handle-not-found "Uh, that's the wrong word. Guess again!"))

  ;; The following resource checks against a set of choices
  ;; Depending on the numeric value it will return stone (1), paper (2) or scissors (3)
  (ANY "/choice" []
       (resource :available-media-types ["text/html"]
                 :exists? (fn [ctx]
                            (if-let [choice
                                     (get {"1" "stone" "2" "paper" "3" "scissors"}
                                          (get-in ctx [:request :params :choice]))]
                              {:choice choice}))
                 :handle-ok (fn [ctx]
                              (format "<html>Your choice: &quot;%s&quot;."
                                        (get ctx :choice)))
                 :handle-not-found (fn [ctx]
                                     (format "<html>There is no value for the option &quot;%s&quot;"
                                             (get-in ctx [:request :params "choice"] "")))))

  ;; This is how content negotiation is handled
  (ANY "/babel" []
       (resource :available-media-types ["text/plain" "text/html"
                                         "application/json" "application/clojure;q=0.9"]
                 :handle-ok
                 #(let [media-type
                        (get-in % [:representation :media-type])]
                    (condp = media-type
                      "text/plain" "You requested plain text"
                      "text/html" "<html><h1>You requested HTML</h1></html>"
                      {:message "You requested a media type"
                       :media-type media-type}))
                 :handle-not-acceptable "Uh, Oh, I cannot speak those languages!"))

  ;; Conditional request based on Last modified since:
  (ANY "/timehop" []
       (resource
        :available-media-types ["text/plain"]
        ;; timestamp changes every 10s
        :last-modified (* 10000 (long  (/ (System/currentTimeMillis) 10000)))
        :handle-ok (fn [_] (format "It's now %s" (java.util.Date.)))))

  ;; Conditional request based on eTag
  (ANY "/changetag" []
       (resource
        :available-media-types ["text/plain"]
        ;; etag changes every 10s
        :etag (let [i (int (mod (/ (System/currentTimeMillis) 10000) 10))]
                (.substring "abcdefhghijklmnopqrst"  i (+ i 10)))
        :handle-ok (format "It's now %s" (java.util.Date.))))

  ;; POST example when you are using different HTTP methods you must be specific:
  (ANY "/postbox" []
       (resource
        :allowed-methods [:post :get]
        :available-media-types ["text/html"]
        :handle-ok (fn [ctx]
                     (format  (str "<html>Post text/plain to this resource.<br>\n"
                                   "There are %d posts at the moment.")
                              (count @posts)))
        :post! (fn [ctx]
                 (dosync
                  (let [body (slurp (get-in ctx [:request :body]))
                        id   (count (alter posts conj body))]
                    {::id id})))
        ;; actually http requires absolute urls for redirect but let's
        ;; keep things simple.
        :post-redirect? (fn [ctx] {:location (format "/postbox/%s" (::id ctx))})))

  (ANY "/cond-postbox" []
       (resource
        :allowed-methods [:post :get]
        :available-media-types ["text/html"]
        :handle-ok (fn [ctx]
                     (format  (str "<html>Post text/plain to this resource.<br>\n"
                                   "There are %d posts at the moment.")
                              (count @posts)))
        :post! (fn [ctx]
                 (dosync
                  (let [body (slurp (get-in ctx [:request :body]))
                        id   (count (alter posts conj body))]
                    {::id id})))
        ;; actually http requires absolute urls for redirect but let's
        ;; keep things simple.
        :post-redirect? (fn [ctx] {:location (format "/postbox/%s" (::id ctx))})
        :etag (fn [_] (str (count @posts)))))

  (ANY "/postbox/:x" [x]
       (resource
        :allowed-methods [:get]
        :available-media-types ["text/html"]
        :exists? (fn [ctx] (if-let [d (get @posts (dec (Integer/parseInt x)))] {::data d}))
        :handle-ok ::data))

  (route/resources "/")
  (route/not-found "No page"))

;; Examples of how to debug a Liberator compojure resuorce

; (def dbg-counter  (atom 0))
; (defresource dbg-resource
;   :available-media-types ["text/plain"]
;   :allowed-methods [:get :post]
;   :handle-ok (fn [_] (format "The counter is %d" @dbg-counter))
;   :post! (fn [_] (swap! dbg-counter inc)))

; (defroutes app
;   (ANY "/dbg-count" [] dbg-resource))

; (def handler
;   (-> app
;       (wrap-trace :header :ui)))
;; EOF Examples of how to debug a Liberator compojure resuorce


(def handler
  (-> app-routes
      (wrap-params)))

(def app
  (handler/site handler))
