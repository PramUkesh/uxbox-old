(ns uxbox.ui.dashboard.icons
  (:require [sablono.core :refer-macros [html]]
            [rum.core :as rum]
            [cuerdas.core :as str]
            [cats.labs.lens :as l]
            [uxbox.state :as st]
            [uxbox.rstore :as rs]
            [uxbox.schema :as sc]
            [uxbox.library :as library]
            [uxbox.shapes :as shapes]
            [uxbox.data.dashboard :as dd]
            [uxbox.util.lens :as ul]
            [uxbox.ui.icons :as i]
            [uxbox.ui.form :as form]
            [uxbox.ui.lightbox :as lightbox]
            [uxbox.ui.dom :as dom]
            [uxbox.ui.mixins :as mx]
            [uxbox.ui.util :as util]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Lenses
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(def ^:static dashboard-state
  (as-> (l/in [:dashboard]) $
    (l/focus-atom $ st/state)))

;; (def ^:static collections-state
;;   (as-> (l/in [:icons-by-id]) $
;;     (l/focus-atom $ st/state)))

;; (def ^:static collection-state
;;   (as-> (ul/dep-in [:icons-by-id] [:dashboard :collection-id]) $
;;     (l/focus-atom $ st/state)))


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Menu
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn menu-render
  []
  (let [pcount 20]
    (html
     [:section#dashboard-bar.dashboard-bar
      [:div.dashboard-info
       [:span.dashboard-projects pcount " projects"]
       [:span "Sort by"]]
      [:div.dashboard-search i/search]])))

(def ^:static menu
  (util/component
   {:render menu-render
    :name "icons-menu"
    :mixins [rum/reactive]}))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Page Title
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn page-title-render
  [own coll]
  (let [dashboard (rum/react dashboard-state)
        own? (:builtin coll false)]
    (html
     [:div.dashboard-title {}
      (if coll
        [:h2 {}
         [:span "Library: "]
         [:span {:content-editable ""
                 :on-key-up (constantly nil)}
          (:name coll)]]
        [:h2 "No library selected"])
      (if (and (not own?) coll)
        [:div.edition {}
         [:span {:on-click (constantly nil)}
          i/trash]])])))

(def ^:static page-title
  (util/component
   {:render page-title-render
    :name "page-title"
    :mixins [mx/static rum/reactive]}))


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Nav
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn nav-render
  [own]
  (let [dashboard (rum/react dashboard-state)
        ;; colors (rum/react collections-state)
        collid (:collection-id dashboard)
        own? (= (:collection-type dashboard) :own)
        builtin? (= (:collection-type dashboard) :builtin)
        collections (if own?
                      [] #_(sort-by :id (vals colors))
                      library/+icon-collections+)]
    (html
     [:div.library-bar
      [:div.library-bar-inside
       [:ul.library-tabs
        [:li {:class-name (when builtin? "current")
              :on-click #(rs/emit! (dd/set-collection-type :builtin))}
         "STANDARD"]
        [:li {:class-name (when own? "current")
              :on-click #(rs/emit! (dd/set-collection-type :own))}
         "YOUR LIBRARIES"]]
       [:ul.library-elements
        (when own?
          [:li
           [:a.btn-primary
            {:on-click (constantly nil)}
            "+ New library"]])
        (for [props collections]
          [:li {:key (str (:id props))
                :on-click #(rs/emit! (dd/set-collection (:id props)))
                :class-name (when (= (:id props) collid) "current")}
           [:span.element-title (:name props)]
           [:span.element-subtitle
            (str (count (:icons props)) " elements")]])]]])))

(def ^:static nav
  (util/component
   {:render nav-render
    :name "nav"
    :mixins [rum/reactive]}))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Grid
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn grid-render
  [own]
  (let [dashboard (rum/react dashboard-state)
        coll-type (:collection-type dashboard)
        coll-id (:collection-id dashboard)
        own? (= coll-type :own)
        coll (get library/+icon-collections-by-id+ coll-id)]
        ;; coll (case coll-type
        ;;        :builtin (get library/+color-collections-by-id+ coll-id)
        ;;        :own (rum/react collection-state))]
        ;; edit-cb #(lightbox/open! :icon-form {:coll coll :icon %})
        ;; remove-cb #(rs/emit! (dd/remove-icon {:id (:id coll) :icon %}))]
    (when coll
      (html
       [:section.dashboard-grid.library
        (page-title coll)
        [:div.dashboard-grid-content
         (for [icon (:icons coll)
               :let [_ (println icon)]]
           [:div.grid-item.small-item.project-th {:key (str (:id icon))}
            [:span.grid-item-image #_i/toggle (shapes/render icon)]
            [:h3 "Custom icon"]
            #_[:div.project-th-actions
             [:div.project-th-icon.edit i/pencil]
             [:div.project-th-icon.delete i/trash]]])]]))))

