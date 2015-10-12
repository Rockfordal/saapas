(ns frontend.core
  (:require-macros [frontend.macro :refer [foobar]])
  (:require [om.core :as om :include-macros true]
            [om-tools.core :refer-macros [defcomponentk]]
            [om-tools.dom :as dom :include-macros true]
            [sablono.core :as html :refer-macros [html]]
            [common.hello :refer [foo-cljc]]
            [frontend.ws :refer [test-socket-callback]]
            [foo.bar]))

;(js/foo)
(defonce app-state (atom {:y 2015}))

;; (defn company_v []
;;   (dom/div
;;     (dom/ul {:class "a-list"}
;;       (for [i (range 2)]
;;         (dom/li {:style {:color "red"}}
;;           (str "Projekt " i))))))

;; (defn kanaltest []
;;   [:div
;;     [:h3 "Kanal test"]
;;     [:button {:on-click #(js/alert "alert")} "alert"]
;;     ;[:button {:on-click #(testa)} "Testa"]
;;    ])

(defn wstest []
  (let [kuk "snopp"]
    (dom/div
      (dom/h3 "Kanal test")
      (dom/button {:on-click #(test-socket-callback)} "Testa"))))

(defcomponentk main
  [[:data y :as cursor]]
  (render [_]
    ;(company_v)
    (wstest)
    ;; (html
    ;;   [:div
    ;;     (kanaltest)
    ;;   [:h1 (foo-cljc y)]
    ;;   [:div.btn-toolbar
    ;;   [:button.btn.btn-danger {:type "button" :on-click #(om/transact! cursor :y (partial + 5))} "+"]
    ;;   [:button.btn.btn-success {:type "button" :on-click #(om/transact! cursor :y dec)} "-"]]
    ;;   (project_v)
    ;])
    ))

(defn start! []
  ;(js/console.log "Starting the app")
  (om/root main app-state {:target (. js/document (getElementById "app"))}))

(start!)
;(foobar :abc 3) ; Macro test
