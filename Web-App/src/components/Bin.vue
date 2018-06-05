<template>
    <v-content>
      <v-toolbar app fixed>
        <v-btn icon @click="$router.go(-1)">
              <v-icon>arrow_back</v-icon>
        </v-btn>
        <v-toolbar-title>Bin</v-toolbar-title>
        <v-spacer></v-spacer>
        <v-menu>
          <v-btn slot="activator" icon>
            <v-icon>more_vert</v-icon>
          </v-btn>
          <v-list>
            <v-list-tile v-for="(item, i) in menuItems" :key="item.name" @click="item.action()">
              <v-list-tile-title>{{ item.name }}</v-list-tile-title>
            </v-list-tile>
          </v-list>
        </v-menu>
      </v-toolbar>
      <v-container fluid>
          <v-layout row>
              <v-flex xs12 sm6 offset-sm3>
                  <transition-group tag="v-layout" name="slide-x-transition" class="row wrap" mode="out-in">
                      <element-card v-for="(item, index) in elements" :key="index" :element="item" @click.native="restoreElement(item)">
                      </element-card>
                  </transition-group>
              </v-flex>
          </v-layout>
      </v-container>
    </v-content>
</template>

<script>
import BaseElementListComponentMixin from "./shared/BaseElementListComponentMixin";
import ElementCard from "./shared/ElementCard.vue";
import firebase from "../services/firebase.js";

export default {
  name: "Bin",
  extends: BaseElementListComponentMixin,
  firebase() {
    return {
      elements: firebase.getBinRef()
    };
  }
};
</script>
