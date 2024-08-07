;; TODO 

;; callout

;; - Should we do callout-data as separate function, 
;;   or document the :data? option?

;; - Should callout spread the &args, or just take a
;;   message, which can be a string or vector for enriched


;; point of interest 

;; - Should take vector as the :line entry
;;     If a vector is supplied, draw a range of numbered lines if you can read
;;     ns at those lines.

;; - Should additional file-info (string), which would override the gen option
;; 


;; Adds about 9kb to a cljs bundle
(ns get-rich.core
  (:require [clojure.string :as string]
            #?(:cljs [goog.object])))

;; Defs -----------------------------------------------------------------------

(def ^:private xterm-colors-by-id
  {39  "#00afff"
   40  "#00d700"
   178 "#d7af00"
   247 "#9e9e9e"
   231 "#ffffff"
   201 "#ff00ff"
   202 "#ff5f00"
   16  "#000000"})

(def ^:private browser-dev-console-props 
  [:text-decoration-line      
   :text-decoration-style     
   :text-decoration-color 
   :text-underline-offset     
   :text-decoration-thickness 
   :line-height               
   :font-weight
   :font-style
   :color
   :background-color
   :border-radius
   :padding
   :padding-block
   :padding-block-start
   :padding-block-end
   :padding-inline
   :padding-inline-start
   :padding-inline-end
   :padding-bottom
   :padding-top
   :padding-right
   :padding-left
   :margin
   :margin-block
   :margin-block-start
   :margin-block-end
   :margin-inline
   :margin-inline-start
   :margin-inline-end
   :margin-bottom
   :margin-top
   :margin-right
   :margin-left])

(def ^:private colors-source
  {"red"    {:sgr 202 :semantic "negative"} 
   "yellow" {:sgr 178 :semantic "warning"}
   "green"  {:sgr 40 :semantic "positive"}
   "blue"   {:sgr 39 :semantic "accent"}
   "magenta"{:sgr 201}
   "gray"   {:sgr 247 :semantic "subtle"}
   "black"  {:sgr 16}
   "white"  {:sgr 231}})

(select-keys xterm-colors-by-id
             (->> colors-source
                  vals
                  (map :sgr)
                  (into [])))

(def ^:private semantics-by-callout-type
  {"error"    "negative"
   "warning"  "warning"
   "positive" "positive"
   "info"     "accent"
   "accent"   "accent"
   "subtle"   "subtle"
   "neutral"  "neutral"})

(def ^:private all-color-names
  (into #{}
        (concat (keys semantics-by-callout-type)
                (keys colors-source))))

(def ^:private color-names-by-semantic*
  (reduce-kv (fn [m color {:keys [semantic]}]
               (if semantic
                 (assoc m semantic color)
                 m))
             {}
             colors-source))

(def ^:private alert-type->label
  {"warning" "WARNING"
   "error"   "ERROR"
   "info"    "INFO"})


;; Helper functions -----------------------------------------------------------

(defn- ?sgr
  "For debugging of sgr code printing"
  [s]
  (println (string/replace s
                           #"\u001b\[([0-9;]*)[mK]"
                           (str "\033[38;5;231;48;5;247m"
                                "\\\\033["
                                "$1"
                                "m"
                                "\033[0;m")))
  s)

(defn- !?sgr
  "Temporarily silence debugging of sgr code printing"
  [s]
  s)

(defn- squiggly-underline [s]
  (string/join (repeat (count s) "^")))


(defn- maybe [x pred]
  (when (if (set? pred)
          (contains? pred x)
          (pred x))
    x))

(defn- nameable? [x]
  (or (string? x) (keyword? x) (symbol? x)))

(defn- as-str [x]
  (str (if (or (keyword? x) (symbol? x)) (name x) x)))

(defn- char-repeat [n s]
  (when (pos-int? n)
    (string/join (repeat n (or s "")))))

(defn- spaces [n] (string/join (repeat n " ")))

(defn- readable-sgr [x]
  #?(:cljs x
     :clj (str "\\033" (subs x 1))))

(defn- ns-info-str
  [{:keys [file line column]}]
  (str (some-> file (str ":")) line ":" column))

(defn- string-like? [v]
  (or (string? v)
      #?(:clj (-> v type str (= "java.util.regex.Pattern"))
         :cljs (-> v type str (= "#object[RegExp]")))
      (symbol? v)
      (keyword? v)
      (number? v)))

(defn- surround-with-quotes [x]
  (str "\"" x "\""))

