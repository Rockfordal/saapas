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

(defn ednstate [db]
  (edn-res (get-state db)))

(defn myroutes [db]
  (cj/routes
      (resources "/js"  {:root "js"})
      (resources "/css" {:root "css"})
      (GET "/"     [] (-> (ok index-page) (texthtml)))
      (GET "/test" [] (-> (ok test-page)  (texthtml)))
      (GET  "/chsk" req ((:ring-ajax-get-or-ws-handshake (:sente system)) req))
      (POST "/chsk" req ((:ring-ajax-post (:sente system)) req))
      (if (nil? db)
        (GET "/getstate" _ (-> (ok "getstate nil") (texthtml)))
        (GET "/getstate" _ (ednstate)))
      (route/not-found "<h1>Sidan kan inte hittas</h1>")))

(defn make-handler [db]
  (let [ring-defaults-config
        (-> site-defaults
          (assoc-in [:security :anti-forgery]
                    {:read-token (fn [req] (-> req :params :csrf-token))})
          (assoc-in [:static :resources] "public"))]
  (-> (myroutes db)
      (wrap-defaults site-defaults)
      ;(wrap-dir-index)
      ;(wrap-resource "/META-INF/resources")
      (wrap-edn-params))))

(comment
  (defroutes ...)

  (defn wrap-app-component [f web-app]
    (fn [req]
      (f (assoc req ::web-app web-app))))

  (-> routes
    (wrap-app-component web-app))

   ;http-kit
   (run-server (make-handler web-app))

   (defn web-server []
     (component/using (map->WebServer {})
       [:web-app]))
  )

(def app
  (make-handler nil))
