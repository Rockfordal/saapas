(ns components.routes
  (:require [com.stuartsierra.component :refer [Lifecycle using]]
            [taoensso.timbre :refer (tracef debugf infof warnf errorf)]))

(defrecord Routes [datomic]
  Lifecycle
  (start [self]
    (debugf (str "Routing startad " (:msg self)))
     self)
  (stop [self]
    (debugf "Routing stoppad")
     self))

(defn new-routes [msg]
  (using
    (map->Routes {:msg msg})
    [:datomic]))
