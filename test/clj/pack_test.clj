(ns pack-test
  (:require pack)
  (:use clojure.test
        frinj.ops))

(deftest shelf-parts
  (let [shelf {:type :shelf
               :quantity 2
               :width 120
               :depth 30}
        parts (pack/parts shelf)]
    (is (= (count parts) (:quantity shelf)))
    (is (fj= (fj 4.959 :kg) (-> parts first :mass)))))

(deftest bookcase-parts
  (let [bookcase {:type :bookcase
                  :quantity 2
                  :depth 30
                  :width 120
                  :height 200}
        parts (pack/parts bookcase)]
    (is (= 20 (count parts)))
    (is (= 4 (count (filter #(= :lateral (:type %)) parts))))
    (is (= 4 (count (filter #(= :vertical (:type %)) parts))))
    (is (= 12 (count (filter #(= :horizontal (:type %)) parts))))))

(deftest part-max-dimension
  (is (= (fj 3 :m)
         (pack/part-max-dimension {:x (fj 2 :m) :y (fj 3 :m) :z (fj 1 :cm)}))))

(deftest make-boxes
  (let [bookcase {:type :bookcase
                  :quantity 1
                  :depth 30
                  :width 120
                  :height 120}
        parts (pack/parts bookcase)
        boxes (pack/make-boxes parts)]
    (is (= 2 (count boxes)))
    (is (= 8 (apply + (map count boxes))))))
