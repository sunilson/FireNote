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

function lockElement() {

}

function deleteElement() {

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
    getElementRef
}