var user;
var database;
var config = {
    apiKey: "AIzaSyBkH4zNyP3rxPD0zqDiLuD7h3I8JYZNWA4",
    authDomain: "pro3-48b05.firebaseapp.com",
    databaseURL: "https://pro3-48b05.firebaseio.com",
    storageBucket: "pro3-48b05.appspot.com",
    messagingSenderId: "376414129715"
  };
var mElementReference;
var dRef;
var mContentReference;
var mSettingsReference;
var started = false;
var authStarted = false;
var detachedContent;
var isMobileSize;
var dict = {};
var currentSort = "nameA";


$("document").ready(function() 
{
      //Lokalisierung initialisieren
       $("[data-localize]").localize("local");

       //Nur auf Desktop Bildschirmen fortfahren
       isMobileSize = window.matchMedia("only screen and (max-width: 760px)");
       if (!isMobileSize.matches) {
          loadAuth();
       }

       $("#logOut").click(function(e) {
          firebase.auth().signOut().then(function() {
              window.location.replace('login');
          }, function(error) {

          });
       });
});

//Bei Window Resize Auth initialisieren (falls auf mobile gestartet wurde)
$(window).resize(function() {
       if (!isMobileSize.matches) {
          loadAuth();
       }
});

//Elemente sortieren nach Klick auf Sortier Methode
function initializeSortListener() {
  $(".orderCategory").click(function(e) {
      currentSort = $(this).attr("id");
      $("#elements").html(sortElementList($(this).attr("id"), "#elements"));
      $("#orderModal").modal('toggle');

      //Click Listener von Element Liste aktualisieren
      refreshClickListener();
  });
}

//Mit gegebener Sortier Methode die Element Liste sortieren
function sortElementList(type, id) {
    var sortedList;

    if(type === "nameA") {
          sortedList = $(id + " .element").sort(function (a, b) {
          var contentA = $(a).attr('title').toLowerCase();
          var contentB = $(b).attr('title').toLowerCase();
          return (contentA < contentB) ? -1 : (contentA > contentB) ? 1 : 0;
          });
    } else if (type === "nameD") {
          sortedList = $(id + " .element").sort(function (a, b) {
          var contentA = $(a).attr('title').toLowerCase();
          var contentB = $(b).attr('title').toLowerCase();
          return (contentA < contentB) ? 1 : (contentA > contentB) ? -1 : 0;
          });
    } else if (type === "category") {
          sortedList = $(id + " .element").sort(function (a, b) {
          var contentA = $(a).attr('category').toLowerCase();
          var contentB = $(b).attr('category').toLowerCase();
          return (contentA < contentB) ? -1 : (contentA > contentB) ? 1 : 0;
          });
    } else if (type === "dateA") {
          sortedList = $(id + " .element").sort(function (a, b) {
          var timeA = parseInt($(a).attr('time'));
          var timeB = parseInt($(b).attr('time'));
          if(timeA > timeB) {
            return 1;
          } else if (timeA < timeB) {
            return -1;
          }

          return 0;
        });
    }  else if (type === "dateD") {
          sortedList = $(id + " .element").sort(function (a, b) {
          var timeA = parseInt($(a).attr('time'));
          var timeB = parseInt($(b).attr('time'));
          if(timeA > timeB) {
            return -1;
          } else if (timeA < timeB) {
            return 1;
          }

          return 0;
        });
    }

   return sortedList;
    
}

//Authentifizierungs Listener und Firebase initialisieren
function loadAuth() {
        if(!authStarted) {
          authStarted = true;
          firebase.initializeApp(config);
          database = firebase.database();
          firebase.auth().onAuthStateChanged(function(firebaseUser) {
                  if(firebaseUser) {
                      if(firebaseUser.emailVerified == true) {
                          user = firebaseUser;
                          if(!started){
                            started = true;
                            //FireNote WebApp starten
                            start();
                          }
                      } else {
                        //Zu Login weiterleiten wenn ausgeloggt oder nicht verifiziert
                        window.location.replace('login');
                      }
                  } else {
                       window.location.replace('login');
                  }
          });
        }
}

//Wenn eingeloggt, Listener starten
function start() {
        if(user != null) {
            dRef = database.ref().child("users").child(user.uid);
            mElementReference = dRef.child("elements").child("main");
            mSettingsReference = dRef.child("settings");
            startElementListeners();
            initializeSortListener();
          }
      }

