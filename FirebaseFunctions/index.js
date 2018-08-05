var admin = require("firebase-admin");

var serviceAccount = require("./serviceAccountKey.json");
admin.initializeApp({
    credential: admin.credential.cert(serviceAccount),
    databaseURL: "https://pro3-48b05.firebaseio.com"
});

var db = admin.database();
var ref = db.ref("users");

function dateToTimestamp(date) {

}

ref.once("value", function (snapshot) {
    for (key in snapshot.val()) {
        if (snapshot.val()[key]["elements"] && snapshot.val()[key]["elements"]["main"]) {
            for (elementKey in snapshot.val()[key]["elements"]["main"]) {
                if (!snapshot.val()[key]["elements"]["main"][elementKey]["timeStamp"] && snapshot.val()[key]["elements"]["main"][elementKey]["creationDate"] && snapshot.val()[key]["elements"]["main"][elementKey]["creationDate"]["time"]) {
                    //db.ref(`users/${key}/elements/main/${elementKey}/timeStamp`).remove()
                    db.ref(`users/${key}/elements/main/${elementKey}/timeStamp`).set(snapshot.val()[key]["elements"]["main"][elementKey]["creationDate"]["time"])
                }
            }
        }

        if (snapshot.val()[key]["elements"] && snapshot.val()[key]["elements"]["bundles"]) {
            for (elementKey in snapshot.val()[key]["elements"]["bundles"]) {
                for (elementSubKey in snapshot.val()[key]["elements"]["bundles"][elementKey]) {
                    if (!snapshot.val()[key]["elements"]["bundles"][elementKey][elementSubKey]["timeStamp"] && snapshot.val()[key]["elements"]["bundles"][elementKey][elementSubKey]["creationDate"] && snapshot.val()[key]["elements"]["bundles"][elementKey][elementSubKey]["creationDate"]["time"]) {
                        //db.ref(`users/${key}/elements/bundles/${elementKey}/${elementSubKey}/timeStamp`).remove()
                        db.ref(`users/${key}/elements/bundles/${elementKey}/${elementSubKey}/timeStamp`).set(snapshot.val()[key]["elements"]["bundles"][elementKey][elementSubKey]["creationDate"]["time"])
                    }
                }
            }
        }

        if (snapshot.val()[key]["bin"] && snapshot.val()[key]["bin"]["main"]) {
            for (elementKey in snapshot.val()[key]["bin"]["main"]) {
                if (!snapshot.val()[key]["bin"]["main"][elementKey]["timeStamp"] && snapshot.val()[key]["bin"]["main"][elementKey]["creationDate"] && snapshot.val()[key]["bin"]["main"][elementKey]["creationDate"]["time"]) {
                    //db.ref(`users/${key}/bin/main/${elementKey}/timeStamp`).remove()
                    db.ref(`users/${key}/bin/main/${elementKey}/timeStamp`).set(snapshot.val()[key]["bin"]["main"][elementKey]["creationDate"]["time"])
                }
            }
        }

        if (snapshot.val()[key]["bin"] && snapshot.val()[key]["bin"]["bundles"]) {
            for (elementKey in snapshot.val()[key]["bin"]["bundles"]) {
                for (elementSubKey in snapshot.val()[key]["bin"]["bundles"][elementKey]) {
                    if (!snapshot.val()[key]["bin"]["bundles"][elementKey][elementSubKey]["timeStamp"] && snapshot.val()[key]["bin"]["bundles"][elementKey][elementSubKey]["creationDate"] && snapshot.val()[key]["bin"]["bundles"][elementKey][elementSubKey]["creationDate"]["time"]) {
                        //db.ref(`users/${key}/bin/bundles/${elementKey}/${elementSubKey}/timeStamp`).remove()
                        db.ref(`users/${key}/bin/bundles/${elementKey}/${elementSubKey}/timeStamp`).set(snapshot.val()[key]["bin"]["bundles"][elementKey][elementSubKey]["creationDate"]["time"])
                    }
                }
            }
        }
    }
}, function (errorObject) {
    console.log("The read failed: " + errorObject.code);
});
