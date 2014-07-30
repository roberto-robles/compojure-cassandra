(ns mywebapp.views
  (:use [hiccup core page]))

(defn header []
  [:div.navbar.navbar-inverse.navbar-fixed-top
   [:div.navbar-inner
    [:div.container
     [:a.brand {:href "/"} "Compojure - Cassandra example"]
     [:div.nav-collapse.collapse
      [:ul.nav
       [:li.active [:a {:href "/"} "Home"]]
       [:li.active [:a {:href "/about"} "About"]]]]]]])

(defn template [& body]
  (html5
   [:head
    [:title "Compojure - Cassandra Example"]
    [:meta {:name "viewport"
            :content "width=device-width, initial-scale=1.0"}]
    (include-css "/css/bootstrap.min.css")
    (include-js "http://code.jquery.com/jquery-2.1.0.min.js")
    (include-js "/js/functions.js")]
   [:body {:style "padding-top:60px;"}
    (header)
    [:div.container
     body]]))

(defn index-page []
  (template

   [:div {:class "hero-unit"}
    [:h1 "Index"]
    [:p "Welcome to the Clojure Clickstart!"]
    [:div {:class "row"}
      [:div {:class "col-md-4"}
        [:a {:class "btn btn-primary btn-large"
             :href "/bluebutton"}
        "View patient Info"]
      ]
      [:br]
      [:div {:class "col-md-4"}
        [:a {:class "btn btn-primary btn-large"
          :href "/bluebutton/patient"}
        "Add/Update Patient Info by MRN"]]
      [:br]
      [:div {:class "col-md-4"}
        [:a {:class "btn btn-primary btn-large"
          :href "/bluebutton/patient"}
        "View Bluebutton Data"]]
      ]]

        ; [:div {:class "well"}
        ; [:p
        ;   [:h3 "Next steps"]
        ;   [:ul
        ;     [:li (str "Make sure you have an ssh public key setup on"
        ;               "<a href=\"https://grandcentral.cloudbees.com/user/ssh_keys\">
        ;               Cloudbees</a> and then run:")]
        ;     [:li
        ;       [:code (str
        ;               "git clone ssh://git@git.cloudbees.com/"
        ;               "<script>document.write"
        ;                 "(location.hostname.split(\".\")[1])</script>"
        ;               "/<script>document.write"
        ;               "(location.hostname.split(\".\")[0])</script>.git")]]
        ;     [:li "Make your changes and then push to the git repo"]
        ;     [:li "This will trigger a build and deploy cycle on cloudbees"]]]]))
    ))

(defn about-page []
  (template
   [:div {:class "well"}
    [:h1 "About This:"]
    [:p "This Clojure clickstart was developed by members of the "
     [:a {:href "http://www.meetup.com/Austin-Clojure-Meetup/"} "Austin Clojure Meetup"]
     ".  You can find us as "
     [:a {:href "https://github.com/AustinClojure"} "AustinClojure on github"]
     "."]
     [:h1 "About ClickStarts:"]
      "Read about what ClickStarts are "
     [:a {:href "https://developer.cloudbees.com/bin/view/RUN/ClickStart"} "at CloudBees"]

     ]))

(defn patient-page []
  (template

   [:div {:class "hero-unit"}
    [:h1 "Welcome to  bluebutton test with Clojure and Cassandra"]
    [:p "Add a patient"]
    [:input {:name "id" :class "form-control" :placeholder "Patient's MRN" :type "text"}]
    [:input {:name "name" :class "form-control" :placeholder "Patient Full Name" :type "text"}]
    [:input {:name "info" :class "form-control" :placeholder "Patient Info" :type "text"}]
    [:button {:class "btn btn-success" :id "sendPatient"} "Send"]
    [:br]]))


(defn bluebutton-page []
  (template

   [:div {:class "hero-unit"}
    [:h1 "Welcome to  bluebutton test with Clojure and Cassandra"]
    [:p "Please select a Patient to retrieve the bluebutton patient data"]
    [:select {:id "patients"}
      [:option {:value "1"} "Patient A" ]
      [:option {:value "2"} "Patient B" ]
      [:option {:value "3"} "Patient C" ]
    ]
    [:br]
    [:textarea {:class "form-control" :style "width: 100%;" :rows "6" :readonly "true" :id "result"} ""]]))
