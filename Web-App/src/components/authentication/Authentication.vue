<template>
  <v-content>
    <v-toolbar app fixed>
      <v-btn v-if="$route.path != '/auth'" icon @click="$router.go(-1)">
        <v-icon>arrow_back</v-icon>
      </v-btn>
      <v-toolbar-title>FireNote {{$route.name}}</v-toolbar-title>
      <v-spacer></v-spacer>
    </v-toolbar>
    <v-container fluid fill-height>
      <v-layout justify-center align-center>
        <transition name="slide-x-transition" mode="out-in">
            <keep-alive include="Login">
                <router-view></router-view>
            </keep-alive>
        </transition>
      </v-layout>
    </v-container>
  </v-content>
</template>

<script>
    export default {
      name: "Authentication"
    }
</script>

<style scoped>
</style>