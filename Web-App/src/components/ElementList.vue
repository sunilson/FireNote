<template>
    <v-content>
      <v-toolbar app fixed>
      <v-toolbar-side-icon disabled><img src="../assets/firenote_logo.png" width="30"></v-toolbar-side-icon>
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
                  <transition-group tag="v-layout" name="slide-x-transition" class="row wrap" mode="out-in">
                      <element-card v-for="(item, index) in elements" :key="index" :element="item" @click.native="tryOpenElement(item)">
                      </element-card>
                  </transition-group>
              </v-flex>
          </v-layout>
          <passwordDialog  @openLockedElement="openLockedElement($event)"></passwordDialog>
      </v-container>
      <v-speed-dial fixed bottom right v-model="fab">
        <v-btn slot="activator" v-model="fab" color="fabColor" dark fab>
            <v-icon>add</v-icon>
            <v-icon>close</v-icon>
        </v-btn>
        <v-tooltip left :value="tooltips">
          <v-btn fab dark small color="green" slot="activator" @click="addElement('checklist')">
            <v-icon>done_all</v-icon>
          </v-btn>
          <span>Checklist</span>
        </v-tooltip>   
        <v-tooltip left :value="tooltips">
          <v-btn fab dark small color="green" slot="activator" @click="addElement('note')">
            <v-icon>event_note</v-icon>
          </v-btn>
          <span>Note</span>
        </v-tooltip>   
        <v-tooltip left :value="tooltips">
          <v-btn fab dark small color="green" slot="activator" @click="addElement('bundle')">
            <v-icon>list</v-icon>
          </v-btn>
          <span>Bundle</span>
        </v-tooltip>    
      </v-speed-dial>
      <addElementDialog></addElementDialog>
    </v-content>
  </div>
</template>

<script>
import ElementCard from "./shared/ElementCard.vue";
import firebase from "../services/firebase.js"
import {hashPassword} from "../services/utilities.js"
import {EventBus} from "../services/EventBus.js"
import PasswordDialog from '../components/shared/PasswordDialog.vue';
import AddElementDialog from "../components/shared/AddElementDialog"

export default {
  name: "ElementList",
  data() {
    return {
      tooltips: false,
      fab: false,
      noteType: "note",
      selectedElement: null,
      menuItems: [
          {
            name: "Papierkorb",
            action: () => {}
          },
          {
            name: "Log Out",
            action: () => firebase.fb.auth().signOut()
          }
      ],
      items: ["abc", "def", "ghi"],
      elements: []
    };
  },
  watch: {
    fab(val) {
      this.tooltips = false
      val && setTimeout(() => {
        this.tooltips = true
      }, 250)
    }
  },
  methods: {
    addElement(type) {
      EventBus.$emit("addElement", {type})
    },
    tryOpenElement(element) {
      this.selectedElement = null
      if(!element.locked) {
        this.$router.push({name: 'BaseElement', params: {id: element[".key"]}})
      } else {
        this.selectedElement = element
        EventBus.$emit("showPasswordDialog")
      }
    },
    openLockedElement(password) {
      if(hashPassword(password) == this.settings.masterPassword) {
        this.$router.push({name: 'BaseElement', params: {id: this.selectedElement[".key"]}})
      } else {
        EventBus.$emit("showSnackbar", "Wrong password!")
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
    passwordDialog: PasswordDialog,
    addElementDialog: AddElementDialog,
  }
};
</script>

<style scoped>
  
</style>

<style>
.toolbar .toolbar__content>.btn:first-child, .toolbar .toolbar__extension>.btn:first-child {
    margin-left: 8px !important;
  }
  .toolbar__title {
    margin-left: 8px !important;
  }
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