(ns user
  (:require
   [clojure.walk :as walk]
   [com.walmartlabs.lacinia :as lacinia]
   [com.walmartlabs.lacinia.pedestal :as lacinia.pedestal]
   [io.pedestal.http :as pedestal.http]
   [todo.schema]))

(def schema (todo.schema/load-schema))

(defn query [gql-query]
  (->> (lacinia/execute schema gql-query nil nil)
       (walk/postwalk
        #(cond
           (instance? clojure.lang.IPersistentMap %) (into {} %)
           (seq? %) (vec %)
           :else %))))

(comment
  (query "{ all_todo_items { id body } }")

  (query "mutation { create_todo_item(body: \"Test\") { id } }")

  )

(defonce server nil)

(defn start-server []
  (-> (lacinia.pedestal/service-map schema {:graphiql true})
      (pedestal.http/create-server)
      (pedestal.http/start)))

(defn stop-server []
  (when server (pedestal.http/stop server))
  nil)

(defn go []
  (alter-var-root #'server (constantly (start-server)))
  :started)

(defn stop []
  (alter-var-root #'server (constantly (stop-server)))
  :stopped)

(comment
  (go)

  )
