(ns metrology.lib.gen-html
  (:require [clojure.string :as string]))

(defn indent
  "Добавить отступ к строкам."
  [s]
  #_(reduce (fn [a p] (str a "\n  " p))
          ""
          (string/split s #"\n"))
  (str "  " (string/join "\n  " (string/split s #"\n"))))

(defmacro html-tag
  [tag]
  (let [s (gensym "s")]
    `(defn ~(symbol tag)
       ([& ~s]
        (string/join "\n" 
                     (list 
                       ~(str \< tag \>)
                       (indent (string/join "\n" ~s))
                       ~(str \< \/ tag \>))))
       ([]
        (~tag "")))))

(html-tag html)
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

(spit "/media/sf_YandexDisk/Ermolaev/midb/protocol.html"
      (html (head head-content) (section (header) (main (p "Hello!")) (footer))))

(def head-content
  "  <meta charset=\"utf-8\">
  <meta name=\"author\" content=\"Aleksandr Ermolaev\">
  <meta name=\"e-mail\" content=\"ave6990@ya.ru\">
  <meta name=\"version\" content=\"2023-04-19\">
  <title>protocols</title>
  <style type=\"text/css\">
    html {
      font-family: Times New Roman;
      font-size: 12pt;
    }
    section {
      position: relative;
      height: 100%;
      page-break-after: always;
    }
    .appendix-section {
      margin: 0 0 6pt 0;  
    }
    .capitalize {
      text-transform: uppercase;
    }
    p {
      margin: 0;
      text-align: justify;
    }
    header {
      margin: 0 0 10pt 0;
    }
    .header1 > p {
      text-align: center;
    }
    .field {
      margin: 0 0 6pt 0;
    }
    table {
      width: 100%;
      border-collapse: collapse;
      border: 1px solid;
      font-size: 10pt;
      line-height: 0.9;
    }
    td, th {
      border: 1px solid;
    }
    .measurement-table th {
      max-width: 25%;
    }
    .centered-cell {
      text-align: center;
    }
    .channel-cell {
      white-space: nowrap;
      min-width: 20%;
    }
    .sign {
      margin: 10pt 0;
      position: relative;
    }
    ol {
      margin: 0;
      padding: 0 0.35cm;
    }
    .comment {
      font-size: 9pt;
    }
    footer > p {
      position: absolute;
      top: 26.5cm;
      right: 0;
    }
    @media print {
      @page {
        margin: 1cm;
      }
    }
    .two-column {
      margin: 0 0 6pt 0;
      display: flex;
      flex-direction: row;
    }
    .two-column > p {
      flex: 1;
    }
    .sign > img {
    	position: absolute;
     	top: -3.5cm;
     	left: 3.5cm;
      	transform: scale(0.28);
    }
  </style>
  <script type=\"text/javascript\">
    document.addEventListener(\"dblclick\", (event) => {
      console.log(document.getElementsByClassName(\"sign_img\"))
      const signs = document.getElementsByClassName(\"sign_img\")
      for (const el of signs) {
        el.style.visibility = el.style.visibility == \"visible\" ? \"hidden\" : \"visible\"
      }
    })
  </script>")


(comment

(require '[clojure.repl :refer :all])

(doc string/split)

)
