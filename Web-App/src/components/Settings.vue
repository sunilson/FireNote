<template>
    <v-content>
      <v-toolbar app fixed :class="[backgroundColor]">
        <v-btn icon @click="goBack()">
              <v-icon :class="{whiteColor: color != null}">arrow_back</v-icon>
        </v-btn>
        <v-toolbar-title :class="{whiteColor: color != null}">Settings</v-toolbar-title>
        <v-spacer></v-spacer>
      </v-toolbar>
      <v-container fluid fill-height>
          <transition name="slide-x-transition" mode="out-in">
            <v-layout row  v-if="!showReauth" key="settings">
                <v-flex sm6 offset-sm3 xs12>
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
                </v-flex>
            </v-layout>
            <v-layout v-else justify-center align-center key="login">
              <login :reauth="true" @reauth="reauth($event)"></login>
            </v-layout>
          </transition>
      </v-container>
      <v-dialog v-model="resetMasterPasswordDialog" max-width="400">
            <v-card>
                <v-card-title class="headline">Set master password</v-card-title>
                <div style="padding-left: 16px; padding-right: 16px">
                    <v-text-field label="New password" 
                    type="password"
                     v-model="newMasterPassword"
                    @keyup.enter="resetMasterPassword()"></v-text-field>
                    <v-text-field label="Repeat New password" 
                    type="password"
                     v-model="newMasterPasswordAgain"
                    @keyup.enter="resetMasterPassword()"></v-text-field>
                </div>
                <v-card-actions>
                    <v-spacer></v-spacer>
                    <v-btn color="green darken-1" flat="flat" @click.native="resetMasterPasswordDialog = false">Cancel</v-btn>
                    <v-btn color="green darken-1" flat="flat" @click.native="resetMasterPassword()">Save</v-btn>
                </v-card-actions>
            </v-card>
          </v-dialog>
      <v-dialog v-model="masterPasswordDialog" max-width="400">
            <v-card>
                <v-card-title class="headline">Set master password</v-card-title>
                <div style="padding-left: 16px; padding-right: 16px">
                    <v-text-field label="Current password" 
                    type="password"
                    autofocus v-model="currentMasterPassword"
                    @keyup.enter="changeMasterPassword()"></v-text-field>
                    <v-text-field label="New password" 
                    type="password"
                     v-model="newMasterPassword"
                    @keyup.enter="changeMasterPassword()"></v-text-field>
                    <v-text-field label="Repeat new password" 
                    type="password"
                     v-model="newMasterPasswordAgain"
                    @keyup.enter="changeMasterpassword()"></v-text-field>
                </div>
                <v-card-actions>
                    <v-spacer></v-spacer>
                    <v-btn color="green darken-1" flat="flat" @click.native="masterPasswordDialog = false">Cancel</v-btn>
                    <v-btn color="green darken-1" flat="flat" @click.native="changeMasterpassword()">Save</v-btn>
                </v-card-actions>
            </v-card>
          </v-dialog>
      <v-dialog v-model="loginPasswordDialog" max-width="400">
            <v-card>
                <v-card-title class="headline">Set login password</v-card-title>
                <div style="padding-left: 16px; padding-right: 16px">
                    <v-text-field label="New login password" 
                    type="password"
                    autofocus 
                    v-model="newLoginPassword"></v-text-field>
                    <v-text-field label="Repeat new login password" 
                    type="password"
                    v-model="newLoginPasswordAgain"
                    @keyup.enter="changeLoginPassword()"></v-text-field>
                </div>
                <v-card-actions>
                    <v-spacer></v-spacer>
                    <v-btn color="green darken-1" flat="flat" @click.native="loginPasswordDialog = false">Cancel</v-btn>
                    <v-btn color="green darken-1" flat="flat" @click.native="changeLoginPassword()">Change</v-btn>
                </v-card-actions>
            </v-card>
          </v-dialog>
          <v-dialog v-model="deleteAccountDialog" max-width="400">
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
          <v-dialog v-model="infoDialog" max-width="400">
            <v-card>
              <v-card-title class="headline">About FireNote</v-card-title>
              <v-card-text>Created as a semester project for FH Hagenberg in Austria. If you have feedback or a bug report  for us, please contact support@firenote.at</v-card-text>
              <v-card-actions>
                <v-spacer></v-spacer>
                <v-btn color="darken-1" flat @click.native="infoDialog = false">Close</v-btn>
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
      masterPasswordDialog: false,
      deleteAccountDialog: false,
      infoDialog: false,
      deletingAccount: false,
      resettingPassword: false,
      resetMasterPasswordDialog: false,
      loginPasswordDialog: false,
      currentMasterPassword: "",
      newMasterPassword: "",
      newMasterPasswordAgain: "",
      newLoginPassword: "",
      newLoginPasswordAgain: "",
      items: [
        {
          icon: "lock",
          iconClass: "grey lighten-1 white--text",
          title: "Master Password",
          subtitle: "The password used for locking your notes",
          action: () => {
            this.currentMasterPassword = "";
            this.newMasterPasswordAgain = "";
            this.newMasterPassword = "";
            firebase
              .checkMasterPassword()
              .then(() => {
                this.masterPasswordDialog = true;
              })
              .catch(() => {
                this.resetMasterPasswordDialog = true;
              });
          }
        },
        {
          icon: "delete",
          iconClass: "grey lighten-1 white--text",
          title: "Delete user account",
          subtitle: "Permanently delete your user account and all contents",
          action: () => {
            this.showReauth = true;
            this.deletingAccount = true;
          }
        },
        {
          icon: "person",
          iconClass: "grey lighten-1 white--text",
          title: "Reset password",
          subtitle: "Change the password used to login",
          action: () => {
            this.showReauth = true;
            this.resettingPassword = true;
          }
        },
        {
          icon: "info",
          iconClass: "grey lighten-1 white--text",
          title: "About FireNote",
          subtitle: "Learn more about FireNote",
          action: () => {
            this.infoDialog = true;
          }
        }
      ],
      fb: firebase.fb
    };
  },
  components: {
    login: Login
  },
  methods: {
    goBack() {
      if (this.showReauth) {
        this.showReauth = false;
      } else {
        this.$router.go(-1);
      }
    },
    reauth(data) {
      if (data["error"]) alert("error");
      else {
        this.showReauth = false;
        if (this.deletingAccount) {
          this.deleteAccountDialog = true;
          this.deletingAccount = false;
        } else if (this.resettingPassword) {
          this.newLoginPassword = "";
          this.newLoginPasswordAgain = "";
          this.resettingPassword = false;
          this.loginPasswordDialog = true;
        }
      }
    },
    deleteAccount() {
      firebase
        .deleteUserAccount()
        .then(() => {
          this.deleteAccountDialog = false;
          EventBus.$emit("showSnackbar", "Deleted your account!");
          firebase.fb.auth().signOut();
        })
        .catch(err =>
          EventBus.$emit("showSnackbar", "Could not delete account!")
        );
    },
    resetMasterPassword() {
      if (
        this.newMasterPassword.length > 0 &&
        this.newMasterPassword == this.newMasterPasswordAgain
      ) {
        firebase
          .changeMasterPassword(this.newMasterPassword)
          .then(() => {
            this.resetMasterPasswordDialog = false;
            EventBus.$emit("showSnackbar", "Changed password!");
          })
          .catch(err => {
            console.log(err);
            EventBus.$emit("showSnackbar", "Could not change your password!");
          });
      } else {
        EventBus.$emit("showSnackbar", "Passwords are empty or don't match!");
      }
    },
    changeMasterpassword() {
      if (
        this.currentMasterPassword.length > 0 &&
        this.newMasterPassword.length > 0 &&
        this.newMasterPassword == this.newMasterPasswordAgain
      ) {
        firebase
          .changeMasterPassword(
            this.newMasterPassword,
            this.currentMasterPassword
          )
          .then(() => {
            this.masterPasswordDialog = false;
            EventBus.$emit("showSnackbar", "Changed password!");
          })
          .catch(err => {
            console.log(err);
            EventBus.$emit("showSnackbar", "Could not change your password!");
          });
      } else {
        EventBus.$emit("showSnackbar", "Passwords are empty or don't match!");
      }
    },
    changeLoginPassword() {
      if (
        this.newLoginPassword.length > 0 &&
        this.newLoginPassword == this.newLoginPasswordAgain
      ) {
        firebase
          .changeLoginPassword(this.newLoginPassword)
          .then(() => {
            this.loginPasswordDialog = false;
            EventBus.$emit("showSnackbar", "Changed password!");
          })
          .catch(err => {
            EventBus.$emit("showSnackbar", "Could not change your password!");
          });
      } else {
        EventBus.$emit("showSnackbar", "Passwords are empty or don't match!");
      }
    }
  }
};
</script>

<style scoped>
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.5s;
}
.fade-enter, .fade-leave-to /* .fade-leave-active in <2.1.8 */ {
  opacity: 0;
}
</style>

