<template>
    <v-card ripple color="cyan darken-2" class="white--text checklistElement" height="50px">
        <v-layout row>
            <v-flex xs10 class="center-vertical">
                <div class="ellipsis">{{element.text}}</div>
            </v-flex>
            <v-flex xs2 class="center">
                <v-checkbox></v-checkbox>
            </v-flex>
        </v-layout>
    </v-card>
</template>
<script>
    export default {
        props: ["element"]
    }
</script>

<style scoped>
    .checklistElement {
        padding: 10px;
        margin-top: 10px;
        cursor: pointer;
    }
</style>