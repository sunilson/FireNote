<template>
    <transition name="slide-y-transition" mode="out-in">
      <keep-alive include="ElementList">
          <router-view :key="$route.fullPath"></router-view>
      </keep-alive>
    </transition>
</template>

<script>
import firebase from "../services/firebase.js"
import AddElementDialog from "./shared/AddElementDialog"
import {EventBus} from "../services/EventBus.js"

export default {
  name: "Home"
};
</script>

<style>
</style>