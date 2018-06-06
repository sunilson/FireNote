<template>

</template>

<script>
import { EventBus } from "../../services/EventBus.js";

export default {
  data() {
    return {
      password: ""
    };
  },
  methods: {
    openElement() {
      this.$emit("openLockedElement", this.password);
      this.password = "";
    }
  }
};
</script>