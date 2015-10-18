(ns backend.server
  (:require [compojure.core :as cj :refer [defroutes GET POST]]
            [compojure.route :as route :refer [resources]]
            [compojure.handler :refer [api]]
            [ring.middleware.edn :refer [wrap-edn-params]]
            [ring.util.response :refer [redirect]]
            [ring.util.http-response :refer :all]
            [ring.middleware.reload :refer [wrap-reload]]
            ;[ring.middleware.resource :refer (wrap-resource)]
            [ring.middleware.defaults :refer [site-defaults wrap-defaults]]
            [reloaded.repl :refer [system]]
            [backend.db :refer [get-state]]
            [backend.index :refer [index-page test-page]]))

;; (defn wrap-dir-index [handler]
;;   (fn [req]
;;     (handler
;;      (update-in req [:uri]
;;        #(if (= "/" %) "/index.html" %)))))

(defn texthtml [res]
  (content-type res "text/html"))

(defn edn-res [data & [status]]
  {:status (or status 200)
   :headers {"Content-Type" "application/edn"}
   :body (pr-str data)})

(defn testwebapp [req]
  (let [webapp  (::webapp req)
        datomic (:datomic webapp)
        conn    (:conn datomic)]
    (if conn
      (str
        "Ja vi har conn! \n"
        conn
        )
      "Nej vi har inte conn")
  ;"Mja"
  ))

;; (defn ednstate [db]
;;   (edn-res (get-state db)))

;; (defn myroutes [db]
;;   (cj/routes
(defroutes routes
  (resources "/js"  {:root "js"})
  (resources "/css" {:root "css"})
  (GET "/"     [] (-> (ok index-page) (texthtml)))
  (GET "/hello" [] (-> (ok test-page)  (texthtml)))
  ;(GET  "/chsk" req ((:ring-ajax-get-or-ws-handshake (:sente system)) req))
  ;(POST "/chsk" req ((:ring-ajax-post (:sente system)) req))
  ;(GET "/test" req (testwebapp req))
  (GET "/test" req (-> (ok (testwebapp req)) (texthtml)))

  ;(if (nil? db)
  ;  (GET "/getstate" _ (-> (ok "getstate nil") (texthtml)))
  ;  (GET "/getstate" _ (ednstate)))
  (route/not-found "<h1>Sidan kan inte hittas</h1>"))

(defn wrap-app-component [f webapp]
  (fn [req]
    (f (assoc req ::webapp webapp))))

(defn make-handler [web-app]
  (let [ring-defaults-config
        (-> site-defaults
          (assoc-in [:security :anti-forgery]
                    {:read-token (fn [req] (-> req :params :csrf-token))})
          (assoc-in [:static :resources] "public"))]
  (-> routes
      ;(wrap-defaults ring-defaults-config) ; site-defaults
      ;(wrap-resource "/META-INF/resources")
      ;(wrap-dir-index)
      (wrap-edn-params)
      (wrap-app-component web-app)
    )))

;; (defn make-handler [db]
;;   (-> (myroutes db))))
