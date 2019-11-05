// 这个方法就是一个ajax方法
function like(btn, entityType, entityId, entityUserId, postId) {
    $.post(
        // 数据要传送给的地址
        CONTEXT_PATH + "/like",
        // 要传给服务器的数据
        {"entityType":entityType,"entityId":entityId,"entityUserId":entityUserId,"postId":postId},
        // 接收服务器响应数据
        function(data) {
            // 将json格式转化为js对象
            data = $.parseJSON(data);

            if (data.code == 0) {
                // 通过界面传过来的当前标签，可以取到它的子标签，来修改其内容
                $(btn).children("i").text(data.likeCount);
                $(btn).children("b").text(data.likeStatus==1?'已赞':"赞");
            } else {
                alert(data.msg);
            }
        }
    );
}