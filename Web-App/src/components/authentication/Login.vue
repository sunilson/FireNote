<template>  
  <v-flex xs12 md4 class="login-container">
    <img src="../../assets/firenote_logo.png" width="100" class="mb-5" />
    <v-text-field  name="input-1" label="Email" type="email" v-model="username" @keyup.enter="login()" autofocus></v-text-field>
    <v-text-field  name="input-1" label="Password" type="password" v-model="password" @keyup.enter="login()"></v-text-field>
    <v-btn color="success" big @click="login()" :disabled="loggingIn">Login</v-btn>
    <v-progress-linear :indeterminate="true" v-if="loggingIn"></v-progress-linear>
    <br>
    <div class="mt-2 mb-5">
      No account yet? Click <router-link :to="{ name: 'Register'}">here</router-link> to create one
    </div>
  </v-flex>
</template>


<script>
import firebase from "../../services/firebase.js"

export default {
  name: "Login",
  data: function() {
    return {
      loggingIn: false,
      username: "",
      password: ""
    }
  },
  methods: {
    login() {
      this.loggingIn = true
      firebase.fb.auth().signInWithEmailAndPassword(this.username, this.password).then(user => {
          this.$router.replace("/")
        }).catch(error => {
          this.loggingIn = false
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
