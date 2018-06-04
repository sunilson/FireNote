import ElementCard from "./ElementCard.vue";
import { hashPassword } from "../../services/utilities.js"
import firebase from "../../services/firebase.js"
import { EventBus } from "../../services/EventBus.js"
import PasswordDialog from './PasswordDialog.vue';
import AddElementDialog from "./AddElementDialog"

export default {
    data() {
        return {
            tooltips: false,
            fab: false,
            noteType: "note",
            selectedElement: null,
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
    methods: {
        addElement(type) {
            console.log(this.id)
            EventBus.$emit("addElement", {
                type,
                parent: this.id
            })
        },
        tryOpenElement(element) {
            this.selectedElement = null
            if (!element.locked) {
                this.$router.push({ name: 'BaseElement', params: { id: element[".key"] } })
            } else {
                this.selectedElement = element
                EventBus.$emit("showPasswordDialog")
            }
        },
        openLockedElement(password) {
            if (hashPassword(password) == this.settings.masterPassword) {
                this.$router.push({ name: 'BaseElement', params: { id: this.selectedElement[".key"] } })
            } else {
                EventBus.$emit("showSnackbar", "Wrong password!")
            }
        }
    },
    components: {
        elementCard: ElementCard,
        passwordDialog: PasswordDialog,
        addElementDialog: AddElementDialog,
    }
}
