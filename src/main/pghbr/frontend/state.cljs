(ns pghbr.frontend.state
  (:require [pghbr.bible.core :as bible]
            [pghbr.frontend.storage :as storage]
            [reagent.core :as r]))


(defonce *app-state (r/atom {}))



(defn- calculate-stats! [day]
  (let [stats (bible/get-daily-stats day)]
    (reset! *app-state {:day   day
                        :stats stats})))


(defn init! []
  (calculate-stats! (storage/get-day)))


(defn get-app-state! []
  @*app-state)


(defn set-day! [day]
  (storage/set-day! day)
  (calculate-stats! day))
