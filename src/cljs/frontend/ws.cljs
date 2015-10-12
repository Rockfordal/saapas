(ns frontend.ws
  (:require [taoensso.sente :as sente]
            [taoensso.sente.packers.transit :as sente-transit]
            [taoensso.timbre :as timbre :refer-macros (tracef debugf infof warnf errorf)]
            [cognitect.transit :as t]))

(defmulti event-msg-handler :id)

(defmethod event-msg-handler :default
    [{:as ev-msg :keys [event]}]
    (debugf "Okänd event: %s" event))

(defmethod event-msg-handler :chsk/state
  [{:as ev-msg :keys [?data]}]
  (if (= ?data {:first-open? true})
    (debugf "Kanal established!")
    (debugf "Kanal: %s" ?data)))

(defmethod event-msg-handler :chsk/recv
  [{:as ev-msg :keys [?data]}]
  (debugf "PUSH event från server: %s " ?data))

(defn event-msg-handler* [{:as ev-msg :keys [id ?data event]}]
  (event-msg-handler ev-msg))
  (let [packer (sente-transit/get-flexi-packer :edn)
      {:keys [chsk ch-recv send-fn state]}
      (sente/make-channel-socket! "/chsk" {:type :auto :packer packer})]
  (def chsk       chsk)
  (def ch-chsk    ch-recv)
  (def chsk-send! send-fn)
  (def chsk-state state))

(defn test-socket-callback []
  (chsk-send!
    [:rente/testevent {:msg "Hello socket Callback!"}]
    2000 #(js/console.log "CB from server: " (pr-str %))))
