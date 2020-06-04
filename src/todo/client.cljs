(ns todo.client
  (:require
   ["react-dom" :as react-dom]
   ["react" :as react]
   [helix.core :as helix :refer [defnc $ <>]]
   [helix.hooks :as hooks]
   [helix.dom :as dom]))

(defnc todo-item [{:keys [body]}]
  (dom/div
   (dom/input {:type "checkbox" :style {:margin-right ".5rem"}})
   body))

(defnc todo-items-list [{:keys [todo-items]}]
  (for [{:keys [id body]} todo-items]
    ($ todo-item {:key id :body body})))

(defnc create-todo-item-form [{:keys [on-submit]}]
  (dom/form
   {:on-submit
    (fn [event]
      (.preventDefault event)
      (on-submit {:body (.-target.elements.body.value event)})
      (set! (.-target.elements.body.value event) ""))}
   (dom/input
    {:type "text"
     :name "body"
     :placeholder "Add a todo..."})))

(defmulti handle-event (fn [_ [action]] action))

(defmethod handle-event :create-todo-item-submitted
  [state [_ {:keys [body]}]]
  (update state :todo-items conj
          {:id (str (inc (count (:todo-items state))))
           :body body}))

(defnc root []
  (let [[state dispatch] (hooks/use-reducer handle-event {:todo-items []})]
    (<>
     ($ todo-items-list {:todo-items (:todo-items state)})
     ($ create-todo-item-form
        {:on-submit #(dispatch [:create-todo-item-submitted %])}))))

(helix/create-context )

(defn ^:dev/after-load mount-root! []
  (react-dom/render ($ root) (js/document.getElementById "root")))

(defn init! []
  (mount-root!))
