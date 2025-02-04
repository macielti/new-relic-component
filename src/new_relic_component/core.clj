(ns new-relic-component.core
  (:require [cheshire.core :as json]
            [common-clj.traceability.core :as common-traceability]
            [http-client-component.core :as component.http-client]
            [integrant.core :as ig]
            [medley.core :as medley]
            [taoensso.timbre :as log]))

(defn new-relic-http-appender
  [api-key service http-client & [opts]]
  {:enabled? true
   :async?   true
   :fn       (fn [data]
               (let [stacktrace-str (if-let [pr (:pr-stacktrace opts)]
                                      #(with-out-str (pr %))
                                      #(log/default-output-error-fn
                                        {:?err        %
                                         :output-opts {:stacktrace-fonts {}}}))
                     entry (medley/assoc-some {:cid       (common-traceability/correlation-id-appended!)
                                               :service   service
                                               :level     (str (name (:level data)))
                                               :log       (str (force (:msg_ data)))
                                               :namespace (str (:?ns-str data))
                                               :log-key   (-> data :vargs first str)
                                               :hostname  (str (force (:hostname_ data)))}
                                              :error (some-> (:?err data) str)
                                              :stacktrace (some-> (:?err data) (stacktrace-str)))]

                 @(component.http-client/request! {:url         "https://log-api.newrelic.com/log/v1"
                                                   :method      :post
                                                   :endpoint-id :post-log-new-relic
                                                   :payload     {:headers {"Content-Type" "application/json"
                                                                           "Api-Key"      api-key}
                                                                 :body    (json/encode entry)}}
                                                  http-client)))})

(defmethod ig/init-key ::new-relic
  [_ {:keys [components]}]
  (log/info :starting ::new-relic)
  {:timbre (log/merge-config!
            {:appenders {:new-relic-http (new-relic-http-appender (-> components :config :new-relic-api-key)
                                                                  (-> components :config :service-name)
                                                                  (:http-client components))}})})

(defmethod ig/halt-key! ::new-relic
  [_ _]
  (log/info :stopping ::new-relic))
