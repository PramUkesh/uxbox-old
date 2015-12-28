(ns uxbox.ui.workspace
  (:require [sablono.core :as html :refer-macros [html]]
            [rum.core :as rum]
            [uxbox.router :as r]
            [uxbox.rstore :as rs]
            [uxbox.state :as s]
            [uxbox.data.workspace :as dw]
            [uxbox.ui.util :as util]
            [uxbox.ui.mixins :as mx]
            [uxbox.ui.workspace.base :as wb]
            [uxbox.ui.workspace.shortcuts :as wshortcuts]
            [uxbox.ui.workspace.lateralmenu :refer (lateralmenu)]
            [uxbox.ui.workspace.pagesmngr :refer (pagesmngr)]
            [uxbox.ui.workspace.header :refer (header)]
            [uxbox.ui.workspace.rules :refer (h-rule v-rule)]
            [uxbox.ui.workspace.sidebar :refer (aside)]
            [uxbox.ui.workspace.workarea :refer (viewport)]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Coordinates Debug
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn- coordenates-render
  [own]
  (let [[x y] (rum/react wb/mouse-position)]
    (html
     [:div {:style {:position "absolute" :left "80px" :top "20px"}}
      [:table
       [:tbody
        [:tr [:td "X:"] [:td x]]
        [:tr [:td "Y:"] [:td y]]]]])))

(def coordinates
  (util/component
   {:render coordenates-render
    :name "coordenates"
    :mixins [rum/reactive]}))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Workspace
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn- workspace-render
  [own projectid]
  (let [workspace (rum/react wb/workspace-state)
        no-toolbars? (empty? (:toolboxes workspace))]
    (html
     [:div
      (header)
      [:main.main-content
       [:section.workspace-content
        ;; Lateral Menu (left side)
        (lateralmenu)

        ;; Pages management lightbox
        (pagesmngr)

        ;; Rules
        (h-rule)
        (v-rule)

        ;; Canvas
        [:section.workspace-canvas {:class (when no-toolbars? "no-tool-bar")
                                    :on-scroll (constantly nil)}
         #_(when (:selected page)
             (element-options conn
                              page-cursor
                              project-cursor
                              zoom-cursor
                              shapes-cursor))
         (coordinates)
         (viewport)]]

       ;; Aside
       (when-not no-toolbars?
         (aside))]])))

(defn- workspace-will-mount
  [own]
  (let [[projectid pageid] (:rum/props own)]
    (rs/emit! (dw/initialize projectid pageid))
    own))

(defn- workspace-transfer-state
  [old-state state]
  (let [[projectid pageid] (:rum/props state)]
   (rs/emit! (dw/initialize projectid pageid))
   state))

(def ^:static workspace
  (util/component
   {:render workspace-render
    :will-mount workspace-will-mount
    :transfer-state workspace-transfer-state
    :name "workspace"
    :mixins [mx/static rum/reactive wshortcuts/mixin]}))

