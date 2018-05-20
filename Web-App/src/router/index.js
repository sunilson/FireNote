import Vue from "vue";
import Router from "vue-router";
import Home from "../components/Home.vue";
import ElementList from "../components/ElementList.vue";
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
    },
    children: [
      {
        path: "/",
        name: "ElementList",
        component: ElementList,
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
  },
  {
    path: "/login",
    name: "Login",
    component: Login
  }]
});

router.beforeEach((to, from, next) => {
  const currentUser = firebase.auth().currentUser
  const requiresAuth = to.matched.some(record => record.meta.requiresAuth)

  if (requiresAuth && !currentUser) next("Login")
  else if (!requiresAuth && currentUser) next("/")
  else next()
})

export default router
