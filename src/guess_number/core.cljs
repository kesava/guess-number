(ns guess-number.core
  (:require [play-cljs.core :as p]
            [goog.events :as events]))

(defonce game (p/create-game (.-innerWidth js/window) (.-innerHeight js/window)))
(defonce state (atom {}))

(def seed-range {:x (Math/floor (rand 10000)) :y (Math/floor (rand 10000))})
(def seed-range-sorted {:min (min (:x seed-range) (:y seed-range)) 
                        :max (max (:x seed-range) (:y seed-range))}) 
                         
(defn guess
  [smaller bigger]
  (quot (+ smaller bigger) 2))

(def keyEventFns {
                  :up (fn [st] (assoc st :smaller (guess (:smaller @state) (:bigger @state))))
                  :down (fn [st] (assoc st :bigger (guess (:smaller @state) (:bigger @state))))})

(def main-screen
  (reify p/Screen
    (on-show [this]
      (reset! state {:text-x 600 :text-y 400 :smaller (:min seed-range-sorted) :bigger (:max seed-range-sorted) :start false}))
    (on-hide [this])
    (on-render [this]
      (p/render game
        [[:fill {:color "lightgray"}
          [:rect {:x 0 :y 0 :width (.-innerWidth js/window) :height (.-innerHeight js/window)}]]
         [:fill {:color "green"}
          [:text {:value (str "Guess a number between " (:min seed-range-sorted) " and " (:max seed-range-sorted)) :x 200 :y 100 :size 26 :font "Helvetica"}]]
         [:fill {:color "red"}
          [:text {:value (str (guess (:smaller @state) (:bigger @state))) :x (:text-x @state) :y (:text-y @state) :size 76 :font "Helvetica"}]]]))))

(events/listen js/window "keydown"
  (fn [event]
   (condp = (.-keyCode event)
      38 (swap! state (:up keyEventFns))
      40 (swap! state (:down keyEventFns)))))

(events/listen js/window "resize"
  (fn [event]
    (p/set-size game js/window.innerWidth js/window.innerHeight)))

(doto game
  (p/start)
  (p/set-screen main-screen))

