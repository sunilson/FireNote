<template>
    <div class="elementCard">
        <v-card color="cyan darken-2" class="white--text">
            <v-container fluid grid-list-lg>
                <v-layout row>
                    <v-flex xs7>
                        <div>
                        <div class="headline">Supermodel</div>
                        <div>Foster the People</div>
                        </div>
                    </v-flex>
                    <v-flex xs5>
                        <v-card-media src="https://vuetifyjs.com/static/doc-images/cards/foster.jpg" height="125px" contain></v-card-media>
                    </v-flex>
                </v-layout>
            </v-container>
        </v-card>
    </div>
</template>

<script>
export default {
  props: ["element"]
};
</script>

<style>
.elementCard {
  margin-top: 15px;
}
</style>

