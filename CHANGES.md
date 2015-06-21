# Changes

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
