#!/usr/bin/env bb

(ns zig-updater
  (:require [babashka.http-client :as http]
            [cheshire.core :as json]
            [clojure.string :as str]
            [babashka.fs :as fs]))

(defn split-version [version]
  (let [[first-part second-part] (str/split version #"-")]
    [first-part second-part]))

(defn get-master-version [url]
    (let [zig-json (http/get url)
          data (json/parse-string (:body zig-json) true)
          master-version (get-in data [:master :version])]
    :out
    master-version))

(defn read-prev-version []
  (let [prev-file-path ".prev.txt"
        prev-version (slurp prev-file-path)]
    (map str/trim-newline (split-version prev-version))))

(defn update-zig-spec [new-version]
  (let [spec-file-path "zig.spec"
        prev-version (read-prev-version)]
    (println "Previous version:" prev-version)
    (println "New version:" new-version)

    (when (or (not= (first prev-version) (first new-version))
               (not= (second prev-version) (second new-version)))
      (let [content (slurp spec-file-path)
            updated-content (str/replace content (second prev-version) (second new-version)
            )]
        (spit spec-file-path updated-content)
        ))))

(defn write-prev-version [new-version]
  (let [prev-file-path ".prev.txt"]
    (spit prev-file-path (str (str/join "-" new-version) "\n"))))

(let [new-version (split-version (get-master-version "https://ziglang.org/download/index.json"))]
  (update-zig-spec new-version)
  (println "Updated zig.spec with version:" new-version)
  (write-prev-version new-version))
