import fb from "firebase"
import config from "../config"
import {
  EventBus
} from "./EventBus"
import {
  hashPassword
} from "./utilities.js"

const firebaseApp = fb.initializeApp(config.firebaseConfig)

function convertFirebaseToElement(fb) {
  return {
    title: fb.title,
    noteType: fb.noteType,
    categoryName: fb.categoryName,
    color: fb.color,
    categoryID: fb.categoryID,
    locked: fb.locked,
    timeStamp: fb.timeStamp
  }
}

export default {
  fb,
  getElementRef: function (id, parent) {
    return {
      source: fb.database().ref(`users/${fb.auth().currentUser.uid}/elements/${(parent) ? "bundles/" + parent : "main" }/${id}`),
      asObject: true
    }
  },

  getNoteContentRef: function (id, parent) {
    return {
      source: (parent) ? fb.database().ref(`users/${fb.auth().currentUser.uid}/contents/${parent}/${id}`) : fb.database().ref(`users/${fb.auth().currentUser.uid}/contents/${id}`),
      asObject: true
    }
  },

  getChecklistContentRef: function (id) {
    return fb.database().ref(`users/${fb.auth().currentUser.uid}/contents/${id}/elements`)
  },

  getBundleContentRef: function (id) {
    return fb.database().ref(`users/${fb.auth().currentUser.uid}/elements/bundles/${id}`)
  },

  /**
   * 
   * @param {*} value 
   * @param {*} id 
   * @param {*} parent 
   * @param {*} elementId 
   */
  checkChecklistElement: function (value, id, parent, elementId) {
    return fb.database().ref(`users/${fb.auth().currentUser.uid}/contents/${id}/elements/${elementId}/finished`).set(value)
  },

  /**
   * 
   * @param {*} value 
   * @param {*} id 
   * @param {*} parent 
   */
  addChecklistElement: function (value, id, parent) {
    return fb.database().ref(`users/${fb.auth().currentUser.uid}/contents/${id}/elements`).push().set({
      text: value,
      finished: false
    })
  },

  deleteChecklistElements: function (values, id, parent) {
    values.forEach(element => {
      fb.database().ref(`users/${fb.auth().currentUser.uid}/contents/${id}/elements/${element['.key']}`).remove()
    })
  },

  getElementListRef: function () {
    return fb.database().ref(`users/${fb.auth().currentUser.uid}/elements/main`)
  },

  getBinRef: function (parent) {
    console.log(`users/${fb.auth().currentUser.uid}/bin/${(parent) ? "bundles/" + parent : "main"}`)
    return fb.database().ref(`users/${fb.auth().currentUser.uid}/bin/${(parent) ? "bundles/" + parent : "main"}`)
  },

  getSettings: function () {
    return {
      source: fb.database().ref(`users/${fb.auth().currentUser.uid}/settings`),
      asObject: true
    }
  },

  lockElement: function (value, id, parent) {
    fb.database().ref(`users/${fb.auth().currentUser.uid}/settings/masterPassword`).once("value").then(pw => {
      if (pw.val()) {
        fb.database().ref(`users/${fb.auth().currentUser.uid}/elements/${(parent) ? "bundles" : "main"}/${(parent) ? parent + "/"  : ""}${id}/locked`).set(value)
      } else {
        EventBus.$emit("showSnackbar", "No master password set!")
      }
    })
  },

  changeMasterPassword: function (newPassword, oldPassword) {
    return fb.database().ref(`users/${fb.auth().currentUser.uid}/settings/masterPassword`).once("value").then(pw => {
      if (!oldPassword || pw.val() == hashPassword(oldPassword)) {
        return fb.database().ref(`users/${fb.auth().currentUser.uid}/settings/masterPassword`).set(hashPassword(newPassword))
      } else throw new Error("Wrong password!")
    })
  },

  checkMasterPassword: function () {
    return new Promise((resolve, reject) => {
      fb.database().ref(`users/${fb.auth().currentUser.uid}/settings/masterPassword`).once("value").then(pw => {
        if (pw.val()) resolve()
        else reject()
      })
    })
  },

  deleteUserAccount: function () {
    return fb.auth().currentUser.delete()
  },

  changeLoginPassword: function (password) {
    return fb.auth().currentUser.updatePassword(password)
  },

  deleteElement: function (id, parent, element) {
    return fb.database().ref(`users/${fb.auth().currentUser.uid}/elements/${parent ? "bundles/" + parent : "main"}/${id}`).remove()
  },

  restoreElement: function (id, parent, cb) {
    fb.database().ref(`users/${fb.auth().currentUser.uid}/bin/${parent ? "bundles/" + parent : "main"}/${id}`).once("value").then(element => {
      return fb.database().ref(`users/${fb.auth().currentUser.uid}/elements/${parent ? "bundles/" + parent : "main"}/${id}`).set(element.val())
    }).then(() => {
      return fb.database().ref(`users/${fb.auth().currentUser.uid}/bin/${parent ? "bundles/" + parent : "main"}/${id}`).remove()
    }).then(() => {
      cb()
    }).catch(error => {
      cb(error)
    })
  },

  clearBin: function (parent, elements) {
    const ids = elements.map(element => element[".key"])
    ids.forEach(element => {
      console.log("DELETE", element)
      fb.database().ref(`users/${fb.auth().currentUser.uid}/bin/${parent ? "bundles/" + parent : "main"}/${element}`).remove().then(() => {
        fb.database().ref(`users/${fb.auth().currentUser.uid}/contents/${element}`).remove()
      })
    })
  },

  saveNote: function (text, id, parent) {
    fb.database().ref(`users/${fb.auth().currentUser.uid}/contents/${(parent) ? parent + "/" : ""}${id}/text`).set(text)
  },

  createElement: function (element, parent) {
    let ref = fb.database().ref(`users/${fb.auth().currentUser.uid}/elements`)
    if (!parent) {
      ref = ref.child("/main").push()
    } else {
      ref = ref.child(`/bundles/${parent}`).push()
    }

    return ref.set(element)
  },

  updateElement: function (element, id, parent) {
    const ref = fb.database().ref(`users/${fb.auth().currentUser.uid}/elements/${(parent) ? "bundles/"+ parent : "main"}/${id}`)
    const promises = []
    promises.push(ref.child("title").set(element["title"]))
    promises.push(ref.child("categoryName").set(element["categoryName"]))
    promises.push(ref.child("categoryID").set(element["categoryID"]))
    promises.push(ref.child("color").set(element["color"]))
    return Promise.all(promises)
  }
}
