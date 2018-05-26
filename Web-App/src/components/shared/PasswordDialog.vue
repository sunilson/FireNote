<template>
<v-dialog v-model="show" max-width="290">
    <v-card>
        <v-card-title class="headline">Element is locked</v-card-title>
        <div style="padding-left: 16px; padding-right: 16px">
            <v-text-field label="Element password" type="password" autofocus v-model="password" @keyup.enter="openElement()"></v-text-field>
        </div>
        <v-card-actions>
            <v-spacer></v-spacer>
            <v-btn color="green darken-1" flat="flat" @click.native="show = false">Cancel</v-btn>
            <v-btn color="green darken-1" flat="flat" @click.native="openElement()">Open</v-btn>
        </v-card-actions>
    </v-card>
</v-dialog>
</template>

<script>

import {EventBus} from '../../services/EventBus.js'

export default {
    data() {
        return {
            show: false,
            password: "",
        }
    },
    mounted() {
        EventBus.$on('showPasswordDialog', () => {
            this.show = true
        });
    },
    methods: {
        openElement() {
            this.show = false
            this.$emit("openLockedElement", this.password)
            this.password = ""
        }
    }
}
</script>