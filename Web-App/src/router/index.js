import Vue from "vue";
import Router from "vue-router";
import Home from "../components/Home.vue";
import Settings from "../components/Settings.vue";
import ElementList from "../components/ElementList.vue";
import Bin from "../components/Bin.vue";
import Login from "../components/authentication/Login.vue";
import Register from "../components/authentication/Register.vue";
import Authentication from "../components/authentication/Authentication.vue";
import BaseElement from "../components/elements/BaseElement.vue";
import firebase from "../services/firebase"

Vue.use(Router);

const router = new Router({
  routes: [{
      path: "/",
      name: "Home",
      component: Home,
      meta: {
        requiresAuth: true
      },
      children: [{
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
          },
        },
        {
          path: "/element/:parent/:id",
          name: "BaseBundleElement",
          component: BaseElement,
          meta: {
            requiresAuth: true
          }
        },
        {
          path: "/bin",
          name: "Bin",
          component: Bin,
          meta: {
            requiresAuth: true
          }
        },
        {
          path: "/element/:id/bin",
          name: "BundleBin",
          component: Bin,
          props: true,
          meta: {
            requiresAuth: true
          }
        },
        {
          path: "/settings",
          name: "Settings",
          component: Settings,
          meta: {
            requiresAuth: true
          }
        }
      ]
    },
    {
      path: "/auth",
      name: "Authentication",
      component: Authentication,
      children: [{
          path: "/",
          name: "Login",
          component: Login
        },
        {
          path: "/register",
          name: "Register",
          component: Register
        }
      ]
    }
  ]
});

router.beforeEach((to, from, next) => {
  const currentUser = firebase.fb.auth().currentUser
  const requiresAuth = to.matched.some(record => record.meta.requiresAuth)

  if (requiresAuth && !currentUser) next("/auth")
  else if (!requiresAuth && currentUser) next("/")
  else next()
})

export default router
