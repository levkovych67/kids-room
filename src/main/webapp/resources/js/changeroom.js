$(function(){
    if(localStorage["room"] == null) {
        localStorage["room"] = $("#selectRoom option").first().val();
    }

    $("#selectRoom").val(localStorage['room']);
});

function selectRoomForManager(room) {
    localStorage["room"] = room;
}