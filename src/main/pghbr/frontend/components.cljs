(ns pghbr.frontend.components
  (:require [pghbr.frontend.state :as events]
            [reagent.core :as r]))


(defonce *page (r/atom :reading))

(defn show-stats! []
  (reset! *page :stats))

(defn show-reading! []
  (reset! *page :reading))


(defn- day-selection [day]
  (let [col-style {:display "flex" :justify-content "center" :padding "0"}]
    [:div.row
     [:div.col-2.col {:style col-style}
      [:button.btn-small
       {:on-click #(show-stats!)
        }
       "?"]]
     [:div.col-2.col {:style col-style}
      [:button.btn-small
       {:on-click #(events/set-day! (dec day))
        }
       "<"]]
     [:div.col-6.col {:style col-style}
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
  (let [col-style {:padding-top "0" :padding-bottom "0"}]
    [:div
     (->> reading-list
          (map-indexed
            (fn [i {:keys [book chapter list-number completion-percentage-chapter completion-percentage-list total-chapters-list total-chapters-book]}]
              [:div.row {:key i}
               [:div.col.col-9 {:style col-style}
                [:span list-number ". " book " " chapter]]
               [:div.col.col-3 {:style (assoc col-style :text-align "right")}
                [:span.text-muted {:style {:font-size "0.6rem"}} total-chapters-book " | " total-chapters-list]]
               [:div.col.col-12 {:style col-style}
                [progress-bar-book completion-percentage-chapter ""]
                [progress-bar-list completion-percentage-list ""]]])))
     #_[:pre {:style {:white-space "break-spaces"}} (str reading-list)]]))


(defn round [decimal-places num]
  (let [multiplier (js/Math.pow 10 decimal-places)]
    (/ (js/Math.round (* num multiplier)) multiplier)))

(defn- stats [{:keys [total-times-bible-read total-times-books-read]}]
  [:div
   [:button.btn-small {:on-click #(show-reading!)} "Back"]
   [:p "Professor Grant Horner's Bible-Reading System (" [:small [:a {:href "/download.pdf" :target "_blank"} "click to read more about it"]] ")"]
   [:table
    [:thead
     [:tr [:th {:col-span 2} "You have read:"]]]
    [:tbody
     [:tr [:td "The Bible"] [:td {:style {:text-align "right"}} (round 2 total-times-bible-read) " times"]]]
    [:tbody
     (->> total-times-books-read
          (map-indexed
            (fn [i [_ book times]]
              [:tr {:key i}
               [:td book]
               [:td {:style {:text-align "right"}} (round 2 times) " times"]])))]]

   [:div.border.border-2.padding.margin
    [:span "Built using"]
    [:ul
     [:li "Clojurescript"]
     [:li [:a {:href "https://shadow-cljs.github.io/"} "Shaddow-cljs"]]
     [:li [:a {:href "https://www.getpapercss.com/"} "Paper Css"]]]]]
  )



(defn page [app-state]
  (case @*page
    :reading
    [:div
     [day-selection (:day app-state)]
     [reading-list (get-in app-state [:stats :reading-list])]]

    :stats
    [stats (:stats app-state)]

    [:div "WHOOPS"]))
