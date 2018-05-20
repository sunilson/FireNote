<template>
  <transition name="slide-y-transition" mode="out-in">
    <keep-alive include="ElementList">
        <router-view></router-view>
    </keep-alive>
  </transition>
</template>

<script>
import firebase from "../services/firebase.js"

export default {
  name: "Home",
  data() {
    return {
    };
  },
};
</script>

<style>
</style>