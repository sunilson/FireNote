import fb from "firebase"
import config from "../config"

const firebaseApp = fb.initializeApp(config.firebaseConfig)

export default {
    fb,
    getElementRef: function (id) {
        return {
            source: fb.database().ref(`users/${fb.auth().currentUser.uid}/elements/main/${id}`),
            asObject: true
        }
    },

    getNoteContentRef: function (id) {
        return {
            source: fb.database().ref(`users/${fb.auth().currentUser.uid}/contents/${id}`),
            asObject: true
        }
    },

    getChecklistContentRef: function (id) {
        return fb.database().ref(`users/${fb.auth().currentUser.uid}/contents/${id}/elements`)
    },

    getBundleContentRef: function (id) {
        return fb.database().ref(`users/${fb.auth().currentUser.uid}/elements/bundles/${id}/elements`)
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

    getSettings: function () {
        return {
            source: fb.database().ref(`users/${fb.auth().currentUser.uid}/settings`),
            asObject: true
        }
    },

    lockElement: function (value, id, parent) {
        console.log(`users/${fb.auth().currentUser.uid}/elements/main/${id}/locked`)
        fb.database().ref(`users/${fb.auth().currentUser.uid}/elements/main/${id}/locked`).set(value)
    },

    deleteElement: function (id, parent, cb) {
        if (!parent) fb.database().ref(`users/${fb.auth().currentUser.uid}/elements/main/${id}`).remove(error => {
            cb(error)
        })
        else null
    },

    saveNote: function (text, id) {
        fb.database().ref(`users/${fb.auth().currentUser.uid}/contents/${id}/text`).set(text)
    },

    createElement: function (element, cb) {
        fb.database().ref(`users/${fb.auth().currentUser.uid}/elements/main`).push().set(element, err => cb(err))
    },

    restoreElement: function () {

    },

    updateElement: function () {

    }
}

