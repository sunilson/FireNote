import Vue from "vue";
import Router from "vue-router";
import Home from "../components/Home.vue";
import Login from "../components/authentication/Login.vue";
import BaseElement from "../components/elements/BaseElement.vue";
import firebase from "firebase"

Vue.use(Router);

const router = new Router({
  routes: [{
    path: "/",
    name: "Home",
    component: Home,
    meta: {
      requiresAuth: true
    }
  },
  {
    path: "/login",
    name: "Login",
    component: Login
  },
  {
    path: "/element/:id",
    name: "BaseElement",
    component: BaseElement,
    meta: {
      requiresAuth: true
    }
  }
  ]
});

router.beforeEach((to, from, next) => {
  const currentUser = firebase.auth().currentUser
  const requiresAuth = to.matched.some(record => record.meta.requiresAuth)

  if (requiresAuth && !currentUser) next("Login")
  else if (!requiresAuth && currentUser) next("/")
  else next()
})

export default router