//Listener auf alle Elemente des Users 
function startElementListeners() {
          //Neues Element wurde hinzugefügt
           mElementReference.on('child_added', function(data) {
            var element = data.val();
            var key = data.key;
            //Neues HTML Element erstellen mit Daten von Elmenet
            var html = $(createElement(element.title, element.categoryName, key, element.noteType, element.color, element.creationDate, element.locked));
            //Element zu Liste hinzufügen
            html.appendTo("#elements");
            //Ins Dictionary Locked Status eintragen
            dict[key] = element.locked;
            //Liste sortieren
            $("#elements").html(sortElementList(currentSort, "#elements"));
            //Click Listener updaten
            refreshClickListener();
          });

          //Element wurde gelöscht
          mElementReference.on('child_removed', function(data) {
            var key = data.key;
            $("#" + key).remove()
          });

          //Element wurde geändert
          mElementReference.on('child_changed', function(data) {
            var element = data.val();
            var key = data.key;
            //Neues HTML Element erstellen mit Daten von Elmenet
            var html = $(createElement(element.title, element.categoryName, key, element.noteType, element.color, element.creationDate, element.locked));
            //Altes Element ersetzen
            $("#" + key).replaceWith(html);
            //Ins Dictionary Locked Status eintragen
            dict[key] = element.locked;
            //Click Listener updaten
            refreshClickListener();
          });
}

//Click Listener von Element Liste updaten
function refreshClickListener() {
  //Listener entfernen
  $(".element").off();
  //Listener auf alle Elemente hinzufügen
  $(".element").on('click', function(e){
              //Attribute extrahieren
              var id = $(this).attr('id');
              var type = $(this).attr('type');
              var title = $(this).attr('title');
              var color = $(this).attr('color');
              //Checken ob gesperrt
              if(dict[$(this).attr('id')] == true) {
                            //Master Passwort holen und Listener für Modal Dialog setzen
                            mSettingsReference.child("masterPassword").once('value').then(function(snapshot) {
                                var result = snapshot.val();
                                $("#passwordModal").modal('toggle');
                                $("#passwordModal").find(".btnConfirm").unbind();
                                $("#passwordModal").find(".btnConfirm").click(function(event) {
                                    if(hex_sha1($("#master").val()) === result) {
                                      startContentListener(id, type, title, color);
                                    }
                                    $("#master").val('');
                                    $("#passwordModal").modal('toggle');
                                });
                              });
                        } else {
                          //Wenn nicht gesperrt, Content laden
                          startContentListener(id, type, title, color);
                        }
            });
}

//Listener für Inhalt von Element
function startContentListener(id, type, title, color) {
    //Content Area leeren
    $("#content").empty();
    $("#headerContents").removeClass();
    if(mContentReference != null) {
      //Vorhandenen Listener entfernen
      mContentReference.off();
    }

    //Header einfärben
    $("#headerContents").addClass("col-lg-12 col-md-12 col-xs-12 col-sm-12 " + convertColor(parseInt(color))[1]);

    //Content von Note laden
    if(type === "note") {
      mContentReference = dRef.child("contents").child(id).child("text");
      mContentReference.on('value', function(data) {
        $("#content").empty();
        var display_txt = data.val().replace(/(?:\r\n|\r|\n)/g, '<br />');
        $("#content").append(createNote(title));
        $("#note").append(display_txt);
      });
    } else if (type === 'checklist') {
      //Content von Checkliste laden
      mContentReference = dRef.child("contents").child(id).child("elements");
      $("#content").empty();
      $("#content").append(createChecklist(title));
      mContentReference.on('child_added', function(data) {
          var checkbox;
          $("#checklist").append(createChecklistElement(data.key, data.val().text, data.val().finished));
      });
      mContentReference.on('child_changed', function(data) {
          var checkbox;
          if(data.val().finished == true) {
              checkbox = "check_box";
          } else {
              checkbox = "check_box_outline_blank";
          }

          $("#"+data.key).replaceWith(createChecklistElement(data.key, data.val().text, data.val().finished));
      });
      mContentReference.on('child_removed', function(data) {
          $("#"+data.key).remove();
      });
    } else if (type === 'bundle') {
      //Content von Bundle laden
      mContentReference = dRef.child("elements").child("bundles").child(id);
      $("#content").empty();
      $("#content").append(createBundle(title));
      mContentReference.on('child_added', function(data) {
        var element = data.val();
        var key = data.key;
        $("#bundle").append(createBundleElement(data.key, data.val().title, data.val().creationDate, data.val().noteType, data.val().color, data.val().categoryName, data.val().locked));
        dict[key] = element.locked;
        $("#bundle").html(sortElementList(currentSort, "#bundle"));
        refreshClickListener();
      });

      mContentReference.on('child_changed', function(data) {
            var element = data.val();
            var key = data.key;
            var html = $(createElement(element.title, element.categoryName, key, element.noteType, element.color, element.creationDate, element.locked));
            $("#" + key).replaceWith(html);
            refreshClickListener();
        });
    }
}

