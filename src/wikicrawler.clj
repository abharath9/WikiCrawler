(ns wikicrawler
  (:require [net.cgrand.enlive-html :as html])
  (:require [net.cgrand.tagsoup :as tagsoup])
  (:import (java.net URL)))


(def ^:const base-url "https://en.wikipedia.org")
(def ^:dynamic url-set #{})
(def ^:dynamic run-count 0)


(defn parse-wiki-url [url]
  "parse url page for html tags using Enlive library"
  (html/html-resource (URL. url))
  )


(defn parse-valid-string [crawl-links crawl-link-tags crawl-link-italic-tags]
  (def first-link (first (first crawl-links)))
  (def first-link-tag (first crawl-link-tags))
  (def first-link-italic-tag (first crawl-link-italic-tags))
  "check whether the page exists or not"
  (try
    (parse-wiki-url (str base-url first-link))
    (catch Exception e
      (parse-valid-string (rest crawl-links) (rest crawl-link-tags) crawl-link-italic-tags)
      )
    )
  "ignore all the File links, Help links, Languge and special pages,citation links,"
  (if (or (= (re-matches #"/wiki/Help:.*|/wiki/Special:.*|/wiki/.*/media/File.*|/wiki/Wiktionary:.*|/wiki/Wikipedia:.*|/wiki/File:.*" first-link) first-link)
          (= (re-matches #"#cite_note.*|//tools.wmflabs.org/*" first-link) first-link)
          (= (re-matches #"\(.*\)|[.*]" first-link-tag) first-link-tag)
          (= "/wiki/Latin_language" first-link)
          (= "/wiki/Language" first-link)
          (= "/wiki/Greek_language" first-link)
          (= "/wiki/Ancient_Greek" first-link)
          (= "" (clojure.string/trim first-link-tag))
          (contains? url-set first-link)
          (= first-link-tag first-link-italic-tag)
          )
    (do
      (if (= first-link-tag first-link-italic-tag)
        (parse-valid-string (rest crawl-links) (rest crawl-link-tags) (rest crawl-link-italic-tags))
        (parse-valid-string (rest crawl-links) (rest crawl-link-tags) crawl-link-italic-tags)
        )
      )

    [first-link first-link-tag]
    ;;[first-link first-link-tag]
    )
  )


(defn wiki-crawl-to-philosophy [urlstring]
  "adding the argument url to the hashset"
  (if (= run-count 0)
    (def url-set (conj url-set (clojure.string/replace urlstring "https://en.wikipedia.org" "")))
    )
  "try/catch block to ensure right url has been passed as an argument"
  (try
    "crawl-links,crawl-link-tags,and crawl-link-italic-tags stores all the relative link paths,tag names and italic tag urls present in the current wiki page"
    (def crawl-links (map #(html/attr-values % :href) (html/select (parse-wiki-url urlstring) [:p (html/attr? :href)])))
    (def crawl-link-tags (map html/text (html/select (parse-wiki-url urlstring) [:p :a])))
    (def crawl-link-italic-tags (map html/text (html/select (parse-wiki-url urlstring) [:p :i :a])))
    (catch Exception e
      (println "Something wrong with the input url string")
      ))
  "crawl-links is empty or not"
  (if (empty? crawl-links)
    (println "path not found")
    (do
      "call parse-valid-string function to find out the valid links"
      (def link-list (parse-valid-string crawl-links crawl-link-tags crawl-link-italic-tags))
      (def first-link (first link-list))
      (def first-link-tag (second link-list))
      "store the link url to the hashset"
      (def url-set (conj url-set first-link))
      "increase the link count by 1"
      (alter-var-root #'run-count inc)
      (println (str first-link-tag "(" (str base-url first-link) ")" "[" run-count "]"))
      "check if the link is pointing to the Philosophy page or not"
      (if (= first-link "/wiki/Philosophy")
        (println "end..path found")
        (do
          (println "      |     ")
          "recursive call to wiki-crawl-to-philosophy by passing new wiki page"
          (wiki-crawl-to-philosophy (str base-url first-link))
          )
        )
      )
    )
  )

;;(wiki-crawl-to-philosophy "https://en.wikipedia.org/wiki/20th_meridian_east")
;;https://en.wikipedia.org/wiki/Modern_Greek
;;https://en.wikipedia.org/wiki/Wikipedia:Red_link