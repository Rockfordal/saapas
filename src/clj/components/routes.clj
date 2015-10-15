(ns components.routes
  (:require [com.stuartsierra.component :refer [Lifecycle using]]
            [taoensso.timbre :refer (tracef debugf infof warnf errorf)]))

(defrecord Routes [datomic]
  Lifecycle
  (start [self]
    (debugf (str "Routing startad "))
    self)
  (stop [self]
    (debugf "Routing stoppad")
    self))

(defn new-routes []
  (using
    (map->Routes {})
    [:datomic]))
