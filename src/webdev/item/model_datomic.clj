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

(defn create-item [db])
(defn update-item [db])
(defn delete-item [db])
(defn read-items [db]
  '({:date_created #inst "2014-06-03T19:02:10.396340000-00:00",
     :checked true,
     :description "testset ",
     :name "This is a test from datomic",
     :id #uuid "a92f247d-854c-47d1-b1aa-a9f17d610f45"}))
