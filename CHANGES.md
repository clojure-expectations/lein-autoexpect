# Changes

## 1.10.0

- lein-autoexpect will only reload the directories in `:source-paths` and `:test-paths`.
- If you press the Enter/Return key when focused on the terminal running lein autoexpect, the tests will rerun.

## 1.9.0

- Growl notifications happen quicker and have a green or red icon
  depending on test status.

## 1.8.0

- Adds support for only sending notifications if your test status
changes state (move from failing to passing or passing to failing). To
turn on supply the flag `:change-only` at the command line: `lein autoexpect :notify :change-only`.

 ## 1.7.0

- Use `clojure.stacktrace/root-cause` and
  `clojure.stacktrace/print-cause-trace` to print stackstraces when
  there is an exception reloading an environment.

## 1.6.0

- Upgrade to `[org.clojure/tools.namespace "0.2.11"]`. This should
  improve reader conditional support.

## 1.5.0

- Printing to stdout no longer suppressed when reloading namespaces.

## 1.4.3

- Upgrade to `[org.clojure/tools.namespace "0.2.10"]`.

## 1.4.2

- Upgrade to `[org.clojure/tools.namespace "0.2.8"]`.

## 1.4.0

- Adds support for using
  [terminal-notifier](https://github.com/alloy/terminal-notifier) for
  notifications. Use `lein autoexpect :notify` to use terminal-notifier.

## 1.3.0

- Upgrade to [org.clojure/tools.namespace "0.2.7"].
- Run all expectations after reloading code instead of just
expectations affected by code reloading.

## 1.2.2

- lein-autoexpect prints out current time after running the tests.

## 0.2.2

- Adds support for reporting test results using Growl. Use `lein
autoexpect :growl`
- Upgrade to `org.clojure/tools.namespace 0.2.1`. This version of
  tools.namespace provides better backwards compatibility with
  versions prior to 0.2.0.
