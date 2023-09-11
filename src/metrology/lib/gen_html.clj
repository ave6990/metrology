(ns metrology.lib.gen-html
  (:require [clojure.string :as string]))

(defn indent
  "Добавить отступ к строкам."
  [s]
  #_(reduce (fn [a p] (str a "\n  " p))
          ""
          (string/split s #"\n"))
  (str "  " (string/join "\n  " (string/split s #"\n"))))

(defn set-attributes
 "Преобразуеть hash-map в строку с аттрибутами html-тэгов."
  [m]
  (reduce (fn [s [k v]] (str s " "
                             (string/replace (str k) ":" "")
                             "=\"" v "\""))
          ""
          m))

(defmacro html-tag
  [tag]
  (let [s (gensym "s")]
    `(defn ~(symbol tag)
       ([& ~s]
        (string/join "\n" 
                     (list 
                       (str ~(str \< tag)
                            (if (map? (first ~s))
                                (set-attributes (first ~s))
                                "")
                            ~(str \>))
                       (indent (string/join "\n" (if (map? (first ~s))
                                                     (rest ~s)
                                                     ~s)))
                       ~(str \< \/ tag \>))))
       ([]
        (~tag "")))))

(html-tag html)
(html-tag head)
(html-tag title)
(html-tag style)
(html-tag script)
(html-tag body)
(html-tag section)
(html-tag div)
(html-tag header)
(html-tag main)
(html-tag footer)
(html-tag table)
(html-tag tr)
(html-tag th)
(html-tag td)
(html-tag p)
(html-tag time)
(html-tag strong)
(html-tag ul)
(html-tag ol)
(html-tag li)

(defn doctype
  "<!doctype html>"
  [& xs]
  (str "<!doctype html>\n" (string/join "\n" xs)))

(defn meta
  "Tag _meta_ with attributes."
  [m]
  (str "<meta"
       (if (= (class m) java.lang.String)
           (str " " m)
           (reduce (fn [s [k v]]
                     (str s " name=\""
                           (string/replace k ":" "")
                           "\" content=\"" v "\""))
                   ""
                   m))
       ">"))

(def br
  "<br>")

(comment

(spit "/media/sf_YandexDisk/Ermolaev/midb/protocol.html"
      (html
        (head)
        (body
          (section
            (header)
            (main
              (p {:id "greeting"} "Hello!"))
            (footer)))))

(require '[clojure.repl :refer :all])

(doc time)

)
