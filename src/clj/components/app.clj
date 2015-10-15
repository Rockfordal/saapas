(ns components.app
  (:require [com.stuartsierra.component :refer [Lifecycle using]]
            [taoensso.timbre :refer (tracef debugf infof warnf errorf)]))

(defrecord App []
  Lifecycle
  (start [self]
    (debugf (str "Saapas startad " (:msg self)))
    self)
  (stop [self]
    (debugf "App logik stoppad")
    self))

(defn new-app [msg]
  (map->App {:msg msg}))
