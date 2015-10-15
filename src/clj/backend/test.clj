(ns backend.test
  (:require [clojure.test :refer [run-tests]]
            [components.datomic-test]
            ;[components.elasticsearch-test]
            ))

(defn run []
  (run-tests 'components.datomic-test 'components.datomic-test)
  ;(run-tests 'components.elasticsearch-test 'components.elasticsearch-test)
  )
