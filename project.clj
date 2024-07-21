(defproject io.github.paintparty/get-rich "0.1.0-a"
  :description "Rich text console printing for Clojure(Script)"
  :url "https://github.com/paintparty/fireworks"
  :license {:name "MIT"}
  :dependencies [[org.clojure/clojure "1.10.3"]]
  :repl-options {:init-ns get-rich.core}
  :deploy-repositories [["clojars" {:url           "https://clojars.org/repo"
                                    :sign-releases false}]])
