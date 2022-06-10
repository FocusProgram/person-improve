$(function () {
    $("#dailogForm").Validform({
        btnSubmit: "#btn_sub",
        tiptype: 4,
        ajaxPost: true,
        usePlugin: {
            passwordstrength: {
                minLen: 6,
                maxLen: 18,
                trigger: function (obj, error) {
                    if (error) {
                        obj.parent().next().find(".Validform_checktip").show();
                        obj.find(".passwordStrength").hide();
                    } else {
                        $(".passwordStrength").show();
                        obj.parent().next().find(".Validform_checktip").hide();
                    }
                }
            }
        },
        callback: function (data) {
            if (data.success) {
                alert(data.msg);
                document.getElementById('formSubmit').submit();
            } else {
                alert(data.msg);
            }
            //$('#myModal').modal('hide');
        }
    });
});