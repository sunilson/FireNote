// The Cloud Functions for Firebase SDK to create Cloud Functions and setup triggers.
const functions = require('firebase-functions');

// The Firebase Admin SDK to access the Firebase Realtime Database.
const admin = require('firebase-admin');

admin.initializeApp();

exports.handleUserCreation = functions.auth.user().onCreate((user) => {
    //TODO: Beispiel Daten anlegen
    //TODO: Master Passwort setzen
});

exports.handleElementDeletion = functions.database.ref("users/{uid}/elements/main/{elementId}").onDelete((snapshot, context) => {
    return admin.database()
        .ref(`users/${context.params.uid}/bin/main/${context.params.elementId}`)
        .set(snapshot.val())
});

exports.handleBundleElementDeletion = functions.database.ref("users/{uid}/elements/bundles/{parentId}/{elementId}").onDelete((snapshot, conext) => {
    return admin.database()
        .ref(`users/${context.params.uid}/bin/bundles/${context.params.parentId}/${context.params.elementId}`)
        .set(snapshot.val())
});