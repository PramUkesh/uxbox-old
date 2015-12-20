(ns uxbox.ui.library-bar
  (:require [sablono.core :as html :refer-macros [html]]
            [rum.core :as rum]
            [uxbox.ui.util :as ui]
            [uxbox.ui.mixins :as mx]
            [uxbox.ui.icons :as i]))

(defn library-bar-render
  [own]
  (html
   [:div.library-bar
    [:div.library-bar-inside
     [:ul.library-tabs
      [:li "STANDARD"]
      [:li.current "YOUR LIBRARIES"]
      ]
     [:ul.library-elements
      [:li
       [:a.btn-primary {:href "#"} "+ New library"]
       ]
      [:li
       [:span.element-title "Library 1"]
       [:span.element-subtitle "21 elements"]
       ]
      [:li
       [:span.element-title "Library 2"]
       [:span.element-subtitle "21 elements"]
       ]
      [:li
       [:span.element-title "Library 3"]
       [:span.element-subtitle "21 elements"]
       ]
      [:li
       [:span.element-title "Library 4"]
       [:span.element-subtitle "21 elements"]
       ]
      [:li
       [:span.element-title "Library 5"]
       [:span.element-subtitle "21 elements"]
       ]
      [:li
       [:span.element-title "Library 6"]
       [:span.element-subtitle "21 elements"]
       ]
      [:li
       [:span.element-title "Library 7"]
       [:span.element-subtitle "21 elements"]
       ]
      [:li
       [:span.element-title "Library 8"]
       [:span.element-subtitle "21 elements"]
       ]
      [:li
       [:span.element-title "Library 9"]
       [:span.element-subtitle "21 elements"]
       ]
      [:li
       [:span.element-title "Library 10"]
       [:span.element-subtitle "21 elements"]
       ]
      [:li
       [:span.element-title "Library 11"]
       [:span.element-subtitle "21 elements"]
       ]
      [:li
       [:span.element-title "Library 12"]
       [:span.element-subtitle "21 elements"]
       ]
      [:li
       [:span.element-title "Library 13"]
       [:span.element-subtitle "21 elements"]
       ]
      [:li
       [:span.element-title "Library 14"]
       [:span.element-subtitle "21 elements"]
       ]
      ]
     ]
    ]))

(def ^:static library-bar
  (ui/component
   {:render library-bar-render
    :name "library-bar"
    :mixins [mx/static]}))
