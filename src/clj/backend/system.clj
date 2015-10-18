(ns backend.system
  (:require [com.stuartsierra.component :as component]
            [taoensso.sente.server-adapters.http-kit :refer (sente-web-server-adapter)]
            [taoensso.sente.packers.transit :as sente-transit]
            [components.datomic :refer [new-datomic]]
            [components.sente   :refer [new-sente]]
            [components.httpkit :refer [new-httpkit]]
            [components.webapp  :refer [new-webapp]]
            [backend.ws         :refer [event-msg-handler*]]
            [backend.server     :refer [make-handler]]))

(def dbhost "datomic:dev://localhost:4334/frejm")
(def ednpacker (sente-transit/get-flexi-packer :edn))
(def senteopts {:packer ednpacker})
(def httpopts  {:make-handler make-handler})

(defn new-system [opts]
  ;(let [{:keys [port dburi]} opts]
  (component/system-map
    :datomic (new-datomic dbhost)
    :sente   (new-sente event-msg-handler* sente-web-server-adapter senteopts)
    :httpkit (new-httpkit httpopts)
    :webapp  (new-webapp "hej")))


    ;:customers (customers)
    ;:email     (->Email)
