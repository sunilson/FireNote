import ElementCard from "./ElementCard.vue";
import {
  hashPassword
} from "../../services/utilities.js"
import firebase from "../../services/firebase.js"
import {
  EventBus
} from "../../services/EventBus.js"
import PasswordDialog from './PasswordDialog.vue';
import AddElementDialog from "./AddElementDialog"

export default {
  data() {
    return {
      tooltips: false,
      fab: false,
      noteType: "note",
      selectedElement: null,
      selectedParent: null,
      passwordDialog: false,
      password: ""
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
      settings: firebase.getSettings()
    }
  },
  methods: {
    addElement(type) {
      EventBus.$emit("addElement", {
        type,
        parent: this.id
      })
    },
    tryOpenElement(element, parent) {
      this.selectedElement = null
      if (!element.locked) {
        this.$router.push({
          name: (parent) ? 'BaseBundleElement' : 'BaseElement',
          params: (parent) ? {
            parent,
            id: element[".key"]
          } : {
            id: element[".key"]
          }
        })
      } else {
        this.selectedParent = parent
        this.selectedElement = element
        this.passwordDialog = true
        this.password = ""
      }
    },
    openLockedElement(password) {
      this.passwordDialog = false
      if (hashPassword(this.password) == this.settings.masterPassword) {
        this.$router.push({
          name: (this.selectedParent) ? 'BaseBundleElement' : 'BaseElement',
          params: (this.selectedParent) ? {
            parent: this.selectedParent,
            id: this.selectedElement[".key"]
          } : {
            id: this.selectedElement[".key"]
          }
        })
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
