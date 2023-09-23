(ns pghbr.bible.core
  (:require [pghbr.bible.config :as config]))


(defn get-lists-to-read [day]
  (let [day-index (dec day)]
    (->> config/list-book-chapters
         (map (fn [[list-index list-total-chapters chapters]]
                (let [i                     (mod day-index list-total-chapters)
                      [book-number chapter] (nth chapters i)
                      book-total-chapters   (get config/book-chapters book-number)
                      total-times-list-read (double (/ day-index list-total-chapters))]
                  {:list-index                    list-index
                   :list-number                   (inc list-index)
                   :list-chapter-index            i
                   :book-number                   book-number
                   :book                          (get config/book-names book-number)
                   :chapter                       chapter
                   :total-chapters-book           book-total-chapters
                   :total-chapters-list           list-total-chapters
                   :completion-percentage-chapter (double (* 100 (/ chapter book-total-chapters)))
                   :completion-percentage-list    (double (* 100 (/ (inc i) list-total-chapters)))
                   :total-times-list-read         total-times-list-read
                   :total-times-book-read         (+ (int total-times-list-read)
                                                     (double (/ chapter book-total-chapters)))}))))))


(defn get-daily-stats [day]
  (let [reading-lists           (get-lists-to-read day)
        times-entire-bible-read (->> reading-lists
                                     (map :total-times-list-read)
                                     sort
                                     first)]
    {:reading-list reading-lists
     :total-times-bible-read times-entire-bible-read
     :total-times-books-read
     (->> reading-lists
          (map (fn [{:keys [list-index book-number total-times-book-read]}]
                 (let [list-books   (nth config/reading-lists list-index)
                       after-total  (int total-times-book-read)
                       before-total (inc after-total)]
                   (->> list-books
                        (reduce
                          (fn [r next-book-number]
                            (let [before-current-book? (:before-current-book? r)
                                  current-book?        (= next-book-number book-number)
                                  times-read           (cond
                                                         current-book?
                                                         total-times-book-read

                                                         before-current-book?
                                                         before-total

                                                         :else
                                                         after-total)]
                              (cond-> r
                                current-book?
                                (assoc :before-current-book? false)
                                :always
                                (update :read-counts conj [next-book-number
                                                           (get config/book-names next-book-number)
                                                           times-read]))))
                          {:before-current-book? true
                           :read-counts          []})
                        :read-counts))))
          (reduce into)
          (sort-by first))}))
