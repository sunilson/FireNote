<template>
  <v-app>
    <v-toolbar app fixed>
          <v-toolbar-title>{{$route.name}}</v-toolbar-title>
          <v-spacer></v-spacer>
          <v-menu>
            <v-btn slot="activator" icon>
              <v-icon>more_vert</v-icon>
            </v-btn>
            <v-list>
              <v-list-tile v-for="(item, i) in menuItems" :key="item.name" @click="item.action()">
                <v-list-tile-title>{{ item.name }}</v-list-tile-title>
              </v-list-tile>
            </v-list>
          </v-menu>
        </v-toolbar>
    <v-content>
      <v-container fluid fill-height>
        <transition>
          <router-view></router-view>
        </transition>
      </v-container>
    </v-content>
  </v-app>
</template>

<script>
export default {
  data() {
    return {
      menuItems: [
        {
          name: "Log Out",
          action: () => {
            this.$router.push({ path: "Login" });
          }
        }
      ]
    };
  },
  name: "App"
};
</script>

<style>

</style>
