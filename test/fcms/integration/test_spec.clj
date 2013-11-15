(ns fcms.integration.test-spec
  (:require [speclj.core :refer :all]))

(describe "Truth"

  (it "is true"
    (should true))

  (it "is not false"
    (should-not false)))