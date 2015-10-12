(ns backend.system
  (:require [com.stuartsierra.component :as component]
            [taoensso.sente.server-adapters.http-kit :refer (sente-web-server-adapter)]
            [taoensso.sente.packers.transit :as sente-transit]
            [components.httpkit :refer [new-web-server]]
            [components.sente   :refer [new-channel-sockets]]
            [components.datomic :refer [new-datomic]]
            [components.routes  :refer [new-routes]]
            [components.app     :refer [new-app]]
            [backend.ws         :refer [event-msg-handler*]]
            [backend.server     :refer [app make-handler]]))

(def dbhost "datomic:dev://localhost:4334/frejm")
(def senteopts {:packer (sente-transit/get-flexi-packer :edn)
                :handler #'app})

(defn new-sente []
  (new-channel-sockets event-msg-handler* sente-web-server-adapter senteopts))

(defn new-system [opts]
  (component/system-map
    :datomic (new-datomic dbhost)
    :sente   (new-sente)
    ;:routes  (component/using (new-routes #'make-routes) [:datomic])
    ;:web     (component/using (new-web-server) [:handler])
    :web     (new-web-server  {:handler (make-handler nil)})
    ;:web     (new-web-server  {:handler #'app})
    ;:app     (new-app)
    ))

; Service Provider
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
    (component/using
      (map->Customers {})
      [:db :email]))

  (defn systen []
    (component/system-map
      :customers (customers)
      :db (db ...)
      :email (->Email)
    ))
  )

;; (defn vanlig [config]
;;   (let [{:keys [port dburi]} config]
;;      :ws-conn       (new-ws)
;;      :http-server   (using (new-http-server port)
;;                            [:ws-conn])
