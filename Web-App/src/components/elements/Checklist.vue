<template>
    <v-container fluid fill-height>
        <v-layout row>
            <v-flex xs12 sm6 offset-sm3 class="note">
                <transition-group name="slide-x-transition">
                    <checklist-element v-for="(item, index) in content" :key="index" :element="item" @click.native="openElement(item)">
                    </checklist-element>
                </transition-group>
            </v-flex>
        </v-layout>
    </v-container>
</template>
<script>
import firebase from "../../services/firebase.js"
import ChecklistElement from "../ChecklistElement.vue";
import BaseElementContent from "./BaseElementContent"

export default {
    props: ["id"],
    extends: BaseElementContent,
    data() { 
        return {
            content: []
        }
    },
    firebase() {
        return {
            content: firebase.getChecklistContentRef(this.id)
        }
    },
    methods: {
        lineBreak (value) {
            console.log(value)
            if(!value) return ""
            return value.replace(/(?:\r\n|\r|\n)/g, '<br/>')
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