(def grid
  (util/component
   {:render grid-render
    :name "grid"
    :mixins [mx/static rum/reactive]}))


;; (defn grid-render
;;   [own]
;;   (html
;;    [:div.dashboard-grid-content
;;     [:div.grid-item.small-item.add-project
;;      {on-click #(lightbox/open! :new-icon)}
;;      [:span "+ New icon"]]
;;     [:div.grid-item.small-item.project-th
;;      [:span.grid-item-image i/logo-icon]
;;      [:h3 "Custom icon"]
;;      [:div.project-th-actions
;;       [:div.project-th-icon.edit i/pencil]
;;       [:div.project-th-icon.delete i/trash]]]
;;     [:div.grid-item.small-item.project-th
;;      [:span.grid-item-image i/pencil]
;;      [:h3 "Custom icon"]
;;      [:div.project-th-actions
;;       [:div.project-th-icon.edit i/pencil]
;;       [:div.project-th-icon.delete i/trash]]]
;;     [:div.grid-item.small-item.project-th
;;      [:span.grid-item-image i/trash]
;;      [:h3 "Custom icon"]
;;      [:div.project-th-actions
;;       [:div.project-th-icon.edit i/pencil]
;;       [:div.project-th-icon.delete i/trash]]]
;;     [:div.grid-item.small-item.project-th
;;      [:span.grid-item-image i/search]
;;      [:h3 "Custom icon"]
;;      [:div.project-th-actions
;;       [:div.project-th-icon.edit i/pencil]
;;       [:div.project-th-icon.delete i/trash]]]
;;     [:div.grid-item.small-item.project-th
;;      [:span.grid-item-image i/image]
;;      [:h3 "Custom icon"]
;;      [:div.project-th-actions
;;       [:div.project-th-icon.edit i/pencil]
;;       [:div.project-th-icon.delete i/trash]]]
;;     [:div.grid-item.small-item.project-th
;;      [:span.grid-item-image i/toggle]
;;      [:h3 "Custom icon"]
;;      [:div.project-th-actions
;;       [:div.project-th-icon.edit i/pencil]
;;       [:div.project-th-icon.delete i/trash]]]
;;     [:div.grid-item.small-item.project-th
;;      [:span.grid-item-image i/chat]
;;      [:h3 "Custom icon"]
;;      [:div.project-th-actions
;;       [:div.project-th-icon.edit i/pencil]
;;       [:div.project-th-icon.delete i/trash]]]
;;     [:div.grid-item.small-item.project-th
;;      [:span.grid-item-image i/close]
;;      [:h3 "Custom icon"]
;;      [:div.project-th-actions
;;       [:div.project-th-icon.edit i/pencil]
;;       [:div.project-th-icon.delete i/trash]]]
;;     [:div.grid-item.small-item.project-th
;;      [:span.grid-item-image i/page]
;;      [:h3 "Custom icon"]
;;      [:div.project-th-actions
;;       [:div.project-th-icon.edit i/pencil]
;;       [:div.project-th-icon.delete i/trash]]]
;;     [:div.grid-item.small-item.project-th
;;      [:span.grid-item-image i/folder]
;;      [:h3 "Custom icon"]
;;      [:div.project-th-actions
;;       [:div.project-th-icon.edit i/pencil]
;;       [:div.project-th-icon.delete i/trash]]]
;;     [:div.grid-item.small-item.project-th
;;      [:span.grid-item-image i/infocard]
;;      [:h3 "Custom icon"]
;;      [:div.project-th-actions
;;       [:div.project-th-icon.edit i/pencil]
;;       [:div.project-th-icon.delete i/trash]]]
;;     [:div.grid-item.small-item.project-th
;;      [:span.grid-item-image i/fill]
;;      [:h3 "Custom icon"]
;;      [:div.project-th-actions
;;       [:div.project-th-icon.edit i/pencil]
;;       [:div.project-th-icon.delete i/trash]]]
;;     [:div.grid-item.small-item.project-th
;;      [:span.grid-item-image i/stroke]
;;      [:h3 "Custom icon"]
;;      [:div.project-th-actions
;;       [:div.project-th-icon.edit i/pencil]
;;       [:div.project-th-icon.delete i/trash]]]
;;     [:div.grid-item.small-item.project-th
;;      [:span.grid-item-image i/action]
;;      [:h3 "Custom icon"]
;;      [:div.project-th-actions
;;       [:div.project-th-icon.edit i/pencil]
;;       [:div.project-th-icon.delete i/trash]]]
;;     [:div.grid-item.small-item.project-th
;;      [:span.grid-item-image i/undo]
;;      [:h3 "Custom icon"]
;;      [:div.project-th-actions
;;       [:div.project-th-icon.edit i/pencil]
;;       [:div.project-th-icon.delete i/trash]]]
;;     [:div.grid-item.small-item.project-th
;;      [:span.grid-item-image i/redo]
;;      [:h3 "Custom icon"]
;;      [:div.project-th-actions
;;       [:div.project-th-icon.edit i/pencil]
;;       [:div.project-th-icon.delete i/trash]]]
;;     [:div.grid-item.small-item.project-th
;;      [:span.grid-item-image i/export]
;;      [:h3 "Custom icon"]
;;      [:div.project-th-actions
;;       [:div.project-th-icon.edit i/pencil]
;;       [:div.project-th-icon.delete i/trash]]]
;;     [:div.grid-item.small-item.project-th
;;      [:span.grid-item-image i/exit]
;;      [:h3 "Custom icon"]
;;      [:div.project-th-actions
;;       [:div.project-th-icon.edit i/pencil]
;;       [:div.project-th-icon.delete i/trash]]]
;;     [:div.grid-item.small-item.project-th
;;      [:span.grid-item-image i/user]
;;      [:h3 "Custom icon"]
;;      [:div.project-th-actions
;;       [:div.project-th-icon.edit i/pencil]
;;       [:div.project-th-icon.delete i/trash]]]]))

;; (def grid
;;   (util/component
;;    {:render grid-render
;;     :name "grid"
;;     :mixins [mx/static]}))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Lightbox
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn- new-icon-lightbox-render
  [own]
  (html
   [:div.lightbox-body
    [:h3 "New icon"]
    [:div.row-flex
     [:div.lightbox-big-btn
      [:span.big-svg i/shapes]
      [:span.text "Go to workspace"]
      ]
     [:div.lightbox-big-btn
      [:span.big-svg.upload i/exit]
      [:span.text "Upload file"]
      ]
     ]
    [:a.close {:href "#"
               :on-click #(do (dom/prevent-default %)
                              (lightbox/close!))}
     i/close]]))

(def new-icon-lightbox
  (util/component
   {:render new-icon-lightbox-render
    :name "new-icon-lightbox"}))
