<template>
    <v-content>
      <v-toolbar app fixed :class="[backgroundColor]">
        <v-btn icon @click="$router.go(-1)">
              <v-icon :class="{whiteColor: color != null}">arrow_back</v-icon>
        </v-btn>
        <v-toolbar-title :class="{whiteColor: color != null}">Settings</v-toolbar-title>
        <v-spacer></v-spacer>
      </v-toolbar>
      <v-container fluid>
          <v-layout row>
              <v-flex xs12>
                  <v-list two-line>
                    <v-list-tile avatar>
                        <v-list-tile-content>
                        <v-list-tile-title>Logged in as</v-list-tile-title>
                        <v-list-tile-sub-title>{{fb.auth().currentUser.email}}</v-list-tile-sub-title>
                        </v-list-tile-content>
                    </v-list-tile>
                </v-list>
                <v-divider></v-divider>
                <v-list two-line>
                    <v-list-tile v-for="item in items" :key="item.title" avatar @click="item.action()">
                        <v-list-tile-avatar>
                            <v-icon :class="[item.iconClass]">{{ item.icon }}</v-icon>
                        </v-list-tile-avatar>
                        <v-list-tile-content>
                            <v-list-tile-title>{{ item.title }}</v-list-tile-title>
                            <v-list-tile-sub-title>{{ item.subtitle }}</v-list-tile-sub-title>
                        </v-list-tile-content>
                    </v-list-tile>
                </v-list>
                <login v-if="showReauth" :reauth="true" @reauth="reauth($event)"></login>
              </v-flex>
          </v-layout>
      </v-container>
      <v-dialog v-model="passwordDialog" max-width="290">
            <v-card>
                <v-card-title class="headline">Set master password</v-card-title>
                <div style="padding-left: 16px; padding-right: 16px">
                    <v-text-field label="Current password" 
                    type="password"
                    autofocus v-model="currentPassword"
                    @keyup.enter="changeMasterPassword()"></v-text-field>
                    <v-text-field label="Repeat current password" 
                    type="password"
                     v-model="currentPasswordAgain"
                    @keyup.enter="changeMasterPassword()"></v-text-field>
                    <v-text-field label="New password" 
                    type="password"
                     v-model="newPassword"
                    @keyup.enter="changeMasterpassword()"></v-text-field>
                </div>
                <v-card-actions>
                    <v-spacer></v-spacer>
                    <v-btn color="green darken-1" flat="flat" @click.native="passwordDialog = false">Cancel</v-btn>
                    <v-btn color="green darken-1" flat="flat" @click.native="changeMasterpassword()">Save</v-btn>
                </v-card-actions>
            </v-card>
          </v-dialog>
          <v-dialog v-model="deleteAccountDialog" max-width="290">
          <v-card>
            <v-card-title class="headline">Delete account</v-card-title>
            <v-card-text>Do you really want to delete your account?</v-card-text>
            <v-card-actions>
              <v-spacer></v-spacer>
              <v-btn color="red darken-1" flat @click.native="deleteAccountDialog = false">Cancel</v-btn>
              <v-btn color="green darken-1" flat @click.native="deleteAccount()">Agree</v-btn>
            </v-card-actions>
          </v-card>
        </v-dialog>
    </v-content>
</template>

<script>
import firebase from "../services/firebase.js";
import { EventBus } from "../services/EventBus.js";
import Login from "./authentication/Login.vue"; 

export default {
  data() {
    return {
      showReauth: false,
      passwordDialog: false,
      deleteAccountDialog: false,
      currentPassword: "",
      currentPasswordAgain: "",
      newPassword: "",
      items: [
        {
          icon: "lock",
          iconClass: "grey lighten-1 white--text",
          title: "Master Password",
          subtitle: "The password used for locking your notes",
          action: () => {
            this.currentPassword = "";
            this.currentPasswordAgain = "";
            this.newPassword = "";
            this.passwordDialog = true;
          }
        },
        {
          icon: "delete",
          iconClass: "grey lighten-1 white--text",
          title: "Delete user account",
          subtitle: "Permanently delete your user account and all contents",
          action: () => {
            this.showReauth = true
          }
        },
        {
          icon: "person",
          iconClass: "grey lighten-1 white--text",
          title: "Reset password",
          subtitle: "Change the password used to login",
          action: () => {
            this.showReauth = true
          }
        },
        {
          icon: "info",
          iconClass: "grey lighten-1 white--text",
          title: "About FireNote",
          subtitle: "Jan 28, 2014"
        }
      ],
      items2: [
        {
          icon: "assignment",
          iconClass: "blue white--text",
          title: "Vacation itinerary",
          subtitle: "Jan 20, 2014"
        },
        {
          icon: "call_to_action",
          iconClass: "amber white--text",
          title: "Kitchen remodel",
          subtitle: "Jan 10, 2014"
        }
      ],
      fb: firebase.fb
    };
  },
  components: {
    login: Login
  },
  methods: {
    reauth(data) {
      if(data["error"]) alert("error")
    },
    deleteAccount() {
      firebase
        .deleteUserAccount()
        .then(() => {})
        .catch(err =>
          EventBus.$emit("showSnackbar", "Could not delete account!")
        );
    },
    changeMasterpassword() {
      if (
        this.currentPassword.length > 0 &&
        this.newPassword.length > 0 &&
        this.currentPassword == this.currentPasswordAgain
      ) {
        firebase
          .changeMasterPassword(this.newPassword, this.currentPassword)
          .then(() => {
            this.passwordDialog = false;
            EventBus.$emit("showSnackbar", "Changed password!");
          })
          .catch(err => {
            console.log(err);
            EventBus.$emit("showSnackbar", "Could not change your password!");
          });
      } else {
        EventBus.$emit("showSnackbar", "Passwords are empty or don't match!");
      }
    }
  }
};
</script>
