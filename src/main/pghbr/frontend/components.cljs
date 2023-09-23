(ns pghbr.frontend.components
  (:require [pghbr.frontend.state :as events]))


(defn- day-selection [day]
  (let [col-style {:display "flex" :justify-content "center" :padding "0"}]
    [:div.row
     [:div.col-2.col {:style col-style}
      [:button.btn-small
       {:on-click #(events/set-day! (dec day))
        }
       "<"]]
     [:div.col-8.col {:style col-style}
      [:input {:type      "number"
               :value     day
               :min       1
               :on-change #(events/set-day! (js/parseInt (.. % -target -value)))
               :style     {:max-height "2.1rem"}}]]
     [:div.col-2.col {:style col-style}
      [:button.btn-small {:on-click #(events/set-day! (inc day))}
       ">"]]]))


(defn- progress-width [progress]
  (str "w-" (int progress)))



(defn- progress-bar-book [progress]
  [:div.progress {:style (merge {:height "0.3rem" :border-width "0 0 0 0 "})}
   [:div.bar.secondary
    {:class (progress-width progress)
     :style {:border-right "none"}}]])


(defn- progress-bar-list [progress]
  [:div.progress {:style (merge {:height "0.3rem" :border-width "1px 0 0 0 "})}
   [:div.bar.success
    {:class (progress-width progress)
     :style {:border-right "none"}}]])


(defn- reading-list [reading-list]
  [:div
   (->> reading-list
        (map-indexed
          (fn [i {:keys [book chapter list-number completion-percentage-chapter completion-percentage-list]}]
            [:div.margin-bottom {:key i}
             [:span list-number ". " book " " chapter]
             [progress-bar-book completion-percentage-chapter ""]
             [progress-bar-list completion-percentage-list ""]])))
   [:pre {:style {:white-space "break-spaces"}} (str reading-list)]])


(defn- stats [{:keys [total-times-bible-read total-times-books-read]}]
  [:div
   "Bible Read " total-times-bible-read " times"
   [:br]
   (->> total-times-books-read
        (map-indexed
          (fn [i [_ book times]]
            [:div book " " times])))]
  )


(defn page [app-state]
  [:div
   [day-selection (:day app-state)]
   [reading-list (get-in app-state [:stats :reading-list])]
   #_[stats (:stats app-state)]])
