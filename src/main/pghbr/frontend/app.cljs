(ns pghbr.frontend.app
  (:require [reagent.core :as r]
            [goog.dom :as gdom]
            ["react-dom/client" :refer [createRoot]]
            [pghbr.frontend.state :as app-state]
            [pghbr.frontend.components :as components]))


(def ^:private root-element-id "root")

(defonce root (createRoot (gdom/getElement root-element-id)))


(defn view []
  []
  [components/page (app-state/get-app-state!)])

(defn render! []
  (.render root (r/as-element [view])))


(defn init! []
  (app-state/init!)
  (render!))


(defn ^:dev/after-load re-render!
  []
  (render!))
