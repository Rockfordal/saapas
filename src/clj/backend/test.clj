(ns backend.test
  (:require [clojure.test :refer [run-tests]]))

(defn run []
  (run-tests 'components.datomic-test 'components.datomic-test))
