<template>
    <v-container fluid fill-height>
        <v-layout row>
            <v-flex xs12 sm6 offset-sm3 class="note">
                <transition name="fade-transition" >
                    <textarea v-if="content.text" :value="content.text" class="noteText"></textarea>
                    <div v-if="content.text" v-html="lineBreak(content.text)"></div>
                </transition>
            </v-flex>
        </v-layout>
    </v-container>
</template>
<script>
import firebase from "../../services/firebase.js"

export default {
    props: ["id"],
    data() { 
        return {
            content: null
        }
    },
    firebase() {
        return {
            content: firebase.getNoteContentRef(this.id, true)
        }
    },
    methods: {
        lineBreak (value) {
            console.log(value)
            if(!value) return ""
            return value.replace(/(?:\r\n|\r|\n)/g, '<br/>')
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