import fb from "firebase"

const firebaseApp = fb.initializeApp({
    apiKey: "AIzaSyBkH4zNyP3rxPD0zqDiLuD7h3I8JYZNWA4",
    authDomain: "pro3-48b05.firebaseapp.com",
    databaseURL: "https://pro3-48b05.firebaseio.com",
    projectId: "pro3-48b05",
    storageBucket: "pro3-48b05.appspot.com",
    messagingSenderId: "376414129715"
})

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