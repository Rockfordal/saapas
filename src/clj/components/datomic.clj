(ns components.datomic
  (:require [com.stuartsierra.component :refer [Lifecycle using]]
            [taoensso.timbre :refer (tracef debugf infof warnf errorf)]
            [backend.dbschema :refer [get-schema]]
            [datomic.api :as d]))

(defrecord Datomic [host conn]
  Lifecycle
  (start [this]
    (let [db   (d/create-database host)
          conn (d/connect host)]
          (d/transact conn (get-schema))
          (debugf "Datomic ansluten")
      (assoc this :conn conn)))
  (stop [this]
    (debugf "Datomic nerkopplad")
    ;(d/release conn)
    (d/shutdown false)
    (assoc this :conn nil)))

(defn new-datomic [dbhost]
  (map->Datomic {:host dbhost}))
