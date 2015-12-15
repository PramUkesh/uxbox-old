(ns uxbox.ui.dashboard
  (:require [sablono.core :as html :refer-macros [html]]
            [rum.core :as rum]
            [cats.labs.lens :as l]
            [cuerdas.core :as str]
            [uxbox.util :as util]
            [uxbox.router :as r]
            [uxbox.rstore :as rs]
            [uxbox.state :as s]
            [uxbox.data.projects :as dp]
            [uxbox.ui.icons.dashboard :as icons]
            [uxbox.ui.icons :as i]
            [uxbox.ui.dom :as dom]
            [uxbox.ui.header :as ui.h]
            [uxbox.ui.lightbox :as lightbox]
            [uxbox.time :refer [ago]]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Helpers & Constants
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;; FIXME rename
(def +ordering-options+ {:name "name"
                         :last-updated "date updated"
                         :created "date created"})

(def +layouts+ {:mobile {:name "Mobile"
                         :id "mobile"
                         :width 320
                         :height 480}
                :tablet {:name "Tablet"
                         :id "tablet"
                         :width 1024
                         :height 768}
                :notebook {:name "Notebook"
                           :id "notebook"
                           :width 1366
                           :height 768}
                :desktop {:name "Desktop"
                          :id "desktop"
                          :width 1920
                          :height 1080}})

(def ^:static ^:private
  +project-defaults+ {:name ""
                      :width 1920
                      :height 1080
                      :layout :desktop})

;; (def name->order (into {} (for [[k v] project-orderings] [v k])))

;; Views

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Lightbox
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn layout-input
  [local layout-id]
  (let [layout (get-in +layouts+ [layout-id])
        id (:id layout)
        name (:name layout)
        width (:width layout)
        height (:height layout)]
    (html
     [:div
      [:input
       {:type "radio"
        :key id
        :id id
        :name "project-layout"
        :value name
        :checked (= layout-id (:layout @local))
        :on-change #(swap! local merge {:layout layout-id :width width :height height})}]
      [:label {:value (:name @local) :for id} name]])))

(defn- layout-selector
  [local]
  (html
   [:div.input-radio.radio-primary
    (layout-input local :mobile)
    (layout-input local :tablet)
    (layout-input local :notebook)
    (layout-input local :desktop)]))

(defn- new-project-lightbox-render
  [own]
  (let [local (:rum/local own)
        name (:name @local)
        width (:width @local)
        height (:height @local)]
   (html
    [:div.lightbox-body
     [:h3 "New project"]
     [:form {:on-submit (constantly nil)}
      [:input#project-name.input-text
        {:placeholder "New project name"
         :type "text"
         :value name
         :auto-focus true
         :on-change #(swap! local assoc :name (.-value (.-target %)))}]
      [:div.project-size
       [:input#project-witdh.input-text
        {:placeholder "Width"
         :type "number"
         :min 0 ;;TODO check this value
         :max 666666 ;;TODO check this value
         :value width
         :on-change #(swap! local assoc :width (.-value (.-target %)))}]
       [:a.toggle-layout
        {:href "#"
         :on-click #(swap! local assoc :width width :height height)}
        i/toggle]
       [:input#project-height.input-text
        {:placeholder "Height"
         :type "number"
         :min 0 ;;TODO check this value
         :max 666666 ;;TODO check this value
         :value height
         :on-change #(swap! local assoc :height (.-value (.-target %)))}]]
      ;; Layout selector
      (layout-selector local)
      ;; Submit
      (when-not (empty? (str/trim name))
        [:input#project-btn.btn-primary
         {:value "Go go go!"
          :on-click #(do
                       (dom/prevent-default %)
                       (rs/emit! (dp/create-project @local))
                       (lightbox/close!))

          :type "submit"}])]
     [:a.close {:href "#"
                :on-click #(do (dom/prevent-default %)
                               (lightbox/close!))}
      i/close]])))

                    ;; (.preventDefault e)
                    ;; (let [new-project-attributes {:name (trim name)
                    ;;                               :width (int width)
                    ;;                               :height (int height)
                    ;;                               :layout layout}]
                    ;;  ;; (actions/create-project conn new-project-attributes)
                    ;;  (close-lightbox!)))}

(def new-project-lightbox
  (util/component
   {:render new-project-lightbox-render
    :name "new-project-lightbox"
    :mixins [(rum/local +project-defaults+)]}))

(defmethod lightbox/render-lightbox :new-project
  [_]
  (new-project-lightbox))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Menu
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(def ^:static menu-state
  (as-> (l/select-keys [:projects]) $
    (l/focus-atom $ s/state)))

(rum/defc project-sort-selector < rum/reactive
  [sort-order]
  nil)
  ;; (let [sort-name (get project-orderings (rum/react sort-order))]
  ;;   [:select.input-select
  ;;    {:on-change #(reset! sort-order (name->order (.-value (.-target %))))
  ;;     :value sort-name}
  ;;    (for [order (keys project-orderings)
  ;;          :let [name (get project-orderings order)]]
  ;;      [:option {:key name} name])]))

(defn menu-render
  []
  (let [state (rum/react menu-state)
        pcount (count (:projects state))]
    (html
     [:section#dashboard-bar.dashboard-bar
      [:div.dashboard-info
       [:span.dashboard-projects pcount " projects"]
       [:span "Sort by"]
       #_(project-sort-selector (atom :name))]
      [:div.dashboard-search
       icons/search]])))

(def menu
  (util/component
   {:render menu-render
    :name "dashboard-menu"
    :mixins [rum/reactive]}))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Project Item
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn project-render
  [own project]
  (let [on-click #(rs/emit! (dp/go-to-project (:id project)))]
    (html
     [:div.grid-item.project-th {:on-click on-click
                                 :key (:id project)}
      [:h3 (:name project)]
      [:span.project-th-update
       (str "Updated " (ago (:last-update project)))]
      [:div.project-th-actions
       [:div.project-th-icon.pages
        icons/page
        [:span 0]]
       [:div.project-th-icon.comments
        i/chat
        [:span 0]]
       [:div.project-th-icon.delete
        {:on-click #(do
                      (dom/stop-propagation %)
                      ;; (actions/delete-project conn uuid)
                      %)}
        icons/trash]]])))

(def project-item
  (util/component
   {:render project-render
    :name "project"
    :mixins [rum/static]}))

;; (defn sorted-projects
;;   [projects sort-order]
;;   (let [project-cards (map (partial project-card conn) (sort-by sort-order projects))]
;;     (if (= sort-order :project/name)
;;       project-cards
;;       (reverse project-cards))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Grid
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(def ^:static grid-state
  (as-> (l/select-keys [:projects-by-id]) $
    (l/focus-atom $ s/state)))

(defn grid-render
  [own]
  (letfn [(on-click [e]
            (dom/prevent-default e)
            (lightbox/set! :new-project))]
    (let [state (rum/react grid-state)]
      (html
       [:section.dashboard-grid
        [:h2 "Your projects"]
        [:div.dashboard-grid-content
         [:div.dashboard-grid-content
          [:div.grid-item.add-project {:on-click on-click}
           [:span "+ New project"]]
          (for [item (vals (:projects-by-id state))]
            (rum/with-key (project-item item) (:id item)))]]]))))

(def grid
  (util/component
   {:render grid-render
    :name "grid"
    :mixins [rum/reactive]}))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Main
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn dashboard-render
  [own]
  (html
   [:main.dashboard-main
    (ui.h/header)
    [:section.dashboard-content
     (menu)
     (grid)]]))

(def dashboard
  (util/component {:render dashboard-render
                   :mixins [rum/static]
                   :name "dashboard"}))
