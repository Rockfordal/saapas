(ns backend.dbschema
  (:require [datomic-schema.schema :as s]))


(def parts [(s/part "frejm")])

(def schema
  [(s/fields
     [type        :keyword :indexed])

;; --- SNI ---
   (s/schema sni (s/fields
     [code        :long   :unique-value]
     [name        :string :unique-value]))

;; --- Callcenter ---
   (s/schema project (s/fields
     [name        :string :unique-value]
     [desc        :string :indexed]))

   (s/schema company (s/fields
     [project      :ref]
     [sni          :ref]
     [orgnr        :long   :unique-value]
     [name         :string :unique-value]
     [phone        :string :indexed]
     [email        :string :indexed]
     [employees    :long   :indexed]
     [contact      :string :indexed]
     [contacttype  :string :indexed]

     [homepage     :string :indexed]
     [visitadr     :string]
     [zipcode      :long]
     [postal       :string]

     [info         :string :indexed]
     [workphone    :string :indexed]
     [oms          :string :indexed]
     [othercontact :string :indexed]

     [salesman     :string :indexed] ; FC Försäljningschef
     [marketingdir :string :indexed] ; MC (marknadschef)
     [vd           :string :indexed] ; behövs?
     [snicode      :long]))

   (s/schema activity (s/fields
     [project :ref]
     [company :ref]
     [status  :enum [:pending :active :inactive :cancelled]]
     [note    :string :fulltext]
     [datum   :instant]
     ))
  ])

(defn get-schema []
  (concat (s/generate-parts parts)
          (s/generate-schema schema)))
