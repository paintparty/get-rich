(ns get-rich.core-test
  (:require [clojure.test :refer :all]
            [clojure.string :as string]
            [get-rich.core :refer [enriched callout point-of-interest]]))

(println )

(defn example-custom-callout [opts]
  (let [poi-opts (merge opts
                        {:header "Your header of your template goes here."
                         :body   ["The body of your template goes here."
                                  "Second line of copy."
                                  "Another line."]})
        message (point-of-interest poi-opts)
        callout-opts (select-keys opts [:type :border-weight])]
    (callout callout-opts message)))

;; Normal callout w/ poi
(example-custom-callout
 {:file   "example.ns.core"
  :line   11
  :column 1
  :form   '(+ 1 true)
  :type   :error })

;; Heavy callout w/ poi
(example-custom-callout
 {:file          "example.ns.core"
  :line          11
  :column        1
  :form          '(+ 1 true)
  :type          :error
  :border-weight :heavy})

;; Basics
(println (enriched [:bold "bold"] ", " [:italic "italic"] ", or " [:blue "colored"]))

;; Combo
(println (enriched [:bold.italic "bold & italic"]
                   ", "
                   [:italic.blue "italic & colored"]
                   ", "
                   [:bold.italic.white.blue-bg "bold & italic & colored & colored-bg"]))

;; Colors
(println (enriched [:bold.red "Red"]
                   ", "
                   [:bold.yellow "Yellow"]
                   ", "
                   [:bold.green "Green"]
                   ", "
                   [:bold.blue "Blue"]
                   ", "
                   [:bold.magenta "Magenta"]
                   ", "
                   [:bold.gray "Gray"]
                   ", "
                   [:bold.black "Black"]
                   ", "
                   [:bold.white "White"] ))

;; Semantic colors
(println (enriched [:bold.negative "Negative"]
                   ", "
                   [:bold.error "Error"]
                   ", "
                   [:bold.warning "Warning"]
                   ", "
                   [:bold.positive "Positive"]
                   ", "
                   [:bold.info "Info"]
                   ", "
                   [:bold.subtle "Subtle"]
                   ", "
                   [:bold.neutral "Neutral"] ))


;; callout examples

(callout
 {:type :info}
 "Example callout, with :type of :info")

(callout
 {:type :info
  :label      "My custom label"}
 "Example callout, with :type of :info and custom :label")

(callout
 {:type :warning}
  "Example callout, with :type of :warning")

(callout
 {:type :error}
 "Example callout, with :type of :error")

(callout
 {:type :positive
  :label      "SUCCESS!"}
  "Example callout, with :type of :positive, and custom :label")

(callout
 {:type :subtle}
 "Example callout, with :type of :subtle (or :gray)")

(callout
 {:type :magenta}
 "Example callout, with :type of :magenta")

(callout
 {:type           :error
  :margin-top     3 ; default is 1
  :margin-bottom  3 ; default is 1
  :padding-top    2 ; default is 0
  :padding-bottom 2 ; default is 0
  }
 "Example callout, with :type of :error, and custom spacing")

(println "WARNING CALLOUT BELOW IS RESULT OF BAD CALL TO CALLOUT:")
;; Bad call to callout, should produce warning callout
(callout
 [:red "Example malformed call to callout, default"])
