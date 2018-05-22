<template>
    <v-content>
      <v-toolbar app fixed>
        <v-toolbar-title>FireNote</v-toolbar-title>
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
                  <transition-group tag="v-layout" class="row wrap" mode="out-in">
                      <element-card v-for="(item, index) in elements" :key="index" :element="item" @click.native="openElement(item)">
                      </element-card>
                  </transition-group>
              </v-flex>
              <v-speed-dial fixed bottom right v-model="fab">
                  <v-btn slot="activator" v-model="fab" color="fabColor" dark fab>
                      <v-icon>add</v-icon>
                      <v-icon>close</v-icon>
                  </v-btn>
                  <v-btn fab dark small color="green" @click="addElement = true">
                      <v-icon>list</v-icon>
                  </v-btn>
                  <v-btn fab dark small color="green" @click="addElement = true">
                      <v-icon>event_note</v-icon>
                  </v-btn>
                  <v-btn fab dark small color="green" @click="addElement = true">
                      <v-icon>done_all</v-icon>
                  </v-btn>
              </v-speed-dial>
          </v-layout>
          <addElementDialog :show="addElement"></addElementDialog>
          <v-dialog v-model="lockedElement" max-width="290">
            <v-card>
              <v-card-title class="headline">Element is locked</v-card-title>
              <div style="padding-left: 16px; padding-right: 16px">
                <v-text-field label="Element password" type="password" autofocus v-model="password" @keyup.enter="openElement()"></v-text-field>
              </div>
              <v-card-actions>
                <v-spacer></v-spacer>
                <v-btn color="green darken-1" flat="flat" @click.native="lockedElement = false">Cancel</v-btn>
                <v-btn color="green darken-1" flat="flat" @click.native="openElement()">Open</v-btn>
              </v-card-actions>
            </v-card>
          </v-dialog>
      </v-container>
    </v-content>
  </div>
</template>

<script>
import ElementCard from "./ElementCard.vue";
import AddElementDialog from "./AddElementDialog"
import firebase from "../services/firebase.js"
import {hashPassword} from "../services/utilities.js"

export default {
  name: "ElementList",
  created() {
    console.log("CREATED ELEMENTLIST")
  },
  data() {
    return {
      password: "",
      lockedElement: false,
      selectedElement: null,
      elementToAdd: {
        title: "",
        category: ""
      },
      addElement: false,
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
              this.$router.replace("/auth")
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
      if(element) this.selectedElement = element
      else element = this.selectedElement

      if(element.locked) {
        if(this.password.length > 0) {
          this.lockedElement = false
          console.log(hashPassword(this.password))
          console.log(this.settings.masterPassword)
          if(hashPassword(this.password) == this.settings.masterPassword) {
            this.$router.push({name: 'BaseElement', params: {id: element[".key"]}})
          } else {
            alert("Falsches Passwort!")
          }
          this.password = ""
          this.selectedElement = null
        } else {
          this.lockedElement = true
        }
      } else {
        this.selectedElement = null
        this.$router.push({name: 'BaseElement', params: {id: element[".key"]}})
      }
    }
  },
  firebase() {
    return {
      elements: firebase.getElementListRef(),
      settings: firebase.getSettings()
    }
  },
  components: {
    elementCard: ElementCard,
    addElementDialog: AddElementDialog
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
  .fabColor {
    background-color: #9E9E9E !important
  }
</style>