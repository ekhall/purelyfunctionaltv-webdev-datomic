(ns webdev.core
  (:require [ring.adapter.jetty :as jetty]
            [ring.middleware.reload :refer [wrap-reload]]
            [compojure.core :refer [defroutes GET]]
            [compojure.route :refer [not-found]]
            [ring.handler.dump :refer [handle-dump]]))

(defn greet
  [req]
  {:status 200
   :body "Hello, World!"
   :headers {}})

(defn goodbye
  [req]
  {:status 200
   :body "Goodbye cruel world!"
   :headers {}})

(defn about
  [req]
  {:status 200
   :body "This is me, about me"
   :headers {}})

(defn yo-name
  [req]
  (let [name (get-in req [:route-params :name])]
    {:status 200
     :body (str "Yo! " name "!+++" handle-dump)}))

(def ops
  {"+" + "-" - "*" * ":" /})

(defn calc
  [req]
  (let [a (Integer. (get-in req [:route-params :a]))
        op (get-in req [:route-params :op])
        f (get ops op)
        b (Integer. (get-in req [:route-params :b]))]
    (if f
      {:status 200
       :body (str "Answer is: " (f a b))})))

(defroutes app
  (GET "/" [] greet)
  (GET "/goodbye" [] goodbye)
  (GET "/about" [] about)
  (GET "/request" [] handle-dump)
  (GET "/yo/:name" [] yo-name)
  (GET "/calc/:a/:op/:b" [] calc)
  (not-found "Page not found!"))

(defn -main [port]
  (jetty/run-jetty app                 {:port (Integer. port)}))

(defn -dev-main [port]
  (jetty/run-jetty (wrap-reload #'app) {:port (Integer. port)}))
