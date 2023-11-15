(ns pghbr.frontend.state
  (:require [pghbr.bible.core :as bible]
            [pghbr.frontend.storage :as storage]
            [reagent.core :as r]))


(defonce *app-state (r/atom {:read-lists #{}}))

;; Read lists

(defn- init-read-list! []
  (swap! *app-state assoc :read-lists (storage/get-read-lists)))


(defn- update-lists-read! [f list-index]
  (let [app-state (swap! *app-state update :read-lists f list-index)]
    (storage/save-read-list! (:read-lists app-state))))


(defn mark-list-read! [list-index]
  (update-lists-read! conj list-index))


(defn mark-list-unread! [list-index]
  (update-lists-read! disj list-index))


(defn clear-lists-read! []
  (swap! *app-state assoc :read-lists #{})
  (storage/save-read-list! #{}))


;; Daily Stats

(defn- calculate-stats! [day]
  (let [stats (bible/get-daily-stats day)]
    (swap! *app-state assoc
           :day day
           :stats stats)))


(defn set-day! [day]
  (storage/set-day! day)
  (calculate-stats! day)
  (clear-lists-read!))


;; Boilerplate

(defn init! []
  (init-read-list!)
  (calculate-stats! (storage/get-day)))


(defn get-app-state! []
  @*app-state)
