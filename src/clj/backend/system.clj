(ns backend.system
  (:require [com.stuartsierra.component :as component]
            [taoensso.sente.server-adapters.http-kit :refer (sente-web-server-adapter)]
            [taoensso.sente.packers.transit :as sente-transit]
            [components.datomic :refer [new-datomic]]
            [components.sente   :refer [new-channel-sockets]]
            [components.routes  :refer [new-routes]]
            [components.httpkit :refer [new-httpkit]]
            [components.app     :refer [new-webapp]]
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
    :webapp  (new-webapp "Hej")))


(comment
  (system-map
    :customers (customers)
    :email     (->Email))

  (defn vanlig [config]
    (let [{:keys [port dburi]} config]
       :ws-conn     (new-ws)
       :http-server (using (new-http-server port) [:ws-conn])))
)
