;; (ns backend.queries
;;   (:require [rente.db :as db]
;;             [datomic.api :as d]))


;; (defn by-name [ref name]
;;   (d/q '[:find   ?e :in $ ?name ?ref
;;          :where [?e ?ref  ?name]]
;;          (db/db) name ref))

;; (defn get-all [typ]
;;   (map d/touch (db/read :type typ)))

;; (defn get-by-name [ref name]
;;   (d/touch (d/entity (db/db) (ffirst (by-name ref name)))))

;; (defn show-entity [entity]
;;   (d/touch
;;     (d/entity
;;       (db/db)
;;       (ffirst entity))))
