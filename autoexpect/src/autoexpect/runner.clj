(ns autoexpect.runner
  (:require clojure.tools.namespace.track
            clojure.tools.namespace.repl
            expectations
            jakemcc.clojure-gntp.gntp
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

(defn- growl [title-postfix message]
  (try
    (jakemcc.clojure-gntp.gntp/message (str "AutoExpect - " title-postfix) message)
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
      {:status "Error" :message (str "Error refreshing environment: " clojure.core/*e)})))

(defn- something-changed? [x y]
  (not= x y))

(defn monitor-project [& {:keys [should-growl should-notify]}]
  (turn-off-testing-at-shutdown)
  (loop [tracker (make-change-tracker)]
    (let [new-tracker (scan-for-changes tracker)]
      (try
        (when (something-changed? new-tracker tracker)
          (print-banner)
          (let [result (run-tests)]
            (when should-growl
              (growl (:status result) (:message result)))
            (when should-notify
              (notify (:status result) (:message result)))
            (when (= (:status result) "Error")
              (println (:message result)))
            (print-end-message)))
        (Thread/sleep 500)
        (catch Exception ex (.printStackTrace ex)))
      (recur new-tracker))))
