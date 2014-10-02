(ns lein2.sample-expectations
  (:use expectations
        lein2.core))

(expect 1 1)

(expect 2 (add-1 1))

;; uncomment to test failures
;(expect 3 4)





;; uncomment to test failures to reload code
;; (expect 3 (what))



