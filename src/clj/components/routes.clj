(ns components.routes
  (:require [com.stuartsierra.component :as component]
            [taoensso.timbre :as timbre :refer (tracef debugf infof warnf errorf)]))

(defrecord Routes [datomic]
  component/Lifecycle
  (start [component]
    ;((:handler component) (:conn datomic))
    (debugf "Routing startad")
    component)
  (stop [component]
    (debugf "Routing stoppad")
    component))

(defn new-routes []
  (map->Routes {}))

;(defn new-routes [make-routes]
;  (map->Routes {:handler make-routes}))

;(:conn (:datomic (:routes system)))
