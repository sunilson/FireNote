<template>
    <v-content>
      <v-toolbar app fixed :class="[backgroundColor]">
        <v-btn icon @click="$router.go(-1)">
              <v-icon :class="{whiteColor: color != null}">arrow_back</v-icon>
        </v-btn>
        <v-toolbar-title :class="{whiteColor: color != null}">Bin</v-toolbar-title>
        <v-spacer></v-spacer>
        <v-menu>
          <v-btn slot="activator" icon>
            <v-icon :class="{whiteColor: color != null}">more_vert</v-icon>
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
                      <element-card v-for="(item, index) in elements" :key="index" :element="item" @click.native="askRestoreElement(item)">
                      </element-card>
                  </transition-group>
              </v-flex>
          </v-layout>
      </v-container>
      <v-dialog v-model="restoreDialog" max-width="290">
          <v-card>
            <v-card-title class="headline">Restore Element</v-card-title>
            <v-card-text>Do you really want to restore this element?</v-card-text>
            <v-card-actions>
              <v-spacer></v-spacer>
              <v-btn color="red darken-1" flat @click.native="dialog = false">Cancel</v-btn>
              <v-btn color="green darken-1" flat @click.native="restoreElement()">Agree</v-btn>
            </v-card-actions>
          </v-card>
        </v-dialog>
        <v-dialog v-model="clearDialog" max-width="290">
          <v-card>
            <v-card-title class="headline">Clear bin</v-card-title>
            <v-card-text>Do you really want to clear the bin?</v-card-text>
            <v-card-actions>
              <v-spacer></v-spacer>
              <v-btn color="red darken-1" flat @click.native="dialog = false">Cancel</v-btn>
              <v-btn color="green darken-1" flat @click.native="clearBin()">Agree</v-btn>
            </v-card-actions>
          </v-card>
        </v-dialog>
    </v-content>
</template>

<script>
import BaseElementListComponentMixin from "./shared/BaseElementListComponentMixin";
import ElementCard from "./shared/ElementCard.vue";
import firebase from "../services/firebase.js";
import { EventBus } from "../services/EventBus.js";
import colormap from "../services/colormap.js";

export default {
  props: ["color"],
  name: "Bin",
  extends: BaseElementListComponentMixin,
  data() {
    return {
      restoreDialog: false,
      clearDialog: false,
      restoringElement: null,
      menuItems: [
        {
          name: "Clear",
          action: () => {
            this.clearDialog = true;
          }
        }
      ]
    };
  },
  firebase() {
    return {
      elements: firebase.getBinRef(this.$route.params.id)
    };
  },
  methods: {
    clearBin() {
      firebase.clearBin(this.$route.params.id, this.elements);
      this.clearDialog = false;
    },
    askRestoreElement(element) {
      this.restoringElement = element;
      this.restoreDialog = true;
    },
    restoreElement() {
      this.restoreDialog = false;
      firebase.restoreElement(
        this.restoringElement[".key"],
        this.$route.params.id,
        error => {
          if (error)
            EventBus.$emit("showSnackbar", "Element could not be restored!");
          else EventBus.$emit("showSnackbar", "Element restored!");
        }
      );
    }
  },
  computed: {
    backgroundColor: function() {
      if (this.color) {
        return colormap(this.color)[0];
      } else {
        return null;
      }
    }
  }
};
</script>

<style scoped>
.whiteColor {
  color: white !important;
}
</style>