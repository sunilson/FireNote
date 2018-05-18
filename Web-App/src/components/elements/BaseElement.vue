<template>
    <v-app>
      <v-toolbar app fixed>
        <transition name="slide-x-transition" mode="out-in">
          <v-btn icon @click="$router.go(-1)">
            <v-icon>arrow_back</v-icon>
          </v-btn>
        </transition>
        <transition name="fade-transition">
          <v-toolbar-title>{{element.title}}</v-toolbar-title>
        </transition>
        <v-spacer></v-spacer>
        <v-btn icon>
          <v-icon v-if="element.locked">lock_outline</v-icon>
          <v-icon v-if="!element.locked">lock_open</v-icon>
        </v-btn>
        <v-btn icon>
          <v-icon>delete</v-icon>
        </v-btn>
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
        <note v-if="element.noteType == 'note'" :id="$route.params.id"></note>
        <checklist v-else-if="element.noteType == 'checklist'" :id="$route.params.id"></checklist>
        <bundle v-else-if="element.noteType == 'bundle'" :id="$route.params.id"></bundle>
        <v-btn color="pink" dark fixed bottom right fab style="display: flex">
          <v-icon>add</v-icon>
        </v-btn>
      </v-content>
    </v-app>
</template>

<script>
import firebase from "../../services/firebase.js"
import Checklist from "./Checklist"
import Bundle from "./Bundle"
import Note from "./Note"

export default {
  name: "BaseElement",
  data() {
    return {
      element: {}
    }
  },
  firebase() {
    return {
      element: firebase.getElementRef(this.$route.params.id)
    }
  },
  components: {
    note: Note,
    checklist: Checklist,
    bundle: Bundle
  }
};
</script>


<style scoped>
</style>
