$(document).ready(function () {
    $("#myNeighbor").click(function () {
        let myNeighbor = false;
        myNeighbor = !!$(this).children().hasClass("bi-heart-fill");

        const urlParts = window.location.pathname.split('/');
        const email = urlParts[2]; // "/blog/{email}"에서 {email} 부분을 추출

        $.ajax({
            type: "POST",
            url: `/blog/${email}/neighbor`,
            data: {myNeighbor: myNeighbor},
            success: function (data) {
                if (data.success) {
                    location.reload();
                } else {
                    location.reload();
                }
            },
            error: function () {
                alert("서버 오류가 발생했습니다.");
            },
        });
    });
});
