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

; --- anlu ---
;; (defrecord WSRingHandlers [ajax-post-fn ajax-get-or-ws-handshake-fn])

;; (defrecord WSConnection [ch-recv connected-uids send-fn ring-handlers]
;;   Lifecycle
;;   (start [self]
;;     (if (and ch-recv connected-uids send-fn ring-handlers)
;;       self
;;       (let [self (component/stop self)
;;             packer (get-flexi-packer :edn)
;;             {:keys [ch-recv send-fn connected-uids
;;                     ajax-post-fn ajax-get-or-ws-handshake-fn]}
;;             (sente/make-channel-socket! sente-http/http-kit-adapter {:packer packer})]
;;         (log/debug "WebSocket anslutning startad")
;;         (assoc self
;;           :ch-recv ch-recv
;;           :connected-uids connected-uids
;;           :send-fn send-fn
;;           :stop-the-thing (sente/start-chsk-router! ch-recv event-msg-handler*)
;;           :ring-handlers
;;           (->WSRingHandlers ajax-post-fn ajax-get-or-ws-handshake-fn)))))
;;   (stop [self]
;;     (when ch-recv (async/close! ch-recv))
;;     (log/debug "WebSocket anslutning stoppad")
;;     (:stop-the-thing self)
;;     (assoc self
;;       :ch-recv nil :connected-uids nil :send-fn nil :ring-handlers nil)))

;; (defn send! [ws-conn user-id event]
;;   ((:send-fn ws-conn) user-id event))

;; (defn broadcast! [ws-conn event]
;;   (let [uids (ws-conn :connected-uids )]
;;     (doseq [uid (:any @uids)] (send! ws-conn uid event))))

;; (defn ring-handlers [ws-conn]
;;   (:ring-handlers ws-conn))

;; (defn new-ws []
;;   (map->WSConnection {}))
