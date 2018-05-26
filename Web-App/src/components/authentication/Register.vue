<template>
    <v-flex xs12 md4 class="login-container">
        <img src="../../assets/firenote_logo.png" width="75" class="mb-3" />
        <h2 class="mb-4 small-headline">Register for a new account</h2>
        <v-text-field  name="input-1" label="Email" type="email" v-model="username" @keyup.enter="login()" autofocus></v-text-field>
        <v-text-field  name="input-1" label="Password" type="password" v-model="password" @keyup.enter="login()"></v-text-field>
        <v-text-field  name="input-1" label="Repeat password" type="password" v-model="password2" @keyup.enter="login()"></v-text-field>
        <v-btn color="success" big @click="register()" :disabled="registering">Register</v-btn>
        <v-progress-linear :indeterminate="true" v-if="registering"></v-progress-linear>
    </v-flex>
</template>

<script>
import firebase from "../../services/firebase.js"
import {EventBus} from "../../services/EventBus.js"

export default {
    name: "Register",
    data: function() {
        return {
            registering: false,
            username: "",
            password: "",
            password2: ""
        }
    },
    mounted: {
        //firebase.fb.auth().aut
    },
    methods: {
        register() {
            if(this.password === this.password2) {
                this.registering = true
                firebase.fb.auth().createUserWithEmailAndPassword(this.username, this.password)
                .catch(err => {
                    this.registering = false
                    EventBus.$emit("showSnackbar", "Error occured during registering!")
                })
            } else {
                EventBus.$emit("showSnackbar", "Passwords don't match!")
            }
        }
    }
}
</script>