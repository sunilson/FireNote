<template>
    <v-container fluid fill-height>
        <v-layout row>
            <div v-for="(item, index) in content" :key="index"> {{item}}</div>
        </v-layout>
        <v-speed-dial fixed bottom right v-model="fab">
            <v-btn slot="activator" v-model="fab" color="fabColor" dark fab>
                <v-icon>add</v-icon>
                <v-icon>close</v-icon>
            </v-btn>
            <v-tooltip left :value="tooltips">
            <v-btn fab dark small color="green" slot="activator" @click="addElement('checklist')">
                <v-icon>done_all</v-icon>
            </v-btn>
            <span>Checklist</span>
            </v-tooltip>   
            <v-tooltip left :value="tooltips">
            <v-btn fab dark small color="green" slot="activator" @click="addElement('note')">
                <v-icon>event_note</v-icon>
            </v-btn>
            <span>Note</span>
            </v-tooltip>   
            <v-tooltip left :value="tooltips">
            <v-btn fab dark small color="green" slot="activator" @click="addElement('bundle')">
                <v-icon>list</v-icon>
            </v-btn>
            <span>Bundle</span>
            </v-tooltip>    
        </v-speed-dial>
    </v-container>
</template>

<script>
    import BaseElementContent from "./BaseElementContent.vue"
    import firebase from "../../services/firebase.js" 
    import {EventBus} from "../../services/EventBus.js"

    export default {
        extends: BaseElementContent,
        data() {
            return {
                fab: false,
                tooltips: false,
            }
        },
        watch: {
            fab(val) {
                this.tooltips = false
                val && setTimeout(() => {
                    this.tooltips = true
                }, 250)
            }
        },
        firebase() {
            return {
                content: firebase.getBundleContentRef(this.id)
            }
        },
        methods: {
            addElement(type) {
                EventBus.$emit("addElement", {type, id: this.id})
            },
        }
    }
</script>