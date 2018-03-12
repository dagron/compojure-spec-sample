(ns c2.specrest.core
  (:require [clojure.spec.alpha :as s]
            [spec-tools.spec :as spec]
            [spec-tools.core :as stc]))

(s/def ::x spec/int?)
(s/def ::y spec/int?)
(s/def ::total spec/int?)
(s/def ::get-req (s/keys :req-un [::x ::y]))
(s/def ::total-map (s/keys :req-un [::total]
                           :opt-un [::x ::y]))


(def x-spec
	(stc/spec
		{:spec ::x
		 :description "x value"
		 :json-schema/default 42}))

(def y-spec
	(stc/spec
		{:spec ::y
		 :name "y-value"
		 :description "y value for request"
		 :json-schema/default 42}))


(def total-map-spec
	(stc/spec {
		:spec ::total-map
		:description "Total map specification"
		:name "total-map"}))
