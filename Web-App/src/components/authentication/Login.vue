<template>
  <v-layout justify-center align-center>
      <v-flex xs12 md4 class="login-container">
        <img src="../../assets/firenote_logo.png" width="100" class="mb-5"/>
        <v-text-field id="testing" name="input-1" label="Email" type="email" ></v-text-field>
        <v-text-field id="testing" name="input-1" label="Password" type="password"></v-text-field>
        <v-btn color="success" big>Login</v-btn>
        <br>
        <div class="mt-2 mb-5">
            No account yet? Click
            <a href="#">here</a> to create one
        </div>
    </v-flex>
  </v-layout>
</template>


<script>
export default {
  name: "Login",
  data: () => {
    {
    }
  },
  methods: {}
};
</script>

<style>
.login-container {
  text-align: center;
}
</style>
