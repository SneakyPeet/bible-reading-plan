(ns pghbr.frontend.storage)

(def ^:private localstorage-key "pghbr-day")
(def ^:private default-start-day "1")


(defn get-day []
  (js/parseInt
    (try
      (or (js/localStorage.getItem localstorage-key) default-start-day)
      (catch :default _ default-start-day))))


(defn set-day! [day]
  (try
    (js/localStorage.setItem localstorage-key day)
    (catch :default e
      (js/console.log e))))


(defn clear-storage! []
  (try
    (js/localStorage.clear)
    (catch :default e
      (js/console.log e))))
