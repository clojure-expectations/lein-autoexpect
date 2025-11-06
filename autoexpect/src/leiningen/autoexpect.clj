(ns leiningen.autoexpect
  (:require leiningen.core.eval))

(defn add-if-missing [project specification]
  (cond-> project
    (not (some (fn [[dep-name _]] (= (first specification) dep-name))
               (:dependencies project)))
    (update :dependencies (fnil conj []) specification)))

(defn- add-deps [project]
  (let [dep-specification (first
                           (filter (fn [[name _]] (= name 'lein-autoexpect/lein-autoexpect))
                                   (:plugins project)))]
    (-> project
        (add-if-missing dep-specification)
        (add-if-missing '[org.clojure/tools.namespace "0.2.11"]))))

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
        should-exit-on-pass (some #{:exit-on-pass ":exit-on-pass" "exit-on-pass"} args)
        change-only (some #{:change-only ":change-only" "change-only"} args)
        refresh-dirs (vec
                      (concat (:test-paths project)
                              (:source-paths project)))]
    (leiningen.core.eval/eval-in-project
     (add-deps project)
     `(autoexpect.runner/monitor-project
       :should-growl ~should-growl
       :should-notify ~should-notify
       :should-exit-on-pass ~should-exit-on-pass
       :change-only ~change-only
       :refresh-dirs ~refresh-dirs)
     `(require 'autoexpect.runner))))
