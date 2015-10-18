(set-env!
  :source-paths #{"src/cljs" "src/less" "src/scss"}
  :resource-paths #{"src/clj" "src/cljc"}
  :dependencies '[[org.clojure/clojure    "1.7.0"]
                  [org.clojure/clojurescript "1.7.145"] ; 1.7.48 ; .145
                  [com.taoensso/timbre "4.1.1"]

                  [adzerk/boot-cljs       "1.7.48-5" :scope "test"]
                  [adzerk/boot-cljs-repl  "0.2.0"    :scope "test"]
                  [adzerk/boot-reload     "0.4.0"    :scope "test"]
                  [deraen/boot-less       "0.4.2"    :scope "test"]
                  [deraen/boot-sass       "0.1.1"    :scope "test"]
                  [deraen/boot-ctn        "0.1.0"    :scope "test"]

                  ; Backend
                  [http-kit "2.1.19"]
                  [org.clojure/tools.namespace "0.2.11"]
                  [reloaded.repl "0.2.0"]
                  [com.stuartsierra/component "0.3.0"]
                  [metosin/ring-http-response "0.6.5"]
                  [prismatic/om-tools "0.4.0"]
                  [prismatic/plumbing "0.5.0"]
                  [prismatic/schema "1.0.1"]
                  [ring/ring-defaults "0.1.5"]
                  [ring "1.4.0"]
                  [compojure "1.4.0"]
                  [org.clojure/algo.generic "0.1.2" :only [fmap]] ; massera data
                  [com.datomic/datomic-pro "0.9.5206" :exclusions [joda-time]]
                  [datomic-schema "1.3.0"]
                  [hiccup "1.0.5"]
                  [com.cognitect/transit-clj  "0.8.283" :exclusions [commons-codec]] ; 0.8.283
                  [fogus/ring-edn "0.3.0"]
                  ;[com.cemerick/friend "0.2.2-SNAPSHOT"]

                  ; Both
                  ;[bidi "1.21.1"]
                  [com.taoensso/sente "1.6.0" :exclusions [org.clojure/tools.reader]]

                  ; Frontend
                  [com.cognitect/transit-cljs "0.8.225"]
                  [org.omcljs/om "0.8.8"]
                  [sablono "0.3.6"]
                  [prismatic/schema "1.0.1"]

                  [rum "0.5.0"] ; Sablono 0.3.6 men senaste är 0.3.7-SNAPSHOT 
                  [datascript "0.13.1"]
                  [secretary "1.2.3"]
                  [prismatic/schema "1.0.1"]
                  [cljs-http "0.1.37"]
                  [jayq "2.5.4"]


                  ; LESS
                  [org.webjars/bootstrap "3.3.4"]
                  ; SASS
                  [org.webjars.bower/bootstrap "4.0.0-alpha" :exclusions [org.webjars.bower/jquery]]])

(require
  '[adzerk.boot-cljs      :refer [cljs]]
  '[adzerk.boot-cljs-repl :refer [cljs-repl start-repl repl-env]]
  '[adzerk.boot-reload    :refer [reload]]
  '[deraen.boot-less      :refer [less]]
  '[deraen.boot-sass      :refer [sass]]
  '[deraen.boot-ctn       :refer [init-ctn!]]
  '[backend.boot          :refer [start-app]]
  '[reloaded.repl         :refer [go reset start stop system]])

; Watch boot temp dirs
(init-ctn!)

(task-options!
  pom {:project 'saapas
       :version "0.1.0-SNAPSHOT"
       :description "Application template for Cljs/Om with live reloading, using Boot."
       :license {"The MIT License (MIT)" "http://opensource.org/licenses/mit-license.php"}}
  aot {:namespace #{'backend.main}}
  jar {:main 'backend.main}
  cljs {:source-map true}
  less {:source-map true})

(deftask dev
  "Start the dev env..."
  [s speak           bool "Notify when build is done"
   p port       PORT int  "Port for web server"
   a use-sass        bool "Use Scss instead of less"]
  (comp
    (watch)
    (if use-sass
      (sass)
      (less))
    (reload :open-file "vim --servername saapas --remote-silent +norm%sG%s| %s"
            :ids #{"js/main"})
    (cljs-repl :ids #{"main"} :port 7888)
    (cljs :ids #{"js/main"})
    (start-app :port port)
    (if speak (boot.task.built-in/speak) identity)))

(deftask package []
  (comp
    (less :compression true)
    (cljs :optimizations :advanced)
    (aot)
    (pom)
    (uber)
    (jar)))
