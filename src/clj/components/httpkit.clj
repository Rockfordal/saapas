(ns components.httpkit
  (:require [com.stuartsierra.component :refer [Lifecycle using]]
            [taoensso.timbre :refer [tracef debugf infof warnf errorf]]
            [org.httpkit.server :refer [run-server]]))

(defrecord HttpKit [port make-handler]
  Lifecycle
  (start [this]
    (let [port (or port 3000)]
      (debugf (str "Starting web server on http://localhost:" port))
      (assoc this :http-kit
                  (run-server (make-handler (:webapp this))
                    {:port port
                     :join? false}))))
  (stop [this]
    (if-let [http-kit (:http-kit this)]
      (http-kit))
    (assoc this :http-kit nil)))

(defn new-httpkit [opts]
  (using
    (map->HttpKit opts)
    [:webapp]))
