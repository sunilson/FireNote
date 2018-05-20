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
        <v-btn icon @click="toggleLock()">
          <v-icon v-if="element.locked">lock_outline</v-icon>
          <v-icon v-if="!element.locked">lock_open</v-icon>
        </v-btn>
        <v-btn icon @click="confirmDeletion()">
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
        <v-dialog v-model="dialog" max-width="290">
          <v-card>
            <v-card-title class="headline">Delete Element</v-card-title>
            <v-card-text>Do you really want to delete this note?</v-card-text>
            <v-card-actions>
              <v-spacer></v-spacer>
              <v-btn color="red darken-1" flat @click.native="dialog = false">Cancel</v-btn>
              <v-btn color="green darken-1" flat @click.native="deleteElement()">Agree</v-btn>
            </v-card-actions>
          </v-card>
        </v-dialog>
        <v-snackbar
          :timeout="4000"
          bottom
          v-model="snackbar">
          {{ snackbarText }}
          <v-btn flat color="pink" @click.native="snackbar = false">Close</v-btn>
        </v-snackbar>
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
      dialog: false,
      snackbar: false,
      snackbarText: "",
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
  },
  methods: {
    toggleLock() { 
      firebase.lockElement(!this.element.locked, this.element[".key"], null)
    },
    confirmDeletion() {
      this.dialog = true
    },
    deleteElement(){
      this.dialog = false
      firebase.deleteElement(this.element[".key"], null, err => {
        console.log(err)
        if(!err) this.$router.go(-1) 
        else {
          this.snackbarText = err.message
          this.snackbar = true
        }
      })
    }
  }
};
</script>

<style scoped>
</style>
