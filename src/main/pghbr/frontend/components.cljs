(ns pghbr.frontend.components
  (:require [pghbr.frontend.state :as events]
            [reagent.core :as r]))


(defonce ^:private *page (r/atom ::reading))

(defn- show-stats! []
  (reset! *page ::stats))

(defn- show-reading! []
  (reset! *page ::reading))


(defn- day-selection [day]
  (let [col-style {:display "flex" :justify-content "center" :padding "0"}]
    [:div.row
     [:div.col-2.col {:style col-style}
      [:button.btn-small {:on-click #(show-stats!)} "?"]]

     [:div.col-2.col {:style col-style}
      [:button.btn-small {:on-click #(events/set-day! (dec day))} "<"]]

     [:div.col-6.col {:style col-style}
      [:input {:type      "number"
               :value     day
               :min       1
               :on-change #(events/set-day! (js/parseInt (.. % -target -value)))
               :style     {:max-height "2.1rem"
                           :max-width "5rem"}}]]

     [:div.col-2.col {:style col-style}
      [:button.btn-small {:on-click #(events/set-day! (inc day))} ">"]]]))


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
                [:span.text-muted {:style {:font-size "0.6rem"}}
                 [:span.text-secondary total-chapters-book] " | "
                 [:span.text-success total-chapters-list]]]
               [:div.col.col-12 {:style col-style}
                [progress-bar-book completion-percentage-chapter ""]
                [progress-bar-list completion-percentage-list ""]]])))
     #_[:pre {:style {:white-space "break-spaces"}} (str reading-list)]]))


;; HELP

(defn- round [decimal-places num]
  (let [multiplier (js/Math.pow 10 decimal-places)]
    (/ (js/Math.round (* num multiplier)) multiplier)))


(defn- help []
  [:div
   [:div "This app is an electronic bookmark for"
    [:strong.text-secondary " Professor Grant Horner's Bible-Reading System"]]
   [:h5 "How to use it?"]
   [:ul
    [:li "Read all 10 chapters as displayed on the page."]
    [:li "Click '>' to load the chapters for the next days reading."]
    [:li "The app saves the selected day on your current device."]
    [:li "You can also type in the day you want to jump to."]
    [:li "The blue progress bar shows chapter progress."]
    [:li "The green progress bar shows list progress."]
    [:li "Once you complete a list, that list will start over."]
    [:li "For more information, the plan can be downloaded " [:a {:href "/download.pdf" :target "_blank"} "here"] "."]]])

(defn- back-to-reading []
  [:button.btn-small {:on-click #(show-reading!)} "Back"])

(defn- credits []
  [:div
   [:h5 "The app was built using"]
   [:ul
    [:li "Clojurescript"]
    [:li [:a {:href "https://shadow-cljs.github.io/"} "Shaddow-cljs"]]
    [:li [:a {:href "https://www.getpapercss.com/"} "Paper Css"]]]])


(defn- about-page []
  [:div
   [help]
   [credits]])


;; STATS

(defn- list-times-read-td [v]
  [:td {:style {:text-align "right"}} (round 2 v) " times"])


(defn- most-read-books-table [{:keys [total-times-books-read]}]
  [:table
   [:tbody
    (->> total-times-books-read
         (sort-by last)
         reverse
         (map-indexed
           (fn [i [_ book times]]
             [:tr {:key (str "t" i)}
              [:td book]
              [list-times-read-td times]])))]])


(defn- stats-table [{:keys [total-times-bible-read total-times-books-read reading-list]}]
  [:table
   [:thead
    [:tr [:th {:col-span 2} "You have read:"]]]
   [:tbody
    [:tr
     [:td "The Bible"]
     [list-times-read-td total-times-bible-read]]]
   [:tbody
      (->> reading-list
         (map-indexed
           (fn [i {:keys [list-number total-times-list-read]}]
             [:tr {:key (str "l" i)}
              [:td "List " list-number]
              [list-times-read-td total-times-list-read]])))]
   [:tbody
    (->> total-times-books-read
         (map-indexed
           (fn [i [_ book times]]
             [:tr {:key i}
              [:td book]
              [list-times-read-td times]])))]])


;; TABS

(defonce ^:private *stats-tab (r/atom ::stats-overall))

(def ^:private tabs [[::stats-overall
                      "Stats"
                      stats-table]
                     [::stats-books
                      "Most Read"
                      most-read-books-table]
                     [::about
                      "About"
                      about-page]])

(defn stats-tabs [stats]
  (let [selected-tab @*stats-tab
        render (some
                 (fn [[k _ f]]
                   (when (= selected-tab k)
                     f))
                 tabs)]
    (prn selected-tab)
    (prn render)
    [:div
     [:div.row.flex-spaces.tabs
      (->> tabs
           (map (fn [[k title _]]
                  [:<> {:key title}
                   [:input {:type     "radio"
                            :name     "tabs"
                            :checked  (= selected-tab k)
                            :id       title
                            :readOnly true
                            :on-click #(reset! *stats-tab k)}]
                   [:label {:for title} title]])))]
     [render stats]]))


(defn- help-page [stats-input]
  [:div
   [back-to-reading]
   [stats-tabs stats-input]])


(defn page [app-state]
  (case @*page
    ::reading
    [:div
     [day-selection (:day app-state)]
     [reading-list (get-in app-state [:stats :reading-list])]]

    ::stats
    [help-page (:stats app-state)]

    [:div "WHOOPS"]))
