(ns sistemi.model.bookcase
  (:require [dgraph :as dg]))

(def price-model
  (dg/make-dgraph

   ;; --------------------------------------------------
   ;; ORDER PARAMETERS
   :length 1.000
   :depth 0.3900
   :height 2.000
   :cutout :rectangle
   :finish :rubio-monocoat

   ;; --------------------------------------------------
   ;; DESIGN CONSTANTS
   :lateral_width 0.09
   :material_thickness 0.018
   :cutout_margin 0.05

   ;; --------------------------------------------------
   ;; BOARD COUNTS
   :num_horizontals (dg/lazy #(let [h (% :height)]
                                (cond (>= h 2.04) 7
                                      (>= h 1.68) 6
                                      (>= h 1.31) 5
                                      (>= h 0.94) 4
                                      (>= h 0.71) 3
                                      :else 2)))
   :num_verticals (dg/lazy #(let [h (% :length)]
                              (cond (>= h 1.96) 4
                                    (>= h 1.21) 3
                                    :else 2)))
   :num_laterals 2

   ;; --------------------------------------------------
   ;; SURFACE AREA
   :sa_horizontal (dg/lazy #(* (% :length) (% :depth) (% :num_horizontals)))
   :sa_vertical (dg/lazy #(* (% :height) (% :depth) (% :num_verticals)))
   :sa_lateral (dg/lazy #(* (% :length) (% :lateral_width) (% :num_laterals)))
   :sa_total (dg/lazy #(+ (% :sa_horizontal) (% :sa_vertical) (% :sa_lateral)))

   ;; --------------------------------------------------
   ;; CUTOUT CUT LENGTH
   :num_cutouts (dg/lazy #(if (= (% :cutout) :none)
                            0
                            (* (- (% :num_horizontals) 1) (% :num_verticals))))
   :cutout_length (dg/lazy #(case (% :cutout)
                              :none 0
                              ;; 2 times the height of the cutout plus the width of the cutout
                              ;; TODO: ? Do we need to handle ovals differently?
                              #_ :rectangle
                              #_ :ovale
                              (+ (* 2 
                                    (- (/ (- (% :height) (* (% :num_laterals) (% :lateral_width)) (* (% :num_horizontals) (% :material_thickness)))
                                          (- (% :num_horizontals) 1))
                                       (* 2 (% :cutout_margin))))
                                 (* 2 (- (% :depth) (* 2 (% :cutout_margin)))))
                              ))

   ;; --------------------------------------------------
   ;; SLOTS
   :slot_perimeter (dg/lazy #(+
                             (* (% :num_horizontals) (+ (% :length) (% :depth)) 2)
                             (* (% :num_verticals) (+ (% :height) (% :depth)) 2)
                             (* (% :num_laterals) (+ (% :lateral_width) (% :length)) 2)))
   :slot_perimeter_scaled (dg/lazy #(/ (% :slot_perimeter) 4))
   :slot_num_slots (dg/lazy #(+
                              (* 2 (% :num_horizontals) (% :num_verticals))
                              (* 2 (% :num_verticals) (% :num_laterals))))
   :slot_num_slots_scaled  (dg/lazy #(* (% :slot_num_slots) (/ 4 60)))
   :slot_cut_length (dg/lazy #(* (% :num_horizontals) (% :num_verticals) 2 (% :depth)))
   :slot_cut_length_scaled (dg/lazy #(/ (% :slot_cut_length) 4))
   :slot_board_length (dg/lazy #(+ (* (% :num_horizontals) (% :length))
                                   (* (% :num_verticals) (% :height))
                                   (* (% :num_laterals) (% :length))))
   :slot_board_length_scaled (dg/lazy #(/ (% :slot_board_length) 6))
   :slot_total (dg/lazy #(apply + (map % [:slot_perimeter_scaled :slot_num_slots_scaled :slot_cut_length_scaled :slot_board_length_scaled])))

   ;; --------------------------------------------------
   ;; RAW COST
   :cost_raw_fabrication (dg/lazy #(+
                                    (* 100/60 (% :slot_total))
                                    (* (/ (* 100 (% :num_cutouts) (% :cutout_length)) 3)
                                       2/60)
                                    ))
   :cost_raw_material (dg/lazy #(* 27.5 (% :sa_total)))
   :cost_raw_man1 (dg/lazy #(+ 10 (* (/ (% :sa_total) 7.5) 0.25 50)))
   :cost_raw_man2 30
   :cost_raw_man3 30
   :cost_raw_total (dg/lazy #(apply + (map % [:cost_raw_fabrication :cost_raw_material :cost_raw_man1 :cost_raw_man2 :cost_raw_man3])))

   ;; --------------------------------------------------
   ;; FINISH COST
   :cost_fin_materials (dg/lazy #(* (/ (% :sa_total) 25) 180))
   :cost_fin_man1 (dg/lazy #(* (% :sa_total) 6))
   :cost_fin_man2 (dg/lazy #(* (+ (% :slot_perimeter) (* (% :num_cutouts) (% :cutout_length))) 0.5))
   :cost_fin_man3 (dg/lazy #(* 20
                               (+ (/ (+ (% :slot_perimeter) (* (% :num_cutouts) (% :cutout_length)))
                                     40)
                                  (/ (% :sa_total) 5))))
   :cost_fin_fixed 20
   :cost_fin_total (dg/lazy #(apply + (map % [:cost_fin_materials :cost_fin_man1 :cost_fin_man2 :cost_fin_man3 :cost_fin_fixed])))

   ;; --------------------------------------------------
   ;; PACKAGING COST
   :cost_packaging (dg/lazy #(+ 10/60
                                (* (% :sa_total) 2/60)))

   ;; --------------------------------------------------
   ;; TOTAL COST
   :factory_total_cost (dg/lazy #(+ (% :cost_raw_total)
                                    (case (% :finish)
                                      :none 0
                                      (% :cost_fin_total))
                                    (% :cost_packaging)))
   :factory_margin 0.05
   :factory_total_bt (dg/lazy #(* (% :factory_total_cost) (+ 1 (% :factory_margin))))
   :vat 0.196 ; france
   :factory_total (dg/lazy #(* (% :factory_total_bt) (+ 1 (% :vat))))
   ))


;; TODO: do report
;; TODO: ? where is the finish price for oiled?
;; TODO: remove frinj
;; TODO: include box cost
;; TODO: include shipping cost
;; TODO: include sistemi margin
;; TODO: include sistemi VAT

;; TODO: perf test
;;  ? what is a resonable upper bound target? < 10ms
;;  - actual ~4ms on thinkpad
#_ (time (dotimes [i 100000] (let [pm (price-model :length 1.2)]
                               (pm :total_after_vat))))

#_ (time (dotimes [i 100000] (price-model :total_base)))
#_ (price-model :total_before_vat)
#_ (price-model :total_after_vat)
#_ (price-model :factory_total)

#_ (price-model :cost_packaging)

#_ (price-model :cost_fin_materials)
#_ (price-model :cost_fin_man1)
#_ (price-model :cost_fin_man2)
#_ (price-model :cost_fin_man3)
#_ (price-model :cost_fin_fixed)
#_ (price-model :cost_fin_total)

#_ (price-model :cost_raw_fabrication)
#_ (price-model :cost_raw_material)
#_ (price-model :cost_raw_man1)
#_ (price-model :cost_raw_total)


#_ (price-model :slot_perimeter)
#_ (price-model :slot_perimeter_scaled)
#_ (price-model :slot_num_slots)
#_ (price-model :slot_num_slots_scaled)
#_ (price-model :slot_cut_length)
#_ (price-model :slot_cut_length_scaled)
#_ (price-model :slot_board_length)
#_ (price-model :slot_board_length_scaled)
#_ (price-model :slot_total)

#_ (price-model :num_cutouts)
#_ (price-model :cutout_length)


#_ (price-model :num_horizontals)
#_ (price-model :num_verticals)

#_ (price-model :sa_horizontal)
#_ (price-model :sa_vertical)
#_ (price-model :sa_lateral)
#_ (price-model :sa_total)

;; ? should there be range guards on the inputs?

;; ? how to write a cell macro?
;;   ? how to tell constants versus cells?
;;   ? how does javelin do it?
;;     ? introduces a cell data type and builds the dep graph by
;;       parsing the function forms?

;; TODO: ? make a model consisten with javelin?
;;       ? or better to just port javelin?

