(defproject metrology "0.1.0-SNAPSHOT"
  :description ""
  :url "http://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [
    [org.clojure/clojure "1.11.1"]
    [org.clojure/java.jdbc "0.7.12"]
    [org.xerial/sqlite-jdbc "3.42.0.0"]
    [org.clojure/core.specs.alpha "0.2.62"]
    [seesaw "1.5.0"]
    [hiccup "2.0.0-RC3"]
    [ring/ring-core "2.0.0-alpha1"]
    [ring/ring-jetty-adapter "2.0.0-alpha1"]
    [compojure "1.7.1"]
    [ring/ring-defaults "0.5.0"]]
  :main ^:skip-aot metrology.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all
                       :jvm-opts ["-Dclojure.compiler.direct-linking=true"]}})
