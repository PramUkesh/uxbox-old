(ns uxbox.shapes.core
  (:require [uxbox.pubsub :as pubsub]
            [uxbox.geometry :as geo]
            [reagent.core :refer [atom]]))

;;=============================
;; SHAPE PROTOCOL DEFINITION
;;=============================
(defprotocol Shape
  (intersect [shape px py]
    "Retrieves true when the point (px, py) is inside the shape")

  (toolbar-coords [shape]
    "Retrieves a pair of coordinates (px, py) where the toolbar has to be displayed for this shape")

  (shape->svg [shape]
    "Returns the markup for the SVG of static shape")

  (shape->selected-svg [shape]
    "Returns the markup for the SVG of the elements selecting the shape")

  (shape->drawing-svg [shape]
    "Returns the markup for the SVG of the shape while is bein drawed"))

;;=============================
;; LINES
;;=============================
(defrecord Line [x1 y1 x2 y2 stroke stroke-width stroke-opacity]
  Shape

  (intersect
    [{:keys [x1 y1 x2 y2]} px py]
    (let [distance (geo/distance-line-point x1 y1 x2 y2 px py)]
      (<= distance 15)))

  (toolbar-coords
    [{:keys [x1 y1 x2 y2]}]
    (let [max-x (if (> x1 x2) x1 x2)
          min-y (if (< y1 y2) y1 y2)
          vx (+ max-x 50)
          vy min-y]
      (geo/viewportcord->clientcoord vx vy)))

  (shape->svg
    [{:keys [x1 y1 x2 y2 stroke stroke-width stroke-opacity]}]
    [:line {:x1 x1 :y1 y1 :x2 x2 :y2 y2 :stroke stroke :strokeWidth stroke-width :stroke-opacity stroke-opacity}])

  (shape->selected-svg
    [{:keys [x1 y1 x2 y2 stroke stroke-width stroke-opacity]}]
    [:line {:x1 x1 :y1 y1 :x2 x2 :y2 y2 :stroke stroke :strokeWidth (+ stroke-width 2) :stroke-opacity stroke-opacity}])

  (shape->drawing-svg
    [{:keys [x1 y1 x2 y2]}]
    (let [coordinates1 (atom [x1 y1])
          coordinates2 (atom [x2 y2])
          viewport-move (fn [state coords]
                          (reset! coordinates2 coords))]
      (pubsub/register-event :viewport-mouse-move viewport-move)
      (fn []
        (let [[mouseX mouseY] @coordinates2]
          [:line {:x1 x1 :y1 y1 :x2 mouseX :y2 mouseY
                  :style #js {:fill "transparent" :stroke "gray" :stroke-width 2 :strokeDasharray "5,5"}}]))))
  )

(defn new-line
  "Retrieves a line with the default parameters"
  [x1 y1 x2 y2]
  (Line. x1 y1 x2 y2 "gray" 4 1))

;;=============================
;; RECTANGLES
;;=============================
(defrecord Rectangle [x y width height rx ry fill fill-opacity stroke stroke-width stroke-opacity]
  Shape

  (intersect [{:keys [x y width height]} px py]
    (and (>= px x)
         (<= px (+ x width))
         (>= py y)
         (<= py (+ y height))))

  (toolbar-coords [{:keys [x y width height]}]
    (let [vx (+ x width 50)
          vy y]
      (geo/viewportcord->clientcoord vx vy)))

  (shape->svg [{:keys [x y width height rx ry fill fill-opacity stroke stroke-width stroke-opacity]}]
    [:rect {:x x
     :y y
     :width width
     :height height
     :fill fill
     :fillOpacity fill-opacity
     :rx rx
     :ry ry
     :stroke stroke
     :strokeWidth stroke-width
     :stroke-opacity stroke-opacity}])

  (shape->selected-svg [{:keys [x y width height rx ry fill fill-opacity stroke stroke-width stroke-opacity]}]
    [:rect {:x x
            :y y
            :width width
            :height height
            :fill fill
            :fillOpacity fill-opacity
            :rx rx
            :ry ry
            :stroke stroke
            :strokeWidth (+ stroke-width 2)
            :stroke-opacity stroke-opacity}])

  (shape->drawing-svg [{:keys [x y]}]
    (let [coordinates (atom [[x y]])
          viewport-move (fn [state coord]
                          (reset! coordinates coord))]
      (pubsub/register-event :viewport-mouse-move viewport-move)
      (fn []
        (let [[mouseX mouseY] @coordinates
              [rect-x rect-y rect-width rect-height] (geo/coords->rect x y mouseX mouseY)]
          (if (and (> rect-width 0) (> rect-height 0))
            [:rect {:x rect-x :y rect-y :width rect-width :height rect-height
                    :style #js {:fill "transparent" :stroke "gray" :strokeDasharray "5,5"}}])))))
  )

(defn new-rectangle
  "Retrieves a line with the default parameters"
  [x y width height]
  (Rectangle. x y width height 0 0 "#cacaca" 1 "gray" 5 1))


;;=============================
;; PATH
;;=============================
(defrecord Path [path icowidth icoheight x y width height fill]
  Shape

  (intersect [{:keys [x y width height]} px py]
    (and (>= px x)
         (<= px (+ x width))
         (>= py y)
         (<= py (+ y height))))

  (toolbar-coords [{:keys [x y width height]}]
    (let [vx (+ x width 50)
          vy y]
      (geo/viewportcord->clientcoord vx vy)))

  (shape->svg [{:keys [path icowidth icoheight x y width height fill]}]
    [:svg {:viewBox (str "0 0 " icowidth " " icoheight) :width width :height height :x x :y y
           :preserveAspectRatio "none"}
     [:path {:d path}]])

  (shape->selected-svg [{:keys [x y width height]}]
    [:rect {:x x
            :y y
            :width width
            :height height
            :fill "transparent"
            :stroke "#4af7c3"
            :strokeWidth 2
            :strokeDasharray "5,5"
            :fill-opacity "0.5"}])

  (shape->drawing-svg [{:keys [x y]}]
    (let [coordinates (atom [[x y]])
          viewport-move (fn [state coord]
                          (reset! coordinates coord))]
      (pubsub/register-event :viewport-mouse-move viewport-move)
      (fn []
        (let [[mouseX mouseY] @coordinates
              [rect-x rect-y rect-width rect-height] (geo/coords->rect x y mouseX mouseY)]
          (if (and (> rect-width 0) (> rect-height 0))
            [:rect {:x rect-x :y rect-y :width rect-width :height rect-height
                    :style #js {:fill "transparent" :stroke "gray" :strokeDasharray "5,5"}}])))))
  )

(defn new-path
  "Retrieves a path with the default parameters"
  [x y width height path icowidth icoheight]
  (Path. path icowidth icoheight x y width height "black"))
