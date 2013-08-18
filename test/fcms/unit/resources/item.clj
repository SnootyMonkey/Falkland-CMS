(ns fcms.unit.resources.item
  (:require [clojure.test :refer :all]
            [clj-time.format :refer (parse)]
            [fcms.resources.item :refer :all]
            [fcms.resources.collection :as collection]))

(def c "c")
(def i "i")
(def ascii-name "test this")
(def unicode-name "私はガラスを食")
(def mixed-name (str "test " unicode-name))
(def unicode-description "er stîget ûf mit grôzer kraft Τη γλώσσα μου έδωσαν ελληνική მივჰხვდე მას ჩემსა الزجاج و هذا لا يؤلمني. मैं काँच खा सकता ฉันกินกระจกได้ לא מזיק Mogę jeść szkło €")
(def slug "slug")

(defn verify-new-item [coll-slug item]
  (is (= (:collection item) c))
  (is (= (:version item) 1))
  (is (instance? String (:id item)))
  (is (instance? String (:created-at item)))
  (is (instance? org.joda.time.DateTime (parse (:created-at item))))
  (is (= (:created-at item) (:updated-at item))))

(defn- empty-collection-c [f]
  (collection/delete-collection c)
  (collection/create-collection c)
  (f)
  (collection/delete-collection c))

(use-fixtures :each empty-collection-c)

(deftest new-item-validity)

;; TODO error for reserved props (created-at, updated-at, collection, links, id, version, name)
(deftest item-creation
  (testing "item creation"

    (testing "with a generated slug"
      (let [item (create-item c ascii-name)]
        (is (= (:name item) ascii-name))
        (is (= (:slug item) "test-this"))
        (verify-new-item c item)))

    (testing "with a unicode name"
      (let [item (create-item c unicode-name)]
        (is (= (:name item) unicode-name))
        (is (= (:slug item) "1"))
        (verify-new-item c item))
      (let [item (create-item c mixed-name)]
        (is (= (:name item) mixed-name))
        (is (= (:slug item) "test"))
        (verify-new-item c item)))
    
    (testing "with unicode properties"
      (let [item (create-item c i {:description unicode-description})]
        (is (= (:name item) i))
        (is (= (:slug item) i))
        (is (= (:description item) unicode-description))
        (verify-new-item c item)))

    (testing "with a generated slug that is already used"
      (let [item (create-item c ascii-name)]
        (is (= (:name item) ascii-name))
        (is (= (:slug item) "test-this-1"))
        (verify-new-item c item))
      (let [item (create-item c ascii-name)]
        (is (= (:name item) ascii-name))
        (is (= (:slug item) "test-this-2"))
        (verify-new-item c item)))
    
    (testing "with a provided slug"
      (let [item (create-item c ascii-name {:slug slug})]
        (is (= (:name item) ascii-name))
        (is (= (:slug item) slug))
        (verify-new-item c item)))
    
    (testing "with a provided slug that is already used"
      (is (= :slug-conflict (create-item c ascii-name {:slug slug}))))

    (testing "with a provided slug that is invalid"
      (is (= :invalid-slug (create-item c ascii-name {:slug "i I"}))))
    
    (testing "with a collection that doesn't exist"
      (is (= :bad-collection (create-item "not-here" ascii-name))))))

(deftest item-retrieval)

(deftest updated-item-validity)

(deftest item-updates)

(deftest item-deletion)

(deftest item-listing)
