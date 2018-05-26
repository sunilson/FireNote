<template>
    <v-app>
        <v-snackbar
        :timeout="4000"
        bottom
        v-model="snackbar">
        {{ snackbarText }}
        <v-btn flat color="pink" @click.native="snackbar = false">Close</v-btn>
        </v-snackbar>
        <router-view></router-view>
    </v-app>
</template>

<script>
import {EventBus} from "./services/EventBus.js"
import firebase from "./services/firebase.js"

export default {
  data() {
    return {
      snackbarText: "",
      snackbar: false
    };
  },
  name: "App",
  mounted() {
    firebase.fb.auth().onAuthStateChanged(user => {
        if(user) {
            this.$router.replace("/")
        } else {
            this.$router.replace("/auth")
        }
    })
    EventBus.$on('showSnackbar', message => {
        this.snackbarText = message
        this.snackbar = true
    });
  },
};
</script>

<style>

.small-headline {
    font-size: 18px;
    font-weight: normal;
}

.ellipsis {
  white-space: nowrap; 
    overflow: hidden;
    text-overflow: ellipsis;
}

.center-vertical {
    display: flex;
    align-items: center;
}

.center {
  display: flex;
  align-items: center;
  justify-content: center;
}

/* Colors */

.noteColor1 {
    background:#F44336 !important;
}

.noteColor2 {
    background:#E91E63 !important;
}

.noteColor3 {
    background:#9C27B0 !important;
}

.noteColor4 {
    background:#2196F3 !important;
}

.noteColor5 {
    background:#4CAF50 !important;
}

.noteColor6 {
    background:#009688 !important;
}

.noteColor7 {
    background:#FFC107 !important;
}

.noteColor8 {
    background:#FF5722 !important;
}

.noteColor9 {
    background:#795548 !important;
}

.noteColorTransparent1 {
    background: rgba(244, 67, 54, 0.6) !important;
}

.noteColorTransparent2 {
    background: rgba(233, 30, 99, 0.6) !important;
}

.noteColorTransparent3 {
    background: rgba(156, 39, 176, 0.6) !important;
}

.noteColorTransparent4 {
    background: rgba(33, 150, 243, 0.6) !important;
}

.noteColorTransparent5 {
    background: rgba(76, 175, 80, 0.6) !important;
}

.noteColorTransparent6 {
    background: rgba(0, 150, 136, 0.6) !important;
}

.noteColorTransparent7 {
    background: rgba(255, 193, 7, 0.6) !important;
}

.noteColorTransparent8 {
    background: rgba(255, 87, 34, 0.6) !important;
}

.noteColorTransparent9 {
    background: rgba(121, 85, 72, 0.6) !important;
}
</style>
