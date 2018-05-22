<template>
  <v-app>
    <v-snackbar
      :timeout="4000"
      bottom
      v-model="snackbar">
      {{ snackbarText }}
      <v-btn flat color="pink" @click.native="snackbar = false">Close</v-btn>
    </v-snackbar>
    <transition name="slide-y-transition" mode="out-in">
      <keep-alive include="ElementList">
          <router-view></router-view>
      </keep-alive>
    </transition>
  </v-app>
</template>

<script>
import firebase from "../services/firebase.js"
import {EventBus} from "../services/EventBus.js"

export default {
  name: "Home",
  mounted() {
    EventBus.$on('showSnackbar', message => {
      this.snackbarText = message
      this.snackbar = true
    });
  },
  data() {
    return {
      snackbarText: "",
      snackbar: false
    };
  },
};
</script>

<style>
</style>