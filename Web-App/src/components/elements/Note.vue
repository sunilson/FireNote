<template>
    <v-container fluid fill-height>
        <v-layout row>
            <v-flex xs12 sm6 offset-sm3 class="note">
            {{color}}
                <transition name="fade-transition" >
                    <textarea v-if="content.text && editMode" v-model="content.text" class="noteText"></textarea>
                    <textarea v-else-if="!content.text && editMode" v-model="emptyContent" class="noteText"></textarea>
                </transition>
                <transition name="fade-transition" >
                    <p v-if="content.text && !editMode" v-html="lineBreak(content.text)" class="noteText"></p>
                    <p v-else-if="!content.text && !editMode" v-html="lineBreak(emptyContent)" class="noteText"></p>
                </transition>
                <v-btn color="pink" :class="[color]" dark fixed bottom right fab style="display: flex" @click="toggleEdit()">
                    <v-icon v-if="editMode">check</v-icon>
                    <v-icon v-else>edit</v-icon>
                </v-btn>
            </v-flex>
        </v-layout>
    </v-container>
</template>
<script>
import firebase from "../../services/firebase.js"
import {EventBus} from "../../services/EventBus.js"
import BaseElementContent from "./BaseElementContent"

export default {
    extends: BaseElementContent,
    data() { 
        return {
            editMode: false,
            content: null,
            emptyContent: "\n\n\n"
        }
    },
    firebase() {
        return {
            content: firebase.getNoteContentRef(this.id, this.parent)
        }
    },
    methods: {
        lineBreak (value) {
            if(!value) return ""
            return value.replace(/(?:\r\n|\r|\n)/g, '<br/>')
        },
        toggleEdit() {
            if(this.editMode) {
                EventBus.$emit("showSnackbar", "Saved Note!")
                firebase.saveNote((this.content.text) ? this.content.text : this.emptyContent, this.id, this.parent)
            }
            this.editMode = !this.editMode
        },
        willLeave() {
            if(this.editMode) this.toggleEdit()
        }
    }
}
</script>
<style scoped>
  .container {
    padding: 0 !important;
    background: #FFFFA5;
  }
    .note {
        background: #FFFFA5;
        padding: 10px;
    }
    .container.fill-height {
        font-size: 16px;
        display: block !important;
    }
    .noteText {
        width: 100%;
        height: 100%;
        font-size: 20px;
        border: none;
        border: none;
        overflow: auto;
        outline: none;
        -webkit-box-shadow: none;
        -moz-box-shadow: none;
        box-shadow: none;
        background-attachment: local;
        padding-top: 5px;
        background-image:
            linear-gradient(to right, transparent 10px, transparent 10px),
            linear-gradient(to left, transparent 10px, transparent 10px),
            repeating-linear-gradient(transparent, transparent 39px, rgba(0, 0, 0, 0.2) 39px, rgba(0, 0, 0, 0.2) 40px, transparent 40px);
        line-height: 40px;
        resize: none;
    }
</style>