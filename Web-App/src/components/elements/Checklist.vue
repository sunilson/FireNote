<template>
    <v-container fluid fill-height>
        <v-layout row>
            <v-flex xs12 sm6 offset-sm3 class="note">
                <transition-group name="slide-x-transition">
                    <checklist-element v-for="(item, index) in content" :key="index" :element="item" @checked="checkElement($event)">
                    </checklist-element>
                </transition-group>
                <v-btn color="pink" dark fixed bottom right fab style="display: flex" @click="addElementDialog = !addElementDialog">
                    <v-icon>add</v-icon>
                </v-btn>
                <v-dialog v-model="addElementDialog" max-width="290">
                    <v-card>
                    <v-card-title class="headline">Add checklist element</v-card-title>
                    <div style="padding-left: 16px; padding-right: 16px">
                        <v-text-field label="Element text" type="text" autofocus v-model="elementText" @keyup.enter="openElement()"></v-text-field>
                    </div>
                    <v-card-actions>
                        <v-spacer></v-spacer>
                        <v-btn color="green darken-1" flat="flat" @click.native="addElementDialog = false">Cancel</v-btn>
                        <v-btn color="green darken-1" flat="flat" @click.native="addElement()">Add</v-btn>
                    </v-card-actions>
                    </v-card>
                </v-dialog>
            </v-flex>
        </v-layout>
    </v-container>
</template>
<script>
import firebase from "../../services/firebase.js"
import ChecklistElement from "../ChecklistElement.vue";
import BaseElementContent from "./BaseElementContent"
import {EventBus} from "../../services/EventBus.js"

export default {
    props: ["parent"],
    extends: BaseElementContent,
    data() { 
        return {
            addElementDialog: false,
            elementText: "",
            content: []
        }
    },
    mounted() {
        EventBus.$on('clearChecklist', () => {
            firebase.deleteChecklistElements(this.content.filter(item => item.finished), this.id, this.parent)
        });
    },
    firebase() {
        return {
            content: firebase.getChecklistContentRef(this.id)
        }
    },
    methods: {
        addElement() {
            firebase.addChecklistElement(this.elementText, this.id, this.parent)
            this.elementText = ""
            this.addElementDialog = false
        },
        lineBreak (value) {
            if(!value) return ""
            return value.replace(/(?:\r\n|\r|\n)/g, '<br/>')
        },
        checkElement(event) {
            console.log(event)
            firebase.checkChecklistElement(event.value, this.id, this.parent, event.id)
        }
    },
    components: {
        checklistElement: ChecklistElement
    }
}
</script>
<style>
    .container.fill-height {
        font-size: 16px;
        display: block !important;
    }
</style>