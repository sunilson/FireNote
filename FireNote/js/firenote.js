 (function() {
  var config = {
    apiKey: "AIzaSyBkH4zNyP3rxPD0zqDiLuD7h3I8JYZNWA4",
    authDomain: "pro3-48b05.firebaseapp.com",
    databaseURL: "https://pro3-48b05.firebaseio.com",
    storageBucket: "pro3-48b05.appspot.com",
    messagingSenderId: "376414129715"
  };
     
  firebase.initializeApp(config);
  
     firebase.auth().onAuthStateChanged(firebaseUser => {
        if(firebaseUser) {
            if(firebaseUser.emailVerified == true) {
                console.log('user logged in');
                window.location.replace('index.html');
            }
        } else {
            console.log('not logged in');
        }
    });

     const promise = auth.signInWithEmailAndPassword('weisslinus@gmail.com', 'blabla');
         
         promise.catch(e => console.log(e.message));
 }());

$(document).ready(function() {
  const txtEmail = document.getElementById('email');
  const txtPassword = document.getElementById('password');
  const btnLogin = document.getElementById('login');
              
  btnLogin.addEventListener('click', e => {
         const email = txtEmail.value;
         const pass = txtPassword.value;
         const auth = firebase.auth();
         
         const promise = auth.signInWithEmailAndPassword(email, pass);
         
         promise.catch(e => console.log(e.message));
         
     });
});

