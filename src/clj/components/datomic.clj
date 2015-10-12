(ns components.datomic
  (:require [com.stuartsierra.component :as component]
            [taoensso.timbre :as timbre :refer (tracef debugf infof warnf errorf)]
            [backend.dbschema :refer [get-schema]]
            [datomic.api :as d]))

(defrecord Datomic [host conn]
  component/Lifecycle
  (start [this]
    (let [db   (d/create-database host)
          conn (d/connect host)]
          (d/transact conn (get-schema))
          (infof "Datomic ansluten")
      (assoc this :conn conn)))
  (stop [this]
    (infof "Datomic nerkopplad")
    (d/release conn)
    (assoc this :conn nil)))

(defn new-datomic [host]
  (map->Datomic {:host host}))
