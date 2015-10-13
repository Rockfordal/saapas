(ns components.sente
  (:require [com.stuartsierra.component :refer [Lifecycle using]]
            [taoensso.timbre :refer (tracef debugf infof warnf errorf)]
            [taoensso.sente :as sente]))

(defrecord ChannelSockets [ring-ajax-post ring-ajax-get-or-ws-handshake ch-chsk chsk-send! connected-uids router server-adapter event-msg-handler options]
  Lifecycle
  (start [self]
    (let [{:keys [ch-recv send-fn ajax-post-fn ajax-get-or-ws-handshake-fn connected-uids]}
          (sente/make-channel-socket! server-adapter options)]
      (debugf "Starting sente")
      (assoc self
        :ring-ajax-post ajax-post-fn
        :ring-ajax-get-or-ws-handshake ajax-get-or-ws-handshake-fn
        :ch-chsk    ch-recv
        :chsk-send! send-fn
        :connected-uids connected-uids
        :router (atom (sente/start-chsk-router! ch-recv event-msg-handler)))))
  (stop [self]
    (if-let [stop-f @router]
      (do (debugf "Stopping sente")
          (assoc self :router (stop-f)))
      self)))

(defn new-channel-sockets
  ([event-msg-handler server-adapter]
   (new-channel-sockets event-msg-handler server-adapter {}))
  ([event-msg-handler server-adapter options]
   (map->ChannelSockets {:server-adapter    server-adapter
                         :event-msg-handler event-msg-handler
                         :options options})))
