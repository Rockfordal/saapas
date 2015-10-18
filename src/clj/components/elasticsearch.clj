(ns components.elasticsearch
  (:require [com.stuartsierra.component :refer [Lifecycle using]])
  (:import  [org.elasticsearch.client.transport Transportclient]
            [org.elasticsearch.common.transport InetSocketTransportAddress]
            [org.elasticsearch.common.settings  ImmutableSettings]))

(defrecord Elasticsearch [addresses settings client]
  Lifecycle
  (start [this]
    (let [builder (.. (ImmutableSettings/settingsBuilder)
                      (put ^java.util.Map settings))
          client (doto (TransportClient. builder)
                   (.addTransportAddresses (into-array addresses)))]
      (assoc this :client client)))
  (stop [this]
    (when client
      (.close ^TransportClient client))
    (assoc this :client nil)))

(defn new-elasticsearch-db
  ([addresses]
    (new-elasticsearch-db addresses {}))
  ([addresses settings]
    (map->Elasticsearch {:addresses (for [[^String host ^int port] addresses]
                                      (InetSocketTransportAddress. host port))
                         :settings settings})))
