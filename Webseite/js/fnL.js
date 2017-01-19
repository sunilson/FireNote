var config = {
    apiKey: "AIzaSyBkH4zNyP3rxPD0zqDiLuD7h3I8JYZNWA4",
    authDomain: "pro3-48b05.firebaseapp.com",
    databaseURL: "https://pro3-48b05.firebaseio.com",
    storageBucket: "pro3-48b05.appspot.com",
    messagingSenderId: "376414129715"
  };
     
$("document").ready(function() {

  $("[data-localize]").localize("local");
  firebase.initializeApp(config);
    firebase.auth().onAuthStateChanged(function(firebaseUser){
            if(firebaseUser) {
                if(firebaseUser.emailVerified == true) {
                    window.location.replace('/');
                }
            } else {
            }
    });

  const txtEmail = document.getElementById('email');
  const txtPassword = document.getElementById('password');
  const btnLogin = document.getElementById('login');

  $("#login").click(function() {
      const auth = firebase.auth();

      $(this).text("Loading...");
      const promise = auth.signInWithEmailAndPassword($("#email").val(), $("#password").val());
      promise.catch(function(e){
        $("#login").text("Login");
        $(".description").addClass("error");
        $(".description").html("ERROR: Credentials invalid!");
      });
  });

  $("#googleLogin").click(function() {
    var provider = new firebase.auth.GoogleAuthProvider();

    firebase.auth().signInWithPopup(provider).then(function(result) {
        // This gives you a Google Access Token. You can use it to access the Google API.
        var token = result.credential.accessToken;
        // The signed-in user info.
        var user = result.user;
        // ...
      }).catch(function(error) {
        // Handle Errors here.
        var errorCode = error.code;
        var errorMessage = error.message;
        // The email of the user's account used.
        var email = error.email;
        // The firebase.auth.AuthCredential type that was used.
        var credential = error.credential;
        // ...
      });

  });

});

