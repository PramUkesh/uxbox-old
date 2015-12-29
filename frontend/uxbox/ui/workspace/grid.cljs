(ns uxbox.ui.workspace.grid
  (:require [sablono.core :as html :refer-macros [html]]
            [rum.core :as rum]
            [uxbox.ui.mixins :as mx]
            [uxbox.ui.util :as util]
            [uxbox.ui.workspace.base :as wb]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Grid
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(def ^:static grid-color "#cccccc")

(defn grid-render
  [own zoom]
  (letfn [(vertical-line [page position value]
            (let [ticks-mod (/ 100 zoom)
                  step-size (/ 10 zoom)]
              (if (< (mod value ticks-mod) step-size)
                (html [:line {:key position
                              :y1 0
                              :y2 (:height page)
                              :x1 position
                              :x2 position
                              :stroke grid-color
                              :stroke-width (/ 0.5 zoom)
                              :opacity 0.75}])
                (html [:line {:key position
                              :y1 0
                              :y2 (:height page)
                              :x1 position
                              :x2 position
                              :stroke grid-color
                              :stroke-width (/ 0.5 zoom)
                              :opacity 0.25}]))))
          (horizontal-line [page position value]
            (let [ticks-mod (/ 100 zoom)
                  step-size (/ 10 zoom)]
              (if (< (mod value ticks-mod) step-size)
                (html [:line {:key position
                              :y1 position
                              :y2 position
                              :x1 0
                              :x2 (:width page)
                              :stroke grid-color
                              :stroke-width (/ 0.5 zoom)
                              :opacity 0.75}])
                (html [:line {:key position
                              :y1 position
                              :y2 position
                              :x1 0
                              :x2 (:width page)
                              :stroke grid-color
                              :stroke-width (/ 0.5 zoom)
                              :opacity 0.25}]))))]
    (let [padding (* 0 zoom)
          ticks-mod (/ 100 zoom)
          step-size (/ 10 zoom)
          workspace (rum/react wb/workspace-state)
          page (rum/react wb/page-state)
          enabled? (:grid-enabled workspace false)
          vertical-ticks (range (- 0 wb/document-start-y)
                                (- (:width page) wb/document-start-y)
                                step-size)
          horizontal-ticks (range (- 0 wb/document-start-x)
                                  (- (:height page) wb/document-start-x)
                                  step-size)]
      (html
       [:g.grid
        {:style {:display (if enabled? "block" "none")}}
        (for [tick vertical-ticks]
          (let [position (+ tick wb/document-start-x)
                line (vertical-line page position tick)]
            (rum/with-key line (str "tick-" tick))))
        (for [tick horizontal-ticks]
          (let [position (+ tick wb/document-start-y)
                line (horizontal-line page position tick)]
            (rum/with-key line (str "tick-" tick))))]))))

(def grid
  (util/component
   {:render grid-render
    :name "grid"
    :mixins [mx/static rum/reactive]}))

