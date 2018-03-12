(ns c2.spec
  (:require [compojure.api.sweet :refer [context GET resource]]
            [ring.util.http-response :refer [ok, forbidden, not-found, moved-permanently]]
            [clojure.spec.alpha :as s]
            [spec-tools.spec :as spec]
            [c2.specrest.core :as rest]
            [spec-tools.core :as stc]))

; (s/def ::x spec/int?)
; (s/def ::y spec/int?)
; (s/def ::total spec/int?)
; (s/def ::total-map (s/keys :req-un [::total]
;                            :opt-un [::x ::y]))


;;(s/def ::xxx (s/keys :req-un [c/x-spec c/y-spec]))

(s/def ::total spec/int?)

(s/def ::name (stc/spec string?))

;; map-syntax with extra info
(s/def ::age
  (stc/spec
    {:spec integer?
     :description "Age on a person"
     :json-schema/default 20}))

(s/def ::person
  (stc/spec
    {:spec (s/keys :req-un [::name ::age])
     :description "a Person"}))


; (s/def ::x spec/int?)
; (s/def ::y spec/int?)
; (s/def ::total spec/int?)
; (s/def ::get-req (s/keys :req-un [::x ::y]))
; (s/def ::total-map (s/keys :req-un [::total]
;                            :opt-un [::x ::y]))

(def routes
  (context "/spec" []
    :tags ["spec"]
    :coercion :spec

    (GET "/xxx" []
      :query-params [return :- (s/spec #{200 403 404 301})]
      :responses    {200 {:schema (s/keys :req-un [::total])}
                     301 {:schema string?, :description "new place!" } ;:headers {:location string?}}
                     403 {:schema {:code string?}, :description "spiders?"}
                     404 {:schema {:reason string?}, :description "lost?"}}
      ;;:return       Total
      :summary      "multiple returns models"
      (case return
        200 (ok {:total 42})
        301 (moved-permanently "http://www.new-total.com")
        403 (forbidden {:code "forest"})
        404 (not-found {:reason "lost"})))

    (GET "/xxxx" []
      :query-params [return :- int?]
      :responses    {200 {:schema (s/keys :req-un [::total])}
                     301 {:schema string?, :description "new place!" } ;:headers {:location string?}}
                     403 {:schema {:code string?}, :description "spiders?"}
                     404 {:schema {:reason string?}, :description "lost?"}}
      ;;:return       Total
      :summary      "multiple returns models"
      (case return
        200 (ok {:total 42})
        301 (moved-permanently "http://www.new-total.com")
        403 (forbidden {:code "forest"})
        404 (not-found {:reason "lost"})))

    (GET "/plus" []
      :summary "plus with clojure.spec"
      :query-params [x :- rest/x-spec, y :- rest/y-spec]
      :return ::rest/total-map
      (ok {:total (+ x y)}))

    (GET "/test" []
      :summary "hz"
      :query-params [person-name :- string?, person-age :- ::age]
      ;;:return ::person
      :responses {200 {:schema ::person}
                  403 {:schema {:code string?}, :description "forbidden"}}
      (if (> person-age 18) 
        (ok {:name person-name :age person-age})
        (forbidden {:code "forbidden"})))

    (context "/data-plus" []
      (resource
        {:post
         {:summary "data-driven plus with clojure.spec"
          :parameters {:body-params {:x rest/x-spec, :y rest/y-spec}}
          ;;:responses {200 {:schema ::rest/total-map}}
          :responses {200 {:schema rest/total-map-spec}}
          :handler (fn [{{:keys [x y]} :body-params}]
                     (ok {:total (+ x y)}))}}))))
