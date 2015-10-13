;; (ns system.components.rabbitmq
;;   (:require [com.stuartsierra.component :refer [Lifecycle using]]
;;             [environ.core :refer [env]]
;;             [langohr.core      :as rmq]
;;             [langohr.channel   :as lch]))

;; (defrecord Rabbit [uri conn ch]
;;   Lifecycle
;;   (start [self]
;;     (let [conn (rmq/connect {:uri uri})
;;           ch   (lch/open conn)]
;;       (assoc self :conn conn :ch ch)))
;;   (stop [self]
;;     (rmq/close ch)
;;     (rmq/close conn)
;;     self))

;; (defn new-rabbit-mq [uri]
;;   (map->Rabbit {:uri uri}))
