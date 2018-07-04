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
          <v-dialog v-model="passwordDialog" max-width="290">
            <v-card>
                <v-card-title class="headline">Element is locked</v-card-title>
                <div style="padding-left: 16px; padding-right: 16px">
                    <v-text-field label="Element password" type="password" autofocus v-model="password" @keyup.enter="openLockedElement()"></v-text-field>
                </div>
                <v-card-actions>
                    <v-spacer></v-spacer>
                    <v-btn color="green darken-1" flat="flat" @click.native="show = false">Cancel</v-btn>
                    <v-btn color="green darken-1" flat="flat" @click.native="openLockedElement()">Open</v-btn>
                </v-card-actions>
            </v-card>
          </v-dialog>
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
import BaseElementListComponentMixin from "./shared/BaseElementListComponentMixin";
import firebase from "../services/firebase.js";

export default {
  name: "ElementList",
  mixins: [BaseElementListComponentMixin],
  data() {
    return {
      menuItems: [
        {
          name: "Papierkorb",
          action: () =>
            this.$router.push({
              name: "Bin"
            })
        },
        {
          name: "Settings",
          action: () => this.$router.push({ name: "Settings" })
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
  firebase() {
    return {
      elements: firebase.getElementListRef()
    };
  }
};
</script>

<style scoped>
</style>

<style>
.v-toolbar .v-toolbar__content > .btn:first-child,
.v-toolbar .v-toolbar__extension > .btn:first-child {
  margin-left: 8px !important;
}
.v-toolbar__title {
  margin-left: 8px !important;
}
.v-speed-dial__list {
  padding-bottom: 10px;
}
.v-list {
  background: #fafafa !important;
}
.v-icon {
  display: flex !important;
}
.fabColor {
  background-color: #9e9e9e !important;
}
</style>