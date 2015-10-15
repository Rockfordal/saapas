(ns components.elasticsearch
  (:require [com.stuartsierra.component :refer [Lifecycle using]])
  (:import  [org.elasticsearch.client.transport Transportclient]
            [org.elasticsearch.common.transport InetSocketTransportAddress]
            [org.elasticsearch.common.settings  ImmutableSettings]))

(defrecord Elasticsearch [addresses settings client]
  Lifecycle
  (start [self]
    (let [builder (.. (ImmutableSettings/settingsBuilder)
                      (put ^java.util.Map settings))
          client (doto (TransportClient. builder)
                   (.addTransportAddresses (into-array addresses)))]
      (assoc self :client client)))
  (stop [self]
    (when client
      (.close ^TransportClient client))
    (assoc self :client nil)))

(defn new-elasticsearch-db
  ([addresses]
    (new-elasticsearch-db addresses {}))
  ([addresses settings]
    (map->Elasticsearch {:addresses (for [[^String host ^int port] addresses]
                                      (InetSocketTransportAddress. host port))
                         :settings settings})))
