(ns components.httpkit
  (:require [com.stuartsierra.component :as component]
            ;[taoensso.timbre :as timbre :refer (tracef debugf infof warnf errorf)]
            [org.httpkit.server :refer [run-server]]))


(defrecord HttpKit [port reload reload-dirs handler]
  component/Lifecycle
  (start [this]
    (let [port (or port 3000)]
      (println (str "Starting web server on http://localhost:" port))
      (assoc this
             :http-kit
             (run-server handler
               {:port port
                :join? false}))))
  (stop [this]
    (if-let [http-kit (:http-kit this)]
      (http-kit))
    (assoc this :http-kit nil)))

(defn new-web-server [opts]
  (map->HttpKit opts))
