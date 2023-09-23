(ns pghbr.bible.config)

(def books
  [[1 "Genesis" "Gen" 50]
   [2 "Exodus" "Exod" 40]
   [3 "Leviticus" "Lev" 27]
   [4 "Numbers" "Num" 36]
   [5 "Deuteronomy" "Deut" 34]
   [6 "Joshua" "Josh" 24]
   [7 "Judges" "Judg" 21]
   [8 "Ruth" "Ruth" 4]
   [9 "1 Samuel" "1 Sam" 31]
   [10 "2 Samuel" "2 Sam" 24]
   [11 "1 Kings" "1 Kgs" 22]
   [12 "2 Kings" "2 Kgs" 25]
   [13 "1 Chronicles" "1 Chr" 29]
   [14 "2 Chronicles" "2 Chr" 36]
   [15 "Ezra" "Ezra" 10]
   [16 "Nehemiah" "Neh" 13]
   [17 "Esther" "Esth" 10]
   [18 "Job" "Job" 42]
   [19 "Psalms" "Ps" 150]
   [20 "Proverbs" "Prov" 31]
   [21 "Ecclesiastes" "Eccl" 12]
   [22 "Song of Songs" "Song" 8]
   [23 "Isaiah" "Isa" 66]
   [24 "Jeremiah" "Jer" 52]
   [25 "Lamentations" "Lam" 5]
   [26 "Ezekiel" "Ezek" 48]
   [27 "Daniel" "Dan" 12]
   [28 "Hosea" "Hos" 14]
   [29 "Joel" "Joel" 3]
   [30 "Amos" "Amos" 9]
   [31 "Obadiah" "Obad" 1]
   [32 "Jonah" "Jonah" 4]
   [33 "Micah" "Mic" 7]
   [34 "Nahum" "Nah" 3]
   [35 "Habakkuk" "Hab" 3]
   [36 "Zephaniah" "Zeph" 3]
   [37 "Haggai" "Hag" 2]
   [38 "Zechariah" "Zech" 14]
   [39 "Malachi" "Mal" 4]
   [40 "Matthew" "Matt" 28]
   [41 "Mark" "Mark" 16]
   [42 "Luke" "Luke" 24]
   [43 "John" "John" 21]
   [44 "Acts of the Apostles" "Acts" 28]
   [45 "Romans" "Rom" 16]
   [46 "1 Corinthians" "1 Cor" 16]
   [47 "2 Corinthians" "2 Cor" 13]
   [48 "Galatians" "Gal" 6]
   [49 "Ephesians" "Eph" 6]
   [50 "Philippians" "Phil" 4]
   [51 "Colossians" "Col" 4]
   [52 "1 Thessalonians" "1 Thess" 5]
   [53 "2 Thessalonians" "2 Thess" 3]
   [54 "1 Timothy" "1 Tim" 6]
   [55 "2 Timothy" "2 Tim" 4]
   [56 "Titus" "Titus" 3]
   [57 "Philemon" "Phlm" 1]
   [58 "Hebrews" "Heb" 13]
   [59 "James" "Jas" 5]
   [60 "1 Peter" "1 Pet" 5]
   [61 "2 Peter" "2 Pet" 3]
   [62 "1 John" "1 John" 5]
   [63 "2 John" "2 John" 1]
   [64 "3 John" "3 John" 1]
   [65 "Jude" "Jude" 1]
   [66 "Revelation (to John)" "Rev" 22]])


(def reading-lists
  [[40 41 42 43]
   [1 2 3 4 5]
   [45 46 47 48 49 50 51 58]
   [52 53 54 55 56 57 59 60 61 62 63 64 65 66]
   [18 21 22]
   [19]
   [20]
   [6 7 8 9 10 11 12 13 14 15 16 17]
   [23 24 25 26 27 28 29 30 31 32 33 34 35 36 37 38 39]
   [44]])


;; PROJECTIONS

(def book-names
  (->> books
       (map (juxt first second))
       (into {})))

(def book-chapters
  (->> books
       (map (juxt first last))
       (into {})))


(def list-book-chapters
  (->> reading-lists
       (map-indexed (fn [i lst]
                      (let [chapters (->> lst
                                          (map (fn [book]
                                                 (let [chapters (get book-chapters book)]
                                                   (->> (range 1 (inc chapters))
                                                        (map (fn [c]
                                                               [book c]))))))
                                          (reduce into []))]
                        [i (count chapters) chapters])))))


;; CONFIG TESTS
(comment

  (def expected-list-lengths
    [89
     187
     78
     65
     62
     150
     31
     249
     250
     28])

  ;; TOTAL CHAPTERS AS EXPECTED
  (let [expected (reduce + expected-list-lengths)
        actual   (->> books
                      (map last)
                      (reduce +))]
    (= expected actual))

  ;; LIST TOTALS AS EXPECTED
  (let [actual (->> reading-lists
                    (map (fn [lst]
                           (->> lst
                                (map (fn [book]
                                       (last (nth books (dec book)))))
                                (reduce +)))))]
    (= expected-list-lengths actual))

  ;; LIST BOOK CHAPTERS AS EXPECTED

  (let [actual 
        (map second list-book-chapters)]
    (= expected-list-lengths actual))

  ,)

