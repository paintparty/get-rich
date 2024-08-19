(ns get-rich.core-test
  (:require [clojure.test :as test]
            [clojure.string :as string]
            [get-rich.core :as gr :refer [enriched callout point-of-interest]]
            #?(:cljs [get-rich.core :refer [print-enriched enriched]])))


(def printer
  #?(:cljs gr/print-enriched :clj println))


(defn example-custom-callout
  [{:keys [point-of-interest-opts callout-opts]}]
  (let [poi-opts     (merge {:header "Your header message goes here."
                             :body   (str "The body of your template goes here."
                                          "\n"
                                          "Second line of copy."
                                          "\n"
                                          "Another line.")}
                            point-of-interest-opts)
        message      (point-of-interest poi-opts)
        callout-opts (merge callout-opts
                            {:padding-top 1})]
    (callout callout-opts message)))


;; ;; Default callout w/ poi
;; (example-custom-callout
;;  {:point-of-interest-opts {:file   "example.ns.core"
;;                            :line   11
;;                            :column 1
;;                            :form   '(+ 1 true)
;;                            :type   :error}
;;   :callout-opts           {:type  :error
;;                            :border-weight :medium}})

;; ;; Default callout w/ poi, regex-form
;; (example-custom-callout
;;  {:point-of-interest-opts {:file   "example.ns.core"
;;                            :line   11
;;                            :column 1
;;                            :form   #"dude"
;;                            :type   :error}
;;   :callout-opts           {:type  :error}})

;; ;; Medium callout w/ poi
;; (example-custom-callout
;;  {:point-of-interest-opts {:file   "example.ns.core"
;;                            :line   11
;;                            :column 1
;;                            :form   '(+ 1 true)
;;                            :type   :error}
;;   :callout-opts           {:type          :error
;;                            :border-weight :medium}})

;; ;; Heavy callout w/ poi
;; (example-custom-callout
;;  {:point-of-interest-opts {:file   "example.ns.core"
;;                            :line   11
;;                            :column 1
;;                            :form   '(+ 1 true)
;;                            :type   :error}
;;   :callout-opts           {:type          :error
;;                            :border-weight :heavy}})

;; ;; Normal callout w/ poi, label enriched
;; (example-custom-callout
;;  {:point-of-interest-opts {:file   "example.ns.core"
;;                            :line   11
;;                            :column 1
;;                            :form   '(+ 1 true)
;;                            :type   :error}
;;   :callout-opts {:label         (enriched [:red-bg.bold.white " ERROR "])
;;                  :type          :error}})

;; ;; callout w/ enriched header and body on poi
;; (example-custom-callout
;;  {:point-of-interest-opts {:file   "example.ns.core"
;;                            :line   11
;;                            :column 1
;;                            :form   '(+ 1 true)
;;                            :type   :error
;;                            :header (enriched [:blue.italic "Enriched header."])
;;                            :body   (enriched [:green.italic "Enriched body."]
;;                                              "\n"
;;                                              [:yellow.italic "Body line 2."])}
;;   :callout-opts           {:type  :error}})


;; (callout "Default callout, no options")

;; (callout {:padding-left 2} "Default callout, no label, left padding")

;; (callout {:label "My label"} "Default callout, custom label")

;; (callout {:type :warning} "Callout, type :warning, default label")

;; (callout {:type :warning :label "My warning"}
;;          "Callout, type :warning, custom label")

;; (callout {:type :warning :label (enriched [:bold.yellow-bg.black " My warning "])}
;;          "Callout, type :warning, custom enriched label")

;; (callout {:type           :warning
;;           :padding-top    1
;;           :padding-bottom 1
;;           :padding-left   2} 
;;          "Callout, type :warning, default label, custom padding")

;; (callout {:type  :positive
;;           :label (enriched [:positive-bg.white.bold " YES "])}
;;          "Callout, type :positve, enriched label")

;; (callout {:label "YES" :type "positive"}
;;          "Callout, type :positve, user label")

;; (callout {:type :warning}
;;          (point-of-interest {:line   11
;;                              :column 2
;;                              :form   '(+ 1 1)
;;                              :file   "myfile.cljs"
;;                              :type   :warning}))


;; (callout {:type :magenta}
;;          (point-of-interest {:line   11
;;                              :column 2
;;                              :form   '(+ 1 1)
;;                              :file   "myfile.cljs"
;;                              :type   :magenta}))

;; ;; poi with (default) :margin-block of 1
;; (callout {:type :error}
;;          (point-of-interest {:line   11
;;                              :column 2
;;                              :form   '(+ 1 1)
;;                              :file   "myfile.cljs"
;;                              :type   :error}))
;; (callout {:type          :magenta
;;           :border-weight :heavy}
;;          (point-of-interest {:line   11
;;                              :column 2
;;                              :form   '(+ 1 1)
;;                              :file   "myfile.cljs"
;;                              :type   :magenta}))

;; ;; poi with :margin-block of 0
;; (callout {:type          :magenta
;;           :border-weight :heavy}
;;          (point-of-interest {:line         11
;;                              :column       2
;;                              :form         '(+ 1 1)
;;                              :file         "myfile.cljs"
;;                              :type         :magenta
;;                              :margin-block 0}))


;; (callout {:type          :magenta
;;           :border-weight :heavy}
;;          (enriched [:blue "One line."]
;;                    #_"\n"
;;                    #_[:red "red"]))

;; (callout {:type          :magenta
;;           :border-weight :heavy
;;           ;; :label "foo"
;;           }
;;          (enriched [:blue "1 of 2 lines."]
;;                    "\n"
;;                    [:red "2nd line"]))

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

;; (callout
;;  {:type :info
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
;;  {:type :info
;;   :border-weight :heavy
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
;;  "Example callout, single-arity")

;; (callout
;;  {:label "FOO"}
;;  "Example callout, custom label")

;; (callout
;;  {:label (enriched [:magenta-bg.white.bold " WTF "])}
;;  (enriched [:magenta "Example callout, styled-label"]))

;; (callout
;;  {:type :info}
;;  "Example callout, with :type of :info")



;; ;;; CALLOUT examples with light border -----------------------------------------

;; (callout {:type :info}
;;          "Example callout, with :type of :info")

;; (callout {:type  :info
;;           :label "My custom label"}
;;          "Example callout, with :type of :info and custom :label")

;; (callout {:type :warning}
;;          "Example callout, with :type of :warning")

;; (callout {:type :error}
;;          "Example callout, with :type of :error")

;; (callout {:type  :positive
;;           :label "SUCCESS!"}
;;          "Example callout, with :type of :positive, and custom :label")

;; (callout {:type :subtle}
;;          "Example callout, with :type of :subtle (or :gray)")

;; (callout {:type :magenta}
;;          "Example callout, with :type of :magenta")

;; (callout "Example callout, default")



; CALLOUT examples with medium border ----------------------------------------

;; (callout {:type          :info
;;           :border-weight :medium}
;;          "Example callout, with :type of :info")

;; ;; (callout {:type          :info
;; ;;           :border-weight :medium
;; ;;           :label         "My custom label"}
;; ;;          "Example callout, with :type of :info and custom :label")

;; (callout {:type          :warning
;;           :border-weight :medium}
;;          "Example callout, with :type of :warning")

;; (callout {:type :error
;;           :border-weight :medium}
;;          "Example callout, with :type of :error")

;; (callout {:type          :positive
;;           :label         "SUCCESS!"
;;           :border-weight :medium}
;;          "Example callout, with :type of :positive, and custom :label")

;; (callout {:type          :subtle
;;           :border-weight :medium}
;;          "Example callout, with :type of :subtle (or :gray)")

;; (callout {:type          :magenta
;;           :border-weight :medium}
;;          "Example callout, with :type of :magenta")

;; (callout {:border-weight :medium} "Example callout, default")



;; ;;; CALLOUT examples with heavy border ----------------------------------------

;; (callout {:type          :info
;;           :border-weight :heavy}
;;          "Example callout, with :type of :info")

;; (callout {:type          :info
;;           :border-weight :heavy
;;           :label         "My custom label"}
;;          "Example callout, with :type of :info and custom :label")

;; (callout {:type          :warning
;;           :border-weight :heavy}
;;          "Example callout, with :type of :warning")

;; (callout {:type :error
;;           :border-weight :heavy}
;;          "Example callout, with :type of :error")

;; (callout {:type          :positive
;;           :label         "SUCCESS!"
;;           :border-weight :heavy}
;;          "Example callout, with :type of :positive, and custom :label")

;; (callout {:type          :subtle
;;           :border-weight :heavy}
;;          "Example callout, with :type of :subtle (or :gray)")

;; (callout {:type          :magenta
;;           :border-weight :heavy}
;;          "Example callout, with :type of :magenta")

;; (callout {:border-weight :heavy} "Example callout, default")


;; (callout
;;  {:type          :warning
;;   :border-weight :heavy
;;   :padding-left  2}
;;  "Example callout, with :type of :warning, padding-left of 2.")

;; (callout
;;  {:type :info
;;   :label "My custom label warning"}
;;  "Example callout, with :type of :warning, and custom :label." )

;; (callout
;;  {:type :warning
;;   :label {:a :b}}
;;  "Example callout, with :type of :warning, and custom :label." )

#_(callout
 {:type           :error
  :margin-top     3 ; default is 1
  :margin-bottom  3 ; default is 1
  :padding-top    2 ; default is 0
  :padding-bottom 2 ; default is 0
  }
 "Example callout, with :type of :error, and custom spacing")

(callout {
          ;; :border-weight :medium
          ;; :label         "My label"
          :margin-left   2
          :margin-top    0
          :margin-bottom 0}
         (enriched [:bold "Hi"]))

#_(callout {:border-weight :medium
          :label         "My label"
          :margin-left   4}
         "Hi")


#_(callout {:border-weight :medium
                   :type          :positive
                   :margin-top    0
                   :data?         true}
                  (str "line 1"
                       "\n"
                       
                       "\n"
                       "line 3"))

#_(callout {:border-weight :medium
          :type          :blue
          :margin-top    0
          :data?         true}
         (string/join "\n" (range 10)))


#_(callout {
          :border-weight :heavy
          :label         "my label"}
         (callout {
                   :border-weight :medium
                   :type          :positive
                   :margin-top    0
                   :data?         true}
                  (str "line 1"
                       "\n"
                       (callout {
                                 :border-weight :medium
                                 :type          :blue
                                 :margin-top    0
                                 :data?         true}
                                (string/join "\n" (range 10)))
                       "\n"
                       "line 3")))
