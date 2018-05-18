// The Vue build version to load with the `import` command
// (runtime-only or standalone) has been set in webpack.base.conf with an alias.
import Vue from 'vue'
import App from './App'
import Vuetify from 'vuetify'
import router from './router'
import VueFire from 'vuefire'
import firebase from "firebase"
import { unzipSync } from 'zlib';

Vue.config.productionTip = false

Vue.use(Vuetify)
Vue.use(VueFire)

Vue.filter("parseDate", element => "20.12.1993")

const unsubscribe = firebase.auth().onAuthStateChanged(user => {
  new Vue({
    el: '#app',
    render: h => h(App),
    router
  })
  unsubscribe()
})


