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
                    <v-list-tile v-for="item in items" :key="item.title" avatar>
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
      </v-container>
    </v-content>
</template>

<script>
import firebase from "../services/firebase.js";

export default {
  data() {
    return {
      items: [
        {
          icon: "lock",
          iconClass: "grey lighten-1 white--text",
          title: "Master Password",
          subtitle: "The password used for locking your notes"
        },
        {
          icon: "delete",
          iconClass: "grey lighten-1 white--text",
          title: "Delete user account",
          subtitle: "Permanently delete your user account and all contents"
        },
        {
          icon: "person",
          iconClass: "grey lighten-1 white--text",
          title: "Reset password",
          subtitle: "Change the password used to login"
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
  }
};
</script>
