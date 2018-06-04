<template>
    <v-dialog v-model="show" max-width="400">
        <v-card>
            <v-card-title class="headline">Neues Element erstellen</v-card-title>
            <div class="px-3">
                <v-text-field name="input-1" label="Element Titel wählen" type="text" v-model="title"></v-text-field>
                <v-select :items="categories" v-model="category" item-text="name" item-value="id" label="Kategorie wählen"></v-select >
                <v-select :items="colors" v-model="color" item-value="id" label="Farbe wählen" class="colorList">
                    <template slot="selection" slot-scope="data">
                        <div :class="[data.item.value]" style="width: 100%; height: 100%; margin-top: 10px"></div>
                    </template>
                    <template slot="item" slot-scope="data">
                        <div :class="[data.item.value]" style="width: 100%; height: 100%"></div>
                    </template>
                    </v-select>
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
import { Constants } from "../../services/constants"
import firebase from "../../services/firebase.js"
import {EventBus} from "../../services/EventBus.js"

export default {
    data() {
        return {
            show: false,
            noteType: "note",
            title: "",
            category: "general",
            color: -769226,
            categories: Constants.CATEGORIES,
            colors: Constants.COLORS,
            parent: null
        }
    },
    mounted () {
        EventBus.$on('addElement', (data) => {
            this.noteType = data.type
            if(data["parent"]) this.parent = data.parent
            this.show = true
            this.title = ""
            this.category = "general"
            this.color = -769226
        });
    },
    methods: {
        confirm() {
            firebase.createElement({
                title: this.title,
                noteType: this.noteType,
                categoryName: Constants.CATEGORIES.find(category => category.id == this.category)["name"],
                color: this.color,
                categoryID: this.category,
                locked: false,
                timeStamp: new Date().getTime()
            }, err => {
                console.log(err)
                if(err) EventBus.$emit("showSnackbar", err)
                else this.show = false
            },
            this.parent)
        }
    }
}
</script>

<style>
.colorList .icon {
    display: none !important;
}

.colorList .input-group__details:after {
    display: none !important;
    height: 0 !important;
}

.colorList .input-group__input {
    min-height: 40px;
}
</style>