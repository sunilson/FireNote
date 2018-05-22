<template>
    <v-dialog v-model="show" max-width="400">
        <v-card>
            <v-card-title class="headline">Neues Element erstellen</v-card-title>
            <div class="px-3">
                <v-text-field name="input-1" label="Element Titel wählen" type="text" v-model="title"></v-text-field>
                <v-select :items="categories" v-model="category" item-text="name" item-value="id" label="Kategorie wählen" single-line></v-select>
            </div>
            <v-card-actions>
                <v-spacer></v-spacer>
                <v-btn color="red darken-1" flat @click.native="show = false">Abbrechen</v-btn>
                <v-btn color="green darken-1" flat @click.native="confirm()">Speichern</v-btn>
            </v-card-actions>
        </v-card>
    </v-dialog>
</template>

<script>
import { Constants } from "../services/constants"
import firebase from "../services/firebase.js"
import {EventBus} from "../services/EventBus.js"

export default {
    props: ["show", "noteType"],
    data() {
        return {
            title: "",
            category: "",
            categories: Constants.CATEGORIES,
        }
    },
    methods: {
        confirm() {
            firebase.createElement({
                title: this.title,
                noteType: this.noteType,
                categoryName: this.category,
                categoryId: "TODO"
            }, err => {
                console.log(err)
                if(err) EventBus.$emit("showSnackbar", err)
                else this.show = false
            })
        }
    }
}
</script>