(ns get-rich.core-test
  (:require [clojure.test :refer :all]
            [clojure.string :as string]
            [get-rich.core :refer [enriched callout point-of-interest]]))

(defn example-custom-callout [opts]
  (let [message (point-of-interest
                 (merge opts
                        {:squiggly-color :error
                         :header         "Your header of your template goes here."
                         :body           ["The body of your template goes here."
                                          "Second line of copy."
                                          "Another line."]}))]
    (callout opts message)))

#_(example-custom-callout
 {:file       "example.ns.core"
  :line       11
  :column     1
  :form       '(+ 1 true)
  :type :error})

;; (println 
;;  (point-of-interest
;;   {:file           "example.ns.core"
;;    :line           11
;;    :column         1
;;    :form           '(+ 1 true)
;;    :squiggly-color :error}))

;; (println)
;; (println)
;; (println (point-of-interest
;;           (merge {:file           "example.ns.core"
;;                   :line           11
;;                   :column         1
;;                   :form           '(+ 1 true)
;;                   :squiggly-color :error
;;                   :header         "Your header of your template goes here."
;;                   :body           ["The body of your template goes here."
;;                                    "Second line of copy."
;;                                    "Another line."]})))

;; (println)
;; (println)
;; (example-custom-callout
;;  {:type   :error
;;   :file   "example.ns.core"
;;   :line   11
;;   :column 1
;;   :form   '(+ 1 true)})

;; (println)
;; (println)
;; (println)
;; (println (enriched [:bold "bold"] ", " [:italic "italic"] ", or " [:blue "colored"]))

;; (println)
;; (println (enriched [:bold.italic "bold & italic"]
;;                    ", "
;;                    [:italic.blue "italic & colored"]
;;                    ", "
;;                    [:bold.italic.blue "bold & italic & colored"]))

;; (println)
;; (println (enriched [:bold.red "Red"]
;;                    ", "
;;                    [:bold.yellow "Yellow"]
;;                    ", "
;;                    [:bold.green "Green"]
;;                    ", "
;;                    [:bold.blue "Blue"]
;;                    ", "
;;                    [:bold.purple "Purple"]
;;                    ", "
;;                    [:bold.magenta "Magenta"]
;;                    ", "
;;                    [:bold.gray "Gray"]
;;                    ", "
;;                    [:bold.black "Black"]
;;                    ", "
;;                    [:bold.white "White"] ))

;; (println)
;; (println (enriched [:bold.negative "Negative"]
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


;; ;; ;; Rich text examples

;; ;; ;; callout examples


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
 {:type :purple}
 "Example callout, with :type of :purple")

(callout
 "Example callout, default")

(callout
 {:type           :error
  :margin-top     3 ; default is 1
  :margin-bottom  3 ; default is 1
  :padding-top    2 ; default is 0
  :padding-bottom 2 ; default is 0
  }
 "Example callout, with :type of :error, and custom spacing")

;; (example-custom-callout
;;  {:form-meta   {:file   "example.ns.core"
;;                 :line   11
;;                 :column 1}
;;   :quoted-form '(+ 1 true)
;;   :type  :warning})

;; (example-custom-callout
;;  {:form-meta   {:file   "example.ns.core"
;;                 :line   11
;;                 :column 1}
;;   :quoted-form '(+ 1 true)
;;   :type  :info})

;; (example-custom-callout
;;  {:form-meta   {:file   "example.ns.core"
;;                 :line   11
;;                 :column 1}
;;   :quoted-form '(+ 1 true)})

;; (callout
;;  {:type     :info
;;   :margin-top     1
;;   :margin-bottom  1
;;   :padding-top    0
;;   :padding-bottom 0
;;   :label          label
;;   :message        (problem-with-line-info 
;;                    form-meta
;;                    {:type alert-type
;;                     :header     "Your header message goes here."
;;                     :form       quoted-form
;;                     :body       [(str "The body of your message goes here.\n"
;;                                       "Second line of copy.\n"
;;                                       "Another line.")]})})


;; (doseq [color #_[:error "error" :warning :info :positive :negative]
;;         [:red :yellow :green :blue :purple :magenta :gray :black :white]
;;         :let [nm (string/capitalize (name color))]]
;;   (println (enriched [{:color color} nm]
;;                      " "
;;                      [{:color       color
;;                        :font-weight :bold} nm])))

;; (callout
;;  {:message (string/join "\n"
;;             (for [color [:red :yellow :green :blue :purple :magenta :gray :black :white]
;;                   :let  [nm (string/capitalize (name color))]]
;;               (enriched #_[{:color color} nm]
;;                         #_" "
;;                         [{:color       color
;;                           :font-weight :bold} nm])))})
