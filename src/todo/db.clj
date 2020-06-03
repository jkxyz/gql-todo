(ns todo.db)

(def todo-items (atom nil))

(defn get-all-todo-items []
  @todo-items)

(defn create-todo-item! [todo-item]
  (first (swap! todo-items #(conj % (assoc todo-item :id (str (inc (count %))))))))
