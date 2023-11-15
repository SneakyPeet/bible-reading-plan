(ns pghbr.frontend.storage
  (:require [clojure.string :as string]))


(defn clear-storage! []
  (try
    (js/localStorage.clear)
    (catch :default e
      (js/console.log e))))


(defn- get-from-storage [k default]
  (try
    (or (js/localStorage.getItem k) default)
    (catch :default _ default)))


(defn- save-in-storage! [k value]
  (try
    (js/localStorage.setItem k value)
    (catch :default e
      (js/console.log e) ;; no one will ever know
      )))


;; Day

(def ^:private localstorage-day-key "pghbr-day")
(def ^:private default-start-day "1")


(defn get-day []
  (js/parseInt
    (get-from-storage localstorage-day-key default-start-day)))


(defn set-day! [day]
  (save-in-storage! localstorage-day-key day))

;; Lists Reading

(def ^:private localstorage-read-lists-key "pghbr-read-lists")
(def ^:private default-read-lists "")

(defn get-read-lists []
  (let [v (get-from-storage localstorage-read-lists-key default-read-lists)]
    (->> (string/split v #",")
         (remove empty?)
         (map js/parseInt)
         set)))

(defn save-read-list! [read-list]
  (->> (string/join "," read-list)
       (save-in-storage! localstorage-read-lists-key)))
