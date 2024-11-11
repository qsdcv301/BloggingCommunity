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
            success: function (response) {
                if (response.success) {
                    location.reload();
                } else {
                    alert("저장에 실패했습니다.");
                }
            },
            error: function () {
                alert("서버 오류가 발생했습니다.");
            }
        });
    });

    // 블로그 셋팅 시작

    $(document).on("click", ".blog-edit, .category-edit, .category-create", function () {
        const type = $(this).data("type");
        const id = $(this).data("id");

        // 공통 필드 설정
        $('#editForm input[name="id"]').val(id);

        // 모달 필드 초기화
        $('#modalBlogTitle, #modalBlogDescription, #modalCategoryName').closest('.mb-3').hide();

        if (type === 'blog') {
            $('#modalBlogTitle').val($(this).data("title")).closest('.mb-3').show();
            $('#modalBlogDescription').val($(this).data("description")).closest('.mb-3').show();
            $('#settingPost').attr("data-type", "blog");
        } else if (type === 'category') {
            $('#modalCategoryName').val($(this).data("name")).closest('.mb-3').show();
            $('#settingPost').attr("data-type", "category");
        } else if (type === 'categoryAdd') {
            $('#modalCategoryName').val($(this).data("name")).closest('.mb-3').show();
            $('#settingPost').attr("data-type", "categoryAdd");
        }
        // 모달 열기
        $('#editModal').modal("show");
    });

    $(document).on("click", ".category-del, .post-del, .comment-del .neighbor-del", function () {
        const urlParts = window.location.pathname.split('/');
        const email = urlParts[2]; // "/blog/{email}"에서 {email} 부분을 추출
        const type = $(this).data("type");
        const id = $(this).data("id");
        const view = $(this).data("view");

        if (type === 'category') {
            if (confirm("정말로 삭제 하시겠습니까?")) {
                $.ajax({
                    type: "POST",
                    url: `/blog/${email}/setting/categoryDelete`,
                    data: {
                        id: id,
                        view: view,
                    },
                    success: function (response) {
                        if (response.success) {
                            location.reload();
                        } else {
                            alert("삭제를 실패했습니다.");
                        }
                    },
                    error: function () {
                        alert("서버 오류가 발생했습니다.");
                    }
                });
            } else {

            }
        } else if (type === 'post') {
            if (confirm("정말로 삭제 하시겠습니까?")) {
                $.ajax({
                    type: "POST",
                    url: `/blog/${email}/setting/postDelete`,
                    data: {
                        id: id,
                        view: view,
                    },
                    success: function (response) {
                        if (response.success) {
                            if(response.view){
                                location.replace( `/blog/${email}`);
                            }else{
                                location.reload();
                            }
                        } else {
                            alert("삭제를 실패했습니다.");
                        }
                    },
                    error: function () {
                        alert("서버 오류가 발생했습니다.");
                    }
                });
            } else {

            }
        } else if (type === 'comment') {
            if (confirm("정말로 삭제 하시겠습니까?")) {
                $.ajax({
                    type: "POST",
                    url: `/blog/${email}/setting/commentDelete`,
                    data: {
                        id: id,
                    },
                    success: function (response) {
                        if (response.success) {
                            location.reload();
                        } else {
                            alert("삭제를 실패했습니다.");
                        }
                    },
                    error: function () {
                        alert("서버 오류가 발생했습니다.");
                    }
                });
            } else {

            }
        } else if (type === 'neighbor') {
            if (confirm("정말로 삭제 하시겠습니까?")) {
                $.ajax({
                    type: "POST",
                    url: `/blog/${email}/setting/neighborDelete`,
                    data: {
                        id: id,
                    },
                    success: function (response) {
                        if (response.success) {
                            location.reload();
                        } else {
                            alert("삭제를 실패했습니다.");
                        }
                    },
                    error: function () {
                        alert("서버 오류가 발생했습니다.");
                    }
                });
            } else {

            }
        }
    });

    $("#settingPost").click(function () {

        const urlParts = window.location.pathname.split('/');
        const email = urlParts[2]; // "/blog/{email}"에서 {email} 부분을 추출

        let updateMethod;
        if ($(this).data("type") === "blog") {
            const id = $("#modalBlogId").val();
            const title = $("#modalBlogTitle").val();
            const description = $("#modalBlogDescription").val();
            $.ajax({
                type: "POST",
                url: `/blog/${email}/setting/blogUpdate`,
                data: {
                    id: id,
                    title: title,
                    description: description
                },
                success: function (response) {
                    if (response.success) {
                        $('#editModal').modal("hide");
                        location.reload();
                    } else {
                        alert("저장에 실패했습니다.");
                    }
                },
                error: function () {
                    alert("서버 오류가 발생했습니다.");
                }
            });
        } else if ($(this).data("type") === "category") {
            const id = $("#modalCategoryId").val();
            const name = $("#modalCategoryName").val();
            $.ajax({
                type: "POST",
                url: `/blog/${email}/setting/categoryUpdate`,
                data: {
                    id: id,
                    name: name,
                },
                success: function (response) {
                    if (response.success) {
                        $('#editModal').modal("hide");
                        location.reload();
                    } else {
                        alert("저장에 실패했습니다.");
                    }
                },
                error: function () {
                    alert("서버 오류가 발생했습니다.");
                }
            });
        } else if ($(this).data("type") === "categoryAdd") {
            const name = $("#modalCategoryName").val();
            $.ajax({
                type: "POST",
                url: `/blog/${email}/setting/categoryCreate`,
                data: {
                    name: name,
                },
                success: function (response) {
                    if (response.success) {
                        $('#editModal').modal("hide");
                        location.reload();
                    } else {
                        alert("저장에 실패했습니다.");
                    }
                },
                error: function () {
                    alert("서버 오류가 발생했습니다.");
                }
            });
        }
    });

    // 블로그 셋팅 끝

});
