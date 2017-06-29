/**
 * Created by 王子恒 on 2017/6/10.
 */
$(document).ready(function() {

    getInitialUserList();

    var role = "VIP";

    function getInitialUserList() {
        role = "VIP";
        $("#modify-button").html('升级为高级用户');
        $("#contentPage").pagination({
            pageIndex: 0,
            pageSize: 10,
            total: 100,
            debug: false,
            showInfo: true,
            showJump: false,
            infoFormat: '共{total}条',
            showPageSizes: false,
            pageElementSort: ['$page', '$size', '$jump', '$info'],
            remote: {
                url: 'user/manage/list',
                pageParams: function(data) {
                    return {
                        size: data.pageSize,
                        page: data.pageIndex,
                        sort: encodeURI("id,desc")
                    };
                },
                totalName: 'page.totalElements',
                success: function(data) {
                    updateUserTable(data.page.content);
                },

            }
        })
    }

    function getInitialVIPList() {
        role = "USER";
        $("#modify-button").html('降级为普通用户');
        $("#contentPage").pagination({
            pageIndex: 0,
            pageSize: 10,
            total: 100,
            debug: false,
            showInfo: true,
            showJump: false,
            infoFormat: '{start}~{end} 共{total}条',
            showPageSizes: false,
            pageElementSort: ['$page', '$size', '$jump', '$info'],
            remote: {
                url: 'user/manage/list?role=VIP',
                pageParams: function(data) {
                    return {
                        size: data.pageSize,
                        page: data.pageIndex,
                        sort: encodeURI("id,desc")
                    };
                },
                totalName: 'page.totalElements',
                success: function(data) {
                    updateVIPTable(data.page.content);
                },

            }
        })
    }


    $("#user").bind("click", function() {
        $("#contentPage").pagination('destroy');
        getInitialUserList();
    });

    $("#vip").bind("click", function() {
        $("#contentPage").pagination('destroy');
        getInitialVIPList();
    });


    function updateUserTable(content) {
        $("#contentTable").empty();
        for (var i = 0; i < content.length; i++) {
            var line = $('<tr id=' + content[i].id + '></tr>');
            line.append('<td><input type="checkbox" name="checkbox"></td>');
            line.append('<td>' + content[i].username + '</td>');
            line.append('<td>普通用户</td>');
            line.append('<td>' + content[i].email + '</td>');
            $('#contentTable').append(line);
        }

    }

    function updateVIPTable(content) {
        $("#contentTable").empty();
        for (var i = 0; i < content.length; i++) {
            var line = $('<tr id=' + content[i].id + '></tr>');
            line.append('<td><input type="checkbox" name="checkbox"></td>');
            line.append('<td>' + content[i].username + '</td>');
            line.append('<td>高级用户</td>');
            line.append('<td>' + content[i].email + '</td>');
            $('#contentTable').append(line);
        }

    }



    $('#delete-button').click(function() {
        var ids = new Array();
        $('input[name="checkbox"]:checked').each(function() {
            ids.push($(this).parent().parent().attr("id"));
        })
        $.ajax({
            type: 'post',
            url: 'user/manage/delete',
            data: {
                "ids": ids
            },
            success: function(result) {
                $('input[name="checkbox"]:checked').each(function() {
                    $(this).parent().parent().remove();
                })
                alertInfo("删除用户成功!");
                window.location.reload();

            }
        });
    });

    $('#modify-button').click(function() {
        var ids = new Array();
        $('input[name="checkbox"]:checked').each(function() {
            ids.push($(this).parent().parent().attr("id"));
        })
        if(ids.length!==0){
            $.ajax({
            type: 'post',
            url: 'user/manage/set-permission',
            data: {
                "ids": ids,
                "role": role
            },
            success: function(result) {
                $('input[name="checkbox"]:checked').each(function() {
                    $(this).parent().parent().remove();
                })
                alertInfo("修改权限成功！");
                window.location.reload();


            }
           });
        }
        
    });
    $("#exit").bind("click", function(){
        $.ajax({
            type: 'post',
            url: 'user/logout',
            success: function(result) {
                alertInfo(result.message);
                window.location.href = "home.html";//有问题，模态框闪退或不显示
            },
            fail: function() {
                alert("failed");
            },
            error: function(response) {
                alert("shenhongfei error!!!");
            }
        });
    })

})

