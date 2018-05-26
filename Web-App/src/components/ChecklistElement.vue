<template>
    <v-card ripple color="yellowColor" class="checklistElement" height="50px" @click.native="$emit('checked', {id: element['.key'], value: !element['finished']})">
        <v-layout row>
            <v-flex xs10 class="center-vertical">
                <div v-bind:class="{checked: element.finished}" class="ellipsis">{{element.text}}</div>
            </v-flex>
            <v-flex xs2 class="center" style="padding-top: 25px">
                <v-checkbox v-model="element.finished" :readonly="true" :ripple="false" @click="alert('')"></v-checkbox>
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
    .yellowColor {
        background-color: #FFFFA5;
    }
    .checklistElement {
        padding: 10px;
        margin-top: 10px;
        cursor: pointer;
    }

    .checked {
        text-decoration: line-through;
    }
</style>