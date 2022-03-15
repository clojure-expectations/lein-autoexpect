(ns leiningen.autoexpect
  (:require [leinjacker.deps :as deps]
            [leinjacker.eval :as eval]))

(defn- add-deps [project]
  (let [dep-specification (first
                           (filter (fn [[name version]] (= name 'lein-autoexpect/lein-autoexpect))
                                   (:plugins project)))]
    (-> project
        (deps/add-if-missing dep-specification)
        (deps/add-if-missing '[org.clojure/tools.namespace "0.2.11"]))))

(defn ^{:help-arglists '([])} autoexpect
  "Autoruns expecations on source change

  USAGE: lein autoexpect
  Runs expectations whenever there is a change to code in classpath.
  Reports test successes and failures to STDOUT.

  USAGE: lein autoexpect :growl
  Runs expectations whenever code changes.
  Reports results to growl and STDOUT."
  [project & args]
  (let [should-growl (some #{:growl ":growl" "growl"} args)
        should-notify (some #{:notify ":notify" "notify"} args)
        change-only (some #{:change-only ":change-only" "change-only"} args)
        ;; TODO: this might need to also do :test-path and :source-path. I think that may have changed at some point in Leiningen history.
        refresh-dirs (vec
                      (concat (:test-paths project)
                              (:source-paths project)))]
    (eval/eval-in-project
     (add-deps project)
     `(autoexpect.runner/monitor-project
       :should-growl ~should-growl
       :should-notify ~should-notify
       :change-only ~change-only
       :refresh-dirs ~refresh-dirs)
     `(require 'autoexpect.runner))))
