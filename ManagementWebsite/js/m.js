$("document").ready(function() {
  
  //Auslesen von URL Parametern
  var mode = getParameterByName('mode');
  var actionCode = getParameterByName('oobCode');
  var apiKey = getParameterByName('apiKey');

  //Ungültig, auf Startseite umleiten
  if(!apiKey) {
    window.location.replace('/');
  }

  //Firebase SDK Einrichtung
  var config = {
    'apiKey': apiKey
  };
  var app = firebase.initializeApp(config);
  var auth = app.auth();

  switch (mode) {
    case 'verifyEmail':
      handleVerifyEmail(auth, actionCode);
      break;
    case 'resetPassword':
      handleResetPassword(auth, actionCode);
      break;
    default:
      window.location.replace('/');
      break;
  }
});

//Parameter von URL auslesen
function getParameterByName(name) {
    return decodeURIComponent((new RegExp('[?|&]' + name + '=' + '([^&;]+?)(&|#|;|$)').exec(location.search) || [null, ''])[1].replace(/\+/g, '%20')) || null;
}

//Reset Password UI anzeigen
function handleResetPassword(auth, actionCode) {
  var accountEmail;
  
  //ActionCode überprüfen
  auth.verifyPasswordResetCode(actionCode).then(function(email) {
    var accountEmail = email;

    $("#resetInvalid").hide();

    $("#reset").show();
    $("#resetEmail").html("Please enter the new password you want for the FireNote account " + email);

    $("#confirm").click(function(e) {
        var pw1 = $("#password").val();
        var pw2 = $("#password2").val();

        if(pw1 === pw2) {
          //Passwort zurücksetzen
          auth.confirmPasswordReset(actionCode, pw1).then(function(resp) {
            $("#resetEmail").addClass("success");
            $("#resetEmail").html("SUCCESS: Password has been changed!");
            $("#resetForm").hide();
          }).catch(function(error) {
            $("#resetEmail").addClass("error");
            $("#resetEmail").html("ERROR: Code may have expired or password is too weak/contains invalid characters!");
          }).catch(function(error) {
            $("#resetEmail").addClass("error");
            $("#resetEmail").html("ERROR: Code may have expired! Please try to request a new reset link.");
          });
        } else {
          $("#resetEmail").addClass("error");
          $("#resetEmail").html("ERROR: Passwords don't match!");
        }
      });
    }, function(reason) {
      //Invalider Code
      $("#resetInvalid").show();
    });
}


function handleVerifyEmail(auth, actionCode) {
  //ActionCode überprüfen
  auth.applyActionCode(actionCode).then(function(resp) {
    //Email wurde verifiziert
    $("#verified").show();
  }).catch(function(error) {
    //Code ungültig
    $("#notVerified").show();
  });
}