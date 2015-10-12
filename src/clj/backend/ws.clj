(ns backend.ws
   (:require [taoensso.timbre :as timbre :refer (tracef debugf infof warnf errorf)]))


(def ping-counts (atom 0))

(defmulti event-msg-handler :id)

(defn event-msg-handler* [{:as ev-msg :keys [id ?data event]}]
    (event-msg-handler ev-msg))

(defmethod event-msg-handler :chsk/ws-ping [_]
    (swap! ping-counts inc)
    (when (= 0 (mod @ping-counts 10))
    (println "ping count: " @ping-counts)))

(defmethod event-msg-handler :default
  [{:as ev-msg :keys [event id ?data ring-req ?reply-fn send-fn]}]
  (let [session (:session ring-req)
        uid     (:uid     session)]
    (println "Ej hanterbar event: %s" event)
    (when ?reply-fn
      (?reply-fn {:umatched-event-as-echoed-from-from-server event}))))

(defmethod event-msg-handler :rente/testevent
  [{:as ev-msg :keys [event id ?data ring-req ?reply-fn send-fn]}]
  (if ?reply-fn
    (?reply-fn [:rente/testevent {:msg (str "server CB got: " ?data)}])
    (send-fn :sente/all-users-without-uid [:rente/testevent {:msg (str "Server Event got: " ?data)}])))

;------------ nya --------------------------------------
(defmethod event-msg-handler :rente/get-data
    [{:as ev-msg :keys [event id ?data ring-req ?reply-fn]}]
    (?reply-fn [:rente/get-data "db/get-state" ]))

;------------ generella --------------------------------
;; (defmethod event-msg-handler :rente/get
;;   [{:as ev-msg :keys [event id ?data ring-req ?reply-fn]}]
;;   (let [data (map d/touch (db/read :type (:type ?data)))]
;;     (?reply-fn [:rente/get data])))

;; (defmethod event-msg-handler :rente/delete
;;   [{:as ev-msg :keys [event id ?data ring-req ?reply-fn]}]
;;     (if (db/delete-entity (:db/id ?data))
;;       (?reply-fn [:rente/delete {:db/id (:db/id ?data)}])
;;       (?reply-fn [:rente/delete {:msg "misslyckades radera"}])))

;; (defmethod event-msg-handler :rente/add
;;   [{:as ev-msg :keys [event id ?data ring-req ?reply-fn]}]
;;   (let [entity (:entity ?data)
;;         id     (db/create-entity entity)]
;;     (if id
;;       (?reply-fn [:rente/add {:db/id id :entity entity}])
;;       (?reply-fn [:rente/add {:msg (str "misslyckades adda" ?data entity)}]))))

;; (defmethod event-msg-handler :rente/update
;;   [{:as ev-msg :keys [event id ?data ring-req ?reply-fn]}]
;;   (let [entity (:entity ?data)
;;         ok     (db/update-entity entity)]
;;     (if ok
;;       (?reply-fn [:rente/update {:entity entity}])
;;       (?reply-fn [:rente/update {:msg (str "misslyckades upd" ?data entity)}]))))