(defn- shortened
  "Stringifies a collection and truncates the result with ellipsis 
   so that it fits on one line."
  [v limit]
  (let [limit  limit
        as-str (str v)]
    (if (> limit (count as-str))
      (surround-with-quotes as-str)
      (let [ret* (-> as-str
                     (string/split #"\n")
                     first)
            ret  (if (< limit (count ret*))
                   (let [ret          (->> ret*
                                           (take limit)
                                           string/join)
                         string-like? (string-like? v)]
                     (str (if (string? v) (surround-with-quotes ret) ret)
                          (when-not string-like? " ")
                          "..."))
                   ret*)]
        ret))))

(defn- css-stylemap->str [m]
  (reduce-kv (fn [acc k v]
               (if (and k v)
                 (str acc (as-str k) ":" (as-str v) ";")
                 acc))
             ""
             m))

(defn- x->sgr [x k]
  (when x
    (let [n (if (= k :fg) 38 48)]
      (if (int? x)
        (str n ";5;" x)
        (let [[r g b _] x
              ret       (str n ";2;" r ";" g ";" b)]
          ret)))))

(defn- m->sgr
  [{fgc*        :color
    bgc*        :background-color
    :keys       [font-style 
                 font-weight
                 disable-italics?
                 disable-font-weights?]
    :as         m}]
  (let [fgc    (x->sgr fgc* :fg)
        bgc    (x->sgr bgc* :bg)
        italic (when (and (not disable-italics?)
                          (contains? #{"italic" :italic} font-style))
                 "3;")
        weight (when (and (not disable-font-weights?)
                          (contains? #{"bold" :bold} font-weight))
                 ";1")
        ret    (str "\033[" 
                    italic
                    fgc
                    weight
                    (when (or (and fgc bgc)
                              (and weight bgc))
                      ";")
                    bgc
                    "m")]
    ret))


;; Color-related fns  ---------------------------------------------------------
(defn- assoc-hex-colors [m]
  (reduce-kv (fn [m color {:keys [sgr]}] 
               (let [hex (get xterm-colors-by-id sgr nil)]
                 (assoc m color {:sgr sgr
                                 :css hex})))
             {}
             m))

(defn- reduce-colors [m1 m2]
  (reduce-kv (fn [m k color] 
               (assoc m k (get m2 color)))
             {}
             m1))

(def ^:private color-codes
  (let [colors    (assoc-hex-colors colors-source )
        semantics (reduce-colors color-names-by-semantic* colors)
        callouts  (reduce-colors semantics-by-callout-type semantics)]
    {:all              (merge colors semantics callouts)
     :colors           colors
     :semantics        semantics
     :callouts         callouts
     :colors+semantics (merge colors semantics)}))

(defn- reduce-colors-to-sgr-or-css [ctx m]
  (reduce-kv (fn [m k v]
               (assoc m k (if (map? v) (ctx v) v)))
             {}
             m))

(defn- convert-color [m k v]
  (assoc m
         k 
         (if (contains? #{:background-color :color} k)
           (when (nameable? v)
             (get (:all color-codes)
                  (as-str v)
                  nil))
           v)))

(defn- et-vec? [x]
  (and (vector? x)
       (= 2 (count x))
       (-> x
           (nth 0)
           (maybe #(or (keyword? %)
                       (map? %))))))


;; Formatting helper fns  -----------------------------------------------------

(defn- callout-type [opts]
  (let [x (:type opts)]
    (cond 
      (keyword? x) (name x)
      (string? x)  x
      :else
      (some-> (when (map? x)
                (or (get x :type nil)
                    (get x "type" nil)))
              (maybe nameable?)
              name))))

(defn- default-spacing [n default]
  (if (and (int? n) (<= 0 n)) n default))


(declare callout)
(declare enriched)
(declare print-enriched)


(defn- maybe-wrap [x]
  (when (et-vec? x)
    #?(:cljs
       (js/console.warn
        "get-rich.core/point-of-interest\n\n"
        "Supplied value for :header option:\n\n"
        x
        "\n\n"
        "If you are trying to style this text, this value needs to be wrapped"
        "in a vector like this:\n\n"
        [x]
        "\n\n")
       :clj
       ()))
  (cond (coll? x) x :else [x]))


;; Formatting exceptions ----------------------------------------------------------

(defn stack-trace-preview
  "Creates a user-friendly stack-trace preview, limited to the frames which
   contain a match with the supplied regex, up to the `depth` value, if supplied.
   `depth` defaults to 7."
  [{:keys [error regex depth]}]
  #?(:clj
     (if-let [strace (some->> (maybe error #(instance? Exception %))
                              .getStackTrace
                              seq)]
       (let [_ (try (+ 1 true)
                    (catch Exception e (println (instance? Exception e))))
             strace-len  (count strace)
             depth       (or (maybe depth pos-int?) 7)

             ;; Get a mini-strace, limited to the number of frames that will be
             ;; displayed based on `depth`
             mini-strace (->> strace
                              (take depth)
                              (map StackTraceElement->vec))

             ;; If regex is legit, get a list of indexes that match the regex
             ;; passed in by user. Regex will match on ns or filename where
             ;; user's their program lives. Then get the last index of a match
             ;; (within the mini-strace). If regex is not legit, use the depth.
             last-index  (if (= java.util.regex.Pattern (type regex))
                           (some->> mini-strace
                                    (keep-indexed
                                     (fn [i [f]]
                                       (when (re-find regex (str f)) 
                                         i)))
                                    seq
                                    (take depth)
                                    last)
                           (dec depth))

             ;; Get all the frames up to the last index
             trace*      (when last-index
                           (->> mini-strace (take (inc last-index))))
             len         (when trace* (count trace*)) 
             trace       (some->> trace* (interpose "\n") (into []))
             num-dropped (when trace 
                           (let [n (- (or strace-len 0) (or len 0))]
                             (some->> (maybe n pos-int?)
                                      (str "\n...+"))))

             ;; Conj num-dropped annotation to mini-strace
             trace       (some-> trace (conj num-dropped))]
         ;; Create and return multiline string
         (apply str trace))
       (callout {:type :warning}
                (enriched 
                 "get-rich.core/stack-trace-preview\n\n"
                 "Value of the "
                 [:bold :error]
                 " option should be an instance of "
                 [:bold 'java.lang.Exception.]
                 "\n\n"
                 "Value received:\n"
                 [:bold (shortened error 33)]
                 "\n\n"
                 "Type of value received:\n"
                 [:bold (str (type error))]
                 )))))



;; Line and point of interest public fns  -------------------------------------

(defn ^:public point-of-interest
  "A namespace info diagram which identifies a specific sexp. This provides the
   namespace, column, and line number, and a bolded, potentially truncated,
   representation of the specific form of interest. This form representation is
   accented with a squiggly underline.
   
   The `:line`, `:column`, `:form`, and `:file` options must all be present in
   order for the namespece info diagram to be rendered. If the `:form` option is
   supplied, but any of the others are omitted, only the form will be rendered
   (with a squiggly underline and no stacktrace diagram)."
  [{:keys [line file column form header body]
    squiggly-color :type
    :as opts}]
  (let [file-info    (ns-info-str opts) 
        gutter       (some-> line str count spaces)
        color        (or (some-> squiggly-color
                                 as-str
                                 (maybe all-color-names))
                         "neutral")
        form-as-str  (shortened form 33)
        squig        (squiggly-underline form-as-str)
        header-lines (maybe-wrap header)
        body-lines   (maybe-wrap body)
        bolded-form  [{:font-weight :bold} form-as-str]
        bolded-squig [{:font-weight :bold :color color} squig]
        ]
    (apply enriched
     (concat
      header-lines
      (when header ["\n"])
      (cond (and line column file form)
            ["\n"
             gutter " ┌─ " file-info "\n"
             gutter " │  \n"
             line   " │ " bolded-form "\n"
             gutter " │ " bolded-squig
             "\n"]
            
            form
            ["\n"
             bolded-form "\n"
             bolded-squig
             "\n"])
      (when body ["\n"])
      body-lines))))

;; Race-condition-free version of clojure.core/println,
;; maybe useful if any weird behavior arises
#?(:clj
   (defn- safe-println [& more]
     (.write *out* (str (clojure.string/join " " more) "\n"))))

#?(:cljs 
   (do
     (defn ^:public print-enriched
       ([o]
        (print-enriched o js/console.log))
       ([o f]
        ;; (js/console.log o)
        (.apply f js/console (goog.object/get o "consoleArray"))))
     (deftype Enriched [tagged css consoleArray])))

(defn ^:public callout
  "Prints a message to the console with a block-based coloring motif
  controlled by the `:type` option.
  
  In terminal emulator consoles, this will print a colored bounding
  border in the inline start position. The color of the border is
  determined by the value of the `:type` option.
   
  In browser consoles, a border is not used, as the background and
  foreground text of the message block is automatically colored by
  the browser dev tools logging mechanism based on the type of logging
  function that is used. However, this automatic styling only applies
  to the `:type` options of `:error` and `:warn`.
       
  Prints an optional, bolded label in the same color as the callout
  styling, in the block start postion. If a :type option is set, the
  label string will default to an uppercased version of that string,
  e.g. {:type :INFO} => \"INFO\". If a `:label` option is
  supplied, that value is used instead.
       
  The amount of vertical padding (in number of lines) within
  the bounds of the message body can be controlled the `padding-top`
  and `padding-bottom` options. The amount of space (in number of lines)
  above and below the message block can be controlled the `margin-top`
  and `margin-bottom` options. Margins are only applicable to callouts
  formatted for the terminal emulator."
  ([value]
   (callout {} value))
  ([{:keys [label
            wrap?
            data?
            border-weight
            margin-top
            margin-bottom
            padding-top
            padding-bottom
            padding-left]
     :as   opts}
    value]
   (if-not (map? opts)
     (callout
      {:type :warning
    ;; :border-weight :heavy
       }
      (point-of-interest
       {:type   :warning
        :header "get-rich.core/callout"
        :form   (cons 'callout (list opts value))
        :body   (str "get-rich-core/callout expects a map of options,\n"
                     "followed by any number of values (usually strings).\n\n"
                     "Nothing will be printed.")}))
     (let [padding-top    (default-spacing padding-top #?(:cljs 1 :clj 0))
           padding-bottom (default-spacing padding-bottom #?(:cljs 1 :clj 0))
           padding-left   (default-spacing padding-left #?(:cljs 0 :clj 1))
           margin-top     (default-spacing margin-top 1)
           margin-bottom  (default-spacing margin-bottom 0)
           callout-type   (callout-type opts)
           color          (or callout-type "neutral")
           border-weight  (or (some-> border-weight
                                      (maybe #{:light
                                               "light"
                                               :heavy
                                               "heavy"
                                               :medium})
                                      name)
                              "light")
           wrap?          (true? wrap?)
           label          (or label
                              (get alert-type->label
                                   callout-type
                                   nil))]


       #?(:cljs
          ;; move to enriched or data
          (let [f
                (case callout-type 
                  "warning" (.-warn  js/console)
                  "error"   (.-error  js/console)
                  (.-log  js/console))


                pb-str
                (when (pos-int? padding-bottom)
                  (char-repeat padding-bottom "\n"))

                arr
                (cond
                  (and (instance? Enriched value)
                       (instance? Enriched label))
                  (let [tagged       (goog.object/get value "tagged")
                        css          (goog.object/get value "css")
                        label-css    (goog.object/get label "css")
                        label        (some-> (goog.object/get label "tagged")
                                             (str "\n"
                                                  (char-repeat padding-top
                                                               "\n")))]
                    (.concat #js[(str label
                                      tagged
                                      pb-str)]
                             (.concat label-css css)))
                  :else
                  #js[(str (some-> label
                                   (str "\n"
                                        (char-repeat padding-top "\n")))
                           value
                           pb-str)])]

            (if (true? data?)
              arr 
              (.apply f js/console arr)))
          

          :clj
          ;; TODO - abstract this so you can use from server-side js
          (let [thin-border-opts
                {:font-weight :bold
                 :color       color}

                label-opts
                thin-border-opts

                thick-style 
                {:background-color color
                 :color            :white
                 :font-weight      :bold}

                border-style
                thick-style

                left-border 
                (case border-weight
                  "light"   "┃"
                  "medium"  " "
                  "heavy"   "  ")

                padding-left
                2

                callout-str
                (str (char-repeat margin-top "\n")
                     (if (contains? #{"heavy" "medium"} border-weight)
                       ;; heavy style border
                       (str
                        (enriched [border-style
                                   (if wrap?
                                     (str "\n   " label)
                                     (str left-border))]
                                  (when label (char-repeat padding-left " "))
                                  (when label [label-opts label]))
                        (str "\n" (enriched [border-style left-border]))
                        (string/replace 
                         (str (char-repeat padding-top "\n")
                              (char-repeat padding-left " ")
                              value)
                         #"\n"
                         (str "\n" (enriched [border-style left-border] " ")))
                        (char-repeat padding-bottom 
                                     (str "\n"
                                          (enriched [border-style left-border])))
                        #_(enriched [thick-style (if wrap? "\n" " ")]))
                       
                       ;; standard border, supports left-padding
                       (str
                        (enriched [thin-border-opts
                                   (str "┏"
                                        (some->> label
                                                 (str (char-repeat padding-left "━")
                                                      " ")))])
                        (string/replace 
                         (str (char-repeat padding-top "\n") "\n"
                              value)
                         #"\n"
                         (str "\n"
                              (enriched [thin-border-opts "┃ "]
                                        (char-repeat padding-left " "))))
                        (char-repeat padding-bottom 
                                     (str "\n" (enriched [thin-border-opts
                                                          left-border])))
                        (str "\n"
                             (enriched [thin-border-opts
                                        (str "┗"
                                             (char-repeat padding-left "━"))]))))
                     (char-repeat margin-bottom "\n"))]
            (if (true? data?)
              callout-str
              (println callout-str))))))))



;; Enriched text public fns and helpers  --------------------------------------

(defn- ^:private tagged-str
  "Expects an EnrichedText record.
   In Clojure, returns string wrapped with appropriate sgr codes for rich
   printing. In ClojureScript, returns a string wrapped in style escape
   chars (%c)."
  [o]
  #?(:cljs
     (str "%c" (:value o) "%c")
     :clj
     (do 
       (str (->> o
                 :style
                 (reduce-colors-to-sgr-or-css :sgr)
                 m->sgr)
            (:value o)
            "\033[0;m"))))

(defn- tag->map [acc s]
  (let [[k m] (case s
                "bold"   [:font-weight "bold"]
                "italic" [:font-style "italic"]
                (let [cs (:all color-codes)
                      m  (get cs s nil)]
                  (if m
                    [:color m]
                    (when-let [nm (string/replace s #"-bg$" "")] 
                      (when-let [m (get cs nm nil)]
                        [:background-color m])))))]
    (if k (assoc acc k m) acc)))

(defrecord EnrichedText [value style])

(defn- enriched-text
  "Returns an EnrichedText record. The `:value` entry is intended to be
   displayed as a text string in the console, while the `:style` entry
   is a map of styling to be applied to the printed text.

   Private, for lib internal use.
   
   Example:
   #my.ns/EnrichedText {:style {:font-weight \"bold\"
                                :color       {:sgr 39
                                              :css \"#00afff\"}
                        :value \"hi\"}"
  [[style v]]
  (->EnrichedText
   (str v)
   (cond 
     (map? style)
     (reduce-kv convert-color {} style)
     
     (or (keyword? style)
         (string? style))
     (-> style
         name
         (string/split (if (keyword? style)
                         #"\."
                         #" "))
         (->> (reduce tag->map {}))))))


(defn- updated-css [css-styles x]
  (if-let [style (some-> x
                         (maybe et-vec?)
                         enriched-text
                         :style)]

    (let [style  (select-keys style browser-dev-console-props) 
          style  (reduce-colors-to-sgr-or-css :css style)
          ks     (keys style)
          resets (reduce (fn [acc k]
                           (assoc acc k "initial"))
                         {}
                         ks)]
      (conj css-styles
            (css-stylemap->str style)
            (css-stylemap->str resets)))
    css-styles))


(defn- enriched-data-inner
  [[coll css] x] 
  (let [s (cond (et-vec? x)
                (tagged-str (enriched-text x))
                (not (coll? x))
                (as-str x))]
    ;; (? {:print-with prn} s)
    [(conj coll s)
     (updated-css css x)]))


(defn- enriched-data* [coll]
  (let [[coll css] (reduce enriched-data-inner
                           [[] []]
                           coll)
        tagged     (string/join coll)]
    {:console-array (into-array (concat [tagged] css))
     :tagged        tagged
     :css           css}))


(defn ^:public enriched-data [coll]
  #?(:cljs
     (enriched-data* coll)
     :clj
     (-> coll enriched-data* :tagged)))


(defn ^:public enriched-data-css [coll]
  (-> coll enriched-data* :css))


(defn ^:public enriched
  "In a terminal emulator context, returns a string tagged with SGR codes to
   style the text as desired.
   
   In a browser context, returns an instance of
   `Enriched`, a js object with the the following fields:

   `tagged`
   A string with the appropriate tags for styling in browser consoles
  
   `css`
   An array of styles that sync with the tagged string.

   `consoleArray`
   An array by `Array.unshift`ing the tagged string onto the css array.
   This is the format that is needed for printing in a browser console, e.g.
   `(.apply js/console.log js/console (goog.object/get o \"consoleArray\"))`.
   
   For browser usage, sugar for the above `.apply` call is provided with
   `get-rich.core/print-enriched`. You can use it like this:
   `(print-enriched (enriched [:bold.blue \"my blue text\"]))"

  [& coll]
  #?(:cljs
     (let [{:keys [css tagged console-array]} (enriched-data* coll)]
      ;;  (js/console.log "tagged:" tagged)
      ;;  (js/console.log "css:" css)
      ;;  (js/console.log "console-array:" console-array)
       #_(.apply js/console.log js/console js-arr)
       #_css
       (Enriched. tagged
                  (into-array css)
                  console-array))
     :clj (:tagged (enriched-data* coll))))

