<template>
    <v-layout row>
        <v-flex xs12 sm6 offset-sm3>
            <v-list two-line>
                <element-card v-for="(item, index) in items" :key="index">
                </element-card>
            </v-list>
        </v-flex>
    </v-layout>
</template>

<script>
import ElementCard from "./ElementCard.vue";

export default {
  data() {
    return {
      items: ["abc", "def", "ghi"]
    };
  },
  components: {
    elementCard: ElementCard
  }
};
</script>
