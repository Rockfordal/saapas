(ns backend.db
  (:refer-clojure :exclude [read])
  (:require [clojure.algo.generic.functor :refer [fmap]]
            [reloaded.repl :refer [system]]
            [datomic.api :as d]))

(defn conn [] (:conn (:datomic system)))
(defn db   [] (d/db (conn)))

(defn create! [m conn]
  (let [dbid (d/tempid :db.part/user)]
    @(d/transact conn (list (assoc m :db/id dbid)))
    dbid))

(defn create-entity [m db conn]
  (let [temp-id (d/tempid :db.part/user)
        tx @(d/transact conn (list (assoc m :db/id temp-id)))]
    (d/resolve-tempid db (:tempids tx) temp-id)))

(defn update-entity [entity conn]
  (d/transact conn (list entity))
  true)

(defn delete-entity [eid conn]
  (do @(d/transact conn [[:db.fn/retractEntity eid]])
      true))

(defn show-entity [entity db]
  (d/touch
    (d/entity
      db
      (ffirst entity))))

(defn read
  ([k v db]
   (let [found (d/q '[:find ?e :in $ ?k ?v :where [?e ?k ?v]] db k v)]
     (map (comp (partial d/entity db) first) found))))

; Queries
(defn by-name [ref name db]
  (d/q '[:find   ?e :in $ ?name ?ref
         :where [?e ?ref  ?name]]
         db name ref))

(defn get-all [typ db]
  (map d/touch (read :type typ db)))

(defn get-by-name [ref name db]
  (d/touch (d/entity db (ffirst (by-name ref name)))))

(defn expand
  ([e]
   (if (instance? datomic.query.EntityMap e)
     (let [m (into {} (d/touch e))]
       (expand m (keys m)))
     e))
  ([e ks]
   (if-not (empty? ks)
     (let [val (get e (first ks))]
       (cond
         (instance? datomic.query.EntityMap val)
         (expand (assoc e (first ks) (expand val)) (rest ks))
         (and (set? val) (instance? datomic.query.EntityMap (first val)))
         (expand (assoc e (first ks) (set (map expand val))) (rest ks))
         :else (expand e (rest ks))))
     e)))

(defn visible-entities [db]
  (d/q '[:find ?e :in $ %
         :where (visible ?e)] db
       '[[(visible ?pj) (?pj :project/name)]
         [(visible ?co) (?co :company/name)]
         [(visible ?ac) (?ac :activity/note)]
         [(visible ?sn) (?sn :sni/code)]]))

(defn load-entity
  "Loads an entity and its attributes. Keep in the db/id
  and replace references with ids (for use by DataScript)"
  [db entity]
  (as->
    (d/entity db entity) e
    (d/touch e)
    (into {:db/id (:db/id e)} e) ; needs to be a hash-map, not an entity map
    (fmap (fn [v]
            (cond (set? v) (mapv #(if (instance? datomic.query.EntityMap %)
                                      (:db/id %) %) v)
                  (instance? datomic.query.EntityMap v) (:db/id v)
                  :else v)) e)))

(defn get-state [db]
  (map (comp (partial load-entity db) first)
       (visible-entities db)))
