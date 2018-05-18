<template>
    <v-flex xs12 sm6 class="elementCard">
        <v-card ripple class="white--text" height="60px" :class="[backgroundColor]">
            <v-layout row>
                <v-flex xs9 style="height: 60px; display: flex; align-items: center">
                    <div class="elementSide left" :class="[sideBackgroundColor]">
                        <v-icon v-if="element.noteType == 'note'">event_note</v-icon>
                        <v-icon v-if="element.noteType == 'checklist'">done_all</v-icon>
                        <v-icon v-if="element.noteType == 'bundle'">list</v-icon>
                    </div>
                    <div style="padding-left: 10px; padding-right: 10px" class="ellipsis">
                        <div class="header ellipsis">{{element.title}}</div>
                        <div class="subHeader ellipsis">{{element.categoryName}}</div>
                    </div>
                </v-flex>
                <v-flex xs3 style="height: 60px; display: flex; align-items: center; font-size: 12px" class="ellipsis">
                    {{element.creationDate | parseDate}}
                </v-flex>
            </v-layout>
        </v-card>
    </v-flex>
</template>

<script>
import colormap from "../services/colormap.js"

export default {
  props: ["element"],
    computed: {
        backgroundColor: function() {
            return colormap(this.element.color)[1]
        },
        sideBackgroundColor: function() {
            return colormap(this.element.color)[0]
        }
    }
};
</script>

<style scoped>
.header {
    font-size:16px
}

.subHeader {
    font-size:12px
}

.elementSide {
    height: 100%; 
    min-width: 40px; 
    display: flex; 
    justify-content: center; 
    align-items:center
}

.icon {
    color: white;
}

.elementCard {
  margin-top: 15px;
  padding-left: 5px;
  padding-right: 5px;
  cursor: pointer;
}
</style>

