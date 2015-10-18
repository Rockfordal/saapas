(ns components.webapp
  (:require [com.stuartsierra.component :refer [Lifecycle using]]
            [taoensso.timbre :refer (tracef debugf infof warnf errorf)]))

(defrecord Webapp []
  Lifecycle
  (start [this]
    (debugf (str "Web app startad " (:msg this)))
    this)
  (stop [this]
    (debugf "Web app stoppad")
    this))

(defn new-webapp [msg]
  (using
    (map->Webapp {:msg msg})
    [:datomic :sente]))



(comment

  ; Encapsulated state
  (defrecord Email [endpoint api-key])

  ; Public API provides services
  (defn send [email address body])

  ; Domain Model
  (defrecord Customers [db email])
  (def notify [customers name message]
    (let [{:keys [db email]} customers
          address (query db .. name)]
      (send email address message)))

  (defn customers []
    (using (map->Customers {}) [:db :email])))
