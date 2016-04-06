(ns autoexpect.runner
  (:require clojure.tools.namespace.track
            clojure.tools.namespace.repl
            [clojure.stacktrace :as stacktrace]
            expectations
            [gntp :refer [make-growler]]
            [clojure.java.io :refer [input-stream resource]]
            [clojure.string :as str]
            [clojure.java.shell :refer [sh]]))

(defn- turn-off-testing-at-shutdown []
  (reset! expectations/run-tests-on-shutdown false))

(defn- make-change-tracker []
  (clojure.tools.namespace.track/tracker))

(defn- scan-for-changes [tracker]
  (clojure.tools.namespace.dir/scan tracker))

(defn- refresh-environment []
  (clojure.tools.namespace.repl/refresh))

(def ^:private top-stars (apply str (repeat 45 "*")))
(def ^:private side-stars (apply str (repeat 15 "*")))

(defn- print-banner []
  (println)
  (println top-stars)
  (println side-stars "Running tests" side-stars))

(defn- print-end-message []
  (let [date-str (.format (java.text.SimpleDateFormat. "HH:mm:ss.SSS")
                          (java.util.Date.))]
    (println "Tests completed at" date-str)))

(def ^:private resource-stream (comp input-stream resource))

(def ^:private growlers
  (delay ((make-growler "AutoExpect")
          "Passed" {:name "Passed"}
          "Failed" {:name "Failed"}
          "Error" {:name "Error"})))

(defn- limit [string n]
  (if (< (count string) n)
    string
    (str (subs string 0 n) "...")))

(defn- growl-icon [status]
  (case status
    "Passed" (resource-stream "pass.png")
    "Failed" (resource-stream "fail.png")
    "Error" (resource-stream "fail.png")))

(defn- growl [status message]
  (try
    ((get @growlers status) status :text (limit message 500) :icon (growl-icon status))
    (catch Exception ex
      (println "Problem communicating with growl, exception:" (.getMessage ex)))))

(defn- escape [message]
  (str/replace message "[" "\\["))


(defn- notify [title-postfix message]
  (try
    (sh "terminal-notifier" "-message" (escape message) "-title" (str "AutoExpect - " (escape title-postfix)))
    (catch Exception ex
      (println "Problem communicating with notification center, please make sure you installed terminal-notifier (e.g. using 'brew install terminal-notifier'), exception:" (.getMessage ex)))))


(defn- report [results]
  (let [{:keys [fail error test run-time]} results]
    (if (< 0 (+ fail error))
      {:status "Failed" :message (format "Failed %s of %s tests." (+ fail error) test)}
      {:status "Passed" :message (format "Passed %s tests" test)})))


(defn- mark-tests-as-unrun []
  (let [all (->> (all-ns)
                 (mapcat (comp vals ns-interns)))
        previously-ran-tests (filter (comp :expectations/run meta) all)]
    (doseq [test previously-ran-tests]
      (alter-meta! test dissoc :expectations/run :status))))

(defn- run-tests []
  (mark-tests-as-unrun)
  (let [result (refresh-environment)]
    (if (= :ok result)
      (report (expectations/run-all-tests))
      {:status "Error" :message (str "Error refreshing environment: " (with-out-str
                                                                        (stacktrace/print-cause-trace
                                                                         (stacktrace/root-cause clojure.core/*e))))})))

(defn- something-changed? [x y]
  (not= x y))

(defn notify? [notify-type change-only last-status new-status]
  (cond
    (not notify-type) false
    (not change-only) true
    :else (not= last-status new-status)))

(def ^:private last-status (atom nil))

(defn monitor-project [& {:keys [should-growl should-notify change-only]}]
  (turn-off-testing-at-shutdown)
  (loop [tracker (make-change-tracker)]
    (let [new-tracker (scan-for-changes tracker)]
      (try
        (when (something-changed? new-tracker tracker)
          (print-banner)
          (let [result (run-tests)
                new-status (:status result)]
            (when (notify? should-growl change-only @last-status new-status)
              (growl new-status (:message result)))
            (when (notify? should-notify change-only @last-status new-status)
              (notify new-status (:message result)))
            (when (= new-status "Error")
              (println (:message result)))
            (reset! last-status new-status)
            (print-end-message)))
        (Thread/sleep 500)
        (catch Exception ex (.printStackTrace ex)))
      (recur new-tracker))))
