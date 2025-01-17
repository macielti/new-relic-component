(defproject net.clojars.macielti/new-relic-component "0.2.1"

  :description "New Relic component for Clojure applications"

  :url "https://github.com/macielti/new-relic-component"

  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url  "https://www.eclipse.org/legal/epl-2.0/"}

  :dependencies [[org.clojure/clojure "1.12.0"]
                 [integrant "0.13.1"]
                 [com.taoensso/timbre "6.6.1"]
                 [net.clojars.macielti/http-client-component "1.1.0"]
                 [net.clojars.macielti/common-clj "42.74.74"]
                 [dev.weavejester/medley "1.8.1"]]

  :profiles {:dev {:resource-paths ^:replace ["test/resources"]

                   :test-paths     ^:replace ["test/unit" "test/integration" "test/helpers"]

                   :plugins        [[lein-cloverage "1.2.4"]
                                    [com.github.clojure-lsp/lein-clojure-lsp "1.4.15"]
                                    [com.github.liquidz/antq "RELEASE"]]

                   :dependencies   [[hashp "0.2.2"]]

                   :injections     [(require 'hashp.core)]

                   :aliases        {"clean-ns"     ["clojure-lsp" "clean-ns" "--dry"] ;; check if namespaces are clean
                                    "format"       ["clojure-lsp" "format" "--dry"] ;; check if namespaces are formatted
                                    "diagnostics"  ["clojure-lsp" "diagnostics"]
                                    "lint"         ["do" ["clean-ns"] ["format"] ["diagnostics"]]
                                    "clean-ns-fix" ["clojure-lsp" "clean-ns"]
                                    "format-fix"   ["clojure-lsp" "format"]
                                    "lint-fix"     ["do" ["clean-ns-fix"] ["format-fix"]]}
                   :repl-options   {:init-ns new-relic-component.core}}}

  :resource-paths ["resources"])