//Neuen Checklisten Container erstellen
function createChecklist(title) {
  var result = "<div id='checklist'></div>";
  $("#contentTitle").text(title);
  return result;
}

//Neues Checklisten Element erstellen
function createChecklistElement(id, text, checkbox) {
  var crossed;
  if(checkbox == true) {
      checkbox = "check_box";
      crossed = "crossed";
  } else {
      checkbox = "check_box_outline_blank";
      crossed="";
  }
  return '<div id="'+id+'" class="checklistElement row noMargin"><div class="col-lg-8 col-md-8 col-sm-8 col-xs-8 '+crossed+' checklistText">'+text+'</div><div class="col-lg-4 col-md-4 col-sm-4 col-xs-4 checkbox"><i class="material-icons">'+checkbox+'</i></div></div>';
}

//Neuen Bundle Container erstellen
function createBundle(title) {
  var result = "<div id='bundle'></div>";
  $("#contentTitle").text(title);
  return result;
}

//Neues Bundle Element erstellen
function createBundleElement(id, title, date, type, color, category, locked) {
   return createElement(title, category, id, type, color, date, locked);
}

//Neuen Note Container erstellen
function createNote(title, text) {
  var result = "<div id='note' class='wordwrap'></div>";
  $("#contentTitle").text(title);
  return result;
}

//Neues Listen Element erstellen
function createElement(title, category, id, type, color, creationDate, locked) {

    //Datum umwandeln
    var date = creationDate.date.toString() + "." + (creationDate.month + 1).toString() + "." + (1900 + creationDate.year).toString();

    //Richtiges Icon setzen
    var icon;
    switch(type) {
      case "checklist":
          icon = "<i class='material-icons'>done_all</i>";
          break;
      case "note":
          icon = "<i class='material-icons'>note</i>";
          break;
      case "bundle":
          icon = "<i class='material-icons'>list</i>";
          break;
      default:
          icon = "<i class='material-icons'>done_all</i>";
          break;
    }

    //Farben umwandeln
    var colors = convertColor(color);
    var bg1 = colors[1];
    var bg2 = colors[2];

    //Locked Icon setzen
    var lock = "";
    if (locked == true) {
            lock = "<i class='material-icons'>lock</i>";
    }

    //HTML Element zurückgeben
    var result = '<div class="element row '+bg2+'" id="'+ id +'" type="'+type+'" color="'+color+'" title="'+title+'" category="'+category+'" time="'+creationDate.time+'"><div class="col-xs-1 col-sm-1 col-md-1 col-lg-1 iconholder '+bg1+'">' +icon+ '</div> <div class="col-xs-11 col-sm-11 col-md-11 col-lg-11 elementMeta"><div class="col-xs-8 col-sm-8 col-md-8 col-lg-8"><div class="row"><div class="col-xs-12 col-sm-12 col-md-12 col-lg-12 elementTitle">' +title+ '</div><div class="col-xs-12 col-sm-12 col-md-12 col-lg-12 elementCategory">' +category+'</div></div></div><div class="col-xs-1 col-sm-1 col-md-1 col-lg-1 elementLock">'+lock+'</div><div class="col-xs-3 col-sm-3 col-md-3 col-lg-3 elementDate">' +date+'</div></div></div>';
    return result;
}

//Farben von Java umwandeln (nicht die beste Lösung, aber aus Zeitmangel so gewählt)
function convertColor(color) {

  var bg = new Array(2);

  switch(color) {
      case -769226:
          bg[1] = "noteColor1";
          bg[2] = "noteColorTransparent1";
          break;
      case -1499549:
          bg[1] = "noteColor2";
          bg[2] = "noteColorTransparent2";
          break;
      case -6543440:
          bg[1] = "noteColor3";
          bg[2] = "noteColorTransparent3";
          break;
      case -14575885:
          bg[1] = "noteColor4";
          bg[2] = "noteColorTransparent4";
          break;
      case -11751600:
          bg[1] = "noteColor5";
          bg[2] = "noteColorTransparent5";
          break;
      case -16738680:
          bg[1] = "noteColor6";
          bg[2] = "noteColorTransparent6";
          break;
      case -16121:
          bg[1] = "noteColor7";
          bg[2] = "noteColorTransparent7";
          break;
      case -43230:
          bg[1] = "noteColor8";
          bg[2] = "noteColorTransparent8";
          break;
      case -8825528:
          bg[1] = "noteColor9";
          bg[2] = "noteColorTransparent9";
          break;
      default: 
          bg[1] = "noteColor1";
          bg[2] = "noteColorTransparent1";
          break;
    }

    return bg;
}

