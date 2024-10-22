(ns quill-sandbox.core
  (:require
    ["./js/quill_editor" :default QuillEditor]
    ["react-dom" :as react-dom]
    [datascript.core :as d]
    [reagent.core :as r]))


(def schema
  {:note/name {;; :db/valueType :db.type/string
               :db/cardinality :db.cardinality/one}

   :note/contents {:db/cardinality :db.cardinality/one}

   :note/updated-on {;; :db/valueType :db.type/instant
                     :db/cardinality :db.cardinality/one}})


(def conn (d/create-conn schema))


(defonce notes (r/atom []))


(defonce selected-id (r/atom nil))


(defonce note-name (r/atom nil))


(defonce quill-ref (atom nil))


(defn get-notes
  []
  (let [notes (->> (d/q '[:find [(pull ?n [:db/id :note/name :note/updated-on]) ...]
                          :where [?n :note/name]] @conn)
                   (sort-by :note/updated-on)
                   reverse)]
    (js/console.log "Loaded notes:" notes)
    notes))


(declare create-note)


(defn refresh-notes
  []
  (let [ns (get-notes)]
    (reset! notes ns)
    (when (empty? ns)
      (create-note))))


(defn load-note
  [id]
  (reset! selected-id id)
  (js/console.log "Loading note:" id)
  (let [note (d/pull @conn '[*] id)
        ^js/ReactQuill quill @quill-ref]
    (js/console.log "Loaded note:" note)
    (some-> quill .-editor (.setContents (:note/contents note)))
    (reset! note-name (:note/name note))))


(defn create-note
  []
  (let [tx-report (d/transact! conn [{:db/id -1
                                      :note/name (str "Note on " (.toLocaleString (js/Date.)))
                                      :note/contents {}
                                      :note/updated-on (js/Date.)}])
        id (-> tx-report :tempids (get -1))]
    (js/console.log "Transaction report:" tx-report)
    (refresh-notes)
    (load-note id)))


(defn on-name-change
  [event]
  (let [new-name (-> event .-target .-value)]
    (js/console.log "Name changed" new-name)
    (reset! note-name new-name)
    (js/console.log "Transacting name...")
    (d/transact! conn [[:db/add @selected-id :note/updated-on (js/Date.)]
                       [:db/add @selected-id :note/name new-name]])
    (refresh-notes)))


(declare on-quill-change)


(defn quill-component
  []
  [:> QuillEditor {:initialValue ""
                   :onChange on-quill-change
                   :refFn (fn [el]
                            (js/console.log "refFn:" el)
                            (reset! quill-ref el))}])


(defn on-quill-change
  [content]
  (js/console.log "Quill changed" content)
  (when (some? @selected-id)
    (when-let [quill @quill-ref]
      (js/console.log "Transacting content...")
      (d/transact conn [[:db/add @selected-id :note/updated-on (js/Date.)]
                        [:db/add @selected-id :note/contents (-> quill .-editor .getContents)]]))))


(defn menu-item
  [{id :db/id name :note/name}]
  (js/console.log "menu-item" @selected-id id (= @selected-id id))
  [:li.border-black.w-full.p-2.hover:font-bold.hover:cursor-pointer
   {:key (str "key-" id)
    :class (if (= @selected-id id) "selected" nil)
    :on-click #(load-note id)}
   [:span name]])


(defn app
  []
  [:div.container.text-center.m-auto.px-6
   [:h1.text-2xl.font-bold.mt-10.text-center "Quill Sandbox"]
   [:hr.my-2]
   [:div.container.flex.h-screen
    [:div.bg-gray-10.p-4 {:class "w-1/4 lg:w-1/3"}
     (into [:ul.text-left] (map menu-item) @notes)]
    [:div.flex.flex-col.w-full
     [:div.flex.flex-row.w-full.justify-around
      [:div {:class "w-1/2"}
       [:input.border.w-full.m-2.px-2.py-1
        {:type "text" :value @note-name :on-change on-name-change}]]
      [:button.text-emerald-700.hover:text-emerald-500.hover:underline {:on-click create-note} "New Note"]]
     [quill-component]]]])


(defn ^:export init
  []
  (react-dom/render
    (r/as-element [app])
    (.getElementById js/document "app"))
  (refresh-notes))


(init)
