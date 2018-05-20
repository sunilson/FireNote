import fb from "firebase"
import config from "../config"

const firebaseApp = fb.initializeApp(config.firebaseConfig)

function getElementRef(id) {
    return {
        source: fb.database().ref(`users/${fb.auth().currentUser.uid}/elements/main/${id}`),
        asObject: true
    }
}

function getNoteContentRef(id) {
    return {
        source: fb.database().ref(`users/${fb.auth().currentUser.uid}/contents/${id}`),
        asObject: true
    }
}

function getChecklistContentRef(id) {
    return fb.database().ref(`users/${fb.auth().currentUser.uid}/contents/${id}/elements`)
}

function getElementListRef() {
    return fb.database().ref(`users/${fb.auth().currentUser.uid}/elements/main`)
}

function lockElement(value, id, parent) {
    console.log(`users/${fb.auth().currentUser.uid}/elements/main/${id}/locked`)
    fb.database().ref(`users/${fb.auth().currentUser.uid}/elements/main/${id}/locked`).set(value)
}

function deleteElement(id, parent, cb) {
    if (!parent) fb.database().ref(`users/${fb.auth().currentUser.uid}/elements/main/${id}`).remove(error => {
        cb(error)
    })
    else null
}

function saveNote(text, id) {
    fb.database().ref(`users/${fb.auth().currentUser.uid}/contents/${id}/text`).set(text)
}

function createElement() {

}

function restoreElement() {

}

function updateElement() {

}

export default {
    fb,
    getElementListRef,
    getNoteContentRef,
    getChecklistContentRef,
    getElementRef,
    lockElement,
    deleteElement,
    saveNote
}