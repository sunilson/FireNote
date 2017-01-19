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
       $("[data-localize]").localize("local");
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

$(window).resize(function() {
       if (!isMobileSize.matches) {
          loadAuth();
       }
});

function initializeSortListener() {
  $(".orderCategory").click(function(e) {
      currentSort = $(this).attr("id");
      $("#elements").html(sortElementList($(this).attr("id"), "#elements"));
      $("#orderModal").modal('toggle');
      refreshClickListener();
  });
}

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
                            start();
                          }
                      } else {
                        window.location.replace('login');
                      }
                  } else {
                       window.location.replace('login');
                  }
          });
        }
}

function start() {
        if(user != null) {
            dRef = database.ref().child("users").child(user.uid);
            mElementReference = dRef.child("elements").child("main");
            mSettingsReference = dRef.child("settings");
            startElementListeners();
            initializeSortListener();
          }
      }

function startElementListeners() {
    mElementReference.on('child_added', function(data) {
            var element = data.val();
            var key = data.key;
            var html = $(createElement(element.title, element.categoryName, key, element.noteType, element.color, element.creationDate, element.locked));
            html.appendTo("#elements");
            dict[key] = element.locked;
            $("#elements").html(sortElementList(currentSort, "#elements"));
            refreshClickListener();
          });

          mElementReference.on('child_removed', function(data) {
            var key = data.key;
            $("#" + key).remove()
          });

          mElementReference.on('child_changed', function(data) {
            var element = data.val();
            var key = data.key;
            var html = $(createElement(element.title, element.categoryName, key, element.noteType, element.color, element.creationDate, element.locked));
            $("#" + key).replaceWith(html);
            dict[key] = element.locked;
            refreshClickListener();
          });
}

function refreshClickListener() {
  $(".element").off();
  $(".element").on('click', function(e){
              var id = $(this).attr('id');
              var type = $(this).attr('type');
              var title = $(this).attr('title');
              var color = $(this).attr('color');
              if(dict[$(this).attr('id')] == true) {
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
                          startContentListener(id, type, title, color);
                        }
            });
}

