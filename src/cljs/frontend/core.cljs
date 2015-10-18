(ns frontend.core
  (:require-macros [frontend.macro :refer [foobar]])
  (:require [om.core :as om :include-macros true]
            [om-tools.core :refer-macros [defcomponentk defcomponent]]
            [om-tools.dom :as dom :include-macros true]
            [sablono.core :as html :refer-macros [html]]
            [common.hello :refer [foo-cljc]]
            [frontend.ws :refer [test-socket-callback]]
            [schema.core :as s :include-macros true]
            ;[rum.core :as r :include-macros true]
            [foo.bar]))


;(js/foo)
(defonce app-state (atom {:y 2015}))

(defn pluralize [n word]
  (if (== n 1)
    word                ;; 'true'-block
    (str word "s")))    ;; 'false'-block

(defn now []
  (js/Date.))

(defn hidden [is-hidden]
  (if is-hidden
    #js {:display "none"}
    #js {}))

;Schema
(def Todo
    {:id s/Str
     :title s/Str
     :completed s/Bool})

;Validera schema
;(defcomponent todo-item [todo :- Todo owner]

;; (defn company_v []
;;   (dom/div
;;     (dom/ul {:class "a-list"}
;;       (for [i (range 2)]
;;         (dom/li {:style {:color "red"}}
;;           (str "Projekt " i))))))

(defcomponent wstest []
  (render [_]
    (dom/div
      (dom/h3 "Kanal test")
      (dom/button {:on-click #(test-socket-callback)} "Testa"))))

;; (defn wstest []
;;   (let [apa "olle"]
;;     (dom/div
;;       (dom/h3 "Kanal test")
;;       (dom/button {:on-click #(test-socket-callback)} "Testa"))))

(defcomponent main []
  (render [_]
    (wstest)))

;; (defn kanaltest []
;;   (html
;;     [:div
;;       [:h3 "Kanal test"]
;;       [:button {:on-click #(js/alert "hej")} "alert"]
;;       [:span " "]
;;       [:button {:on-click #(test-socket-callback)} "Testevent"]]))

;; (defcomponent analtest [a owner]
;;   (render [this]
;;     (html
;;       [:div
;;        ;{:key (:id a)}
;;         [:h3 "Kanal test"]
;;         ;[:p (str (:id a))]
;;         ;[:p a]
;;         [:button {:on-click #(js/alert "alert")} "alert"]])))

;; (defcomponent main []
;;   (render [_]
;;     (html
;;       [:div
;;         (analtest nil nil)
;;        ;(om/build-all analtest ["hej"])
;;         ;[:h1 (foo-cljc 1)]
;;         [:div.btn-toolbar]
;;        ])
;;     ))

;; (defcomponentk main [[:data y :as cursor]]
;;   (render [_]
;;     (html
;;       [:div.btn-toolbar
;;       [:button.btn.btn-danger {:type "button" :on-click #(om/transact! cursor :y (partial + 5))} "+"]
;;       [:button.btn.btn-success {:type "button" :on-click #(om/transact! cursor :y dec)} "-"]]])))

(defn start! []
  (om/root main app-state {:target (. js/document (getElementById "app"))}))

(start!)
;(foobar :abc 3) ; Macro test
