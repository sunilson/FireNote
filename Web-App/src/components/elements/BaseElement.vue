<template>
      <v-content>
        <v-toolbar app fixed :class="[backgroundColor]">
          <transition name="slide-x-transition" mode="out-in">
            <v-btn icon @click="canLeave()">
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
          <v-btn icon @click="openEditDialog()">
            <v-icon>settings</v-icon>
          </v-btn>
          <v-menu>
            <v-btn slot="activator" icon>
              <v-icon>more_vert</v-icon>
            </v-btn>
            <v-list>
              <v-list-tile @click="confirmDeletion()">
                <v-list-tile-title>Delete</v-list-tile-title>
              </v-list-tile>
              <v-list-tile v-if="element.noteType == 'checklist'" @click="clearChecklist()">
                <v-list-tile-title>Clean-Up</v-list-tile-title>
              </v-list-tile>
              <v-list-tile v-if="element.noteType == 'bundle'" @click="$router.push({name: 'BundleBin', id: $route.params.id, params: {color: element.color}})">
                <v-list-tile-title>Bin</v-list-tile-title>
              </v-list-tile>
            </v-list>
          </v-menu>
        </v-toolbar>
        <note v-if="element.noteType == 'note'" :id="$route.params.id" :parent="$route.params.parent" :color="backgroundColor" ref="noteComponent"></note>
        <checklist v-else-if="element.noteType == 'checklist'" :id="$route.params.id" :parent="$route.params.parent" :color="backgroundColor" ref="checklistComponent"></checklist>
        <bundle v-else-if="element.noteType == 'bundle'" :id="$route.params.id" :color="backgroundColor" ref="bundleComponent"></bundle>
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
      </v-content>
</template>

<script>
import firebase from "../../services/firebase.js";
import Checklist from "./Checklist";
import Bundle from "./Bundle";
import Note from "./Note";
import { EventBus } from "../../services/EventBus.js";
import colormap from "../../services/colormap.js";

export default {
  name: "BaseElement",
  data() {
    return {
      dialog: false,
      element: {}
    };
  },
  firebase() {
    return {
      element: firebase.getElementRef(
        this.$route.params.id,
        this.$route.params.parent
      )
    };
  },
  components: {
    note: Note,
    checklist: Checklist,
    bundle: Bundle
  },
  methods: {
    openEditDialog() {
      EventBus.$emit("addElement", {
        elementID: this.$route.params.id,
        parent: this.$route.params.parent,
        title: this.element["title"],
        color: this.element["color"],
        categoryID: this.element["categoryID"]
      });
    },
    toggleLock() {
      firebase.lockElement(
        !this.element.locked,
        this.element[".key"],
        this.$route.params.parent
      );
    },
    confirmDeletion() {
      this.dialog = true;
    },
    deleteElement() {
      this.dialog = false;
      firebase
        .deleteElement(
          this.element[".key"],
          this.$route.params.parent,
          this.element
        )
        .then(() => {
          this.$router.go(-1);
          EventBus.$emit("showSnackbar", "Element moved to bin!");
        })
        .catch(err => EventBus.$emit("showSnackbar", err.message));
    },
    canLeave() {
      for (let key in this.$refs) {
        this.$refs[key].willLeave();
      }

      this.$router.go(-1);
    },
    clearChecklist() {
      EventBus.$emit("clearChecklist");
    }
  },
  computed: {
    backgroundColor: function() {
      return colormap(this.element.color)[0];
    }
  }
};
</script>

<style scoped>
.v-btn.v-btn--icon {
  color: white;
}

.v-toolbar__title {
  color: white;
}
</style>
