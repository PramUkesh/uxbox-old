(ns uxbox.library.icons
  (:require [uxbox.library.icons.material-design-actions :as md-actions]
            [uxbox.library.icons.material-design-alerts :as md-alerts]
            [uxbox.library.icons.material-design-av :as md-av]
            [uxbox.library.icons.material-design-communication :as md-comm]
            [uxbox.library.icons.material-design-content :as md-content]
            [uxbox.library.icons.material-design-device :as md-device]
            [uxbox.library.icons.material-design-editor :as md-editor]
            [uxbox.library.icons.material-design-file :as md-file]
            [uxbox.library.icons.material-design-hardware :as md-hardware]
            [uxbox.library.icons.material-design-image :as md-image]
            [uxbox.library.icons.material-design-maps :as md-maps]
            [uxbox.library.icons.material-design-navigation :as md-nav]
            [uxbox.library.icons.material-design-notification :as md-not]
            [uxbox.library.icons.material-design-social :as md-social]
            [uxbox.library.icons.material-design-toggle :as md-toggle]
            ))

(def ^:private +external+
  [{:name "Custon icon"
    :view-box [0 0 50 50]
    :id :material/foobar
    :type :builtin/icon-svg
    :image {:xlink-href "http://s.cdpn.io/3/kiwi.svg"
            :width 50
            :height 50}}])

(def +collections+
  [{:name "Material design (actions)"
    :builtin true
    :id 1
    :icons md-actions/+icons+}
   {:name "Material design (alerts)"
    :builtin true
    :id 2
    :icons md-alerts/+icons+}
   {:name "Material design (Av)"
    :builtin true
    :id 3
    :icons md-av/+icons+}
   {:name "Material design (Communication)"
    :builtin true
    :id 4
    :icons md-comm/+icons+}
   {:name "Material design (Content)"
    :builtin true
    :id 5
    :icons md-content/+icons+}
   {:name "Material design (Device)"
    :builtin true
    :id 6
    :icons md-device/+icons+}
   {:name "Material design (Editor)"
    :builtin true
    :id 7
    :icons md-editor/+icons+}
   {:name "Material design (File)"
    :builtin true
    :id 8
    :icons md-file/+icons+}
   {:name "Material design (Hardware)"
    :builtin true
    :id 9
    :icons md-hardware/+icons+}
   {:name "Material design (Image)"
    :builtin true
    :id 10
    :icons md-image/+icons+}
   {:name "Material design (Maps)"
    :builtin true
    :id 11
    :icons md-maps/+icons+}
   {:name "Material design (Navigation)"
    :builtin true
    :id 12
    :icons md-nav/+icons+}
   {:name "Material design (Notifications)"
    :builtin true
    :id 13
    :icons md-not/+icons+}
   {:name "Material design (Social)"
    :builtin true
    :id 14
    :icons md-social/+icons+}
   {:name "Material design (Toggle)"
    :builtin true
    :id 15
    :icons md-toggle/+icons+}
   {:name "External icons"
    :builtin true
    :id 40
    :icons +external+}])
