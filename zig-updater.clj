#!/usr/bin/env bb

(ns zig-updater
  (:require [babashka.http-client :as http]
            [cheshire.core :as json]
            [clojure.string :as str]
            [babashka.fs :as fs]))

(defn split-version [version]
  (let [first-part (first (str/split version #"-"))
        second-part (second (str/split version #"-"))]
    [first-part second-part]))

(defn get-master-version [url]
    (let [zig-json (http/get url)
          data (json/parse-string (:body zig-json) true)
          master-version (get-in data [:master :version])]
    :out
    master-version))

(println (split-version (get-master-version "https://ziglang.org/download/index.json")))

(defn update-zig-spec [new-version]
  (let [spec-file-path "zig.spec"
        prev-file-path ".prev.txt"
        prev-version (split-version (slurp prev-file-path))]
    (println "Previous version:" prev-version)
    (when (not= (second prev-version) (second new-version))
      (let [content (slurp spec-file-path)
            updated-content (str/replace content (str/trim-newline (second prev-version)) (str/trim-newline (second new-version))
            )]
        (spit spec-file-path updated-content)
        (spit prev-file-path (str/join "-" new-version))))))

(let [new-version (split-version (get-master-version "https://ziglang.org/download/index.json"))]
  (update-zig-spec new-version)
  (println "Updated zig.spec with version:" new-version))
