<template>
  <v-app>
    <v-toolbar app fixed>
      <transition name="slide-x-transition" mode="out-in">
        <v-btn icon v-if="$route.name !==  'Home'" @click="$router.go(-1)">
          <v-icon>arrow_back</v-icon>
        </v-btn>
      </transition>
      <v-toolbar-title>{{$route.name}}</v-toolbar-title>
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
    <v-content>
      <v-container fluid>
        <v-layout row>
          <v-flex xs12 sm6 offset-sm3>
              <transition-group name="slide-x-transition" tag="v-layout" class="row wrap">
                <element-card v-for="(item, index) in elements" :key="index" :element="item" @click.native="openElement(item)">
                </element-card>
              </transition-group>
          </v-flex>
          <v-speed-dial fixed bottom right v-model="fab">
            <v-btn slot="activator" v-model="fab" color="pink darken" dark fab>
              <v-icon>add</v-icon>
              <v-icon>close</v-icon>
            </v-btn>
            <v-btn fab dark small color="green">
              <v-icon>list</v-icon>
            </v-btn>
            <v-btn fab dark small color="green">
              <v-icon>event_note</v-icon>
            </v-btn>
            <v-btn fab dark small color="green">
              <v-icon>done_all</v-icon>
            </v-btn>
          </v-speed-dial>
        </v-layout>
      </v-container>
    </v-content>
  </v-app>
</template>

<script>
import ElementCard from "./ElementCard.vue";
import firebase from "../services/firebase.js"

export default {
  name: "Home",
  data() {
    return {
      fab: false,
      menuItems: [
        {
          name: "Papierkorb",
          action: () => {}
        },
        {
          name: "Log Out",
          action: () => {
            firebase.fb.auth().signOut().then(() => {
              this.$router.replace("Login")
            })
          }
        }
      ],
      items: ["abc", "def", "ghi"],
      elements: []
    };
  },
  methods: {
    openElement(element) {
      this.$router.push({name: 'BaseElement', params: {id: element[".key"]}})
    }
  },
  firebase() {
    return {
      elements: firebase.getElementListRef()
    }
  },
  components: {
    elementCard: ElementCard
  }
};
</script>

<style>
  .speed-dial__list {
    padding-bottom: 10px
  }
  .list {
        background: #fafafa !important;
  }
  .icon {
    display: flex !important
  }
</style>