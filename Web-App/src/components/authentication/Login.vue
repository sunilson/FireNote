<template>
  <v-app>
    <v-toolbar app fixed>
      <!--
      <transition name="slide-x-transition" mode="out-in">
        <v-btn icon @click="$router.go(-1)">
          <v-icon>arrow_back</v-icon>
        </v-btn>
      </transition>
      -->
      <v-toolbar-title>FireNote Login</v-toolbar-title>
      <v-spacer></v-spacer>
    </v-toolbar>
    <v-content>
      <v-container fluid fill-height>
        <v-layout justify-center align-center>
          <v-flex xs12 md4 class="login-container">
            <img src="../../assets/firenote_logo.png" width="100" class="mb-5" />
            <v-text-field id="testing" name="input-1" label="Email" type="email" v-model="username"></v-text-field>
            <v-text-field id="testing" name="input-1" label="Password" type="password" v-model="password"></v-text-field>
            <v-btn color="success" big @click="login()">Login</v-btn>
            <br>
            <div class="mt-2 mb-5">
              No account yet? Click
              <a href="#">here</a> to create one
            </div>
          </v-flex>
        </v-layout>
      </v-container>
    </v-content>
  </v-app>
</template>


<script>
import firebase from "../../services/firebase.js"

export default {
  name: "Login",
  data: function() {
    return {
      username: "",
      password: ""
    }
  },
  methods: {
    login() {
      firebase.fb.auth().signInWithEmailAndPassword(this.username, this.password).then(user => {
          this.$router.replace("/")
        }).catch(error => {
          alert(error)
      })
    }
  }
}
</script>

<style>
.login-container {
  text-align: center;
}
</style>