function startContentListener(id, type, title, color) {
    $("#content").empty();
    $("#headerContents").removeClass();
    if(mContentReference != null) {
      mContentReference.off();
    }

    $("#headerContents").addClass("col-lg-12 col-md-12 col-xs-12 col-sm-12 " + convertColor(parseInt(color))[1]);

    if(type === "note") {
      mContentReference = dRef.child("contents").child(id).child("text");
      mContentReference.on('value', function(data) {
        $("#content").empty();
        var display_txt = data.val().replace(/(?:\r\n|\r|\n)/g, '<br />');
        $("#content").append(createNote(title));
        $("#note").append(display_txt);
      });
    } else if (type === 'checklist') {
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

function createChecklist(title) {
  var result = "<div id='checklist'></div>";
  $("#contentTitle").text(title);
  return result;
}

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

function createBundle(title) {
  var result = "<div id='bundle'></div>";
  $("#contentTitle").text(title);
  return result;
}

function createBundleElement(id, title, date, type, color, category, locked) {
   return createElement(title, category, id, type, color, date, locked);
}

function createNote(title, text) {
  var result = "<div id='note' class='wordwrap'></div>";
  $("#contentTitle").text(title);
  return result;
}

function createElement(title, category, id, type, color, creationDate, locked) {

    var date = creationDate.date.toString() + "." + (creationDate.month + 1).toString() + "." + (1900 + creationDate.year).toString();

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

    var colors = convertColor(color);
    var bg1 = colors[1];
    var bg2 = colors[2];

    var lock = "";
    if (locked == true) {
            lock = "<i class='material-icons'>lock</i>";
    }

    var result = '<div class="element row '+bg2+'" id="'+ id +'" type="'+type+'" color="'+color+'" title="'+title+'" category="'+category+'" time="'+creationDate.time+'"><div class="col-xs-1 col-sm-1 col-md-1 col-lg-1 iconholder '+bg1+'">' +icon+ '</div> <div class="col-xs-11 col-sm-11 col-md-11 col-lg-11 elementMeta"><div class="col-xs-8 col-sm-8 col-md-8 col-lg-8"><div class="row"><div class="col-xs-12 col-sm-12 col-md-12 col-lg-12 elementTitle">' +title+ '</div><div class="col-xs-12 col-sm-12 col-md-12 col-lg-12 elementCategory">' +category+'</div></div></div><div class="col-xs-1 col-sm-1 col-md-1 col-lg-1 elementLock">'+lock+'</div><div class="col-xs-3 col-sm-3 col-md-3 col-lg-3 elementDate">' +date+'</div></div></div>';
    return result;
}

/**
 * jQuery.browser.mobile (http://detectmobilebrowser.com/)
 *
 * jQuery.browser.mobile will be true if the browser is a mobile device
 *
 **/
 function isMobile() {
  var check = false;
  (function(a){if(/(android|bb\d+|meego).+mobile|avantgo|bada\/|blackberry|blazer|compal|elaine|fennec|hiptop|iemobile|ip(hone|od)|iris|kindle|lge |maemo|midp|mmp|mobile.+firefox|netfront|opera m(ob|in)i|palm( os)?|phone|p(ixi|re)\/|plucker|pocket|psp|series(4|6)0|symbian|treo|up\.(browser|link)|vodafone|wap|windows ce|xda|xiino/i.test(a)||/1207|6310|6590|3gso|4thp|50[1-6]i|770s|802s|a wa|abac|ac(er|oo|s\-)|ai(ko|rn)|al(av|ca|co)|amoi|an(ex|ny|yw)|aptu|ar(ch|go)|as(te|us)|attw|au(di|\-m|r |s )|avan|be(ck|ll|nq)|bi(lb|rd)|bl(ac|az)|br(e|v)w|bumb|bw\-(n|u)|c55\/|capi|ccwa|cdm\-|cell|chtm|cldc|cmd\-|co(mp|nd)|craw|da(it|ll|ng)|dbte|dc\-s|devi|dica|dmob|do(c|p)o|ds(12|\-d)|el(49|ai)|em(l2|ul)|er(ic|k0)|esl8|ez([4-7]0|os|wa|ze)|fetc|fly(\-|_)|g1 u|g560|gene|gf\-5|g\-mo|go(\.w|od)|gr(ad|un)|haie|hcit|hd\-(m|p|t)|hei\-|hi(pt|ta)|hp( i|ip)|hs\-c|ht(c(\-| |_|a|g|p|s|t)|tp)|hu(aw|tc)|i\-(20|go|ma)|i230|iac( |\-|\/)|ibro|idea|ig01|ikom|im1k|inno|ipaq|iris|ja(t|v)a|jbro|jemu|jigs|kddi|keji|kgt( |\/)|klon|kpt |kwc\-|kyo(c|k)|le(no|xi)|lg( g|\/(k|l|u)|50|54|\-[a-w])|libw|lynx|m1\-w|m3ga|m50\/|ma(te|ui|xo)|mc(01|21|ca)|m\-cr|me(rc|ri)|mi(o8|oa|ts)|mmef|mo(01|02|bi|de|do|t(\-| |o|v)|zz)|mt(50|p1|v )|mwbp|mywa|n10[0-2]|n20[2-3]|n30(0|2)|n50(0|2|5)|n7(0(0|1)|10)|ne((c|m)\-|on|tf|wf|wg|wt)|nok(6|i)|nzph|o2im|op(ti|wv)|oran|owg1|p800|pan(a|d|t)|pdxg|pg(13|\-([1-8]|c))|phil|pire|pl(ay|uc)|pn\-2|po(ck|rt|se)|prox|psio|pt\-g|qa\-a|qc(07|12|21|32|60|\-[2-7]|i\-)|qtek|r380|r600|raks|rim9|ro(ve|zo)|s55\/|sa(ge|ma|mm|ms|ny|va)|sc(01|h\-|oo|p\-)|sdk\/|se(c(\-|0|1)|47|mc|nd|ri)|sgh\-|shar|sie(\-|m)|sk\-0|sl(45|id)|sm(al|ar|b3|it|t5)|so(ft|ny)|sp(01|h\-|v\-|v )|sy(01|mb)|t2(18|50)|t6(00|10|18)|ta(gt|lk)|tcl\-|tdg\-|tel(i|m)|tim\-|t\-mo|to(pl|sh)|ts(70|m\-|m3|m5)|tx\-9|up(\.b|g1|si)|utst|v400|v750|veri|vi(rg|te)|vk(40|5[0-3]|\-v)|vm40|voda|vulc|vx(52|53|60|61|70|80|81|83|85|98)|w3c(\-| )|webc|whit|wi(g |nc|nw)|wmlb|wonu|x700|yas\-|your|zeto|zte\-/i.test(a.substr(0,4))) check = true;})(navigator.userAgent||navigator.vendor||window.opera);
  return check;
 }

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

