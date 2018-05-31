var config = {
    apiKey: "AIzaSyBkH4zNyP3rxPD0zqDiLuD7h3I8JYZNWA4",
    authDomain: "pro3-48b05.firebaseapp.com",
    databaseURL: "https://pro3-48b05.firebaseio.com",
    storageBucket: "pro3-48b05.appspot.com",
    messagingSenderId: "376414129715"
  };
     
$("document").ready(function() {
  //Initialisierung von Lokalisation
  $("[data-localize]").localize("local");
  firebase.initializeApp(config);
    //Checken ob User bereits eingeloggt ist
    firebase.auth().onAuthStateChanged(function(firebaseUser){
            if(firebaseUser) {
                if(firebaseUser.emailVerified == true) {
                    //Auf Startseite weiterleiten
                    window.location.replace('/');
                }
            } else {
            }
    });

  const txtEmail = document.getElementById('email');
  const txtPassword = document.getElementById('password');
  const btnLogin = document.getElementById('login');

  //Login Button Click Listener
  $("#login").click(function() {
      const auth = firebase.auth();
      $(this).text("Loading...");
      //Sign In durchführen
      const promise = auth.signInWithEmailAndPassword($("#email").val(), $("#password").val());
      //Promise auf Errors überprüfen
      promise.catch(function(e){
        $("#login").text("Login");
        $(".description").addClass("error");
        $(".description").html("ERROR: Credentials invalid!");
      });
  });

  //Google Login Click Listener
  $("#googleLogin").click(function() {
    var provider = new firebase.auth.GoogleAuthProvider();

    //Sign In Starten und auf Resultat warten
    firebase.auth().signInWithPopup(provider).then(function(result) {
        // This gives you a Google Access Token. You can use it to access the Google API.
        var token = result.credential.accessToken;
        // The signed-in user info.
        var user = result.user;
      }).catch(function(error) {
        //Error handling
        $(".description").addClass("error");
        $(".description").html(error.message);
      });
  });
});

