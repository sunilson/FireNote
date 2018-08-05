<template>  
  <v-flex xs12 md4 class="login-container">
    <img src="../../assets/firenote_logo.png" v-if="!reauth" width="75" class="mb-3" />
    <h2 class="mb-4 small-headline" v-if="!reauth">Login to FireNote</h2>
    <h2 class="mb-4 small-headline" v-if="reauth">Login again to do this</h2>
    <v-text-field  name="input-1" label="Email" type="email" v-model="username" @keyup.enter="login()" autofocus></v-text-field>
    <v-text-field  name="input-1" label="Password" type="password" v-model="password" @keyup.enter="login()"></v-text-field>
    <v-btn color="success" big @click="login()" :disabled="loggingIn">Login</v-btn>
    <v-progress-linear :indeterminate="true" v-if="loggingIn"></v-progress-linear>
    <br>
    <div class="mt-2 mb-5"  v-if="!reauth">
      No account yet? Click <router-link :to="{ name: 'Register'}">here</router-link> to create one
    </div>
  </v-flex>
</template>


<script>
import firebase from "../../services/firebase.js";
import { EventBus } from "../../services/EventBus.js";

export default {
  props: ["reauth"],
  name: "Login",
  data: function() {
    return {
      loggingIn: false,
      username: "",
      password: ""
    };
  },
  methods: {
    login() {
      this.loggingIn = true;
      if (this.reauth) {
        firebase.fb
          .auth()
          .currentUser.reauthenticateAndRetrieveDataWithCredential(
            firebase.fb.auth.EmailAuthProvider.credential(
              this.username,
              this.password
            )
          )
          .then(() => {
            this.loggingIn = false;
            this.$emit("reauth", {});
          })
          .catch(err => {
            this.loggingIn = false;
            this.$emit("reauth", {
              error: err
            });
          });
      } else {
        firebase.fb
          .auth()
          .signInWithEmailAndPassword(this.username, this.password)
          .catch(error => {
            this.loggingIn = false;
            if (error)
              EventBus.$emit("showSnackbar", "Error occured during login!");
          });
      }
    }
  }
};
</script>

<style>
.login-container {
  text-align: center;
}
</style>
