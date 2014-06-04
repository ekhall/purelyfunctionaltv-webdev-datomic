(ns webdev.item.model-datomic
  (:require [datomic.api :only (q db) :as d]))

(def schema
  [
   ;; Item Partition
   {:db/id #db/id[:db.part/db]
    :db/ident :item
    :db.install/_partition :db.part/db}

   ;; Item Information
   {:db/id #db/id[:db.part/db]
    :db/ident :item/name
    :db/valueType :db.type/string
    :db/cardinality :db.cardinality/one
    :db/doc "An item's name"
    :db.install/_attribute :db.part/db}

   {:db/id #db/id[:db.part/db]
    :db/ident :item/description
    :db/valueType :db.type/string
    :db/cardinality :db.cardinality/one
    :db/doc "An item's description"
    :db.install/_attribute :db.part/db}

   {:db/id #db/id[:db.part/db]
    :db/ident :item/checked
    :db/valueType :db.type/boolean
    :db/cardinality :db.cardinality/one
    :db/doc "An item's checked status"
    :db.install/_attribute :db.part/db}
   ])

(defn create-table [db]
  (d/create-database db)
  (let [conn (d/connect db)]
    @(d/transact conn schema) conn))

(defn create-item [db name description]
  (:id (first @(d/transact (d/connect db)
                           [{:db/id (d/tempid :item)
                             :item/name name
                             :item/description description}]))))

(defn update-item [db])
(defn delete-item [db])

(def item-convert-data
  [:item/name
   :item/description])

(defn entity->map [convert-data entity]
  (reduce (fn [m attr]
            (if (keyword? attr)
              (if-let [v (attr entity)]
                (assoc m attr v)
                m)
              (let [[attr conv-fn] attr]
                (if-let [v (attr entity)]
                  (assoc m attr (conv-fn v))
                  m))))
          {}
          convert-data))

(defn read-items2 [db]
  '({:date_created #inst "2014-06-03T19:02:10.396340000-00:00",
     :checked true,
     :description "testset ",
     :name "This is a test from datomic",
     :id #uuid "a92f247d-854c-47d1-b1aa-a9f17d610f45"}))

(defn read-items [db]
  (let [conn (d/connect db)
        results (d/q '[:find ?e ?name ?description
                       :where
                       [?e item/name ?name]
                       [?e item/description ?description]]
                     (d/db conn))]
    (for [r results]
      (d/touch (d/entity (d/db (d/connect db)) (first r))))))
