(ns pghbr.frontend.app)

(def ^:private localstorage-key "pghbr-day")
(def ^:private default-start-day 1)


(defn- get-day-from-storage []
  (try
    (or (js/localStorage.getItem localstorage-key) default-start-day)
    (catch :default _ default-start-day)))


(defn init []
  (prn (get-day-from-storage)))
