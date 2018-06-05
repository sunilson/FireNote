<template>
    <v-container fluid>
        <v-layout row>
              <v-flex xs12 sm6 offset-sm3>
                  <transition-group tag="v-layout" name="slide-x-transition" class="row wrap" mode="out-in">
                      <element-card v-for="(item, index) in elements" :key="index" :element="item" @click.native="tryOpenElement(item, id)">
                      </element-card>
                  </transition-group>
              </v-flex>
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
        </v-speed-dial>
    </v-container>
</template>

<script>
    import BaseElementContent from "./BaseElementContent.vue"
    import BaseElementListComponentMixin from "../shared/BaseElementListComponentMixin"
    import firebase from "../../services/firebase.js" 
    import {EventBus} from "../../services/EventBus.js"
    import ElementCard from "../shared/ElementCard.vue"

    export default {
        extends: BaseElementContent,
        mixins: [BaseElementListComponentMixin],
        data() {
            return {
            }
        },
        firebase() {
            return {
                elements: firebase.getBundleContentRef(this.id)
            }
        },
        components: {
            elementCard: ElementCard
        }
    }
</script>