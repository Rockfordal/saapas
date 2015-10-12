(ns components.app
  (:require [com.stuartsierra.component :as component]
            [taoensso.timbre :as timbre :refer (tracef debugf infof warnf errorf)]))

(defrecord App []
  component/Lifecycle
  (start [component]
    (debugf "App logik startad")
    component)
  (stop [component]
    (debugf "App logik stoppad")
    component))

(defn new-app []
  (map->App {}))
