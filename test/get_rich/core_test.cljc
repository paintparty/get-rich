(ns get-rich.core-test
  (:require [clojure.test :as test]
            [clojure.string :as string]
            [get-rich.core :as gr :refer [enriched callout point-of-interest]]
            #?(:cljs [get-rich.core :refer [print-enriched]])))


(def printer
  #?(:cljs gr/print-enriched :clj println))

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
;; (example-custom-callout
;;  {:file   "example.ns.core"
;;   :line   11
;;   :column 1
;;   :form   '(+ 1 true)
;;   :type   :error })

;; ;; Heavy callout w/ poi
;; (example-custom-callout
;;  {:file          "example.ns.core"
;;   :line          11
;;   :column        1
;;   :form          '(+ 1 true)
;;   :type          :error
;;   :border-weight :heavy})

;; ;; Basics
;; (printer (enriched [:bold "bold"] ", " [:italic "italic"] ", or " [:blue "colored"]))

;; ;; Combo
;; (printer (enriched [:bold.italic "bold & italic"]
;;                    ", "
;;                    [:italic.blue "italic & colored"]
;;                    ", "
;;                    [:bold.italic.white.blue-bg "bold & italic & colored & colored-bg"]))

;; ;; Colors
;; (printer (enriched [:bold.red "Red"]
;;                    ", "
;;                    [:bold.yellow "Yellow"]
;;                    ", "
;;                    [:bold.green "Green"]
;;                    ", "
;;                    [:bold.blue "Blue"]
;;                    ", "
;;                    [:bold.magenta "Magenta"]
;;                    ", "
;;                    [:bold.gray "Gray"]
;;                    ", "
;;                    [:bold.black "Black"]
;;                    ", "
;;                    [:bold.white "White"] ))

;; ;; Semantic colors
;; (printer (enriched [:bold.negative "Negative"]
;;                    ", "
;;                    [:bold.error "Error"]
;;                    ", "
;;                    [:bold.warning "Warning"]
;;                    ", "
;;                    [:bold.positive "Positive"]
;;                    ", "
;;                    [:bold.info "Info"]
;;                    ", "
;;                    [:bold.subtle "Subtle"]
;;                    ", "
;;                    [:bold.neutral "Neutral"] ))


;; callout examples
;; (printer (enriched [:blue "Example callout, default"]))


;; (callout
;;  {:type :warning
;;   :label (enriched 
;;           [:magenta-bg.white.bold " "]
;;           [:red-bg.white.bold "R"]
;;           [:yellow-bg.white.bold "A"]
;;           [:green-bg.white.bold "I"]
;;           [:blue-bg.white.bold "N"]
;;           [:magenta-bg.white.bold "B"]
;;           [:red-bg.white.bold "O"]
;;           [:yellow-bg.white.bold "W"]
;;           [:blue-bg.white.bold " "])}
;;  (enriched "Example callout, :type of :warning, rainbow-bg label" ))

;; (callout
;;  {:type :info
;;   :label (enriched 
;;           [:magenta.bold "R"]
;;           [:red.bold "A"]
;;           [:yellow.bold "I"]
;;           [:green.bold "N"]
;;           [:blue.bold "B"]
;;           [:magenta.bold "O"]
;;           [:red.bold "W"])}
;;  (enriched "Example callout, :type of :warning, rainbow-bg label" ))

;; (callout
;;  "Example callout, with :type of :info, single-arity")

;; (callout
;;  {:label "FOO"}
;;  "Example callout, with :type of :info, single-arity")

;; (callout
;;  {:label (enriched [:red-bg.white.bold " WTF "])}
;;  (enriched [:magenta "Example callout, with :type of :info, styled-label"]))

;; (callout
;;  "Example callout again, with :type of :info")

;; (callout
;;  {:type :info
;;   :label      "My custom label"}
;;  "Example callout, with :type of :info and custom :label")

(callout
 {:type         :warning
  ;; :padding-left 0
  :padding-top 1
  :border-weight :heavy}
 "Example callout, with :type of :warning.")

;; (callout
;;  {:type :warning
;;   :label "My custom label warning"}
;;  "Example callout, with :type of :warning, and custom :label."
;;  "\n")

;; (callout
;;  {:type :error}
;;  "Example callout, with :type of :error")

;; (callout
;;  {:type :positive
;;   :label      "SUCCESS!"}
;;   "Example callout, with :type of :positive, and custom :label")

;; (callout
;;  {:type :subtle}
;;  "Example callout, with :type of :subtle (or :gray)")

;; (callout
;;  {:type :magenta}
;;  "Example callout, with :type of :magenta")

;; (callout
;;  {:type           :error
;;   :margin-top     3 ; default is 1
;;   :margin-bottom  3 ; default is 1
;;   :padding-top    2 ; default is 0
;;   :padding-bottom 2 ; default is 0
;;   }
;;  "Example callout, with :type of :error, and custom spacing")

;; (println "WARNING CALLOUT BELOW IS RESULT OF BAD CALL TO CALLOUT:")
;; ;; Bad call to callout, should produce warning callout
;; (callout
;;  [:red "Example malformed call to callout, default"])

