(ns todo.schema
  (:require
   [clojure.java.io :as io]
   [clojure.edn :as edn]
   [com.walmartlabs.lacinia.util :as lacinia.util]
   [com.walmartlabs.lacinia.schema :as lacinia.schema]
   [todo.db]))

(defn resolvers []
  {:query/all-todo-items
   (fn [_ _ _]
     (todo.db/get-all-todo-items))

   :mutation/create-todo-item
   (fn [_ args _]
     (todo.db/create-todo-item! args))})

(defn load-schema []
  (-> (io/resource "schema.edn")
      (slurp)
      (edn/read-string)
      (lacinia.util/attach-resolvers (resolvers))
      (lacinia.schema/compile)))

(comment
  (load-schema)
  )
