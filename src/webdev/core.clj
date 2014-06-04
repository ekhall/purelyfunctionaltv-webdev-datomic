(ns webdev.core
  (:require [webdev.item.model-datomic :as items]
            [webdev.item.handler :refer [handle-index-items
                                         handle-create-item
                                         handle-delete-item
                                         handle-update-item]])
  (:require [ring.adapter.jetty :as jetty]
            [ring.middleware.reload :refer [wrap-reload]]
            [ring.middleware.params :refer [wrap-params]]
            [ring.middleware.resource :refer [wrap-resource]]
            [ring.middleware.file-info :refer [wrap-file-info]]
            [compojure.core :refer [defroutes ANY GET POST PUT DELETE]]
            [compojure.route :refer [not-found]]
            [ring.handler.dump :refer [handle-dump]]))

(def db-pg "jdbc:postgresql://localhost/webdev")
(def db "datomic:dev://localhost:4334/webdev")

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

(defroutes routes
  (GET "/" [] handle-index-items)
  (ANY "/request" [] handle-dump)
  (GET "/yo/:name" [] yo-name)
  (GET "/calc/:a/:op/:b" [] calc)

  (GET "/items" [] handle-index-items)
  (POST "/items" [] handle-create-item)
  (DELETE "/items/:item-id" [] handle-delete-item)
  (PUT "/items/:item-id" [] handle-update-item)

  (not-found "Page not found!"))

(defn wrap-db [hdlr]
  ;; In model layer, need a referene to the db
  ;; ensude db is available to all paramsters.
  (fn [req]
    (hdlr (assoc req :webdev/db db))))

(defn wrap-server-name [hdlr]
  (fn [req]
    (assoc-in (hdlr req) [:headers "Server"] "Datomickev")))

(def sim-methods {"PUT" :put
                   "DELETE" :delete})

(defn wrap-simulated-methods [hdlr]
  (fn [req]
    (if-let [method (and (= :post (:request-method req))
                         (sim-methods (get-in req [:params "_method"])))]
      (hdlr (assoc req :request-method method))
      (hdlr req))))

(def app
  (-> (wrap-simulated-methods routes)
      (wrap-params)
      (wrap-db)
      (wrap-resource "static")
      (wrap-file-info)
      (wrap-server-name)))

(defn -main [port]
  (items/create-table db)
  (jetty/run-jetty app                 {:port (Integer. port)}))

(defn -dev-main [port]
  (items/create-table db)
  (jetty/run-jetty (wrap-reload #'app) {:port (Integer. port)}))
