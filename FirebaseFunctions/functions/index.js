// The Cloud Functions for Firebase SDK to create Cloud Functions and setup triggers.
const functions = require('firebase-functions');

// The Firebase Admin SDK to access the Firebase Realtime Database.
const admin = require('firebase-admin');

admin.initializeApp();

exports.handleElementDeletion = functions.database.ref("users/{uid}/elements/main/{elementId}").onDelete((snapshot, context) => {
    return admin.database().ref(`users/${context.params.uid}`).once("value").then(userSnapshot => {
        if (userSnapshot && userSnapshot.val()) {
            return admin.database()
                .ref(`users/${context.params.uid}/bin/main/${context.params.elementId}`)
                .set(snapshot.val())
        } else {
            throw new Error("No user found")
        }
    })
});

exports.handleBundleElementDeletion = functions.database.ref("users/{uid}/elements/bundles/{parentId}/{elementId}").onDelete((snapshot, context) => {
    return admin.database().ref(`users/${context.params.uid}`).once("value").then(userSnapshot => {
        if (userSnapshot && userSnapshot.val()) {
            return admin.database()
                .ref(`users/${context.params.uid}/bin/bundles/${context.params.parentId}/${context.params.elementId}`)
                .set(snapshot.val())
        } else {
            throw new Error("No user found")
        }
    })
});

exports.handleUserCreation = functions.auth.user().onCreate((user) => {
    const baseRef = admin.database().ref(`users/${user.uid}`)
    const promises = []

    let dref = baseRef.child("elements").child("main").push()
    const firstChecklist = dref.key
    promises.push(dref.set({
        elementID: dref.key,
        noteType: "checklist",
        title: "Example Checklist",
        categoryName: "General",
        categoryID: "general",
        color: -769226,
        locked: false,
        timeStamp: new Date().getTime()
    }))

    dref = baseRef.child("elements").child("main").push()
    const firstNote = dref.key
    promises.push(dref.set({
        elementID: dref.key,
        noteType: "note",
        title: "Example Note",
        categoryName: "General",
        categoryID: "general",
        color: -14575885,
        locked: false,
        timeStamp: new Date().getTime()
    }))

    dref = baseRef.child("elements").child("main").push()
    const firstBundle = dref.key
    promises.push(dref.set({
        elementID: dref.key,
        noteType: "bundle",
        title: "Example Bundle",
        categoryName: "General",
        categoryID: "general",
        color: -16121,
        locked: false,
        timeStamp: new Date().getTime()
    }))

    for (let i = 0; i < 3; i++) {
        dref = baseRef.child(`contents`).child(firstChecklist).child("elements").push()
        promises.push(dref.set({
            text: "Example checklist element",
            finished: false
        }))
    }

    promises.push(baseRef.child(`contents`).child(firstNote).child("text").set("Example note text!"))

    promises.push(baseRef.child(`elements`).child("bundles").child(firstBundle).push().set({
        elementID: dref.key,
        noteType: "note",
        title: "Example Note",
        categoryName: "General",
        categoryID: "general",
        color: -14575885,
        locked: false,
        timeStamp: new Date().getTime()
    }))

    promises.push(baseRef.child(`elements`).child("bundles").child(firstBundle).push().set({
        elementID: dref.key,
        noteType: "checklist",
        title: "Example Checklist",
        categoryName: "General",
        categoryID: "general",
        color: -769226,
        locked: false,
        timeStamp: new Date().getTime()
    }))

    promises.push(baseRef.child("settings").child("registered").set(true))
    //baseRef.child("settings").child("masterPassword").set("todo")

    return Promise.all(promises)
});

exports.handleUserDeletion = functions.auth.user().onDelete((user, context) => admin.database().ref(`users/${user.uid}`).remove())