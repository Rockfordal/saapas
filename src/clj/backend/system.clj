(ns backend.system
  (:require [com.stuartsierra.component :as component]
            [taoensso.sente.server-adapters.http-kit :refer (sente-web-server-adapter)]
            [taoensso.sente.packers.transit :as sente-transit]
            [components.datomic :refer [new-datomic]]
            [components.sente   :refer [new-channel-sockets]]
            [components.routes  :refer [new-routes]]
            [components.httpkit :refer [new-httpkit]]
            [components.app     :refer [new-app]]
            [backend.ws         :refer [event-msg-handler*]]
            [backend.server     :refer [make-handler]]))

(def dbhost "datomic:dev://localhost:4334/frejm")
(def ednpacker (sente-transit/get-flexi-packer :edn))
(def httpopts  {:handler make-handler})
(def senteopts {:packer ednpacker
                ;:handler #'app
                })

(defn new-sente []
  (new-channel-sockets event-msg-handler* sente-web-server-adapter senteopts))

(defn new-system [opts]
  (component/system-map
    :datomic (new-datomic dbhost)
    :routes  (new-routes)
    :sente   (new-sente)
    :httpkit (new-httpkit httpopts)
    :app     (new-app {:msg "Hej"})))


(comment
; Service Provider

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
    (using (map->Customers {}) [:db :email]))

  (system-map
    :customers (customers)
    :email     (->Email))

  (defn vanlig [config]
    (let [{:keys [port dburi]} config]
       :ws-conn     (new-ws)
       :http-server (using (new-http-server port) [:ws-conn])))
)
