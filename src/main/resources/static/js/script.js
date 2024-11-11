$(document).ready(function () {

    $("#myNeighbor").click(function () {
        let myNeighbor = false;
        myNeighbor = !!$(this).children().hasClass("bi-heart-fill");

        const urlParts = window.location.pathname.split('/');
        const email = urlParts[2]; // "/blog/{email}"에서 {email} 부분을 추출

        $.ajax({
            type: "POST",
            url: `/blog/${email}/neighbor`,
            data: { myNeighbor: myNeighbor },
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

    $(".blog-edit, .category-edit, .category-create").click(function () {
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

    $(".category-del, .post-del, .comment-del, .neighbor-del").click(function () {
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
                            if (response.view) {
                                location.replace(`/blog/${email}`);
                            } else {
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
            }
        }
    });

    $(".comment-create").click(function () {
        const urlParts = window.location.pathname.split('/');
        const email = urlParts[2]; // "/blog/{email}"에서 {email} 부분을 추출
        const content = $("#commentContent").val();
        const post_id = $("#comment-post-id").val();
        $.ajax({
            type: "POST",
            url: `/blog/${email}/comment/write`,
            data: {
                post_id: post_id,
                content: content,
            },
            success: function (response) {
                if (response.success) {
                    location.reload();
                } else {
                    alert("댓글 작성에 실패했습니다.");
                }
            },
            error: function () {
                alert("서버 오류가 발생했습니다.");
            }
        });
    });

    $(".comment-updateReady").click(function () {
        // 댓글 요소를 가져옴
        const commentItem = $(this).closest(".comment-item");

        // 기존 댓글 내용과 버튼을 저장
        const originalContent = commentItem.find(".comment-content").text();

        // 댓글 내용을 input 필드로 변경
        commentItem.find(".comment-content").replaceWith(`
        <input type="text" class="form-control comment-edit-input" name="content" value="${originalContent}">
    `);

        // 수정 준비 버튼을 저장, 취소 버튼으로 교체
        $(this).siblings(".comment-delete").replaceWith(`
        <button class="btn btn-secondary comment-cancel">취소</button>
    `);
        $(this).replaceWith(`
        <button class="btn btn-primary comment-update">저장</button>
    `);
    });

    $(".comment-cancel").click(function () {
        // 댓글 요소를 가져옴
        const commentItem = $(this).closest(".comment-item");

        // input 필드를 원래의 댓글 내용으로 되돌림
        const editedContent = commentItem.find(".comment-edit-input").val();
        commentItem.find(".comment-edit-input").replaceWith(`
        <p class="comment-content card-body p-2">${editedContent}</p>
    `);

        // 저장, 취소 버튼을 다시 수정 준비 및 삭제 버튼으로 변경
        $(this).siblings(".comment-update").replaceWith(`
           <button class="btn btn-success comment-updateReady">수정</button>
    `);
        $(this).replaceWith(`
           <button class="btn btn-danger comment-delete">삭제</button>
    `);
    });

    $(".comment-update").click(function () {
        const urlParts = window.location.pathname.split('/');
        const email = urlParts[2]; // "/blog/{email}"에서 {email} 부분을 추출
        const content = $(".comment-edit-input").val();
        const commentId = $(this).closest(".comment-value").find(".comment-id").val();
        $.ajax({
            type: "POST",
            url: `/blog/${email}/comment/update`,
            data: {
                id: commentId,
                content: content,
            },
            success: function (response) {
                if (response.success) {
                    location.reload();
                } else {
                    alert("댓글 수정에 실패했습니다.");
                }
            },
            error: function () {
                alert("서버 오류가 발생했습니다.");
            }
        });
    });

    $(".comment-delete").click(function () {
        const urlParts = window.location.pathname.split('/');
        const email = urlParts[2]; // "/blog/{email}"에서 {email} 부분을 추출
        const commentId = $("#comment-id").val();
        $.ajax({
            type: "POST",
            url: `/blog/${email}/comment/delete`,
            data: {
                id: commentId,
            },
            success: function (response) {
                if (response.success) {
                    location.reload();
                } else {
                    alert("댓글 삭제에 실패했습니다.");
                }
            },
            error: function () {
                alert("서버 오류가 발생했습니다.");
            }
        });
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
                    description: description,
                },
                success: function (response) {
                    if (response.success) {
                        location.reload();
                    } else {
                        alert("수정에 실패했습니다.");
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
                        location.reload();
                    } else {
                        alert("수정에 실패했습니다.");
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
                        location.reload();
                    } else {
                        alert("카테고리 추가에 실패했습니다.");
                    }
                },
                error: function () {
                    alert("서버 오류가 발생했습니다.");
                }
            });
        }
    });
});
